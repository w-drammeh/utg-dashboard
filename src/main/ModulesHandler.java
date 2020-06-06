package main;

import customs.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ModulesHandler {
    public static final KDefaultTableModel[] models = new KDefaultTableModel[10];//8 and summer and misc
    public static final ArrayList<Course> STARTUP_COURSES = new ArrayList<>();
//    For the popups
    public static final String ADD_STRING = "Add", CONFIRM_STRING = "Verify", DELETE_STRING = "Remove",
        DETAILS_STRING = "Show Details", EDIT_STRING = "Edit";
    private static final String[] COLUMN_IDENTIFIERS = new String[] {"CODE", "NAME", "LECTURER", "GRADE"};
    private static String semesterName;//Value keeps changing to the table-semester currently receiving focus
    //The tables are local to their inner classes, but not the models
    private static KDefaultTableModel model1, model2, model3, model4, model5, model6, model7, model8;
    private static FirefoxDriver allDriver;
    /**
     * This list has complete dominance over all the addition, removal, and editing changes to every
     * single module. All the models delegate to it. They only add or delete or substitute after it does.
     */
    private static final ArrayList<Course> modulesMonitor = new ArrayList<Course>(){
        @Override
        public boolean add(Course course) {
            if (course.isMisc()) {
                MiscellaneousModulesHandler.welcome(course);
            } else if (course.isSummerSemester()) {
                SummerModulesHandler.welcome(course);
            } else if (course.getYear().equals(Student.firstAcademicYear())) {
                if (course.getSemester().equals(Student.FIRST_SEMESTER)) {
                    model1.addRow(new String[]{course.getCode(), course.getName(), course.getLecturer(), course.getGrade()});
                } else {
                    model2.addRow(new String[]{course.getCode(), course.getName(), course.getLecturer(), course.getGrade()});
                }
            } else if (course.getYear().equals(Student.secondAcademicYear())) {
                if (course.getSemester().equals(Student.FIRST_SEMESTER)) {
                    model3.addRow(new String[]{course.getCode(), course.getName(), course.getLecturer(), course.getGrade()});
                } else {
                    model4.addRow(new String[]{course.getCode(), course.getName(), course.getLecturer(), course.getGrade()});
                }
            } else if (course.getYear().equals(Student.thirdAcademicYear())) {
                if (course.getSemester().equals(Student.FIRST_SEMESTER)) {
                    model5.addRow(new String[]{course.getCode(), course.getName(), course.getLecturer(), course.getGrade()});
                } else {
                    model6.addRow(new String[]{course.getCode(), course.getName(), course.getLecturer(), course.getGrade()});
                }
            } else if (course.getYear().equals(Student.finalAcademicYear())) {
                if (course.getSemester().equals(Student.FIRST_SEMESTER)) {
                    model7.addRow(new String[]{course.getCode(), course.getName(), course.getLecturer(), course.getGrade()});
                } else {
                    model8.addRow(new String[]{course.getCode(), course.getName(), course.getLecturer(), course.getGrade()});
                }
            }

            Memory.mayRemember(course);
            return super.add(course);
        }

        @Override
        public boolean remove(Object o) {
            final Course c = (Course) o;
            if (c.isVerified()) {
                final int vInt = App.verifyUser("Enter your your Matriculation Number to proceed with this changes:");
                if (vInt == App.VERIFICATION_FALSE) {
                    App.reportMatError();
                    return false;
                } else if (vInt != App.VERIFICATION_TRUE) {
                    return false;
                }
            }

            if (c.isMisc()) {
                MiscellaneousModulesHandler.ridOf(c);
            } else if (c.isSummerSemester()) {
                SummerModulesHandler.ridOf(c);
            } else if (model1.getRowOf(c.getCode()) >= 0) {
                model1.removeRow(model1.getRowOf(c.getCode()));
            } else if (model2.getRowOf(c.getCode()) >= 0) {
                model2.removeRow(model2.getRowOf(c.getCode()));
            } else if (model3.getRowOf(c.getCode()) >= 0) {
                model3.removeRow(model3.getRowOf(c.getCode()));
            } else if (model4.getRowOf(c.getCode()) >= 0) {
                model4.removeRow(model4.getRowOf(c.getCode()));
            } else if (model5.getRowOf(c.getCode()) >= 0) {
                model5.removeRow(model5.getRowOf(c.getCode()));
            } else if (model6.getRowOf(c.getCode()) >= 0) {
                model6.removeRow(model6.getRowOf(c.getCode()));
            } else if (model7.getRowOf(c.getCode()) >= 0) {
                model7.removeRow(model7.getRowOf(c.getCode()));
            } else if (model8.getRowOf(c.getCode()) >= 0) {
                model8.removeRow(model8.getRowOf(c.getCode()));
            }

            Memory.mayForget(c);
            return super.remove(c);
        }
    };

    public static void uploadModules(){
        for (Course c : STARTUP_COURSES) {
            modulesMonitor.add(c);
        }
    }

    /**
     * Note: the edition dialogs should not use this, since they present an exception
     * by skipping the focused row.
     */
    public static boolean existsInList(String code){
        for (Course course : modulesMonitor) {
            if (course.getCode().equalsIgnoreCase(code)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Useful for the editing.
     * The model is given so that it excludes the selected row of the particular model.
     */
    public static boolean existsInListExcept(KDefaultTableModel targetModel, String code){
        for (KDefaultTableModel model : models) {
            if (model != targetModel && model.getRowOf(code) >= 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * This call is complete.
     * It makes sure the list replaces the old with recent, and inflict
     * the changes on the appropriate table, thereafter.
     * This also cater for the case where the old might not exist for whatever reason,
     * and halt, int that case, the subsequent attempt for visual changes.
     *
     * Do not call set(Course, Course) on the monitor! Call this.
     */
    public static void substitute(Course old, Course recent){
        if (existsInList(old.getCode())) {//Typically for editing. Or if it's from a sync / verification, the details should be merged prior to this call
            modulesMonitor.set(old.getListIndex(), recent);
        } else {//May be an issue, after verification - as the user might have removed it during the process
            modulesMonitor.add(recent);
            return;
        }

        for (KDefaultTableModel defaultTableModel : models) {
            final int targetRow = defaultTableModel.getRowOf(old.getCode());
            if (targetRow >= 0) {
                defaultTableModel.setValueAt(recent.getCode(), targetRow, 0);
                defaultTableModel.setValueAt(recent.getName(), targetRow, 1);
                defaultTableModel.setValueAt(recent.getLecturer(), targetRow, 2);
                defaultTableModel.setValueAt(recent.getGrade(), targetRow, 3);
                if (defaultTableModel == models[8] || defaultTableModel == models[9]) {
                    defaultTableModel.setValueAt(recent.getYear(), targetRow, 4);
                }
                break;
            }
        }
        //Finally
        Memory.mayReplace(old, recent);
    }

    public static void reportCodeDuplication(String dCode){
        App.signalError(dCode.toUpperCase()+" - Repeated",
                "Sorry, there's already a course with the code '"+dCode.toUpperCase()+"' in the list.");
    }

    /**
     * This method is useful. Especially, since indexing of the single static list and the
     * many models cannot coincide, call this - never want to retrieve a course from the list
     * by using get(int#) as the int passed by the model might be matching a different course
     * according to the list's index.
     * This function compares only the code.
     * Null value shall implies no such course in the entire list.
     */
    public static Course getModuleByCode(String code){
        for (Course course : modulesMonitor) {
            if (course.getCode().equalsIgnoreCase(code)) {
                return course;
            }
        }
        return null;
    }

    public static synchronized void fixModulesDriver(){
        if (allDriver != null) {
            return;
        }
        allDriver = DriversPack.forgeNew(true);
    }

    /**
     * Attempts to verify this course using its code only.
     */
    public static void launchVerification(Course target){
        if (!App.showOkCancelDialog("Confirm",
                "Dashboard will now launch Verification sequences for "+target.getName()+" - "+target.getYear()+" "+target.getSemester()+".\n" +
                "It might be taken to another table if this is not its year & semester.\n \n" +
                        "For more info about this action, read "+HelpGenerator.reference("My Courses | Course Verification."))) {
            return;
        }
        if (allDriver == null) {
            fixModulesDriver();
        }
        if (allDriver == null) {
            App.reportMissingDriver();
            return;
        }
        if (!InternetAvailabilityChecker.isInternetAvailable()) {
            App.reportNoInternet();
            return;
        }

        synchronized (allDriver){
            final WebDriverWait loadWaiter = new WebDriverWait(allDriver, 30);
            Course foundOne = null;
            if (DriversPack.isIn(allDriver)) {
                try {
                    allDriver.navigate().refresh();
                    if (Portal.isPortalBusy(allDriver)) {
                        App.reportBusyPortal();
                    }
                } catch (Exception e){
                    App.reportRefreshFailure();
                }
            } else {
                final int loginAttempt = DriversPack.attemptLogin(allDriver);
                if (loginAttempt == DriversPack.ATTEMPT_SUCCEEDED) {
                    try {
                        allDriver.navigate().to(Portal.CONTENTS_PAGE);
                        Portal.nowOnPortal(allDriver);
                    } catch (Exception e) {
                        App.reportConnectionLost();
                        return;
                    }
                } else if (loginAttempt == DriversPack.ATTEMPT_FAILED) {
                    App.reportLoginAttemptFailed();
                    return;
                } else if (loginAttempt == DriversPack.CONNECTION_LOST) {
                    App.reportConnectionLost();
                    return;
                }
            }

            final List<WebElement> tabs = loadWaiter.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".nav-tabs > li")));
            //Firstly, the code, name, and score at grades tab
            tabs.get(6).click();
            final WebElement gradesTable = allDriver.findElementsByCssSelector(".table-warning").get(1);
            final WebElement tBody = gradesTable.findElement(By.tagName("tbody"));
            final List<WebElement> rows = tBody.findElements(By.tagName("tr"));
            for(WebElement t : rows){
                final List<WebElement> instantRow = t.findElements(By.tagName("td"));
                if (instantRow.get(0).getText().equalsIgnoreCase(target.getCode())) {
                    foundOne = new Course("","",instantRow.get(0).getText(),instantRow.get(1).getText(),"","","","",
                            Double.parseDouble(instantRow.get(6).getText()),0,"",true);
                    break;
                }
            }
            //Checking... if the course was found and assigned, then we shall continue to set its succeeding details
            if (foundOne == null) {
                App.promptWarning("Verification Unsuccessful","Your search for "+target.getName()+" was unsuccessful.\n" +
                        "Dashboard could not locate any trace of it on your portal.\nIf you've done this course, then contact the lecturer.\n" +
                        "You may also want to refer to "+HelpGenerator.reference("My Courses | Course Verification."));
                return;
            }
            //Secondly, the code, year and semester at transcript tab
            tabs.get(7).click();
            final WebElement transcriptTable = allDriver.findElementByCssSelector(".table-bordered");
            final WebElement transBody = transcriptTable.findElement(By.tagName("tbody"));
            final List<WebElement> transRows = transBody.findElements(By.tagName("tr"));
            String vYear = null;
            String vSemester = null;
            for (WebElement transRow : transRows) {
                if (transRow.getText().contains("Semester")) {
                    vYear = transRow.getText().split(" ")[0];
                    vSemester = transRow.getText().split(" ")[1] + " Semester";
                } else {
                    if (transRow.getText().contains(foundOne.getCode())) {
                        foundOne.setYear(vYear);
                        foundOne.setSemester(vSemester);
                        break;
                    }
                }
            }
            //Finally, the lecturer name at registration tab - if there
            tabs.get(4).click();
            final WebElement allCourseTable = allDriver.findElementByCssSelector(".table-warning");
            final WebElement tableBody = allCourseTable.findElement(By.tagName("tbody"));
            final List<WebElement> allRows = tableBody.findElements(By.tagName("tr"));

            int l = 0;
            while (l < allRows.size()) {
                final List<WebElement> instantRow = allRows.get(l).findElements(By.tagName("td"));
                if (foundOne.getCode().equals(instantRow.get(0).getText())) {
                    foundOne.setLecturer(instantRow.get(2).getText(), false);
                    break;
                }
                l++;
            }

            final Course existed = getModuleByCode(target.getCode());
            if (existed == null) {//Was removed?
                modulesMonitor.add(foundOne);
            } else {//Merge and replace
                Course.merge(foundOne, existed);
                substitute(existed, foundOne);
            }
            App.promptPlain("Verification Successful","Your search for "+target.getName()+" is completed successfully.\n" +
                    "It will now be included in your Analysis and Transcript.");
        }
    }

    /**
     * Called to perform a thorough sync. This action has a lot of consequences!
     * Never call this directly! Use ModulesGenerator.triggerRefresh() instead, it forges
     * a thread and checks internet-availability prior to this call which does none!
     */
    public static void startThoroughSync(KButton syncButton, boolean userRequested){
        syncButton.setEnabled(false);
        if (allDriver == null) {
            fixModulesDriver();
        }
        if (allDriver == null) {
            if (userRequested) {
                App.reportMissingDriver();
            }
            syncButton.setEnabled(true);
            return;
        }
        if (!InternetAvailabilityChecker.isInternetAvailable()) {
            if (userRequested) {
                App.reportNoInternet();
            }
            syncButton.setEnabled(true);
            return;
        }

        final ArrayList<Course> foundCourses = new ArrayList<>();
        synchronized (allDriver){
            final WebDriverWait loadWaiter = new WebDriverWait(allDriver, 30);
            if (DriversPack.isIn(allDriver)) {
                try {
                    allDriver.navigate().refresh();
                    if (Portal.isPortalBusy(allDriver)) {
                        if (userRequested) {
                            App.reportConnectionLost();
                        }
                        syncButton.setEnabled(true);
                        return;
                    }
                } catch (Exception e){
                    if (userRequested) {
                        App.reportRefreshFailure();
                    }
                }
            } else {
                final int loginAttempt = DriversPack.attemptLogin(allDriver);
                if (loginAttempt == DriversPack.ATTEMPT_SUCCEEDED) {
                    try {
                        allDriver.navigate().to(Portal.CONTENTS_PAGE);
                        Portal.nowOnPortal(allDriver);
                    } catch (Exception e) {
                        App.reportConnectionLost();
                        syncButton.setEnabled(true);
                        return;
                    }
                } else if (loginAttempt == DriversPack.ATTEMPT_FAILED) {
                    if (userRequested) {
                        App.reportLoginAttemptFailed();
                    }
                    syncButton.setEnabled(true);
                    return;
                } else if (loginAttempt == DriversPack.CONNECTION_LOST) {
                    if (userRequested) {
                        App.reportConnectionLost();
                    }
                    syncButton.setEnabled(true);
                    return;
                }
            }

            //Here we go...
            final List<WebElement> tabs;
            try {
                tabs = loadWaiter.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".nav-tabs > li")));
            } catch (Exception e){
                if (userRequested) {
                    App.reportConnectionLost();
                }
                syncButton.setEnabled(true);
                return;
            }
            //Firstly, code, name, year, semester, and credit hour at transcript tab
            //Addition to startupCourses is only here; all the following loops only updates the details. this eradicates the possibility of adding running courses at tab-4
            tabs.get(7).click();
            final WebElement transcriptTable = allDriver.findElementByCssSelector(".table-bordered");
            final WebElement transBody = transcriptTable.findElement(By.tagName("tbody"));
            final List<WebElement> transRows = transBody.findElements(By.tagName("tr"));
            final List<WebElement> semCaptions = transBody.findElements(By.className("warning"));
            String vYear = null;
            String vSemester = null;
            for (WebElement transRow : transRows) {
                if (transRow.getText().contains("Semester")) {
                    vYear = transRow.getText().split(" ")[0];
                    vSemester = transRow.getText().split(" ")[1]+" Semester";
                } else {
                    final List<WebElement> data = transRow.findElements(By.tagName("td"));
                    foundCourses.add(new Course(vYear, vSemester, data.get(1).getText(), data.get(2).getText(), "", "", "", "", 0.0,
                            Integer.parseInt(data.get(3).getText()), "", true));
                }
            }
            final WebElement surrounds = allDriver.findElementsByCssSelector(".pull-right").get(3);
            final String CGPA = surrounds.findElements(By.tagName("th")).get(1).getText();
            Student.setCGPA(Double.parseDouble(CGPA));

            //Secondly, add scores at grades tab
            tabs.get(6).click();
            final WebElement gradesTable = allDriver.findElementsByCssSelector(".table-warning").get(1);
            final WebElement tBody = gradesTable.findElement(By.tagName("tbody"));
            final List<WebElement> rows = tBody.findElements(By.tagName("tr"));
            for(WebElement t : rows){
                final List<WebElement> data = t.findElements(By.tagName("td"));
                for (Course c : foundCourses) {
                    if (c.getCode().equals(data.get(0).getText())) {
                        c.setScore(Double.parseDouble(data.get(6).getText()));
                    }
                }
            }

            //Finally, available lecturer names at all-registered tab
            tabs.get(4).click();
            final WebElement allRegisteredTable = allDriver.findElementByCssSelector(".table-warning");
            final WebElement tableBody = allRegisteredTable.findElement(By.tagName("tbody"));
            final List<WebElement> allRows = tableBody.findElements(By.tagName("tr"));
            int l = 0;
            while (l < allRows.size()) {
                final List<WebElement> instantRow = allRows.get(l).findElements(By.tagName("td"));
                for (Course c : foundCourses) {
                    if (c.getCode().equals(instantRow.get(0).getText())) {
                        c.setLecturer(instantRow.get(2).getText(), false);
                    }
                }
                l++;
            }
            for (Course found : foundCourses) {
                final Course existed = getModuleByCode(found.getCode());
                if (existed == null) {//Does not exist?
                    modulesMonitor.add(found);
                } else {//Merge and replace
                    Course.merge(found, existed);
                    substitute(existed, found);
                }
            }
            final int foundCount = rows.size();
            final int semesterCount = semCaptions.size();
            App.promptPlain("Sync Successful", "Synchronization of the modules completed successfully:\n" +
                    Globals.checkPlurality(foundCount, "courses")+" were found in "+ Globals.checkPlurality(semesterCount, "semesters"));

            syncButton.setEnabled(true);
        }
    }

    /**
     * This method and its co should:
     * - Affect only the obligatory program courses
     * - Spare courses which were requirement set by the user save majorObligatory
     */
    public static void effectMajorCodeChanges(String from, String to) {
        new Thread(()->{
            for (Course t : modulesMonitor) {
                try {
                    if (t.getCode().substring(0, 3).equalsIgnoreCase(from) && t.isMajorObligatory()) {
                        t.setRequirement(Course.NONE);
                    }
                    //Yes, they are independent
                    if (t.getCode().substring(0, 3).equalsIgnoreCase(to) && t.isUnclassified()) {
                        t.setRequirement(Course.MAJOR_OBLIGATORY);
                    }
                } catch (StringIndexOutOfBoundsException i) {
                    App.silenceException(i);
                }
            }
        }).start();
    }

    public static void effectMinorCodeChanges(String from, String to) {
        new Thread(() -> {
            for (Course t : modulesMonitor) {
                try {
                    if (t.getCode().substring(0, 3).equalsIgnoreCase(from) && t.isMinorObligatory()) {
                        t.setRequirement(Course.NONE);
                    }
                    if (t.getCode().substring(0, 3).equalsIgnoreCase(to) && t.isUnclassified()) {
                        t.setRequirement(Course.MINOR_OBLIGATORY);
                    }
                } catch (StringIndexOutOfBoundsException i) {
                    App.silenceException(i);
                }
            }
        }).start();
    }

    public static void reportScoreInvalid(String invalidScore, Container root){
        App.signalError(root,"Invalid score", invalidScore+" is not a valid score. Please try again.");
    }

    public static void reportScoreOutOfRange(Container root){
        App.signalError(root,"Error", "Score cannot be less than 0 or more than 100.");
    }

    public static ArrayList<Course> getModulesMonitor(){
        return modulesMonitor;
    }

    private static void setTablePreferences(KTable t){
        t.setRowHeight(30);
        t.setFont(KFontFactory.createPlainFont(15));
        t.getTableHeader().setFont(KFontFactory.createBoldFont(16));
        t.centerAlignAllColumns();
        t.getColumnModel().getColumn(0).setPreferredWidth(70);
        t.getColumnModel().getColumn(1).setPreferredWidth(280);
        t.getColumnModel().getColumn(2).setPreferredWidth(250);
        t.getColumnModel().getColumn(3).setPreferredWidth(50);
    }

    public static void serializeData(){
        System.out.print("Serializing all modules... ");
        final String[] allCourses = new String[modulesMonitor.size()];
        for (int i = 0; i < allCourses.length; i++) {
            allCourses[i] = modulesMonitor.get(i).exportContent();
        }
        MyClass.serialize(allCourses, "allCourses.ser");
        System.out.println("Completed.");
    }

    public static void deserializeData(){
        System.out.print("Deserializing all modules... ");
        final String[] allCourses = (String[]) MyClass.deserialize("allCourses.ser");
        for (String line : allCourses) {
            modulesMonitor.add(Course.importFromSerial(line));
        }
        System.out.println("Completed.");
    }

    public JComponent presentForYearOne(){
        return new YearOneHandler().getPresent();
    }

    public JComponent presentForYearTwo(){
        return new YearTwoHandler().getPresent();
    }

    public JComponent presentForYearThree(){
        return new YearThreeHandler().getPresent();
    }

    public JComponent presentForYearFour(){
        return new YearFourHandler().getPresent();
    }

    /**
     * Deals with pretty much, everything of the first year, and present the entire frame work
     * on a panel. All the subsequent colleagues do the same.
     */
    private static class YearOneHandler {
        private String y1Name;
        private KTable table1, table2;
        private KTable focusTable;
        private KDefaultTableModel focusModel;
        private JPopupMenu jPopupMenu;
        private JMenuItem detailsItem, editItem, removeItem, confirmItem, newItem;

        private YearOneHandler(){
            y1Name = Student.firstAcademicYear();

            setTableOne();
            setTableTwo();

            detailsItem = new JMenuItem(DETAILS_STRING);
            detailsItem.setFont(KFontFactory.createPlainFont(15));
            detailsItem.addActionListener(e -> {
                final Course c = getModuleByCode(String.valueOf(getFocusModel().getValueAt(getFocusTable().getSelectedRow(), 0)));
                try {
                    Course.exhibit(Board.getRoot(), Objects.requireNonNull(c));
                } catch (NullPointerException npe){
                    App.silenceException("No such course in list");
                }
            });

            editItem = new JMenuItem(EDIT_STRING);
            editItem.setFont(KFontFactory.createPlainFont(15));
            editItem.addActionListener(e -> {
                final Course c = getModuleByCode(getFocusModel().getValueAt(getFocusTable().getSelectedRow(), 0).toString());
                try {
                    SwingUtilities.invokeLater(() -> new ModuleEditor(Objects.requireNonNull(c), getFocusModel()).setVisible(true));
                } catch (NullPointerException npe) {
                    App.silenceException("No such course in list");
                }
            });

            removeItem = new JMenuItem(DELETE_STRING);
            removeItem.setFont(KFontFactory.createPlainFont(15));
            removeItem.addActionListener(e -> {
                final Course c = getModuleByCode(String.valueOf(getFocusModel().getValueAt(getFocusTable().getSelectedRow(), 0)));
                try {
                    Objects.requireNonNull(c);
                    if (App.showYesNoCancelDialog("Confirm Removal","Are you sure you did not do "+c.getName()+",\n" +
                            "and that you wish to remove it from your collection?")) {
                        modulesMonitor.remove(c);
                    }
                } catch (NullPointerException npe) {
                    App.silenceException("No such course in list");
                }
            });

            confirmItem = new JMenuItem(CONFIRM_STRING);
            confirmItem.setFont(KFontFactory.createPlainFont(15));
            confirmItem.addActionListener(e -> {
                final Course c = getModuleByCode(String.valueOf(getFocusModel().getValueAt(getFocusTable().getSelectedRow(),0)));
                new Thread(()->{
                    try {
                        Objects.requireNonNull(c);
                        launchVerification(c);
                    } catch (NullPointerException npe) {
                        App.silenceException("No such course in list");
                    }
                }).start();
            });

            newItem = new JMenuItem(ADD_STRING);
            newItem.setFont(KFontFactory.createPlainFont(15));
            newItem.addActionListener(e -> SwingUtilities.invokeLater(()-> new ModuleAdder(y1Name, semesterName).setVisible(true)));

            jPopupMenu = new JPopupMenu();
            jPopupMenu.add(detailsItem);
            jPopupMenu.add(editItem);
            jPopupMenu.add(removeItem);
            jPopupMenu.add(confirmItem);
            jPopupMenu.add(newItem);
        }

        private void setTableOne(){
            model1 = new KDefaultTableModel();
            model1.setColumnIdentifiers(COLUMN_IDENTIFIERS);
            models[0] = model1;

            table1 = new KTable(model1);
            setTablePreferences(table1);
            table1.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        setFocusTable(table1);
                        setFocusModel(model1);
                        semesterName = Student.FIRST_SEMESTER;

                        table1.getSelectionModel().setSelectionInterval(0, table1.rowAtPoint(e.getPoint()));
                        detailsItem.setEnabled(true);
                        editItem.setEnabled(true);
                        removeItem.setEnabled(true);
                        confirmItem.setEnabled(true);
                        newItem.setEnabled(table1.getRowCount() <= 5);

                        SwingUtilities.invokeLater(()-> jPopupMenu.show(table1, e.getX(), e.getY()));
                    }
                }
                @Override
                public void mouseReleased(MouseEvent e) {
                    mousePressed(e);
                }
            });
        }

        private void setTableTwo(){
            model2 = new KDefaultTableModel();
            model2.setColumnIdentifiers(COLUMN_IDENTIFIERS);
            models[1] = model2;

            table2 = new KTable(model2);
            setTablePreferences(table2);
            table2.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        setFocusTable(table2);
                        setFocusModel(model2);
                        semesterName = Student.SECOND_SEMESTER;

                        table2.getSelectionModel().setSelectionInterval(0, table2.rowAtPoint(e.getPoint()));
                        detailsItem.setEnabled(true);
                        editItem.setEnabled(true);
                        removeItem.setEnabled(true);
                        confirmItem.setEnabled(true);
                        newItem.setEnabled(table2.getRowCount() <= 5);

                        SwingUtilities.invokeLater(()-> jPopupMenu.show(table2,e.getX(),e.getY()));
                    }
                }
                @Override
                public void mouseReleased(MouseEvent e) {
                    mousePressed(e);
                }
            });
        }

        private KPanel getPresent(){
            final KScrollPane scrollPane1 = new KScrollPane(table1,false);
            scrollPane1.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        setFocusTable(table1);
                        setFocusModel(model1);
                        semesterName = Student.FIRST_SEMESTER;

                        table1.getSelectionModel().clearSelection();
                        detailsItem.setEnabled(false);
                        editItem.setEnabled(false);
                        removeItem.setEnabled(false);
                        confirmItem.setEnabled(false);
                        newItem.setEnabled(table1.getRowCount() <= 5);

                        SwingUtilities.invokeLater(()-> jPopupMenu.show(scrollPane1,e.getX(),e.getY()));
                    }
                }
                @Override
                public void mouseReleased(MouseEvent e) {
                    mousePressed(e);
                }
            });
            final KScrollPane scrollPane2 = new KScrollPane(table2,false);
            scrollPane2.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        setFocusTable(table2);
                        setFocusModel(model2);
                        semesterName = Student.SECOND_SEMESTER;

                        table2.getSelectionModel().clearSelection();
                        detailsItem.setEnabled(false);
                        editItem.setEnabled(false);
                        removeItem.setEnabled(false);
                        confirmItem.setEnabled(false);
                        newItem.setEnabled(table2.getRowCount() <= 5);

                        SwingUtilities.invokeLater(()-> jPopupMenu.show(scrollPane2,e.getX(),e.getY()));
                    }
                }
                @Override
                public void mouseReleased(MouseEvent e) {
                    mousePressed(e);
                }
            });

            final KPanel present = new KPanel(new Dimension(825,540));
            present.setLayout(new BoxLayout(present, BoxLayout.Y_AXIS));
            present.add(KPanel.wantDirectAddition(new KLabel(Student.FIRST_SEMESTER, KFontFactory.createPlainFont(18), Color.BLUE)));
            present.add(scrollPane1);
            present.add(Box.createVerticalStrut(30));
            present.add(KPanel.wantDirectAddition(new KSeparator(Color.BLACK,new Dimension(800,1))));
            present.add(KPanel.wantDirectAddition(new KLabel(Student.SECOND_SEMESTER, KFontFactory.createPlainFont(18), Color.BLUE)));
            present.add(scrollPane2);
            present.add(Box.createVerticalStrut(15));
            return present;
        }

        private KTable getFocusTable(){
            return focusTable;
        }

        private void setFocusTable(KTable table){
            focusTable = table;
        }

        private KDefaultTableModel getFocusModel(){
            return focusModel;
        }

        private void setFocusModel(KDefaultTableModel model){
            focusModel = model;
        }
    }

    private static class YearTwoHandler {
        private String y2Name;
        private KTable table3, table4;
        private KTable focusTable;
        private KDefaultTableModel focusModel;
        private JPopupMenu jPopupMenu;
        private JMenuItem detailsItem, editItem, removeItem, confirmItem, newItem;

        private YearTwoHandler(){
            y2Name = Student.secondAcademicYear();

            setTableThree();
            setTableFour();

            detailsItem = new JMenuItem(DETAILS_STRING);
            detailsItem.setFont(KFontFactory.createPlainFont(15));
            detailsItem.addActionListener(e -> {
                final Course c = getModuleByCode(String.valueOf(getFocusModel().getValueAt(getFocusTable().getSelectedRow(), 0)));
                try {
                    SwingUtilities.invokeLater(()-> Course.exhibit(Board.getRoot(), Objects.requireNonNull(c)));
                } catch (NullPointerException npe) {
                    App.silenceException("No such course in list");
                }
            });

            editItem = new JMenuItem(EDIT_STRING);
            editItem.setFont(KFontFactory.createPlainFont(15));
            editItem.addActionListener(e -> {
                final Course c = getModuleByCode(getFocusModel().getValueAt(getFocusTable().getSelectedRow(), 0).toString());
                try {
                    SwingUtilities.invokeLater(()-> new ModuleEditor(Objects.requireNonNull(c), getFocusModel()).setVisible(true));
                } catch (NullPointerException npe) {
                    App.silenceException("No such course in list");
                }
            });

            removeItem = new JMenuItem(DELETE_STRING);
            removeItem.setFont(KFontFactory.createPlainFont(15));
            removeItem.addActionListener(e -> {
                final Course c = getModuleByCode(String.valueOf(getFocusModel().getValueAt(getFocusTable().getSelectedRow(), 0)));
                try {
                    Objects.requireNonNull(c);
                    if (App.showYesNoCancelDialog("Confirm Removal","Are you sure you did not do "+c.getName()+",\n" +
                            "and that you wish to remove it from your collection?")) {
                        modulesMonitor.remove(c);
                    }
                } catch (NullPointerException npe) {
                    App.silenceException("No such course in list");
                }
            });

            confirmItem = new JMenuItem(CONFIRM_STRING);
            confirmItem.setFont(KFontFactory.createPlainFont(15));
            confirmItem.addActionListener(e -> {
                final Course c = getModuleByCode(String.valueOf(getFocusModel().getValueAt(getFocusTable().getSelectedRow(),0)));
                try {
                    new Thread(()-> launchVerification(Objects.requireNonNull(c))).start();
                } catch (NullPointerException npe) {
                    App.silenceException("No such course in list");
                }
            });

            newItem = new JMenuItem(ADD_STRING);
            newItem.setFont(KFontFactory.createPlainFont(15));
            newItem.addActionListener(e -> SwingUtilities.invokeLater(()-> new ModuleAdder(y2Name, semesterName).setVisible(true)));

            jPopupMenu = new JPopupMenu();
            jPopupMenu.add(detailsItem);
            jPopupMenu.add(editItem);
            jPopupMenu.add(removeItem);
            jPopupMenu.add(confirmItem);
            jPopupMenu.add(newItem);
        }

        private void setTableThree(){
            model3 = new KDefaultTableModel();
            model3.setColumnIdentifiers(COLUMN_IDENTIFIERS);
            models[2] = model3;

            table3 = new KTable(model3);
            setTablePreferences(table3);
            table3.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        setFocusTable(table3);
                        setFocusModel(model3);
                        semesterName = Student.FIRST_SEMESTER;

                        table3.getSelectionModel().setSelectionInterval(0, table3.rowAtPoint(e.getPoint()));
                        detailsItem.setEnabled(true);
                        editItem.setEnabled(true);
                        removeItem.setEnabled(true);
                        confirmItem.setEnabled(true);
                        newItem.setEnabled(table3.getRowCount() <= 5);

                        SwingUtilities.invokeLater(()-> jPopupMenu.show(table3,e.getX(),e.getY()));
                    }
                }
                @Override
                public void mouseReleased(MouseEvent e) {
                    mousePressed(e);
                }
            });
        }

        private void setTableFour(){
            model4 = new KDefaultTableModel();
            model4.setColumnIdentifiers(COLUMN_IDENTIFIERS);
            models[3] = model4;

            table4 = new KTable(model4);
            setTablePreferences(table4);
            table4.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        setFocusTable(table4);
                        setFocusModel(model4);
                        semesterName = Student.SECOND_SEMESTER;

                        table4.getSelectionModel().setSelectionInterval(0, table4.rowAtPoint(e.getPoint()));
                        detailsItem.setEnabled(true);
                        editItem.setEnabled(true);
                        removeItem.setEnabled(true);
                        confirmItem.setEnabled(true);
                        newItem.setEnabled(table4.getRowCount() <= 5);

                        SwingUtilities.invokeLater(()-> jPopupMenu.show(table4,e.getX(),e.getY()));
                    }
                }
                @Override
                public void mouseReleased(MouseEvent e) {
                    mousePressed(e);
                }
            });
        }

        private KPanel getPresent(){
            final KScrollPane scrollPane3 = new KScrollPane(table3,false);
            scrollPane3.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        setFocusTable(table3);
                        setFocusModel(model3);
                        semesterName = Student.FIRST_SEMESTER;

                        table3.getSelectionModel().clearSelection();
                        detailsItem.setEnabled(false);
                        editItem.setEnabled(false);
                        removeItem.setEnabled(false);
                        confirmItem.setEnabled(false);
                        newItem.setEnabled(table3.getRowCount() <= 5);

                        SwingUtilities.invokeLater(()-> jPopupMenu.show(scrollPane3,e.getX(),e.getY()));
                    }
                }
                @Override
                public void mouseReleased(MouseEvent e) {
                    mousePressed(e);
                }
            });
            final KScrollPane scrollPane4 = new KScrollPane(table4,false);
            scrollPane4.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        setFocusTable(table4);
                        setFocusModel(model4);
                        semesterName = Student.SECOND_SEMESTER;

                        table4.getSelectionModel().clearSelection();
                        detailsItem.setEnabled(false);
                        editItem.setEnabled(false);
                        removeItem.setEnabled(false);
                        confirmItem.setEnabled(false);
                        newItem.setEnabled(table4.getRowCount() <= 5);

                        SwingUtilities.invokeLater(()-> jPopupMenu.show(scrollPane4,e.getX(),e.getY()));
                    }
                }
                @Override
                public void mouseReleased(MouseEvent e) {
                    mousePressed(e);
                }
            });

            final KPanel present = new KPanel(new Dimension(825,540));
            present.setLayout(new BoxLayout(present, BoxLayout.Y_AXIS));
            present.add(KPanel.wantDirectAddition(new KLabel(Student.FIRST_SEMESTER, KFontFactory.createPlainFont(18), Color.BLUE)));
            present.add(scrollPane3);
            present.add(Box.createVerticalStrut(30));
            present.add(KPanel.wantDirectAddition(new KSeparator(Color.BLACK,new Dimension(800,1))));
            present.add(KPanel.wantDirectAddition(new KLabel(Student.SECOND_SEMESTER, KFontFactory.createPlainFont(18), Color.BLUE)));
            present.add(scrollPane4);
            present.add(Box.createVerticalStrut(15));
            return present;
        }

        private KTable getFocusTable(){
            return focusTable;
        }

        private void setFocusTable(KTable table){
            focusTable = table;
        }

        private KDefaultTableModel getFocusModel(){
            return focusModel;
        }

        private void setFocusModel(KDefaultTableModel model){
            focusModel = model;
        }
    }

    private static class YearThreeHandler {
        private String y3Name;
        private KTable table5, table6;
        private KTable focusTable;
        private KDefaultTableModel focusModel;
        private JPopupMenu jPopupMenu;
        private JMenuItem detailsItem, editItem, removeItem, confirmItem, newItem;

        private YearThreeHandler(){
            y3Name = Student.thirdAcademicYear();

            setTableFive();
            setTableSix();

            detailsItem = new JMenuItem(DETAILS_STRING);
            detailsItem.setFont(KFontFactory.createPlainFont(15));
            detailsItem.addActionListener(e -> {
                final Course c = getModuleByCode(String.valueOf(getFocusModel().getValueAt(getFocusTable().getSelectedRow(), 0)));
                try {
                    SwingUtilities.invokeLater(() -> Course.exhibit(Board.getRoot(), Objects.requireNonNull(c)));
                } catch (NullPointerException npe) {
                    App.silenceException("No such course in list");
                }
            });

            editItem = new JMenuItem(EDIT_STRING);
            editItem.setFont(KFontFactory.createPlainFont(15));
            editItem.addActionListener(e -> {
                final Course c = getModuleByCode(getFocusModel().getValueAt(getFocusTable().getSelectedRow(), 0).toString());
                try {
                    Objects.requireNonNull(c);
                    SwingUtilities.invokeLater(()-> new ModuleEditor(c, getFocusModel()).setVisible(true));
                } catch (NullPointerException npe) {
                    App.silenceException("No such course in list");
                }
            });

            removeItem = new JMenuItem(DELETE_STRING);
            removeItem.setFont(KFontFactory.createPlainFont(15));
            removeItem.addActionListener(e -> {
                final Course c = getModuleByCode(String.valueOf(getFocusModel().getValueAt(getFocusTable().getSelectedRow(), 0)));
                try {
                    Objects.requireNonNull(c);
                    if (App.showYesNoCancelDialog("Confirm Removal","Are you sure you did not do "+c.getName()+",\nand that you wish to remove it from your collection?")) {
                        modulesMonitor.remove(c);
                    }
                } catch (NullPointerException npe) {
                    App.silenceException("No such course in list");
                }
            });

            confirmItem = new JMenuItem(CONFIRM_STRING);
            confirmItem.setFont(KFontFactory.createPlainFont(15));
            confirmItem.addActionListener(e -> {
                final Course c = getModuleByCode(String.valueOf(getFocusModel().getValueAt(getFocusTable().getSelectedRow(),0)));
                try {
                    new Thread(()-> launchVerification(Objects.requireNonNull(c))).start();
                } catch (NullPointerException npe) {
                    App.silenceException("No such course in list");
                }
            });

            newItem = new JMenuItem(ADD_STRING);
            newItem.setFont(KFontFactory.createPlainFont(15));
            newItem.addActionListener(e -> SwingUtilities.invokeLater(()-> new ModuleAdder(y3Name, semesterName).setVisible(true)));

            jPopupMenu = new JPopupMenu();
            jPopupMenu.add(detailsItem);
            jPopupMenu.add(editItem);
            jPopupMenu.add(removeItem);
            jPopupMenu.add(confirmItem);
            jPopupMenu.add(newItem);
        }

        private void setTableFive(){
            model5 = new KDefaultTableModel();
            model5.setColumnIdentifiers(COLUMN_IDENTIFIERS);
            models[4] = model5;

            table5 = new KTable(model5);
            setTablePreferences(table5);
            table5.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        setFocusTable(table5);
                        setFocusModel(model5);
                        semesterName = Student.FIRST_SEMESTER;

                        table5.getSelectionModel().setSelectionInterval(0, table5.rowAtPoint(e.getPoint()));
                        detailsItem.setEnabled(true);
                        editItem.setEnabled(true);
                        removeItem.setEnabled(true);
                        confirmItem.setEnabled(true);
                        newItem.setEnabled(table5.getRowCount() <= 5);

                        SwingUtilities.invokeLater(()-> jPopupMenu.show(table5,e.getX(),e.getY()));
                    }
                }
                @Override
                public void mouseReleased(MouseEvent e) {
                    mousePressed(e);
                }
            });
        }

        private void setTableSix(){
            model6 = new KDefaultTableModel();
            model6.setColumnIdentifiers(COLUMN_IDENTIFIERS);
            models[5] = model6;

            table6 = new KTable(model6);
            setTablePreferences(table6);
            table6.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        setFocusTable(table6);
                        setFocusModel(model6);
                        semesterName = Student.SECOND_SEMESTER;

                        table6.getSelectionModel().setSelectionInterval(0, table6.rowAtPoint(e.getPoint()));
                        detailsItem.setEnabled(true);
                        editItem.setEnabled(true);
                        removeItem.setEnabled(true);
                        confirmItem.setEnabled(true);
                        newItem.setEnabled(table6.getRowCount() <= 5);

                        SwingUtilities.invokeLater(()-> jPopupMenu.show(table6,e.getX(),e.getY()));
                    }
                }
                @Override
                public void mouseReleased(MouseEvent e) {
                    mousePressed(e);
                }
            });
        }

        private KPanel getPresent(){
            final KScrollPane scrollPane5 = new KScrollPane(table5,false);
            scrollPane5.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        setFocusTable(table5);
                        setFocusModel(model5);
                        semesterName = Student.FIRST_SEMESTER;

                        table5.getSelectionModel().clearSelection();
                        detailsItem.setEnabled(false);
                        editItem.setEnabled(false);
                        removeItem.setEnabled(false);
                        confirmItem.setEnabled(false);
                        newItem.setEnabled(table5.getRowCount() <= 5);

                        SwingUtilities.invokeLater(()-> jPopupMenu.show(scrollPane5,e.getX(),e.getY()));
                    }
                }
                @Override
                public void mouseReleased(MouseEvent e) {
                    mousePressed(e);
                }
            });
            final KScrollPane scrollPane6 = new KScrollPane(table6,false);
            scrollPane6.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        setFocusTable(table6);
                        setFocusModel(model6);
                        semesterName = Student.SECOND_SEMESTER;

                        table6.getSelectionModel().clearSelection();
                        detailsItem.setEnabled(false);
                        editItem.setEnabled(false);
                        removeItem.setEnabled(false);
                        confirmItem.setEnabled(false);
                        newItem.setEnabled(table6.getRowCount() <= 5);

                        SwingUtilities.invokeLater(()-> jPopupMenu.show(scrollPane6,e.getX(),e.getY()));
                    }
                }
                @Override
                public void mouseReleased(MouseEvent e) {
                    mousePressed(e);
                }
            });

            final KPanel present = new KPanel(new Dimension(825,540));
            present.setLayout(new BoxLayout(present, BoxLayout.Y_AXIS));
            present.add(KPanel.wantDirectAddition(new KLabel(Student.FIRST_SEMESTER, KFontFactory.createPlainFont(18), Color.BLUE)));
            present.add(scrollPane5);
            present.add(Box.createVerticalStrut(30));
            present.add(KPanel.wantDirectAddition(new KSeparator(Color.BLACK,new Dimension(800,1))));
            present.add(KPanel.wantDirectAddition(new KLabel(Student.SECOND_SEMESTER, KFontFactory.createPlainFont(18), Color.BLUE)));
            present.add(scrollPane6);
            present.add(Box.createVerticalStrut(15));
            return present;
        }

        private KTable getFocusTable(){
            return focusTable;
        }

        private void setFocusTable(KTable table){
            focusTable = table;
        }

        private KDefaultTableModel getFocusModel(){
            return focusModel;
        }

        private void setFocusModel(KDefaultTableModel model){
            focusModel = model;
        }
    }

    private static class YearFourHandler {
        private String y4Name;
        private KTable table7, table8;
        private KTable focusTable;
        private KDefaultTableModel focusModel;
        private JPopupMenu jPopupMenu;
        private JMenuItem detailsItem, editItem, removeItem, confirmItem, newItem;

        private YearFourHandler(){
            y4Name = Student.finalAcademicYear();

            setTableSeven();
            setTableEight();

            detailsItem = new JMenuItem(DETAILS_STRING);
            detailsItem.setFont(KFontFactory.createPlainFont(15));
            detailsItem.addActionListener(e -> {
                final Course c = getModuleByCode(String.valueOf(getFocusModel().getValueAt(getFocusTable().getSelectedRow(), 0)));
                try {
                    SwingUtilities.invokeLater(()-> Course.exhibit(Board.getRoot(), Objects.requireNonNull(c)));
                } catch (NullPointerException npe) {
                    App.silenceException("No such course in list");
                }
            });

            editItem = new JMenuItem(EDIT_STRING);
            editItem.setFont(KFontFactory.createPlainFont(15));
            editItem.addActionListener(e -> {
                final Course c = getModuleByCode(getFocusModel().getValueAt(getFocusTable().getSelectedRow(), 0).toString());
                try {
                    Objects.requireNonNull(c);
                    SwingUtilities.invokeLater(()-> new ModuleEditor(c, getFocusModel()).setVisible(true));
                } catch (NullPointerException npe) {
                    App.silenceException("No such course in list");
                }
            });

            removeItem = new JMenuItem(DELETE_STRING);
            removeItem.setFont(KFontFactory.createPlainFont(15));
            removeItem.addActionListener(e -> {
                final Course c = getModuleByCode(String.valueOf(getFocusModel().getValueAt(getFocusTable().getSelectedRow(), 0)));
                try {
                    Objects.requireNonNull(c);
                    if (App.showYesNoCancelDialog("Confirm Removal","Are you sure you did not do "+c.getName()+",\n" +
                            "and that you wish to remove it from your collection?")) {
                        modulesMonitor.remove(c);
                    }
                } catch (NullPointerException npe) {
                    App.silenceException("No such course in list");
                }
            });

            confirmItem = new JMenuItem(CONFIRM_STRING);
            confirmItem.setFont(KFontFactory.createPlainFont(15));
            confirmItem.addActionListener(e -> {
                final Course c = getModuleByCode(String.valueOf(getFocusModel().getValueAt(getFocusTable().getSelectedRow(),0)));
                try {
                    new Thread(()->{
                        Objects.requireNonNull(c);
                        launchVerification(c);
                    }).start();
                } catch (NullPointerException npe) {
                    App.silenceException("No such course in list");
                }
            });

            newItem = new JMenuItem(ADD_STRING);
            newItem.setFont(KFontFactory.createPlainFont(15));
            newItem.addActionListener(e -> SwingUtilities.invokeLater(()-> new ModuleAdder(y4Name, semesterName).setVisible(true)));

            jPopupMenu = new JPopupMenu();
            jPopupMenu.add(detailsItem);
            jPopupMenu.add(editItem);
            jPopupMenu.add(removeItem);
            jPopupMenu.add(confirmItem);
            jPopupMenu.add(newItem);
        }

        private void setTableSeven(){
            model7 = new KDefaultTableModel();
            model7.setColumnIdentifiers(COLUMN_IDENTIFIERS);
            models[6] = model7;

            table7 = new KTable(model7);
            setTablePreferences(table7);
            table7.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        setFocusTable(table7);
                        setFocusModel(model7);
                        semesterName = Student.FIRST_SEMESTER;

                        table7.getSelectionModel().setSelectionInterval(0, table7.rowAtPoint(e.getPoint()));
                        detailsItem.setEnabled(true);
                        editItem.setEnabled(true);
                        removeItem.setEnabled(true);
                        confirmItem.setEnabled(true);
                        newItem.setEnabled(table7.getRowCount() <= 5);

                        SwingUtilities.invokeLater(()-> jPopupMenu.show(table7,e.getX(),e.getY()));
                    }
                }
                @Override
                public void mouseReleased(MouseEvent e) {
                    mousePressed(e);
                }
            });
        }

        private void setTableEight(){
            model8 = new KDefaultTableModel();
            model8.setColumnIdentifiers(COLUMN_IDENTIFIERS);
            models[7] = model8;

            table8 = new KTable(model8);
            setTablePreferences(table8);
            table8.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        setFocusTable(table8);
                        setFocusModel(model8);
                        semesterName = Student.SECOND_SEMESTER;

                        table8.getSelectionModel().setSelectionInterval(0, table8.rowAtPoint(e.getPoint()));
                        detailsItem.setEnabled(true);
                        editItem.setEnabled(true);
                        removeItem.setEnabled(true);
                        confirmItem.setEnabled(true);
                        newItem.setEnabled(table8.getRowCount() <= 5);

                        SwingUtilities.invokeLater(()-> jPopupMenu.show(table8,e.getX(),e.getY()));
                    }
                }
                @Override
                public void mouseReleased(MouseEvent e) {
                    mousePressed(e);
                }
            });
        }

        private KPanel getPresent(){
            final KScrollPane scrollPane7 = new KScrollPane(table7,false);
            scrollPane7.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        setFocusTable(table7);
                        setFocusModel(model7);
                        semesterName = Student.FIRST_SEMESTER;

                        table7.getSelectionModel().clearSelection();
                        detailsItem.setEnabled(false);
                        editItem.setEnabled(false);
                        removeItem.setEnabled(false);
                        confirmItem.setEnabled(false);
                        newItem.setEnabled(table7.getRowCount() <= 5);

                        SwingUtilities.invokeLater(()-> jPopupMenu.show(scrollPane7,e.getX(),e.getY()));
                    }
                }
                @Override
                public void mouseReleased(MouseEvent e) {
                    mousePressed(e);
                }
            });
            final KScrollPane scrollPane8 = new KScrollPane(table8,false);
            scrollPane8.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        setFocusTable(table8);
                        setFocusModel(model8);
                        semesterName = Student.SECOND_SEMESTER;

                        table8.getSelectionModel().clearSelection();
                        detailsItem.setEnabled(false);
                        editItem.setEnabled(false);
                        removeItem.setEnabled(false);
                        confirmItem.setEnabled(false);
                        newItem.setEnabled(table8.getRowCount() <= 5);

                        SwingUtilities.invokeLater(()-> jPopupMenu.show(scrollPane8,e.getX(),e.getY()));
                    }
                }
                @Override
                public void mouseReleased(MouseEvent e) {
                    mousePressed(e);
                }
            });

            final KPanel present = new KPanel(new Dimension(825,540));
            present.setLayout(new BoxLayout(present, BoxLayout.Y_AXIS));
            present.add(KPanel.wantDirectAddition(new KLabel(Student.FIRST_SEMESTER, KFontFactory.createPlainFont(18), Color.BLUE)));
            present.add(scrollPane7);
            present.add(Box.createVerticalStrut(30));
            present.add(KPanel.wantDirectAddition(new KSeparator(Color.BLACK,new Dimension(800,1))));
            present.add(KPanel.wantDirectAddition(new KLabel(Student.SECOND_SEMESTER, KFontFactory.createPlainFont(18), Color.BLUE)));
            present.add(scrollPane8);
            present.add(Box.createVerticalStrut(15));
            return present;
        }

        private KTable getFocusTable(){
            return focusTable;
        }

        private void setFocusTable(KTable table){
            focusTable = table;
        }

        private KDefaultTableModel getFocusModel(){
            return focusModel;
        }

        private void setFocusModel(KDefaultTableModel model){
            focusModel = model;
        }
    }


    /**
     * Provides the dialog for locally addition of courses.
     */
    public static class ModuleAdder extends KDialog {
        KTextField yearField, semesterField, codeField, nameField, lecturerField, venueField, scoreField;
        JComboBox<String> dayBox, timeBox, requirementBox, creditBox;
        KPanel yearPanel, semesterPanel;
        String yearName, semesterName;
        KButton actionButton;

        public ModuleAdder(String yearName, String semesterName){//The yearName and semesterName fields are provided-set, not editable
            super("New Course");
            this.setResizable(true);
            this.setModalityType(KDialog.DEFAULT_MODALITY_TYPE);
            this.yearName = yearName;
            this.semesterName = semesterName;

            final Font hintFont = KFontFactory.createBoldFont(16);

            if (this instanceof MiscellaneousModulesHandler.MiscModuleAdder) {
                yearField = KTextField.rangeControlField(9);
            } else {
                yearField = new KTextField();
            }
            yearField.setPreferredSize(new Dimension(125, 30));
            yearField.setText(yearName);
            yearField.setEditable(yearName == null);
            yearPanel = new KPanel(new BorderLayout());
            yearPanel.add(KPanel.wantDirectAddition(new KLabel("Year:",hintFont)),BorderLayout.WEST);
            yearPanel.add(KPanel.wantDirectAddition(yearField),BorderLayout.CENTER);

            semesterField = new KTextField(new Dimension(200,30));
            semesterField.setText(semesterName);
            semesterField.setEditable(semesterName == null);
            semesterPanel = new KPanel(new BorderLayout());
            semesterPanel.add(KPanel.wantDirectAddition(new KLabel("Semester:",hintFont)),BorderLayout.WEST);
            semesterPanel.add(KPanel.wantDirectAddition(semesterField),BorderLayout.CENTER);

            codeField = KTextField.rangeControlField(10);
            codeField.setPreferredSize(new Dimension(125,30));
            final KPanel codePanel = new KPanel(new BorderLayout());
            codePanel.add(KPanel.wantDirectAddition(new KLabel("Code:",hintFont)),BorderLayout.WEST);
            codePanel.add(KPanel.wantDirectAddition(codeField),BorderLayout.CENTER);

            nameField = new KTextField(new Dimension(300,30));
            final KPanel namePanel = new KPanel(new BorderLayout());
            namePanel.add(KPanel.wantDirectAddition(new KLabel("Name:",hintFont)),BorderLayout.WEST);
            namePanel.add(KPanel.wantDirectAddition(nameField),BorderLayout.CENTER);

            lecturerField = new KTextField(new Dimension(300,30));
            final KPanel lecturerPanel = new KPanel(new BorderLayout());
            lecturerPanel.add(KPanel.wantDirectAddition(new KLabel("Lecturer:",hintFont)),BorderLayout.WEST);
            lecturerPanel.add(KPanel.wantDirectAddition(lecturerField),BorderLayout.CENTER);

            dayBox = new JComboBox<>(Course.getWeekDays());
            dayBox.setFont(KFontFactory.createPlainFont(15));
            timeBox = new JComboBox<>(Course.availableCoursePeriods());
            timeBox.setFont(KFontFactory.createPlainFont(15));
            final KPanel schedulePanel = new KPanel(new FlowLayout(FlowLayout.CENTER));//this a litte sort of an exception
            schedulePanel.addAll(new KLabel("Day:",hintFont),dayBox);
            schedulePanel.addAll(Box.createRigidArea(new Dimension(25, 30)),new KLabel("Time:",hintFont),timeBox);

            venueField = new KTextField(new Dimension(300,30));
            final KPanel venuePanel = new KPanel(new BorderLayout());
            venuePanel.add(KPanel.wantDirectAddition(new KLabel("Venue:",hintFont)),BorderLayout.WEST);
            venuePanel.add(KPanel.wantDirectAddition(venueField),BorderLayout.CENTER);

            requirementBox = new JComboBox<>(Course.availableCourseRequirements());
            requirementBox.setFont(KFontFactory.createPlainFont(15));
            requirementBox.setSelectedItem(Course.NONE);
            final KPanel requirementPanel = new KPanel(new BorderLayout());
            requirementPanel.add(KPanel.wantDirectAddition(new KLabel("Requirement:",hintFont)),BorderLayout.WEST);
            requirementPanel.add(KPanel.wantDirectAddition(requirementBox),BorderLayout.CENTER);

            creditBox = new JComboBox<>(Course.availableCreditHours());
            creditBox.setFont(KFontFactory.createPlainFont(15));
            final KPanel creditPanel = new KPanel(new BorderLayout());
            creditPanel.add(KPanel.wantDirectAddition(new KLabel("Credit Hours:",hintFont)),BorderLayout.WEST);
            creditPanel.add(KPanel.wantDirectAddition(creditBox),BorderLayout.CENTER);

            scoreField = KTextField.rangeControlField(7);
            scoreField.setPreferredSize(new Dimension(125,30));
            final KPanel scorePanel = new KPanel(new BorderLayout());
            scorePanel.add(KPanel.wantDirectAddition(new KLabel("Score:",hintFont)),BorderLayout.WEST);
            scorePanel.add(KPanel.wantDirectAddition(scoreField),BorderLayout.CENTER);

            final KButton cancelButton = new KButton("Cancel");
            cancelButton.addActionListener(e -> ModuleAdder.this.dispose());

            actionButton = new KButton("Add");
            actionButton.addActionListener(additionListener());
            final KPanel buttonsPanel = new KPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonsPanel.addAll(cancelButton, actionButton);

            this.getRootPane().setDefaultButton(actionButton);
            final KPanel bigPane = new KPanel();
            bigPane.setLayout(new BoxLayout(bigPane, BoxLayout.Y_AXIS));
            bigPane.addAll(Box.createVerticalStrut(10),yearPanel,semesterPanel,codePanel,namePanel,lecturerPanel,schedulePanel,
                    venuePanel,requirementPanel,creditPanel,scorePanel,Box.createVerticalStrut(30),buttonsPanel);
            this.setContentPane(bigPane);
            this.pack();
            this.setMinimumSize(this.getPreferredSize());
            this.setLocationRelativeTo(Board.getRoot());
        }

        private ActionListener additionListener(){
            return e-> {
                if (Globals.isBlank(codeField.getText())) {
                    App.signalError(ModuleAdder.this.getRootPane(),"No Code","Please provide the code of the course.");
                    codeField.requestFocusInWindow();
                } else if (Globals.isBlank(nameField.getText())) {
                    App.signalError(ModuleAdder.this.getRootPane(),"No Name","Please provide the name of the course.");
                    nameField.requestFocusInWindow();
                } else if (Globals.isBlank(scoreField.getText())) {
                    App.signalError(ModuleAdder.this.getRootPane(),"Error","Please enter the score you get from this course.");
                    scoreField.requestFocusInWindow();
                } else {
                    double score;
                    try {
                        score = Double.parseDouble(scoreField.getText());
                    } catch (NumberFormatException formatError){
                        reportScoreInvalid(scoreField.getText(), this.getRootPane());
                        scoreField.requestFocusInWindow();
                        return;
                    }
                    if (score < 0 || score > 100) {
                        reportScoreOutOfRange(this.getRootPane());
                        scoreField.requestFocusInWindow();
                        return;
                    }
                    if (existsInList(codeField.getText())) {
                        reportCodeDuplication(codeField.getText());
                        codeField.requestFocusInWindow();
                        return;
                    }

                    final String day = String.valueOf(dayBox.getSelectedItem()).equals(Course.UNKNOWN) ? "":
                            String.valueOf(dayBox.getSelectedItem());
                    final String time = String.valueOf(timeBox.getSelectedItem()).equals(Course.UNKNOWN) ? "":
                            String.valueOf(timeBox.getSelectedItem());
                    final String lecturerName = lecturerField.getText();
                    final Course incomingCourse = new Course(yearName, semesterName, codeField.getText(), nameField.getText(),
                            lecturerName, venueField.getText(), day, time, score, Integer.parseInt(String.valueOf(creditBox.getSelectedItem())),
                            String.valueOf(requirementBox.getSelectedItem()), false);
                    incomingCourse.setLecturer(lecturerName, true);
                    modulesMonitor.add(incomingCourse);
                    this.dispose();
                }
            };
        }
    }


    /**
     * Extends the Adding-dialog to make it an editing-one. Cool uh...
     */
    private static class ModuleEditor extends ModuleAdder {
        private KDefaultTableModel onModel;
        private Course target;

        /**
         *
         * @param eCourse The course on which edition is to be.
         * @param onModel The model to perform the removal.
         */
        private ModuleEditor(Course eCourse, KDefaultTableModel onModel) {
            super(eCourse.getYear(), eCourse.getSemester());
            this.setTitle(eCourse.getName());
            this.target = eCourse;
            this.onModel = onModel;

            codeField.setText(eCourse.getCode());
            codeField.setEditable(!eCourse.isVerified());

            nameField.setText(eCourse.getName());
            nameField.setEditable(!eCourse.isVerified());

            lecturerField.setText(eCourse.getLecturer());
            lecturerField.setEditable(eCourse.isTutorsNameCustomizable());

            dayBox.setSelectedItem(eCourse.getDay());
            timeBox.setSelectedItem(eCourse.getTime());
            venueField.setText(eCourse.getVenue());
            requirementBox.setSelectedItem(eCourse.getRequirement());
            creditBox.setSelectedItem(String.valueOf(eCourse.getCreditHours()));

            scoreField.setText(String.valueOf(eCourse.getScore()));
            scoreField.setEditable(!eCourse.isVerified());

            actionButton.removeActionListener(actionButton.getActionListeners()[0]);
            actionButton.addActionListener(editionListener());
            actionButton.setText("Refract");
        }

        private ActionListener editionListener(){
            return e-> {
                if (Globals.isBlank(codeField.getText())) {
                    App.signalError(ModuleEditor.this.getRootPane(),"No Code","Please provide the code of the course.");
                    codeField.requestFocusInWindow();
                } else if (Globals.isBlank(nameField.getText())) {
                    App.signalError(ModuleEditor.this.getRootPane(),"No Name","Please provide the name of the course.");
                    nameField.requestFocusInWindow();
                } else {
                    double score;
                    try {
                        score = Double.parseDouble(scoreField.getText());
                    } catch (NumberFormatException formatError){
                        reportScoreInvalid(scoreField.getText(), this.getRootPane());
                        scoreField.requestFocusInWindow();
                        return;
                    }
                    if (score < 0 || score > 100) {
                        reportScoreOutOfRange(this.getRootPane());
                        scoreField.requestFocusInWindow();
                        return;
                    }

                    //Check for exclusive existence in this table first
                    for (int row = 0; row < onModel.getRowCount(); row++) {
                        if (row == onModel.getSelectedRow()) {
                            continue;
                        }
                        final String tempCode = String.valueOf(onModel.getValueAt(row, 0));
                        if (tempCode.equalsIgnoreCase(codeField.getText())) {
                            reportCodeDuplication(codeField.getText());
                            codeField.requestFocusInWindow();
                            return;
                        }
                    }
                    //Check for general existence in other tables
                    if (existsInListExcept(onModel, codeField.getText())) {
                        reportCodeDuplication(codeField.getText());
                        codeField.requestFocusInWindow();
                        return;
                    }

                    final String day = String.valueOf(dayBox.getSelectedItem()).equals(Course.UNKNOWN) ? "":
                            String.valueOf(dayBox.getSelectedItem());
                    final String time = String.valueOf(timeBox.getSelectedItem()).equals(Course.UNKNOWN) ? "":
                            String.valueOf(timeBox.getSelectedItem());
                    final Course course = new Course(yearField.getText(), semesterField.getText(), codeField.getText(),nameField.getText(),
                            "", venueField.getText(), day,time, score,Integer.parseInt(String.valueOf(creditBox.getSelectedItem())),
                            String.valueOf(requirementBox.getSelectedItem()), target.isVerified());
                    course.setLecturer(lecturerField.getText(), lecturerField.isEditable());//Or, target.isTutorsNameCustomizable()

                    substitute(target, course);
                    this.dispose();
                }
            };
        }
    }

}
