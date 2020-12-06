package main;

import proto.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SummerModule {
    private KMenuItem detailsItem, editItem, removeItem, confirmItem, newItem;
    private JPopupMenu popupMenu;
    private static KTable summerTable;
    public static KTableModel summerModel;


    public SummerModule(){
        setupTable();
        configurePopup();
    }

    /**
     * Triggered by the monitor to signal addition is directed to the summer-table.
     */
    public static void welcome(Course summerCourse){
        summerModel.addRow(new String[] {summerCourse.getCode(), summerCourse.getName(), summerCourse.getLecturer(),
                summerCourse.getGrade(), summerCourse.getYear()});
    }

    /**
     * Triggered by the monitor to signal removal is directed to summer-table.
     */
    public static void ridOf(Course summerCourse){
        summerModel.removeRow(summerModel.getRowOf(summerCourse.getCode()));
    }

    private void setupTable(){
        summerModel = new KTableModel();
        summerModel.setColumnIdentifiers(new String[] {"CODE", "NAME", "LECTURER", "GRADE", "YEAR"});
        ModulesHandler.ALL_MODELS.add(summerModel);

        summerTable = new KTable(summerModel);
        summerTable.setRowHeight(30);
        summerTable.setHeaderHeight(30);
        summerTable.setFont(KFontFactory.createPlainFont(15));
        summerTable.getTableHeader().setFont(KFontFactory.createBoldFont(16));
        summerTable.centerAlignAllColumns();
        summerTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    detailsItem.setEnabled(true);
                    editItem.setEnabled(true);
                    removeItem.setEnabled(true);
                    confirmItem.setEnabled(true);
                    summerTable.getSelectionModel().setSelectionInterval(0, summerTable.rowAtPoint(e.getPoint()));
                    popupMenu.show(summerTable, e.getX(), e.getY());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mousePressed(e);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    final int selectedRow = summerTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        Course.exhibit(ModulesHandler.getModuleByCode(String.valueOf(summerTable.getValueAt(selectedRow, 0))));
                    }
                    e.consume();
                }
            }
        });
    }

    private void configurePopup(){
        detailsItem = new KMenuItem(ModulesHandler.DETAILS);
        detailsItem.addActionListener(e-> SwingUtilities.invokeLater(()->
                Course.exhibit(ModulesHandler.getModuleByCode(String.valueOf(summerModel.getValueAt(summerTable.getSelectedRow(), 0))))));

        editItem = new KMenuItem(ModulesHandler.EDIT);
        editItem.addActionListener(e-> {
            final Course course = ModulesHandler.getModuleByCode(String.valueOf(summerModel.getValueAt(summerTable.getSelectedRow(), 0)));
            if (course != null) {
                final SummerModuleEditor editor = new SummerModuleEditor(course);
                SwingUtilities.invokeLater(()-> editor.setVisible(true));
            }
        });

        removeItem = new KMenuItem(ModulesHandler.DELETE);
        removeItem.addActionListener(e-> {
            final Course course = ModulesHandler.getModuleByCode(String.valueOf(summerModel.getValueAt(summerTable.getSelectedRow(),0)));
            if (course != null) {
                ModulesHandler.getModulesMonitor().remove(course);
            }
        });

        confirmItem = new KMenuItem(ModulesHandler.CONFIRM);
        confirmItem.addActionListener(e-> {
            final Course course = ModulesHandler.getModuleByCode(String.valueOf(summerModel.getValueAt(summerTable.getSelectedRow(),0)));
            if (course != null) {
                new Thread(()-> ModulesHandler.launchVerification(course)).start();
            }
        });

        newItem = new KMenuItem(ModulesHandler.ADD);
        newItem.addActionListener(e-> {
            final SummerModuleAdder adder = new SummerModuleAdder();
            SwingUtilities.invokeLater(()-> adder.setVisible(true));
        });

        popupMenu = new JPopupMenu();
        popupMenu.add(detailsItem);
        popupMenu.add(editItem);
        popupMenu.add(removeItem);
        popupMenu.add(confirmItem);
        popupMenu.add(newItem);
    }

    public KPanel getPresent() {
        final KScrollPane summerScrollPane = summerTable.sizeMatchingScrollPane();
        summerScrollPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    summerTable.clearSelection();
                    detailsItem.setEnabled(false);
                    editItem.setEnabled(false);
                    removeItem.setEnabled(false);
                    confirmItem.setEnabled(false);
                    popupMenu.show(summerScrollPane, e.getX(), e.getY());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mousePressed(e);
            }
        });

        final KPanel present = new KPanel();
        present.setLayout(new BoxLayout(present, BoxLayout.Y_AXIS));
        present.addAll(summerScrollPane, Box.createVerticalStrut(15));
        return present;
    }


    public static class SummerModuleAdder extends ModulesHandler.ModuleAdder {
        JComboBox<String> availableYearsBox;

        private SummerModuleAdder(){
            super(null, Student.SUMMER_SEMESTER);
            setTitle("New Summer Course");
            availableYearsBox = new JComboBox<>(new String[] {Student.firstAcademicYear(), Student.secondAcademicYear(),
                    Student.thirdAcademicYear(), Student.fourthAcademicYear()});
            availableYearsBox.setFont(KFontFactory.createPlainFont(15));
            yearPanel.removeLast();
            yearPanel.add(new KPanel(availableYearsBox), BorderLayout.CENTER);

            actionButton.removeActionListener(actionButton.getActionListeners()[0]);
            actionButton.addActionListener(additionListener());
        }

        private ActionListener additionListener() {
            return e-> {
                if (codeField.isBlank()) {
                    App.signalError(getRootPane(), "No Code","Please provide the code of the course.");
                    codeField.requestFocusInWindow();
                } else if (nameField.isBlank()) {
                    App.signalError(getRootPane(),"No Name","Please provide the name of the course.");
                    nameField.requestFocusInWindow();
                } else if (scoreField.isBlank()) {
                    App.signalError(getRootPane(),"Error","Please enter the score you get from this course.");
                    scoreField.requestFocusInWindow();
                } else {
                    final double score;
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

                    final Course course = new Course(String.valueOf(availableYearsBox.getSelectedItem()),
                            semesterField.getText(), codeField.getText(), nameField.getText(), lecturerField.getText(),
                            venueField.getText(), String.valueOf(dayBox.getSelectedItem()), String.valueOf(timeBox.getSelectedItem()),
                            score, Integer.parseInt(String.valueOf(creditBox.getSelectedItem())), String.valueOf(requirementBox.getSelectedItem()), false);
                    ModulesHandler.getModulesMonitor().add(course);
                    dispose();
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
            nameField.setText(target.getName());
            lecturerField.setText(target.getLecturer());
            lecturerField.setEditable(target.canEditTutorName());
            dayBox.setSelectedItem(target.getDay());
            timeBox.setSelectedItem(target.getTime());
            venueField.setText(target.getVenue());
            requirementBox.setSelectedItem(target.getRequirement());
            creditBox.setSelectedItem(target.getCreditHours());
            scoreField.setText(Double.toString(target.getScore()));

            if (summerCourse.isVerified()) {
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
                if (codeField.isBlank()) {
                    App.signalError(this.getRootPane(),"No Code","Please provide the code of the course.");
                    codeField.requestFocusInWindow();
                } else if (nameField.isBlank()) {
                    App.signalError(this.getRootPane(),"No Name","Please provide the name of the course.");
                    nameField.requestFocusInWindow();
                } else if (scoreField.isBlank()) {
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

                    final Course course = new Course(String.valueOf(availableYearsBox.getSelectedItem()),
                            semesterField.getText(), codeField.getText(), nameField.getText(), lecturerField.getText(),
                            venueField.getText(), String.valueOf(dayBox.getSelectedItem()), String.valueOf(timeBox.getSelectedItem()),
                            score, Integer.parseInt(String.valueOf(creditBox.getSelectedItem())), String.valueOf(requirementBox.getSelectedItem()), target.isVerified());
                    ModulesHandler.substitute(target, course);
                    dispose();
                }
            };
        }
    }

}
