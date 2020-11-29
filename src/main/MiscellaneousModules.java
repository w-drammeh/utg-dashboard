package main;

import customs.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MiscellaneousModules {
    private KMenuItem detailsItem, editItem, removeItem, confirmItem, newItem;
    private JPopupMenu jPopupMenu;
    private static KTable miscTable;
    public static KTableModel miscModel;


    public MiscellaneousModules(){
        setupTable();
        configurePopUp();
    }

    /**
     * Triggered by the monitor to signal addition is directed to the miscellaneous-table.
     */
    public static void welcome(Course summerCourse) {
        miscModel.addRow(new String[] {summerCourse.getCode(), summerCourse.getName(), summerCourse.getLecturer(),
                summerCourse.getGrade(), summerCourse.getYear()});
    }

    /**
     * Triggered by the monitor to signal removal is directed to miscellaneous-table.
     */
    public static void ridOf(Course summerCourse){
        miscModel.removeRow(miscModel.getRowOf(summerCourse.getCode()));
    }

    private void setupTable(){
        miscModel = new KTableModel();
        miscModel.setColumnIdentifiers(new String[] {"CODE", "NAME", "LECTURER", "GRADE", "YEAR"});
        ModulesHandler.ALL_MODELS.add(miscModel);

        miscTable = new KTable(miscModel);
        miscTable.setRowHeight(30);
        miscTable.setHeaderHeight(30);
        miscTable.getTableHeader().setFont(KFontFactory.createBoldFont(16));
        miscTable.setFont(KFontFactory.createPlainFont(15));
        miscTable.centerAlignAllColumns();
        miscTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    detailsItem.setEnabled(true);
                    editItem.setEnabled(true);
                    removeItem.setEnabled(true);
                    confirmItem.setEnabled(true);
                    miscTable.getSelectionModel().setSelectionInterval(0, miscTable.rowAtPoint(e.getPoint()));
                    SwingUtilities.invokeLater(()-> jPopupMenu.show(miscTable, e.getX(), e.getY()));
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mousePressed(e);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    final int selectedRow = miscTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        Course.exhibit(ModulesHandler.getModuleByCode(String.valueOf(miscTable.getValueAt(selectedRow, 0))));
                    }
                    e.consume();
                }
            }
        });
    }

    private void configurePopUp(){
        detailsItem = new KMenuItem(ModulesHandler.DETAILS);
        detailsItem.addActionListener(e-> SwingUtilities.invokeLater(()-> {
            final Course course = ModulesHandler.getModuleByCode(String.valueOf(miscModel.getValueAt(
                    miscTable.getSelectedRow(), 0)));
            Course.exhibit(course);
        }));

        editItem = new KMenuItem(ModulesHandler.EDIT);
        editItem.addActionListener(e-> {
            final Course course = ModulesHandler.getModuleByCode(String.valueOf(miscModel.getValueAt(miscTable.getSelectedRow(), 0)));
            if (course != null) {
                final  MiscModuleEditor editor = new MiscModuleEditor(course);
                SwingUtilities.invokeLater(()-> editor.setVisible(true));
            }
        });

        removeItem = new KMenuItem(ModulesHandler.DELETE);
        removeItem.addActionListener(e-> {
            final Course course = ModulesHandler.getModuleByCode(String.valueOf(miscModel.getValueAt(miscTable.getSelectedRow(), 0)));
            if (course != null) {
                ModulesHandler.getModulesMonitor().remove(course);
            }
        });

        confirmItem = new KMenuItem(ModulesHandler.CONFIRM);
        confirmItem.addActionListener(e-> {
            final Course course = ModulesHandler.getModuleByCode(String.valueOf(miscModel.getValueAt(miscTable.getSelectedRow(), 0)));
            if (course != null) {
                new Thread(()-> ModulesHandler.launchVerification(course)).start();
            }
        });

        newItem = new KMenuItem(ModulesHandler.ADD);
        newItem.addActionListener(e-> {
            final MiscModuleAdder adder = new MiscModuleAdder();
            SwingUtilities.invokeLater(()-> adder.setVisible(true));
        });

        jPopupMenu = new JPopupMenu();
        jPopupMenu.add(detailsItem);
        jPopupMenu.add(editItem);
        jPopupMenu.add(removeItem);
        jPopupMenu.add(confirmItem);
        jPopupMenu.add(newItem);
    }

    public JComponent getPresent(){
        final KScrollPane miscScrollPane = miscTable.sizeMatchingScrollPane();
        miscScrollPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    miscTable.clearSelection();
                    detailsItem.setEnabled(false);
                    editItem.setEnabled(false);
                    removeItem.setEnabled(false);
                    confirmItem.setEnabled(false);
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
        present.addAll(miscScrollPane, Box.createVerticalStrut(15));
        return present;
    }


    public static class MiscModuleAdder extends ModulesHandler.ModuleAdder {
        JComboBox<String> semestersBox;

        private MiscModuleAdder(){
            super(null, null);
            setTitle("New Miscellaneous Course");

            semestersBox = new JComboBox<>(new String[] {Student.FIRST_SEMESTER, Student.SECOND_SEMESTER, Student.SUMMER_SEMESTER});
            semestersBox.setFont(KFontFactory.createPlainFont(15));
            semesterPanel.removeLast();
            semesterPanel.add(new KPanel(semestersBox), BorderLayout.CENTER);

            actionButton.removeActionListener(actionButton.getActionListeners()[0]);
            actionButton.addActionListener(additionListener());
        }

        private ActionListener additionListener() {
            return e-> {
                final String givenYear;
                if (Student.isValidAcademicYear(yearField.getText())) {
                    givenYear = yearField.getText();
                } else {
                    App.signalError("Error", "Sorry, "+yearField.getText()+" is not a valid academic year.\n" +
                                    "Dashboard reads years in the format: yyyy/yyyy. E.g 2016/2017");
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
                } else if (givenYear.equals(Student.fourthAcademicYear())) {
                    App.signalError("Error",
                            "Sorry, the year you provided ["+givenYear+"] corresponds your fourth academic year.\n" +
                                    "By convention, Dashboard restricts the addition of specific resources to the miscellaneous table.\n" +
                                    "If you've done this module in your fourth year, consider adding it in the appropriate place.");
                    yearField.requestFocusInWindow();
                    return;
                }

                if (codeField.isBlank()) {
                    App.signalError(getRootPane(),"No Code", "Please provide the code of the course.");
                    codeField.requestFocusInWindow();
                } else if (nameField.isBlank()) {
                    App.signalError(getRootPane(),"No Name","Please provide the name of the course.");
                    nameField.requestFocusInWindow();
                } else if (scoreField.isBlank()) {
                    App.signalError(getRootPane(),"Error","Please enter the score you get from this course.");
                    scoreField.requestFocusInWindow();
                } else {
                    double score;
                    try {
                        score = Double.parseDouble(scoreField.getText());
                    } catch (NumberFormatException formatError){
                        ModulesHandler.reportScoreInvalid(scoreField.getText(), getRootPane());
                        scoreField.requestFocusInWindow();
                        return;
                    }

                    if (score < 0 || score > 100) {
                        ModulesHandler.reportScoreOutOfRange(getRootPane());
                        scoreField.requestFocusInWindow();
                        return;
                    }

                    if (ModulesHandler.existsInList(codeField.getText())) {
                        ModulesHandler.reportCodeDuplication(codeField.getText());
                        codeField.requestFocusInWindow();
                        return;
                    }

                    final Course course = new Course(givenYear, String.valueOf(semestersBox.getSelectedItem()),
                            codeField.getText(), nameField.getText(), lecturerField.getText(), venueField.getText(),
                            String.valueOf(dayBox.getSelectedItem()), String.valueOf(timeBox.getSelectedItem()), score,
                            Integer.parseInt(String.valueOf(creditBox.getSelectedItem())), String.valueOf(requirementBox.getSelectedItem()), false);
                    ModulesHandler.getModulesMonitor().add(course);
                    dispose();
                }
            };
        }
    }


    public static class MiscModuleEditor extends MiscModuleAdder {
        private Course target;

        private MiscModuleEditor(Course miscCourse){
            super();
            setTitle(miscCourse.getName()+" - Miscellaneous");
            target = miscCourse;

            yearField.setText(target.getYear());
            codeField.setText(target.getCode());
            nameField.setText(target.getName());
            lecturerField.setText(target.getLecturer());
            lecturerField.setEditable(target.canEditTutorName());
            dayBox.setSelectedItem(target.getDay());
            timeBox.setSelectedItem(target.getTime());
            venueField.setText(target.getVenue());
            requirementBox.setSelectedItem(target.getRequirement());
            creditBox.setSelectedItem(target.getCreditHours());
            scoreField.setText(Double.toString(target.getScore()));

            if (miscCourse.isVerified()) {
                yearField.setEditable(false);
                codeField.setEditable(false);
                nameField.setEditable(false);
                scoreField.setEditable(false);
            }

            actionButton.removeActionListener(actionButton.getActionListeners()[0]);
            actionButton.addActionListener(editionListener());
            actionButton.setText("Done");
        }

        private ActionListener editionListener() {
            return e-> {
                final String givenYear;
                if (Student.isValidAcademicYear(yearField.getText())) {
                    givenYear = yearField.getText();
                } else {
                    App.signalError("Invalid Format",
                            "Sorry, "+yearField.getText()+" is not a valid academic year.\n" +
                                    "Dashboard reads years in the format: yyyy/yyyy. E.g 2016/2017");
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
                } else if (givenYear.equals(Student.fourthAcademicYear())) {
                    App.signalError("Error",
                            "Sorry, the year you provided ["+givenYear+"] corresponds your fourth academic year.\n" +
                            "By convention, Dashboard restricts the addition of specific resources to the miscellaneous table.\n" +
                                    "If you've done this module in your fourth year, consider adding it in the appropriate place.");
                    yearField.requestFocusInWindow();
                    return;
                }

                if (codeField.isBlank()) {
                    App.signalError(getRootPane(),"No Code", "Please provide the code of the course.");
                    codeField.requestFocusInWindow();
                } else if (nameField.isBlank()) {
                    App.signalError(getRootPane(),"No Name","Please provide the name of the course.");
                    nameField.requestFocusInWindow();
                } else if (scoreField.isBlank()) {
                    App.signalError(getRootPane(),"Error","Please enter the score you get from this course.");
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

                    final Course course = new Course(yearField.getText(), String.valueOf(semestersBox.getSelectedItem()),
                            codeField.getText(), nameField.getText(), lecturerField.getText(),venueField.getText(), String.valueOf(dayBox.getSelectedItem()),
                            String.valueOf(timeBox.getSelectedItem()), score, Integer.parseInt(String.valueOf(creditBox.getSelectedItem())),
                            String.valueOf(requirementBox.getSelectedItem()), target.isVerified());
                    ModulesHandler.substitute(target, course);
                    dispose();
                }
            };
        }
    }

}
