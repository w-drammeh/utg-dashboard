package main;

import customs.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RunningCoursesGenerator implements ActivityAnswerer {
    public static final KLabel noticeLabel = new KLabel("", KFontFactory.createPlainFont(16), Color.RED);
    public static final KLabel semesterBigLabel = new KLabel("", KFontFactory.createBoldFont(20));
    private static KTable runningTable;
    private static KDefaultTableModel runningModel;
    private static FirefoxDriver runningDriver;
    private static KButton popUpButton;
    private static KMenuItem matchItem;
    public static final ArrayList<RunningCourse> STARTUP_REGISTRATIONS = new ArrayList<>();
    private static final ArrayList<RunningCourse> ACTIVE_COURSES = new ArrayList<RunningCourse>(){
        @Override
        public boolean add(RunningCourse course) {
            runningModel.addRow(new String[] {course.getCode(), course.getName(), course.getLecturer(), course.schedule(),
                    course.isConfirmed() ? "Confirmed" : "Unknown"});
            Board.effectRegistrationState(true);
            return super.add(course);
        }

        @Override
        public boolean remove(Object o) {
            runningModel.removeRow(runningModel.getRowOf(((RunningCourse)o).getCode()));
            Board.effectRegistrationState(runningTable.getRowCount() > 0);
            return super.remove(o);
        }

        @Override
        public RunningCourse set(int index, RunningCourse element) {
            final int targetRow = runningModel.getRowOf(element.getCode());
            runningModel.setValueAt(element.getCode(), targetRow, 0);
            runningModel.setValueAt(element.getName(), targetRow, 1);
            runningModel.setValueAt(element.getLecturer(), targetRow, 2);
            runningModel.setValueAt(element.schedule(), targetRow, 3);
            runningModel.setValueAt(element.isConfirmed() ? "Confirmed" : "Unknown", targetRow, 4);

            return super.set(index, element);
        }
    };


    public RunningCoursesGenerator(){
        noticeLabel.setText(Portal.getBufferedNotice_Registration()+ ("    [Last updated: "+Portal.getLastNoticeUpdate()+"]"));
        semesterBigLabel.setText(Student.getSemester());

        matchItem = new KMenuItem("Match Portal", e -> startMatching(true));

        final KMenuItem visitItem = new KMenuItem("Visit Portal Instead");
        visitItem.addActionListener(e -> Portal.userRequestsOpenPortal(visitItem));

        final KMenuItem updateItem = new KMenuItem("Update Registration Notice",
                e -> App.promptPlain("Tip", "To renew the registration notice, go to 'Notifications | Portal Alerts | Update Alerts'."));

        final JPopupMenu jPopup = new JPopupMenu();
        jPopup.add(matchItem);
        jPopup.add(updateItem);
        jPopup.add(visitItem);

        popUpButton = KButton.getIconifiedButton("options.png",25,25);
        popUpButton.undress();
        popUpButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        popUpButton.setFont(KFontFactory.createBoldFont(15));
        popUpButton.setToolTipText("More options");
        popUpButton.addActionListener(e -> jPopup.show(popUpButton, popUpButton.getX(),
                popUpButton.getY()+(popUpButton.getPreferredSize().height)));

        semesterBigLabel.setPreferredSize(new Dimension(925,35));
        semesterBigLabel.setHorizontalAlignment(SwingConstants.CENTER);
        semesterBigLabel.underline(null,true);

        final KPanel upperPanel = new KPanel(new BorderLayout());
        upperPanel.add(popUpButton, BorderLayout.WEST);
        upperPanel.add(KPanel.wantDirectAddition(semesterBigLabel), BorderLayout.CENTER);
        upperPanel.add(Box.createRigidArea(new Dimension(975, 10)), BorderLayout.SOUTH);

        final KPanel runningContainer = new KPanel(new BorderLayout());
        runningContainer.add(upperPanel, BorderLayout.NORTH);
        runningContainer.add(runningSubstances(), BorderLayout.CENTER);
        runningContainer.add(KPanel.wantDirectAddition(new FlowLayout(FlowLayout.LEFT), null, noticeLabel), BorderLayout.SOUTH);

        Board.addCard(runningContainer, "Running Courses");
    }

    public static void uploadInitials(){
        for (RunningCourse course : STARTUP_REGISTRATIONS) {
            ACTIVE_COURSES.add(course);
        }
    }

    /**
     * Goes ahead and launch search for the selected row's data.
     * As for the approach, the codes is the weapon. If a module is found with the same code,
     * the added module will be replaced; otherwise not found.
     */
    private static void launchConfirmationSequence(){
        final String targetCode = String.valueOf(runningModel.getValueAt(runningTable.getSelectedRow(),0));
        final RunningCourse targetCourse = getByCode(targetCode);
        if (targetCourse == null) {
            return;
        }
        if (!App.showOkCancelDialog("Verify "+targetCourse.getName(),
                "Dashboard will initiate a handshake with your portal, and notify you if\n" +
                        targetCourse.getName()+" is among the courses you registered this semester.\n" +
                "Please refer to "+HelpGenerator.reference("Running Courses | Verification")+"\n" +
                "for more information about this action.")) {
            return;
        }
        final String initialValue = String.valueOf(runningModel.getValueAt(runningModel.getRowOf(targetCode),
                runningModel.getColumnCount() - 1));
        runningModel.setValueAt("Checking...", runningModel.getRowOf(targetCode), runningModel.getColumnCount() - 1);
        if (runningDriver == null) {
            fixRunningDriver();
        }
        if (runningDriver == null) {
            App.reportMissingDriver();
            runningModel.setValueAt(initialValue, runningModel.getRowOf(targetCode), runningModel.getColumnCount() - 1);
            return;
        }
        if (!InternetAvailabilityChecker.isInternetAvailable()) {
            App.reportNoInternet();
            runningModel.setValueAt(initialValue, runningModel.getRowOf(targetCode), runningModel.getColumnCount() - 1);
            return;
        }

        synchronized (runningDriver){
            final WebDriverWait loadWaiter = new WebDriverWait(runningDriver, 50);//Should be more?
            if (DriversPack.isIn(runningDriver)) {
                try {
                    runningDriver.navigate().refresh();
                    if (Portal.isPortalBusy(runningDriver)) {
                        App.reportBusyPortal();
                        popUpButton.setEnabled(true);
                        runningModel.setValueAt(initialValue, runningModel.getRowOf(targetCode), runningModel.getColumnCount() - 1);
                        return;
                    }
                } catch (Exception e){
                    App.reportRefreshFailure();
                    runningModel.setValueAt(initialValue, runningModel.getRowOf(targetCode), runningModel.getColumnCount() - 1);
                }
            } else {
                final int loginAttempt = DriversPack.attemptLogin(runningDriver);
                if (loginAttempt == DriversPack.ATTEMPT_SUCCEEDED) {
                    if (Portal.isPortalBusy(runningDriver)) {
                        App.reportBusyPortal();
                        popUpButton.setEnabled(true);
                        runningModel.setValueAt(initialValue, runningModel.getRowOf(targetCode), runningModel.getColumnCount() - 1);
                        return;
                    }
                } else if (loginAttempt == DriversPack.CONNECTION_LOST) {
                    App.reportConnectionLost();
                    popUpButton.setEnabled(true);
                    runningModel.setValueAt(initialValue, runningModel.getRowOf(targetCode), runningModel.getColumnCount() - 1);
                    return;
                } else if (loginAttempt == DriversPack.ATTEMPT_FAILED) {
                    App.reportLoginAttemptFailed();
                    popUpButton.setEnabled(true);
                    runningModel.setValueAt(initialValue, runningModel.getRowOf(targetCode), runningModel.getColumnCount() - 1);
                    return;
                }
            }

            List<WebElement> tabs;
            try {
                runningDriver.navigate().to(Portal.CONTENTS_PAGE);
                Portal.nowOnPortal(runningDriver);
                tabs = loadWaiter.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".nav-tabs > li")));
            } catch (Exception e) {
                App.reportConnectionLost();
                popUpButton.setEnabled(true);
                runningModel.setValueAt(initialValue, runningModel.getRowOf(targetCode), runningModel.getColumnCount() - 1);
                return;
            }

            tabs.get(4).click();
            final WebElement allCourseTable = runningDriver.findElementByCssSelector(".table-warning");
            final WebElement tableBody = allCourseTable.findElement(By.tagName("tbody"));
            final List<WebElement> captions = tableBody.findElements(By.cssSelector("b, strong"));
            final boolean pass = captions.get(captions.size()-1).getText().equalsIgnoreCase(Student.getSemester());
            if (!pass) {
                App.promptWarning("Verification Unsuccessful","The attempt to search for "+targetCourse.getName()+" was unsuccessful.\n" +
                        "You may have not have registered for this semester yet.");
                popUpButton.setEnabled(true);
                runningModel.setValueAt(initialValue, runningModel.getRowOf(targetCode), runningModel.getColumnCount() - 1);
                return;
            }

            final List<WebElement> allRows = tableBody.findElements(By.tagName("tr"));
            //Let the scrapping begin! 'match' refers the row-index of the table's data falling under the required caption
            int match = allRows.size() -1;
            boolean found = false;
            final StringBuilder othersBuilder = new StringBuilder();
            while (!allRows.get(match).getText().equals(Student.getSemester())){
                final List<WebElement> instantData = allRows.get(match).findElements(By.tagName("td"));
                if (instantData.get(0).getText().equals(targetCode)) {
                    final RunningCourse foundCourse = new RunningCourse(instantData.get(0).getText(), instantData.get(1).getText(), instantData.get(2).getText(),
                            instantData.get(3).getText(), instantData.get(4).getText(),targetCourse.getDay(),targetCourse.getTime(), true);
                    final int index = getIndexOf(targetCourse);
                    if (index >= 0) {//Still present?
                        ACTIVE_COURSES.set(index, foundCourse);
                    } else {//Deleted?
                        ACTIVE_COURSES.add(foundCourse);
                    }
                    App.promptPlain("Verification Successful","The attempt to search for the registered module: "+targetCourse.getName()+" was successful.\n" +
                            "It is now confirmed set. You can use Exhibit to view the its details.");
                    found = true;
                    break;
                }
                othersBuilder.append(instantData.get(0).getText()).append(" ").append(instantData.get(1).getText()).append("\n");
                match--;
            }

            if (!found) {
                App.signalError("Verification Unsuccessful","The attempt to search for "+targetCourse.getName()+" was unsuccessful.\n" +
                        "It was not found as a currently registered course on your portal.\n" +
                        "However, the following course(s) were found registered this semester:\n" +
                        othersBuilder.toString() +
                        "Please refer to the "+HelpGenerator.reference("Running Courses | Verification")+"" +
                        "for more information about this action, or use Matching instead.");
                runningModel.setValueAt(initialValue, runningModel.getRowOf(targetCode), runningModel.getColumnCount() - 1);
            }
        }
    }

    /**
     * Sync will only be allowed to commence if one is not already on the way.
     * The difference is that: the user does not need to be prompted when they
     * did not request it.
     */
    public static void startMatching(boolean userRequested){
        if (!userRequested && !matchItem.isEnabled()) {
            return;
        }
        if (!userRequested || App.showOkCancelDialog("Match Registration Table",
                "This feature is experimental: Dashboard will start to contact, and bring all the courses\n" +
                "(if there exists any) you have registered this semester. Continue?")) {
            new Thread(()->{
                matchItem.setEnabled(false);
                if (runningDriver == null) {
                    fixRunningDriver();
                }
                if (runningDriver == null) {
                    if (userRequested) {
                        App.reportMissingDriver();
                    }
                    matchItem.setEnabled(true);
                    return;
                }
                if (!InternetAvailabilityChecker.isInternetAvailable()) {
                    if (userRequested) {
                        App.reportNoInternet();
                    }
                    matchItem.setEnabled(true);
                    return;
                }

                synchronized (runningDriver){
                    final WebDriverWait loadWaiter = new WebDriverWait(runningDriver, 50);//should be more?

                    if (DriversPack.isIn(runningDriver)) {
                        try {
                            runningDriver.navigate().refresh();
                            if (Portal.isPortalBusy(runningDriver)) {
                                if (userRequested) {
                                    App.reportBusyPortal();
                                }
                                matchItem.setEnabled(true);
                                return;
                            }
                        } catch (Exception e){
                            if (userRequested) {
                                App.reportRefreshFailure();
                            }
                        }
                    } else {
                        final int loginAttempt = DriversPack.attemptLogin(runningDriver);
                        if (loginAttempt == DriversPack.ATTEMPT_SUCCEEDED) {
                            if (Portal.isPortalBusy(runningDriver)) {
                                if (userRequested) {
                                    App.reportBusyPortal();
                                }
                                matchItem.setEnabled(true);
                                return;
                            }
                        } else if (loginAttempt == DriversPack.CONNECTION_LOST) {
                            if (userRequested) {
                                App.reportConnectionLost();
                            }
                            matchItem.setEnabled(true);
                            return;
                        } else if (loginAttempt == DriversPack.ATTEMPT_FAILED) {
                            if (userRequested) {
                                App.reportLoginAttemptFailed();
                            }
                            matchItem.setEnabled(true);
                            return;
                        }
                    }

                    List<WebElement> tabs;
                    try {
                        runningDriver.navigate().to(Portal.CONTENTS_PAGE);
                        Portal.nowOnPortal(runningDriver);
                        tabs = loadWaiter.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".nav-tabs > li")));
                    } catch (Exception e) {
                        if (userRequested) {
                            App.reportConnectionLost();
                        }
                        matchItem.setEnabled(true);
                        return;
                    }

                    tabs.get(4).click();
                    final WebElement allCourseTable = runningDriver.findElementByCssSelector(".table-warning");
                    final WebElement tableBody = allCourseTable.findElement(By.tagName("tbody"));
                    final List<WebElement> allRows = tableBody.findElements(By.tagName("tr"));

                    final List<WebElement> captions = tableBody.findElements(By.cssSelector("b, strong"));
                    final boolean pass = captions.get(captions.size()-1).getText().equalsIgnoreCase(Student.getSemester());
                    if (!pass) {
                        if (userRequested) {
                            App.promptWarning("Matching Unsuccessful","Dashboard could not locate any registered course on your portal.\n" +
                                    "Please, register your courses first then run this action.");
                        }
                        matchItem.setEnabled(true);
                        return;
                    }

                    //Let the scrapping begin! 'match' refers the row-index of the table's data falling under the required caption - %this semester%
                    int match = allRows.size() -1;
                    int count = 0;
                    final List<String> foundCodes = new ArrayList<>();
                    final StringBuilder informationBuilder = new StringBuilder("Matching completed successfully:\n-\n");
                    while (!allRows.get(match).getText().equalsIgnoreCase(Student.getSemester())){
                        final List<WebElement> data = allRows.get(match).findElements(By.tagName("td"));
                        final RunningCourse incoming = new RunningCourse(data.get(0).getText(), data.get(1).getText(), data.get(2).getText(), data.get(3).getText(),data.get(4).getText(),
                                "","", true);
                        final RunningCourse present = getByCode(incoming.getCode());
                        if (present == null) {//Does not exist at all
                            ACTIVE_COURSES.add(incoming);
                            informationBuilder.append(incoming.getName()).append(" was found registered and it's now added.\n");
                        } else {//Existed? Override, but merge the schedule
                            incoming.setDay(present.getDay());
                            incoming.setTime(present.getTime());
                            ACTIVE_COURSES.set(getIndexOf(present), incoming);
                            informationBuilder.append(incoming.getName()).append(" was found registered and it's now merged, and confirmed set.\n");
                        }
                        foundCodes.add(incoming.getCode());
                        match--;
                        count++;
                    }
                    informationBuilder.append("-\n");
                    for (String c : codes()) {
                        if (foundCodes.contains(c)) {
                            continue;
                        }
                        final RunningCourse r = getByCode(c);
                        if (r == null) {
                            continue;
                        }
                        if (r.isConfirmed()){
                            ACTIVE_COURSES.remove(r);
                            informationBuilder.append(r.getName()).append(" is not found registered for this semester, hence it's removed.\n");
                        } else {
                            informationBuilder.append(r.getName()).append(" was not found - you can remove it.");
                        }
                    }
                    if (count == 0) {
                        App.promptWarning("Matching Unsuccessful", "Dashboard could not locate any registered courses on your portal.\n" +
                                "Did you register for this semester yet?");
                    } else {
                        App.promptPlain("Matching Successful", informationBuilder.toString());
                    }

                    matchItem.setEnabled(true);
                }
            }).start();
        }
    }

    public static synchronized void fixRunningDriver(){
        if (runningDriver == null) {
            runningDriver = DriversPack.forgeNew(true);
        }
    }

    private static String generateNotificationWarning(String moduleName){
        return "Dear "+Student.getLastName()+", you've added "+moduleName+" to your list of registered courses. " +
                "However, this does not means that Dashboard will effects the changes on your portal.\n" +
                "Dashboard does not write your portal!\n" +
                "If you've registered the course already on your portal, then let Dashboard confirms it " +
                "by selecting the course from the list and clicking the Confirm button." +
                "\n-\nHowever, we recommend that you let Dashboard match the entire table with that of your portal, " +
                "after your registrations. To achieve this, click the 'More Options' button " +
                "which appears at the top-left and select 'Match Portal'.";
    }

    public static String[] names(){
        final String[] names = new String[ACTIVE_COURSES.size()];
        for (int i = 0; i < ACTIVE_COURSES.size(); i++) {
            names[i] = ACTIVE_COURSES.get(i).getName();
        }
        return names;
    }

    public static String[] codes(){
        final String[] codes = new String[ACTIVE_COURSES.size()];
        for (int i = 0; i < ACTIVE_COURSES.size(); i++) {
            codes[i] = ACTIVE_COURSES.get(i).getCode();
        }
        return codes;
    }

    private static RunningCourse getByCode(String code){
        for (RunningCourse r : ACTIVE_COURSES) {
            if (r.getCode().equals(code)) {
                return r;
            }
        }
        return null;
    }

    /**
     * This and co- refers to the index in the list. For index in the table. use theModel.getRowOf(code)
     */
    private static int getIndexOf(RunningCourse rCourse){
        for (int i = 0; i < ACTIVE_COURSES.size(); i++) {
            if (ACTIVE_COURSES.get(i).getCode().equals(rCourse.getCode())) {
                return i;
            }
        }
        return -1;
    }

    private Container runningSubstances(){
        runningModel = new KDefaultTableModel();
        runningModel.setColumnIdentifiers(new String[] {"CODE", "NAME", "LECTURER", "SCHEDULE","STATUS"});
        runningTable = new KTable(runningModel);
        runningTable.setRowHeight(30);
        runningTable.setFont(KFontFactory.createBoldFont(15));
        runningTable.getTableHeader().setFont(KFontFactory.createBoldFont(16));
        runningTable.getTableHeader().setPreferredSize(new Dimension(runningTable.getPreferredSize().width,35));
        runningTable.getTableHeader().setForeground(Color.BLUE);
        runningTable.setFont(KFontFactory.createPlainFont(17));
        runningTable.getColumnModel().getColumn(0).setPreferredWidth(75);
        runningTable.getColumnModel().getColumn(1).setPreferredWidth(275);
        runningTable.getColumnModel().getColumn(2).setPreferredWidth(250);
        runningTable.getColumnModel().getColumn(3).setPreferredWidth(175);
        runningTable.getColumnModel().getColumn(4).setPreferredWidth(75);
        runningTable.centerAlignAllColumns();

        final Font buttonsFont = KFontFactory.createPlainFont(15);

        final KButton addButton = new KButton("Add");
        addButton.setFont(buttonsFont);
        addButton.addActionListener(e -> {
            if (runningTable.getRowCount() >= 6) {
                App.signalError("Limit Reached","Sorry, the maximum number of registrations per semester is attained.");
            } else {
                SwingUtilities.invokeLater(()-> new RunningCourseAdder().setVisible(true));
            }
        });

        final KButton exhibitButton = new KButton("Exhibit");
        exhibitButton.setFont(buttonsFont);
        exhibitButton.setForeground(Color.BLUE);
        exhibitButton.addActionListener(e -> {
            if (runningTable.getSelectedRow() >= 0) {
                final String eCode = String.valueOf(runningModel.getValueAt(runningTable.getSelectedRow(), 0));
                final RunningCourse eCourse = getByCode(eCode);
                if (eCourse != null) {
                    SwingUtilities.invokeLater(()-> RunningCourse.exhibit(Board.getRoot(), eCourse));
                }
            } else {
                App.promptPlain("No selection", "Firstly, select a course from the table that you want to show details of.");
            }
        });

        final KButton editButton = new KButton("Edit");
        editButton.setFont(buttonsFont);
        editButton.addActionListener(e -> {
            if (runningTable.getSelectedRow() >= 0) {
                final String code = String.valueOf(runningModel.getValueAt(runningTable.getSelectedRow(), 0));
                final RunningCourse t = getByCode(code);
                if (t != null) {
                    SwingUtilities.invokeLater(() -> new RunningCourseEditor(t).setVisible(true));
                }
            } else {
                App.promptPlain("No selection", "Firstly, select a course from the table that you want to edit.");
            }
        });

        final KButton removeButton = new KButton("Remove");
        removeButton.setFont(buttonsFont);
        removeButton.setForeground(Color.RED);
        removeButton.addActionListener(e -> {
            if (runningTable.getSelectedRow() >= 0) {
                final RunningCourse t = getByCode(String.valueOf(runningModel.getValueAt(runningTable.getSelectedRow(),0)));
                if (t != null && App.showOkCancelDialog("Confirm","Do you really wish to remove "+t.getName()+"?"+(t.isConfirmed() ? "\n" +
                        "This course was already found on your portal.\n" +
                        "Make sure it's unregistered there as well." : ""))) {
                    if (t.isConfirmed()) {
                        final int vInt = App.verifyUser("Enter your your mat. number to proceed with this changes:\n \n");
                        if (vInt == App.VERIFICATION_TRUE) {
                            ACTIVE_COURSES.remove(t);
                        } else if (vInt == App.VERIFICATION_FALSE) {
                            App.reportMatError();
                        }
                    } else {
                        ACTIVE_COURSES.remove(t);
                    }
                }
            } else {
                App.promptPlain("No selection", "Firstly, select a course from the table that you want to remove.");
            }
        });

        final KButton verifyButton = new KButton("Confirm");
        verifyButton.setFont(buttonsFont);
        verifyButton.addActionListener(e -> {
            if (runningTable.getSelectedRow() >= 0) {
                new Thread(RunningCoursesGenerator::launchConfirmationSequence).start();
            } else {
                App.promptPlain("No selection", "Firstly, select a course from the table that you want to verify on the portal.");
            }
        });

        final KPanel buttonsPanel = new KPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.addAll(addButton, exhibitButton, editButton, verifyButton, removeButton);

        final KPanel substancePanel = new KPanel();
        substancePanel.setLayout(new BoxLayout(substancePanel, BoxLayout.Y_AXIS));
        substancePanel.addAll(new KScrollPane(runningTable, false), buttonsPanel, Box.createVerticalStrut(50));

        return substancePanel;
    }

    @Override
    public void answerActivity() {
        Board.showCard("Running Courses");
    }

    public static void serializeModules(){
        System.out.print("Serializing registered courses... ");
        final String[] runningCourses = new String[ACTIVE_COURSES.size()];
        for (int i = 0; i < runningCourses.length; i++) {
            runningCourses[i] = ACTIVE_COURSES.get(i).exportContent();
        }
        MyClass.serialize(runningCourses, "runCourses.ser");
        System.out.println("Completed.");
    }

    public static void deserializeModules(){
        System.out.print("Deserializing registered courses... ");
        final String[] runningCourses = (String[]) MyClass.deserialize("runCourses.ser");
        for (String line : runningCourses) {
            ACTIVE_COURSES.add(RunningCourse.importFromSerial(line));
        }
        System.out.println("Completed.");
    }


    private static class RunningCourseAdder extends KDialog {
        KPanel layers;
        KTextField codeField, nameField, lecturerField, venueField, roomField;
        JComboBox<String> daysBox, hoursBox;
        KButton doneButton;

        private RunningCourseAdder(){
            super("New Registered Course");
            this.setResizable(true);
            this.setModalityType(KDialog.DEFAULT_MODALITY_TYPE);

            codeField = KTextField.rangeControlField(10);
            codeField.setPreferredSize(new Dimension(150, 30));
            final KPanel codeLayer = new KPanel(new BorderLayout());
            codeLayer.add(KPanel.wantDirectAddition(dialogLabel("Course Code:")),BorderLayout.WEST);
            codeLayer.add(KPanel.wantDirectAddition(codeField),BorderLayout.CENTER);

            nameField = new KTextField(new Dimension(325,30));
            final KPanel nameLayer = new KPanel(new BorderLayout());
            nameLayer.add(KPanel.wantDirectAddition(dialogLabel("Course Name:")),BorderLayout.WEST);
            nameLayer.add(KPanel.wantDirectAddition(nameField),BorderLayout.CENTER);

            lecturerField = new KTextField(new Dimension(325,30));
            final KPanel lecturerLayer = new KPanel(new BorderLayout());
            lecturerLayer.add(KPanel.wantDirectAddition(dialogLabel("Lecturer's Name:")),BorderLayout.WEST);
            lecturerLayer.add(KPanel.wantDirectAddition(lecturerField),BorderLayout.CENTER);

            venueField = new KTextField(new Dimension(275,30));
            final KPanel placeLayer = new KPanel(new BorderLayout());
            placeLayer.add(KPanel.wantDirectAddition(dialogLabel("Venue / Campus:")),BorderLayout.WEST);
            placeLayer.add(KPanel.wantDirectAddition(venueField),BorderLayout.CENTER);

            roomField = new KTextField(new Dimension(325, 30));
            final KPanel roomLayer = new KPanel(new BorderLayout());
            roomLayer.add(KPanel.wantDirectAddition(dialogLabel("Lecture Room:")),BorderLayout.WEST);
            roomLayer.add(KPanel.wantDirectAddition(roomField),BorderLayout.CENTER);

            daysBox = new JComboBox<>(Course.getWeekDays());
            daysBox.setFont(KFontFactory.createPlainFont(15));
            hoursBox = new JComboBox<>(Course.availableCoursePeriods());
            hoursBox.setFont(daysBox.getFont());
            final KPanel scheduleLayer = new KPanel(new FlowLayout(FlowLayout.CENTER));
            scheduleLayer.addAll(dialogLabel("Day:"),daysBox,Box.createRigidArea(new Dimension(50,30)),dialogLabel("Time:"),hoursBox);

            final KButton cancelButton = new KButton("Cancel");
            cancelButton.addActionListener(e -> RunningCourseAdder.this.dispose());

            doneButton = new KButton("Done");
            doneButton.addActionListener(e -> {
                if (Globals.isBlank(codeField.getText())) {
                    App.signalError(RunningCourseAdder.this.getRootPane(),"No Code","Please provide the code of the course.");
                    codeField.requestFocusInWindow();
                } else if (Globals.isBlank(nameField.getText())) {
                    App.signalError(RunningCourseAdder.this.getRootPane(),"No Name","Please provide the name of the course.");
                    nameField.requestFocusInWindow();
                } else if (Globals.isBlank(lecturerField.getText())) {
                    App.signalError(RunningCourseAdder.this.getRootPane(),"No Lecturer","Please provide the name of the lecturer.");
                    lecturerField.requestFocusInWindow();
                } else {
                    for (int i = 0; i < runningModel.getRowCount(); i++) {
                        if (runningModel.getValueAt(i,0).equals(codeField.getText().toUpperCase())) {
                            App.signalError("Duplicate Code","Cannot add this code "+codeField.getText().toUpperCase()+" - already assigned to a course in the list.");
                            return;
                        }
                    }
                    ACTIVE_COURSES.add(new RunningCourse(codeField.getText(), nameField.getText(), lecturerField.getText(), venueField.getText(), roomField.getText(), String.valueOf(daysBox.getSelectedItem()),
                            String.valueOf(hoursBox.getSelectedItem()), false));
                    this.dispose();
                    SwingUtilities.invokeLater(()-> Notification.create("Local Registration", nameField.getText()+" is locally added, and may not be on your portal", generateNotificationWarning(nameField.getText())));
                }
            });

            final KPanel buttonsLayer = new KPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonsLayer.addAll(cancelButton, doneButton);

            this.getRootPane().setDefaultButton(doneButton);
            layers = new KPanel();
            layers.setLayout(new BoxLayout(layers, BoxLayout.Y_AXIS));
            layers.addAll(codeLayer, nameLayer, lecturerLayer, placeLayer, roomLayer, scheduleLayer, ComponentAssistant.contentBottomGap(), buttonsLayer);
            this.setContentPane(layers);
            this.pack();
            this.setMinimumSize(this.getPreferredSize());
            this.setLocationRelativeTo(Board.getRoot());
        }

        private static KLabel dialogLabel(String string){
            return new KLabel(string,KFontFactory.createBoldFont(16));
        }
    }


    private static class RunningCourseEditor extends RunningCourseAdder {

        private RunningCourseEditor(RunningCourse runningCourse){
            super();
            this.setTitle(runningCourse.getName());
            this.codeField.setText(runningCourse.getCode());
            this.nameField.setText(runningCourse.getName());
            this.lecturerField.setText(runningCourse.getLecturer());
            this.venueField.setText(runningCourse.getVenue());
            this.roomField.setText(runningCourse.getRoom());
            this.daysBox.setSelectedItem(runningCourse.getDay());
            this.hoursBox.setSelectedItem(runningCourse.getTime());

            this.doneButton.setText("Refract");
            this.doneButton.removeActionListener(this.doneButton.getActionListeners()[0]);
            if (runningCourse.isConfirmed()) {
                this.codeField.setEditable(false);
                this.nameField.setEditable(false);
                this.lecturerField.setEditable(false);
            }

            this.doneButton.addActionListener(e -> {
                if (Globals.isBlank(codeField.getText())) {
                    App.signalError(RunningCourseEditor.this.getRootPane(),"No Code","Please provide the code of the course.");
                    codeField.requestFocusInWindow();
                } else if (Globals.isBlank(nameField.getText())) {
                    App.signalError(RunningCourseEditor.this.getRootPane(),"No Name","Please provide the name of the course.");
                    nameField.requestFocusInWindow();
                } else if (Globals.isBlank(lecturerField.getText())) {
                    App.signalError(RunningCourseEditor.this.getRootPane(),"No Lecturer","Please provide the name of the lecturer.");
                    lecturerField.requestFocusInWindow();
                } else {
                    final RunningCourse refracted = new RunningCourse(codeField.getText(), nameField.getText(), lecturerField.getText(), venueField.getText(), roomField.getText(), String.valueOf(daysBox.getSelectedItem()),
                            String.valueOf(hoursBox.getSelectedItem()), runningCourse.isConfirmed());
                    for (int row = 0; row < runningModel.getRowCount(); row++) {
                        if (row == runningTable.getSelectedRow()) {
                            continue;
                        }
                        if (runningModel.getValueAt(row ,0).toString().equalsIgnoreCase(codeField.getText())) {
                            App.signalError("Duplicate Code","Cannot add this code "+codeField.getText().toUpperCase()+" - already assigned to a course in the list.");
                            return;
                        }
                    }
                    ACTIVE_COURSES.set(getIndexOf(runningCourse), refracted);
                    dispose();
                }
            });
        }
    }

}
