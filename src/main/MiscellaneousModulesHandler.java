package main;

import customs.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;

public class MiscellaneousModulesHandler {
    private JPopupMenu jPopupMenu;
    private KMenuItem detailsItem, editItem, removeItem, confirmItem, newItem;
    private static KTable miscTable;
    private static KDefaultTableModel miscModel;


    public MiscellaneousModulesHandler(){
        generateMiscTable();
        configurePopUp();
    }

    /**
     * Triggered by the monitor to signal addition is directed to miscellaneousTable.
     */
    public static void welcome(Course summerCourse){
        miscModel.addRow(new String[] {summerCourse.getCode(), summerCourse.getName(), summerCourse.getLecturer(),
                summerCourse.getGrade(), summerCourse.getYear()});
    }

    /**
     * Triggered by the monitor to signal removal is directed to miscellaneousTable.
     */
    public static void ridOf(Course summerCourse){
        miscModel.removeRow(miscModel.getRowOf(summerCourse.getCode()));
    }

    private void generateMiscTable(){
        miscModel = new KDefaultTableModel();
        miscModel.setColumnIdentifiers(new String[] {"CODE", "NAME", "LECTURER", "GRADE", "YEAR"});
        ModulesHandler.models[9] = miscModel;
        miscTable = new KTable(miscModel);
        miscTable.setRowHeight(30);
        miscTable.getTableHeader().setFont(KFontFactory.createBoldFont(16));
        miscTable.setFont(KFontFactory.createPlainFont(15));
        miscTable.centerAlignAllColumns();
        miscTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    final boolean necessary = miscTable.getSelectedRow() >= 0;

                    detailsItem.setEnabled(necessary);
                    editItem.setEnabled(necessary);
                    removeItem.setEnabled(necessary);
                    confirmItem.setEnabled(necessary);
                    newItem.setEnabled(miscTable.getRowCount() <= 15);

                    jPopupMenu.show(miscTable, e.getX(), e.getY());
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                mousePressed(e);
            }
        });
    }

    private void configurePopUp(){
        detailsItem = new KMenuItem(ModulesHandler.DETAILS_STRING);
        detailsItem.addActionListener(e -> SwingUtilities.invokeLater(()->{
            final Course c = ModulesHandler.getModuleByCode(String.valueOf(miscModel.getValueAt(
                    miscTable.getSelectedRow(), 0)));
            try {
                Course.exhibit(Board.getRoot(), Objects.requireNonNull(c));
            } catch (NullPointerException npe){
                App.silenceException("No such course in list");
            }
        }));

        editItem = new KMenuItem(ModulesHandler.EDIT_STRING);
        editItem.addActionListener(e -> {
            final Course c = ModulesHandler.getModuleByCode(String.valueOf(miscModel.getValueAt(miscTable.getSelectedRow(), 0)));
            try {
                SwingUtilities.invokeLater(() -> new MiscModuleEditor(Objects.requireNonNull(c)).setVisible(true));
            } catch (NullPointerException npe) {
                App.silenceException("No such course in list");
            }
        });

        removeItem = new KMenuItem(ModulesHandler.DELETE_STRING);
        removeItem.addActionListener(e -> {
            final Course c = ModulesHandler.getModuleByCode(String.valueOf(miscModel.getValueAt(miscTable.getSelectedRow(), 0)));
            try {
                Objects.requireNonNull(c);
                if (App.showYesNoCancelDialog("Confirm Removal","Are you sure you did not do "+c.getName()+"\n" +
                        "and that you wish to remove it from your summer collection?")) {
                    ModulesHandler.getModulesMonitor().remove(c);
                }
            } catch (NullPointerException npe) {
                App.silenceException("No such course in list");
            }
        });

        confirmItem = new KMenuItem(ModulesHandler.CONFIRM_STRING);
        confirmItem.addActionListener(e -> {
            final Course c = ModulesHandler.getModuleByCode(String.valueOf(miscModel.getValueAt(miscTable.getSelectedRow(), 0)));
            new Thread(()-> {
                try {
                    Objects.requireNonNull(c);
                    ModulesHandler.launchVerification(c);
                } catch (NullPointerException npe) {
                    App.silenceException("No such course in list");
                }
            }).start();
        });

        newItem = new KMenuItem(ModulesHandler.ADD_STRING);
        newItem.addActionListener(e -> SwingUtilities.invokeLater(()-> new MiscModuleAdder().setVisible(true)));

        jPopupMenu = new JPopupMenu();
        jPopupMenu.add(detailsItem);
        jPopupMenu.add(editItem);
        jPopupMenu.add(removeItem);
        jPopupMenu.add(confirmItem);
        jPopupMenu.add(newItem);
    }

    public JComponent getMiscPresent(){
        final KScrollPane miscScrollPane = new KScrollPane(miscTable,false);
        miscScrollPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    final boolean necessary = miscTable.getSelectedRow() >= 0;

                    detailsItem.setEnabled(necessary);
                    editItem.setEnabled(necessary);
                    removeItem.setEnabled(necessary);
                    confirmItem.setEnabled(necessary);

                    jPopupMenu.show(miscScrollPane, e.getX(), e.getY());
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                mousePressed(e);
            }
        });

        final KPanel present = new KPanel();
        present.setLayout(new BoxLayout(present, BoxLayout.Y_AXIS));
        present.addAll(Box.createVerticalStrut(15), miscScrollPane, Box.createVerticalStrut(10));
        return present;
    }


    public static class MiscModuleAdder extends ModulesHandler.ModuleAdder {
        JComboBox<String> semestersBox;

        private MiscModuleAdder(){
            super(null,null);
            this.setTitle("New Miscellaneous Course");

            this.semestersBox = new JComboBox<>(new String[] {Student.FIRST_SEMESTER, Student.SECOND_SEMESTER, Student.SUMMER_SEMESTER});
            semestersBox.setFont(KFontFactory.createPlainFont(15));
            semesterPanel.removeLastChild();
            semesterPanel.add(KPanel.wantDirectAddition(this.semestersBox), BorderLayout.CENTER);

            actionButton.removeActionListener(actionButton.getActionListeners()[0]);
            actionButton.addActionListener(additionListener());
        }

        private ActionListener additionListener() {
            return e -> {
                final String givenYear;
                if (Student.isValidAcademicYear(yearField.getText())) {
                    givenYear = yearField.getText();
                } else {
                    App.signalError("Invalid year format",
                            "Sorry, "+yearField.getText()+" is not a valid academic year. Dashboard reads years in the format: yyyy/yyyy.\n" +
                                    "E.g 2016/2017");
                    yearField.requestFocusInWindow();
                    return;
                }
                if (givenYear.equals(Student.firstAcademicYear())) {
                    App.signalError("Error",
                            "Sorry, the year you provided ["+givenYear+"] corresponds your first academic year.\n" +
                                    "By convention, Dashboard restricts the addition of specific resources to the miscellaneous table.\n" +
                                    "If you've done this module in your first year, consider adding it in the appropriate place.");
                    yearField.requestFocusInWindow();
                    return;
                } else if (givenYear.equals(Student.secondAcademicYear())) {
                    App.signalError("Error",
                            "Sorry, the year you provided ["+givenYear+"] corresponds your second academic year.\n" +
                                    "By convention, Dashboard restricts the addition of specific resources to the miscellaneous table.\n" +
                                    "If you've done this module in your second year, consider adding it in the appropriate place.");
                    yearField.requestFocusInWindow();
                    return;
                } else if (givenYear.equals(Student.thirdAcademicYear())) {
                    App.signalError("Error",
                            "Sorry, the year you provided ["+givenYear+"] corresponds your third academic year.\n" +
                                    "By convention, Dashboard restricts the addition of specific resources to the miscellaneous table.\n" +
                                    "If you've done this module in your third year, consider adding it in the appropriate place.");
                    yearField.requestFocusInWindow();
                    return;
                } else if (givenYear.equals(Student.finalAcademicYear())) {
                    App.signalError("Error",
                            "Sorry, the year you provided ["+givenYear+"] corresponds your fourth academic year.\n" +
                                    "By convention, Dashboard restricts the addition of specific resources to the miscellaneous table.\n" +
                                    "If you've done this module in your fourth year, consider adding it in the appropriate place.");
                    yearField.requestFocusInWindow();
                    return;
                }
                if (codeField.hasNoText()) {
                    App.signalError(MiscModuleAdder.this.getRootPane(), "No Code","Please provide the code of the course.");
                    codeField.requestFocusInWindow();
                } else if (nameField.hasNoText()) {
                    App.signalError(MiscModuleAdder.this.getRootPane(),"No Name","Please provide the name of the course.");
                    nameField.requestFocusInWindow();
                } else if (scoreField.hasNoText()) {
                    App.signalError(MiscModuleAdder.this.getRootPane(),"Error","Please enter the score you get from this course.");
                    scoreField.requestFocusInWindow();
                } else {
                    double score;
                    try {
                        score = Double.parseDouble(scoreField.getText());
                    } catch (NumberFormatException formatError){
                        ModulesHandler.reportScoreInvalid(scoreField.getText(), this.getRootPane());
                        scoreField.requestFocusInWindow();
                        return;
                    }
                    if (score < 0 || score > 100) {
                        ModulesHandler.reportScoreOutOfRange(this.getRootPane());
                        scoreField.requestFocusInWindow();
                        return;
                    }
                    if (ModulesHandler.existsInList(codeField.getText())) {
                        ModulesHandler.reportCodeDuplication(codeField.getText());
                        codeField.requestFocusInWindow();
                        return;
                    }

                    final String day = String.valueOf(dayBox.getSelectedItem()).equals(Course.UNKNOWN) ? "":
                            String.valueOf(dayBox.getSelectedItem());
                    final String time = String.valueOf(timeBox.getSelectedItem()).equals(Course.UNKNOWN) ? "":
                            String.valueOf(timeBox.getSelectedItem());
                    final Course course = new Course(givenYear, String.valueOf(semestersBox.getSelectedItem()),
                            codeField.getText(), nameField.getText(), lecturerField.getText(), venueField.getText(),
                            day, time, score, Integer.parseInt(String.valueOf(creditBox.getSelectedItem())),
                            String.valueOf(requirementBox.getSelectedItem()), false);
                    ModulesHandler.getModulesMonitor().add(course);
                    this.dispose();
                }
            };
        }
    }


    public static class MiscModuleEditor extends MiscModuleAdder {
        private Course target;

        private MiscModuleEditor(Course miscCourse){
            super();
            this.setTitle(miscCourse.getName()+" - Miscellaneous");
            this.target = miscCourse;

            yearField.setText(target.getYear());
            yearField.setEnabled(!target.isVerified());

            codeField.setText(target.getCode());
            codeField.setEditable(!target.isVerified());

            nameField.setText(target.getName());
            nameField.setEditable(!target.isVerified());

            lecturerField.setText(target.getLecturer());
            lecturerField.setEditable(target.isTutorsNameCustomizable());

            dayBox.setSelectedItem(target.getDay());
            timeBox.setSelectedItem(target.getTime());
            venueField.setText(target.getVenue());
            requirementBox.setSelectedItem(target.getRequirement());
            creditBox.setSelectedItem(target.getCreditHours());

            scoreField.setText(Double.toString(target.getScore()));
            scoreField.setEditable(!target.isVerified());

            actionButton.removeActionListener(actionButton.getActionListeners()[0]);
            actionButton.addActionListener(editionListener());
            actionButton.setText("Refract");
        }

        private ActionListener editionListener() {
            return e-> {
                final String givenYear;
                if (Student.isValidAcademicYear(yearField.getText())) {
                    givenYear = yearField.getText();
                } else {
                    App.signalError("Invalid Format",
                            "Sorry, "+yearField.getText()+" is not a valid academic year. Dashboard reads years in the format: yyyy/yyyy.\n" +
                                    "E.g 2016/2017");
                    yearField.requestFocusInWindow();
                    return;
                }
                if (givenYear.equals(Student.firstAcademicYear())) {
                    App.signalError("Error",
                            "Sorry, the year you provided ["+givenYear+"] corresponds your first academic year.\n" +
                                    "By convention, Dashboard restricts the addition of specific resources to the miscellaneous table.\n" +
                                    "If you've done this module in your first year, consider adding it in the appropriate place.");
                    yearField.requestFocusInWindow();
                    return;
                } else if (givenYear.equals(Student.secondAcademicYear())) {
                    App.signalError("Error",
                            "Sorry, the year you provided ["+givenYear+"] corresponds your second academic year.\n" +
                                    "By convention, Dashboard restricts the addition of specific resources to the miscellaneous table.\n" +
                                    "If you've done this module in your second year, consider adding it in the appropriate place.");
                    yearField.requestFocusInWindow();
                    return;
                } else if (givenYear.equals(Student.thirdAcademicYear())) {
                    App.signalError("Error",
                            "Sorry, the year you provided ["+givenYear+"] corresponds your third academic year.\n" +
                                    "By convention, Dashboard restricts the addition of specific resources to the miscellaneous table.\n" +
                                    "If you've done this module in your third year, consider adding it in the appropriate place.");
                    yearField.requestFocusInWindow();
                    return;
                } else if (givenYear.equals(Student.finalAcademicYear())) {
                    App.signalError("Error",
                            "Sorry, the year you provided ["+givenYear+"] corresponds your fourth academic year.\n" +
                            "By convention, Dashboard restricts the addition of specific resources to the miscellaneous table.\n" +
                                    "If you've done this module in your fourth year, consider adding it in the appropriate place.");
                    yearField.requestFocusInWindow();
                    return;
                }
                if (codeField.hasNoText()) {
                    App.signalError(this.getRootPane(),"No Code","Please provide the code of the course.");
                    codeField.requestFocusInWindow();
                } else if (nameField.hasNoText()) {
                    App.signalError(this.getRootPane(),"No Name","Please provide the name of the course.");
                    nameField.requestFocusInWindow();
                } else if (scoreField.hasNoText()) {
                    App.signalError(this.getRootPane(),"Error","Please enter the score you get from this course.");
                    scoreField.requestFocusInWindow();
                } else {
                    double score;
                    try {
                        score = Double.parseDouble(scoreField.getText());
                    } catch (NumberFormatException formatError){
                        ModulesHandler.reportScoreInvalid(scoreField.getText(), this.getRootPane());
                        scoreField.requestFocusInWindow();
                        return;
                    }
                    if (score < 0 || score > 100) {
                        ModulesHandler.reportScoreOutOfRange(this.getRootPane());
                        scoreField.requestFocusInWindow();
                        return;
                    }
//                    Check for exclusive existence in this table first
                    for (int row = 0; row < miscModel.getRowCount(); row++) {
                        if (row == miscModel.getSelectedRow()) {
                            continue;
                        }
                        final String tempCode = String.valueOf(miscModel.getValueAt(row, 0));
                        if (tempCode.equalsIgnoreCase(codeField.getText())) {
                            ModulesHandler.reportCodeDuplication(codeField.getText());
                            codeField.requestFocusInWindow();
                            return;
                        }
                    }
                    if (ModulesHandler.existsInListExcept(miscModel, codeField.getText())) {
                        ModulesHandler.reportCodeDuplication(codeField.getText());
                        codeField.requestFocusInWindow();
                        return;
                    }

                    final String day = String.valueOf(dayBox.getSelectedItem()).equals(Course.UNKNOWN) ? "":
                            String.valueOf(dayBox.getSelectedItem());
                    final String time = String.valueOf(timeBox.getSelectedItem()).equals(Course.UNKNOWN) ? "":
                            String.valueOf(timeBox.getSelectedItem());
                    final Course course = new Course(yearField.getText(), String.valueOf(semestersBox.getSelectedItem()),
                            codeField.getText(), nameField.getText(), "",venueField.getText(), day, time, score,
                            Integer.parseInt(String.valueOf(creditBox.getSelectedItem())), String.valueOf(requirementBox.getSelectedItem()),
                            target.isVerified());
                    course.setLecturer(lecturerField.getText(), lecturerField.isEditable());
                    ModulesHandler.substitute(target, course);
                    this.dispose();
                }
            };
        }
    }

}
