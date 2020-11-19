package main;

import customs.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RunningCoursesGenerator implements Activity {
    private static KTable activeTable;
    private static KTableModel activeModel;
    private static FirefoxDriver activeDriver;
    public static KLabel semesterBigLabel;
    public static KLabel noticeLabel;
    private static KMenuItem matchItem;
    private static KButton optionsButton;
    public static final ArrayList<RunningCourse> STARTUP_REGISTRATIONS = new ArrayList<>();
    private static final ArrayList<RunningCourse> ACTIVE_COURSES = new ArrayList<>() {
        @Override
        public boolean add(RunningCourse course) {
            activeModel.addRow(new String[] {course.getCode(), course.getName(), course.getLecturer(), course.getSchedule(),
                    course.isConfirmed() ? "Confirmed" : "Unknown"});
            return super.add(course);
        }
        @Override
        public boolean remove(Object o) {
            activeModel.removeRow(activeModel.getRowOf(((RunningCourse) o).getCode()));
            return super.remove(o);
        }
        @Override
        public RunningCourse set(int index, RunningCourse course) {
            final int targetRow = activeModel.getRowOf(course.getCode());
            activeModel.setValueAt(course.getCode(), targetRow, 0);
            activeModel.setValueAt(course.getName(), targetRow, 1);
            activeModel.setValueAt(course.getLecturer(), targetRow, 2);
            activeModel.setValueAt(course.getSchedule(), targetRow, 3);
            activeModel.setValueAt(course.isConfirmed() ? "Confirmed" : "Unknown", targetRow, 4);
            return super.set(index, course);
        }
    };


    public RunningCoursesGenerator() {
        semesterBigLabel = new KLabel(Student.getSemester(), KFontFactory.bodyHeaderFont());
        semesterBigLabel.setPreferredSize(new Dimension(925, 35));
        semesterBigLabel.setHorizontalAlignment(SwingConstants.CENTER);
        semesterBigLabel.underline(Color.BLACK, true);

        noticeLabel = new KLabel(Portal.getRegistrationNotice()+("  (Last updated: "+Portal.getLastRegistrationNoticeUpdate()+")"),
                KFontFactory.createPlainFont(16), Color.RED);

        matchItem = new KMenuItem("Match Portal", e-> startMatching(true));

        final KMenuItem updateItem = new KMenuItem("Update Registration Notice",
                e-> App.promptPlain("Tip", "To renew the registration notice, go to 'Notifications | Portal Alerts | Update Alerts'"));

        final KMenuItem visitItem = new KMenuItem("Visit Portal Instead");
        visitItem.addActionListener(e-> Portal.openPortal(visitItem));

        final JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.add(matchItem);
        popupMenu.add(updateItem);
        popupMenu.add(visitItem);

        optionsButton = KButton.getIconifiedButton("options.png",25,25);
        optionsButton.setFont(KFontFactory.createBoldFont(15));
        optionsButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        optionsButton.setToolTipText("More options");
        final int buttonWidth = optionsButton.getPreferredSize().width;
        optionsButton.addActionListener(e -> popupMenu.show(optionsButton,
                optionsButton.getX() + buttonWidth - buttonWidth/4, optionsButton.getY() - 15));

        final KPanel upperPanel = new KPanel(new BorderLayout());
        upperPanel.add(optionsButton, BorderLayout.WEST);
        upperPanel.add(new KPanel(semesterBigLabel), BorderLayout.CENTER);
        upperPanel.add(Box.createRigidArea(new Dimension(975, 10)), BorderLayout.SOUTH);

        final KPanel runningActivity = new KPanel(new BorderLayout());
        runningActivity.add(upperPanel, BorderLayout.NORTH);
        runningActivity.add(runningSubstances(), BorderLayout.CENTER);
        runningActivity.add(new KPanel(new FlowLayout(FlowLayout.LEFT), noticeLabel), BorderLayout.SOUTH);

        Board.addCard(runningActivity, "Running Courses");
    }

    @Override
    public void answerActivity() {
        Board.showCard("Running Courses");
    }

    public static void uploadInitials(){
        for (RunningCourse c : STARTUP_REGISTRATIONS) {
            ACTIVE_COURSES.add(c);
        }
    }

    public static void updateNotice(){
        noticeLabel.setText(Portal.getRegistrationNotice()+("  (Last updated: "+Portal.getLastRegistrationNoticeUpdate()+")"));
    }

    public static synchronized void fixRunningDriver(){
        if (activeDriver == null) {
            activeDriver = DriversPack.forgeNew(true);
        }
    }

    /**
     * Goes ahead and launch search for the selected row's data.
     * As for the approach, the codes is the weapon. If a module is found with the same code,
     * the added module will be replaced; otherwise not found.
     */
    private static void launchConfirmationSequence() {
        final String targetCode = String.valueOf(activeModel.getValueAt(activeTable.getSelectedRow(),0));
        final RunningCourse targetCourse = getByCode(targetCode);
        if (targetCourse == null) {
            return;
        }

        if (!App.showOkCancelDialog("Checkout "+targetCourse.getName(),
                "Dashboard will initiate a handshake with your portal, and notify you if\n\"" +
                        targetCourse.getName()+"\" is among the courses you registered this semester.\n \n" +
                "Please refer to "+ Tips.reference("Running Courses | Verification"))) {
            return;
        }

        final String initialValue = String.valueOf(activeModel.getValueAt(activeModel.getRowOf(targetCode),
                activeModel.getColumnCount() - 1));
        activeModel.setValueAt("Verifying...", activeModel.getRowOf(targetCode), activeModel.getColumnCount() - 1);
        if (activeDriver == null) {
            fixRunningDriver();
            if (activeDriver == null) {
                App.reportMissingDriver();
                final int targetRow = activeModel.getRowOf(targetCode);
                if (targetRow >= 0) {
                    activeModel.setValueAt(initialValue, targetRow, activeModel.getColumnCount() - 1);
                }
                return;
            }
        }
        if (!Internet.isInternetAvailable()) {
            App.reportNoInternet();
            final int targetRow = activeModel.getRowOf(targetCode);
            if (targetRow >= 0) {
                activeModel.setValueAt(initialValue, targetRow, activeModel.getColumnCount() - 1);
            }
            return;
        }

        synchronized (activeDriver){
            final WebDriverWait loadWaiter = new WebDriverWait(activeDriver, Portal.MAXIMUM_WAIT_TIME);
            final int loginAttempt = DriversPack.attemptLogin(activeDriver);
            if (loginAttempt == DriversPack.ATTEMPT_SUCCEEDED) {
                if (Portal.isPortalBusy(activeDriver)) {
                    App.reportBusyPortal();
                    final int targetRow = activeModel.getRowOf(targetCode);
                    if (targetRow >= 0) {
                        activeModel.setValueAt(initialValue, targetRow, activeModel.getColumnCount() - 1);
                    }
                    return;
                }
            } else if (loginAttempt == DriversPack.CONNECTION_LOST) {
                App.reportConnectionLost();
                final int targetRow = activeModel.getRowOf(targetCode);
                if (targetRow >= 0) {
                    activeModel.setValueAt(initialValue, targetRow, activeModel.getColumnCount() - 1);
                }
                return;
            } else if (loginAttempt == DriversPack.ATTEMPT_FAILED) {
                App.reportLoginAttemptFailed();
                final int targetRow = activeModel.getRowOf(targetCode);
                if (targetRow >= 0) {
                    activeModel.setValueAt(initialValue, targetRow, activeModel.getColumnCount() - 1);
                }
                return;
            }

            final List<WebElement> tabs;
            try {
                activeDriver.navigate().to(Portal.CONTENTS_PAGE);
                Portal.nowOnPortal(activeDriver);
                tabs = loadWaiter.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".nav-tabs > li")));
            } catch (Exception e) {
                App.reportConnectionLost();
                final int targetRow = activeModel.getRowOf(targetCode);
                if (targetRow >= 0) {
                    activeModel.setValueAt(initialValue, targetRow, activeModel.getColumnCount() - 1);
                }
                return;
            }

            tabs.get(4).click();
            final WebElement registrationTable = activeDriver.findElementByCssSelector(".table-warning");
            final WebElement tableBody = registrationTable.findElement(By.tagName("tbody"));
            final List<WebElement> captions = tableBody.findElements(By.cssSelector("b, strong"));
            final boolean registered = captions.get(captions.size() - 1).getText().equals(Student.getSemester());
            if (!registered) {
                App.promptWarning("Checkout Failed","The attempt to checkout '"+targetCourse.getName()+"' was unsuccessful.\n" +
                        "You may have not have registered for this semester.");
                final int targetRow = activeModel.getRowOf(targetCode);
                if (targetRow >= 0) {
                    activeModel.setValueAt(initialValue, targetRow, activeModel.getColumnCount() - 1);
                }
                return;
            }

            final List<WebElement> allRows = tableBody.findElements(By.tagName("tr"));
//            Let the scrapping begin!
//            'match' refers the row-index of the table's data falling under the required caption
            int match = allRows.size() -1;
            boolean found = false;
//            iteration works upward until the caption is found
            while (!allRows.get(match).getText().equals(Student.getSemester())) {
                final List<WebElement> instantData = allRows.get(match).findElements(By.tagName("td"));
                if (instantData.get(0).getText().equals(targetCode)) {
                    final RunningCourse foundCourse = new RunningCourse(instantData.get(0).getText(),
                            instantData.get(1).getText(), instantData.get(2).getText(), instantData.get(3).getText(),
                            instantData.get(4).getText(), targetCourse.getDay(), targetCourse.getTime(), true);
                    final int targetIndex = getIndexOf(targetCourse);
                    if (targetIndex >= 0) {//still present?
                        ACTIVE_COURSES.set(targetIndex, foundCourse);
                    } else {//deleted?
                        ACTIVE_COURSES.add(foundCourse);
                    }
                    App.promptPlain("Checkout Successful","Registered course '"+targetCourse.getName()+"' checked out successful.\n" +
                            "It is now confirmed set. You can click on 'Details' to view the its details.");
                    found = true;
                    break;
                }
                match--;
            }

            if (!found) {
                App.signalError("Checkout Unsuccessful","The attempt to checkout '"+targetCourse.getName()+"' was unsuccessful.\n" +
                        "It was not found as a registered course this semester.\n" +
                        "Please refer to the "+ Tips.reference("Running Courses | Verification")+"\n"+
                        "for more information about this action, or simply use 'Matching' instead.");
                activeModel.setValueAt(initialValue, activeModel.getRowOf(targetCode), activeModel.getColumnCount() - 1);
            }
        }
    }

    /**
     * Sync will only be allowed to commence if one is not already on the way.
     * The difference is that: the user does not need to be prompted when they
     * did not request it.
     * This embeds itself in a thread
     */
    public static void startMatching(boolean userRequested){
        if (!(userRequested || matchItem.isEnabled())) {
            return;
        }

        if (!userRequested || App.showOkCancelDialog("Match Registration Table",
                "This feature is experimental. Dashboard will start to contact, and bring all the courses\n" +
                "(if there exists any) you have registered this semester. Continue?\n" +
                        "This action has consequences. Kindly refer to "+ Tips.reference("Running Courses | Matching"))) {
            new Thread(()-> {
                matchItem.setEnabled(false);
                if (activeDriver == null) {
                    fixRunningDriver();
                    if (activeDriver == null) {
                        if (userRequested) {
                            App.reportMissingDriver();
                        }
                        matchItem.setEnabled(true);
                        return;
                    }
                }

                if (!Internet.isInternetAvailable()) {
                    if (userRequested) {
                        App.reportNoInternet();
                    }
                    matchItem.setEnabled(true);
                    return;
                }

                synchronized (activeDriver){
                    final WebDriverWait loadWaiter = new WebDriverWait(activeDriver, Portal.MAXIMUM_WAIT_TIME);
                    final int loginAttempt = DriversPack.attemptLogin(activeDriver);
                    if (loginAttempt == DriversPack.ATTEMPT_SUCCEEDED) {
                        if (Portal.isPortalBusy(activeDriver)) {
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

                    final List<WebElement> tabs;
                    try {
                        activeDriver.navigate().to(Portal.CONTENTS_PAGE);
                        Portal.nowOnPortal(activeDriver);
                        tabs = loadWaiter.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".nav-tabs > li")));
                    } catch (Exception e) {
                        if (userRequested) {
                            App.reportConnectionLost();
                        }
                        matchItem.setEnabled(true);
                        return;
                    }

                    tabs.get(4).click();
                    final WebElement registrationTable = activeDriver.findElementByCssSelector(".table-warning");
                    final WebElement tableBody = registrationTable.findElement(By.tagName("tbody"));
                    final List<WebElement> allRows = tableBody.findElements(By.tagName("tr"));
                    final List<WebElement> captions = tableBody.findElements(By.cssSelector("b, strong"));
                    final boolean isRegistered = captions.get(captions.size() - 1).getText().equals(Student.getSemester());
                    if (!isRegistered) {
                        if (userRequested) {
                            App.promptWarning("Match Failed","Dashboard could not locate any registered course on your portal.\n" +
                                    "Please, register your courses first then try this action.");
                        }
                        matchItem.setEnabled(true);
                        return;
                    }

                    int match = allRows.size() -1;
                    int count = 0;
                    final List<String> foundCodes = new ArrayList<>();
                    final StringBuilder matchBuilder = new StringBuilder("Match completed successfully.\n");
                    while (!allRows.get(match).getText().equalsIgnoreCase(Student.getSemester())) {
                        final List<WebElement> data = allRows.get(match).findElements(By.tagName("td"));
                        final RunningCourse incoming = new RunningCourse(data.get(0).getText(), data.get(1).getText(),
                                data.get(2).getText(), data.get(3).getText(),data.get(4).getText(), "","", true);
                        final RunningCourse present = getByCode(incoming.getCode());
                        if (present == null) {//does not exist at all
                            ACTIVE_COURSES.add(incoming);
                            matchBuilder.append(incoming.getAbsoluteName()).append(" was found registered.\n");
                        } else if (!present.isConfirmed()) {//existed? override, but merge the schedule
                            incoming.setDay(present.getDay());
                            incoming.setTime(present.getTime());
                            ACTIVE_COURSES.set(getIndexOf(present), incoming);
                            matchBuilder.append(incoming.getName()).append(" was found registered - it's now merged, and confirmed set.\n");
                        }
                        foundCodes.add(incoming.getCode());
                        match--;
                        count++;
                    }
                    matchBuilder.append("-\n");

                    for (String existingCode : codes()) {
                        if (!foundCodes.contains(existingCode)) {
                            final RunningCourse existingCourse = Objects.requireNonNull(getByCode(existingCode));
                            if (existingCourse.isConfirmed()) {
                                ACTIVE_COURSES.remove(existingCourse);
                                matchBuilder.append(existingCourse.getAbsoluteName()).append(" is not found registered for this semester, hence it was removed.\n");
                            } else {
                                matchBuilder.append(existingCourse.getAbsoluteName()).append(" was not found registered - you can remove it.");
                            }
                        }
                    }

                    if (count == 0) {
                        App.promptWarning("Match Unsuccessful", "Dashboard could not locate any registered course on your portal.\n" +
                                "Did you register for this semester yet?");
                    } else {
                        App.promptPlain("Matching Successful", matchBuilder.toString());
                    }

                    matchItem.setEnabled(true);
                }
            }).start();
        }
    }

    private static String generateNotificationWarning(String moduleName) {
        return "Dear "+Student.getLastName() +
                "<p>you've added "+moduleName+" to your list of registered courses. " +
                "However, this does not means that Dashboard will <b>register</b> it on your Portal. " +
                "Dashboard does not write your portal!</p>" +
                "<p>If you've registered the course already on your Portal, then let Dashboard check it out " +
                "by clicking the <b>Checkout Button</b>.</p>";
    }

    public static String[] names() {
        final String[] names = new String[ACTIVE_COURSES.size()];
        for (int i = 0; i < ACTIVE_COURSES.size(); i++) {
            names[i] = ACTIVE_COURSES.get(i).getName();
        }
        return names;
    }

    public static String[] codes() {
        final String[] codes = new String[ACTIVE_COURSES.size()];
        for (int i = 0; i < ACTIVE_COURSES.size(); i++) {
            codes[i] = ACTIVE_COURSES.get(i).getCode();
        }
        return codes;
    }

    private static RunningCourse getByCode(String code) {
        for (RunningCourse course : ACTIVE_COURSES) {
            if (course.getCode().equals(code)) {
                return course;
            }
        }
        return null;
    }

    /**
     * This and co- refers to the index in the list.
     * For index in the table. use the model.getRowOf(String)
     */
    private static int getIndexOf(RunningCourse course){
        for (int i = 0; i < ACTIVE_COURSES.size(); i++) {
            if (ACTIVE_COURSES.get(i).getCode().equals(course.getCode())) {
                return i;
            }
        }
        return -1;
    }

    private Container runningSubstances() {
        activeModel = new KTableModel();
        activeModel.setColumnIdentifiers(new String[] {"CODE", "NAME", "LECTURER", "SCHEDULE","STATUS"});
        activeTable = new KTable(activeModel);
        activeTable.setRowHeight(30);
        activeTable.setFont(KFontFactory.createBoldFont(15));
        activeTable.getTableHeader().setFont(KFontFactory.createBoldFont(16));
        activeTable.getTableHeader().setPreferredSize(new Dimension(activeTable.getPreferredSize().width,35));
        activeTable.getTableHeader().setForeground(Color.BLUE);
        activeTable.setFont(KFontFactory.createPlainFont(17));
        activeTable.getColumnModel().getColumn(0).setPreferredWidth(75);
        activeTable.getColumnModel().getColumn(1).setPreferredWidth(275);
        activeTable.getColumnModel().getColumn(2).setPreferredWidth(250);
        activeTable.getColumnModel().getColumn(3).setPreferredWidth(175);
        activeTable.getColumnModel().getColumn(4).setPreferredWidth(75);
        activeTable.centerAlignAllColumns();
        activeTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    final int selectedRow = activeTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        RunningCourse.exhibit(getByCode(String.valueOf(activeTable.getValueAt(selectedRow, 0))));
                        e.consume();
                    }
                }
            }
        });

        final Font buttonsFont = KFontFactory.createPlainFont(15);

        final KButton addButton = new KButton("Add");
        addButton.setFont(buttonsFont);
        addButton.addActionListener(e-> {
            if (activeTable.getRowCount() >= 6) {
                App.signalError("Limit Reached","The maximum number of registrations per semester is six (6) courses.");
            } else {
                SwingUtilities.invokeLater(()-> new RunningCourseAdder().setVisible(true));
            }
        });

        final KButton editButton = new KButton("Edit");
        editButton.setFont(buttonsFont);
        editButton.addActionListener(e-> {
            if (activeTable.getSelectedRow() >= 0) {
                final String code = String.valueOf(activeModel.getValueAt(activeTable.getSelectedRow(), 0));
                final RunningCourse runningCourse = getByCode(code);
                if (runningCourse != null) {
                    SwingUtilities.invokeLater(()-> new RunningCourseEditor(runningCourse).setVisible(true));
                }
            } else {
                App.promptPlain("No selection", "Firstly, select a course from the table that you want to edit.");
            }
        });

        final KButton verifyButton = new KButton("Checkout");
        verifyButton.setFont(buttonsFont);
        verifyButton.addActionListener(e-> {
            if (activeTable.getSelectedRow() >= 0) {
                new Thread(RunningCoursesGenerator::launchConfirmationSequence).start();
            } else {
                App.promptPlain("No selection", "Firstly, select a course from the table that you want to verify on the Portal.");
            }
        });

        final KButton exhibitButton = new KButton("Details");
        exhibitButton.setFont(buttonsFont);
        exhibitButton.addActionListener(e-> {
            if (activeTable.getSelectedRow() >= 0) {
                final String code = String.valueOf(activeModel.getValueAt(activeTable.getSelectedRow(), 0));
                RunningCourse.exhibit(getByCode(code));
            } else {
                App.promptPlain("No selection", "Firstly, select a course from the table that you want to show details of.\n" +
                        "You can also double-click on a row to exhibit its contents.");
            }
        });

        final KButton removeButton = new KButton("Remove");
        removeButton.setFont(buttonsFont);
        removeButton.addActionListener(e-> {
            if (activeTable.getSelectedRow() >= 0) {
                final RunningCourse course = getByCode(String.valueOf(activeModel.getValueAt(activeTable.getSelectedRow(), 0)));
                if (course != null && App.showOkCancelDialog("Remove", "Do you really wish to remove \""+course.getName()+"\"?")){
                    if (course.isConfirmed()) {
                        final int vInt = App.verifyUser("Enter your Mat. number to proceed with this changes:");
                        if (vInt == App.VERIFICATION_TRUE) {
                            ACTIVE_COURSES.remove(course);
                        } else if (vInt == App.VERIFICATION_FALSE) {
                            App.reportMatError();
                        }
                    } else {
                        ACTIVE_COURSES.remove(course);
                    }
                }
            } else {
                App.promptPlain("No selection", "Firstly, select a course from the table that you want to remove.");
            }
        });

        final KPanel buttonsPanel = new KPanel(new FlowLayout(FlowLayout.RIGHT), addButton, editButton, verifyButton,
                exhibitButton, removeButton);

        final KPanel substancePanel = new KPanel();
        substancePanel.setLayout(new BoxLayout(substancePanel, BoxLayout.Y_AXIS));
        substancePanel.addAll(new KScrollPane(activeTable), buttonsPanel, Box.createVerticalStrut(50));
        return substancePanel;
    }


    private static class RunningCourseAdder extends KDialog {
        KPanel layers;
        KTextField codeField, nameField, lecturerField, venueField, roomField;
        JComboBox<String> daysBox, hoursBox;
        KButton doneButton;

        private RunningCourseAdder(){
            super("New Registered Course");
            setResizable(true);
            setModalityType(KDialog.DEFAULT_MODALITY_TYPE);

            codeField = KTextField.rangeControlField(10);
            codeField.setPreferredSize(new Dimension(150, 30));
            final KPanel codeLayer = new KPanel(new BorderLayout());
            codeLayer.add(new KPanel(dialogLabel("Course Code:")), BorderLayout.WEST);
            codeLayer.add(new KPanel(codeField), BorderLayout.CENTER);

            nameField = new KTextField(new Dimension(325,30));
            final KPanel nameLayer = new KPanel(new BorderLayout());
            nameLayer.add(new KPanel(dialogLabel("Course Name:")), BorderLayout.WEST);
            nameLayer.add(new KPanel(nameField), BorderLayout.CENTER);

            lecturerField = new KTextField(new Dimension(325,30));
            final KPanel lecturerLayer = new KPanel(new BorderLayout());
            lecturerLayer.add(new KPanel(dialogLabel("Lecturer's Name:")), BorderLayout.WEST);
            lecturerLayer.add(new KPanel(lecturerField), BorderLayout.CENTER);

            venueField = new KTextField(new Dimension(275,30));
            final KPanel placeLayer = new KPanel(new BorderLayout());
            placeLayer.add(new KPanel(dialogLabel("Venue / Campus:")), BorderLayout.WEST);
            placeLayer.add(new KPanel(venueField), BorderLayout.CENTER);

            roomField = new KTextField(new Dimension(325, 30));
            final KPanel roomLayer = new KPanel(new BorderLayout());
            roomLayer.add(new KPanel(dialogLabel("Lecture Room:")), BorderLayout.WEST);
            roomLayer.add(new KPanel(roomField), BorderLayout.CENTER);

            daysBox = new JComboBox<>(Course.getWeekDays());
            daysBox.setFont(KFontFactory.createPlainFont(15));
            hoursBox = new JComboBox<>(Course.availableCoursePeriods());
            hoursBox.setFont(daysBox.getFont());
            final KPanel scheduleLayer = new KPanel(new FlowLayout(FlowLayout.CENTER));
            scheduleLayer.addAll(dialogLabel("Day:"), daysBox, Box.createRigidArea(new Dimension(50, 30)),
                    dialogLabel("Time:"), hoursBox);

            final KButton cancelButton = new KButton("Cancel");
            cancelButton.addActionListener(e-> dispose());

            doneButton = new KButton("Done");
            doneButton.addActionListener(e-> {
                if (codeField.isBlank()) {
                    App.signalError(getRootPane(),"No Code", "Please provide the code of the course.");
                    codeField.requestFocusInWindow();
                } else if (nameField.isBlank()) {
                    App.signalError(getRootPane(),"No Name", "Please provide the name of the course.");
                    nameField.requestFocusInWindow();
                } else if (lecturerField.isBlank()) {
                    App.signalError(getRootPane(),"No Lecturer","Please provide the name of the lecturer.");
                    lecturerField.requestFocusInWindow();
                } else {
                    final String givenCode = codeField.getText().toUpperCase();
                    if (getByCode(givenCode) != null) {
                        App.signalError("Duplicate Code","Cannot add this code: "+givenCode+". It's already assigned to a course in the list.");
                        return;
                    }

                    ACTIVE_COURSES.add(new RunningCourse(codeField.getText(), nameField.getText(), lecturerField.getText(),
                            venueField.getText(), roomField.getText(), String.valueOf(daysBox.getSelectedItem()),
                            String.valueOf(hoursBox.getSelectedItem()), false));
                    dispose();
                    SwingUtilities.invokeLater(()-> Notification.create("Local Registration", nameField.getText()+
                            " is locally added, and may not be on your portal", generateNotificationWarning(nameField.getText())));
                }
            });

            getRootPane().setDefaultButton(doneButton);
            layers = new KPanel();
            layers.setLayout(new BoxLayout(layers, BoxLayout.Y_AXIS));
            layers.addAll(codeLayer, nameLayer, lecturerLayer, placeLayer, roomLayer, scheduleLayer,
                    MComponent.contentBottomGap(), new KPanel(new FlowLayout(FlowLayout.RIGHT), cancelButton, doneButton));
            setContentPane(layers);
            pack();
            setMinimumSize(getPreferredSize());
            setLocationRelativeTo(Board.getRoot());
        }

        private static KLabel dialogLabel(String string) {
            return new KLabel(string, KFontFactory.createBoldFont(16));
        }
    }


    private static class RunningCourseEditor extends RunningCourseAdder {

        private RunningCourseEditor(RunningCourse original){
            super();
            setTitle(original.getName());
            codeField.setText(original.getCode());
            nameField.setText(original.getName());
            lecturerField.setText(original.getLecturer());
            venueField.setText(original.getVenue());
            roomField.setText(original.getRoom());
            daysBox.setSelectedItem(original.getDay());
            hoursBox.setSelectedItem(original.getTime());

            doneButton.removeActionListener(doneButton.getActionListeners()[0]);
            if (original.isConfirmed()) {
                codeField.setEditable(false);
                nameField.setEditable(false);
                lecturerField.setEditable(false);
            }

            doneButton.addActionListener(e-> {
                if (codeField.isBlank()) {
                    App.signalError(getRootPane(),"No Code", "Please provide the code of the course.");
                    codeField.requestFocusInWindow();
                } else if (nameField.isBlank()) {
                    App.signalError(getRootPane(),"No Name", "Please provide the name of the course.");
                    nameField.requestFocusInWindow();
                } else if (lecturerField.isBlank()) {
                    App.signalError(getRootPane(),"No Lecturer","Please provide the name of the lecturer.");
                    lecturerField.requestFocusInWindow();
                } else {
                    final RunningCourse refracted = new RunningCourse(codeField.getText(), nameField.getText(), lecturerField.getText(),
                            venueField.getText(), roomField.getText(), String.valueOf(daysBox.getSelectedItem()),
                            String.valueOf(hoursBox.getSelectedItem()), original.isConfirmed());
                    for (int i = 0; i < activeModel.getRowCount(); i++) {
                        if (i == activeTable.getSelectedRow()) {
                            continue;
                        }
                        final String refCode = refracted.getCode();
                        if ((refCode.equalsIgnoreCase(String.valueOf(activeModel.getValueAt(i, 0))))) {
                            App.signalError("Duplicate Code","Cannot add this code: "+refCode+
                                    ". It's already assigned to a course in the list.");
                            return;
                        }
                    }
                    ACTIVE_COURSES.set(getIndexOf(original), refracted);
                    dispose();
                }
            });
        }
    }


    public static void serializeModules(){
        final String[] runningCourses = new String[ACTIVE_COURSES.size()];
        for (int i = 0; i < runningCourses.length; i++) {
            runningCourses[i] = ACTIVE_COURSES.get(i).exportContent();
        }
        Serializer.toDisk(runningCourses, "active-modules.ser");
    }

    public static void deserializeModules(){
        final String[] runningCourses = (String[]) Serializer.fromDisk("active-modules.ser");
        if (runningCourses == null) {
            App.silenceException("Error reading Active Courses.");
            return;
        }
        for (String dataLines : runningCourses) {
            ACTIVE_COURSES.add(RunningCourse.importFromSerial(dataLines));
        }
    }

}
