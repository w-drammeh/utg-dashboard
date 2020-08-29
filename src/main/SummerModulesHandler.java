package main;

import customs.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;

public class SummerModulesHandler {
    private JPopupMenu jPopupMenu;
    private JMenuItem detailsItem, editItem, removeItem, confirmItem, newItem;
    private static KTable summerTable;
    private static KDefaultTableModel summerModel;


    public SummerModulesHandler(){
        generateSummerTable();
        configurePopUpOnSummerTable();
    }

    /**
     * Triggered by the monitor to signal addition is directed to summerTable.
     */
    public static void welcome(Course summerCourse){
        summerModel.addRow(new String[] {summerCourse.getCode(), summerCourse.getName(), summerCourse.getLecturer(),
                summerCourse.getGrade(), summerCourse.getYear()});
    }

    /**
     * Triggered by the monitor to signal removal is directed to summerTable.
     */
    public static void ridOf(Course summerCourse){
        summerModel.removeRow(summerModel.getRowOf(summerCourse.getCode()));
    }

    private void generateSummerTable(){
        summerModel = new KDefaultTableModel();
        summerModel.setColumnIdentifiers(new String[] {"CODE", "NAME", "LECTURER", "GRADE", "YEAR"});
        ModulesHandler.models[8] = summerModel;

        summerTable = new KTable(summerModel);
        summerTable.setRowHeight(30);
        summerTable.getTableHeader().setFont(KFontFactory.createBoldFont(16));
        summerTable.setFont(KFontFactory.createPlainFont(15));
        summerTable.centerAlignAllColumns();
        summerTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    final boolean necessary = summerTable.getSelectedRow() >= 0;

                    detailsItem.setEnabled(necessary);
                    editItem.setEnabled(necessary);
                    removeItem.setEnabled(necessary);
                    confirmItem.setEnabled(necessary);
                    newItem.setEnabled(summerTable.getRowCount() <= 15);

                    jPopupMenu.show(summerTable, e.getX(), e.getY());
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                mousePressed(e);
            }
        });
    }

    private void configurePopUpOnSummerTable(){
        detailsItem = new JMenuItem(ModulesHandler.DETAILS_STRING);
        detailsItem.setFont(KFontFactory.createPlainFont(15));
        detailsItem.addActionListener(e -> SwingUtilities.invokeLater(()->{
            try {
                Course.exhibit(Board.getRoot(), Objects.requireNonNull(ModulesHandler.getModuleByCode(
                        String.valueOf(summerModel.getValueAt(summerTable.getSelectedRow(), 0))
                )));
            } catch (NullPointerException npe){
                App.silenceException("No such course in list");
            }
        }));

        editItem = new JMenuItem(ModulesHandler.EDIT_STRING);
        editItem.setFont(KFontFactory.createPlainFont(15));
        editItem.addActionListener(e -> {
            final Course c = ModulesHandler.getModuleByCode(String.valueOf(summerModel.getValueAt(summerTable.getSelectedRow(), 0)));
            try {
                SwingUtilities.invokeLater(()-> new SummerModuleEditor(Objects.requireNonNull(c)).setVisible(true));
            } catch (NullPointerException npe) {
                App.silenceException("No such course in list");
            }
        });

        removeItem = new JMenuItem(ModulesHandler.DELETE_STRING);
        removeItem.setFont(KFontFactory.createPlainFont(15));
        removeItem.addActionListener(e -> {
            final Course c = ModulesHandler.getModuleByCode(String.valueOf(summerModel.getValueAt(summerTable.getSelectedRow(),0)));
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

        confirmItem = new JMenuItem(ModulesHandler.CONFIRM_STRING);
        confirmItem.setFont(KFontFactory.createPlainFont(15));
        confirmItem.addActionListener(e -> {
            final Course c = ModulesHandler.getModuleByCode(String.valueOf(summerModel.getValueAt(summerTable.getSelectedRow(),0)));
            try {
                Objects.requireNonNull(c);
                ModulesHandler.launchVerification(c);
            } catch (NullPointerException npe) {
                App.silenceException("No such course in list");
            }
        });

        newItem = new JMenuItem(ModulesHandler.ADD_STRING);
        newItem.setFont(KFontFactory.createPlainFont(15));
        newItem.addActionListener(e -> SwingUtilities.invokeLater(()-> new SummerModuleAdder().setVisible(true)));

        jPopupMenu = new JPopupMenu();
        jPopupMenu.add(detailsItem);
        jPopupMenu.add(editItem);
        jPopupMenu.add(removeItem);
        jPopupMenu.add(confirmItem);
        jPopupMenu.add(newItem);
    }

    public KPanel getSummerPresent(){
        final KScrollPane summerScrollPane = new KScrollPane(summerTable,false);
        summerScrollPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    final boolean necessary = summerTable.getSelectedRow() >= 0;

                    detailsItem.setEnabled(necessary);
                    editItem.setEnabled(necessary);
                    removeItem.setEnabled(necessary);
                    confirmItem.setEnabled(necessary);
                    newItem.setEnabled(summerTable.getRowCount() <= 15);

                    jPopupMenu.show(summerScrollPane, e.getX(), e.getY());
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                mousePressed(e);
            }
        });

        final KPanel present = new KPanel();
        present.setLayout(new BoxLayout(present, BoxLayout.Y_AXIS));
        present.addAll(Box.createVerticalStrut(15), summerScrollPane, Box.createVerticalStrut(10));
        return present;
    }


    public static class SummerModuleAdder extends ModulesHandler.ModuleAdder {
        JComboBox<String> availableYearsBox;

        private SummerModuleAdder(){
            super(null, Student.SUMMER_SEMESTER);
            this.setTitle("New Summer Course");

            this.availableYearsBox = new JComboBox<>(new String[] {Student.firstAcademicYear(), Student.secondAcademicYear(),
                    Student.thirdAcademicYear(), Student.finalAcademicYear()});
            availableYearsBox.setFont(KFontFactory.createPlainFont(15));
            yearPanel.removeLastChild();
            yearPanel.add(new KPanel(availableYearsBox), BorderLayout.CENTER);

            actionButton.removeActionListener(actionButton.getActionListeners()[0]);
            actionButton.addActionListener(additionListener());
        }

        private ActionListener additionListener() {
            return e -> {
                if (codeField.hasNoText()) {
                    App.signalError(SummerModuleAdder.this.getRootPane(),"No Code","Please provide the code of the course.");
                    codeField.requestFocusInWindow();
                } else if (nameField.hasNoText()) {
                    App.signalError(SummerModuleAdder.this.getRootPane(),"No Name","Please provide the name of the course.");
                    nameField.requestFocusInWindow();
                } else if (scoreField.hasNoText()) {
                    App.signalError(SummerModuleAdder.this.getRootPane(),"Error","Please enter the score you get from this course.");
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
                    final Course course = new Course(String.valueOf(availableYearsBox.getSelectedItem()),
                            semesterField.getText(), codeField.getText(), nameField.getText(), lecturerField.getText(),
                            venueField.getText(), day, time, score, Integer.parseInt(String.valueOf(creditBox.getSelectedItem())),
                            String.valueOf(requirementBox.getSelectedItem()), false);
                    ModulesHandler.getModulesMonitor().add(course);
                    this.dispose();
                }
            };
        }
    }


    public static class SummerModuleEditor extends SummerModuleAdder {
        private Course target;

        private SummerModuleEditor(Course summerCourse){
            super();
            this.setTitle(summerCourse.getName());
            this.target = summerCourse;

            availableYearsBox.setSelectedItem(target.getYear());
            availableYearsBox.setEnabled(!target.isVerified());

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
                    //Check for exclusive existence in this table first
                    for (int row = 0; row < summerModel.getRowCount(); row++) {
                        if (row == summerModel.getSelectedRow()) {
                            continue;
                        }
                        final String tempCode = String.valueOf(summerModel.getValueAt(row, 0));
                        if (tempCode.equalsIgnoreCase(codeField.getText())) {
                            ModulesHandler.reportCodeDuplication(codeField.getText());
                            codeField.requestFocusInWindow();
                            return;
                        }
                    }
                    if (ModulesHandler.existsInListExcept(summerModel, codeField.getText())) {
                        ModulesHandler.reportCodeDuplication(codeField.getText());
                        codeField.requestFocusInWindow();
                        return;
                    }

                    final String day = String.valueOf(dayBox.getSelectedItem()).equals(Course.UNKNOWN) ? "":
                            String.valueOf(dayBox.getSelectedItem());
                    final String time = String.valueOf(timeBox.getSelectedItem()).equals(Course.UNKNOWN) ? "":
                            String.valueOf(timeBox.getSelectedItem());
                    final Course course = new Course(String.valueOf(availableYearsBox.getSelectedItem()),
                            semesterField.getText(), codeField.getText(), nameField.getText(), "",
                            venueField.getText(), day, time, score, Integer.parseInt(String.valueOf(creditBox.getSelectedItem())),
                            String.valueOf(requirementBox.getSelectedItem()), target.isVerified());
                    course.setLecturer(lecturerField.getText(), lecturerField.isEditable());
                    ModulesHandler.substitute(target, course);
                    this.dispose();
                }
            };
        }
    }

}
