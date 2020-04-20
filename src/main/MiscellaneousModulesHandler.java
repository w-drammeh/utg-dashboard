package main;

import customs.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.Objects;

public class MiscellaneousModulesHandler implements Serializable {
    private static KTable miscTable;
    private static KDefaultTableModel miscModel;
    private static JPopupMenu jPopupMenu;
    private static JMenuItem detailsItem, editItem, removeItem, confirmItem, newItem;


    public MiscellaneousModulesHandler(){
        generateMiscTable();
        configurePopUp();
    }

    /**
     * <p>Triggered by the list to signal addition is directed to miscellaneousTable.</p>
     */
    public static void welcome(Course summerCourse){
        miscModel.addRow(new String[] {summerCourse.getCode(),summerCourse.getName(),summerCourse.getLecturer(),summerCourse.getGrade(),summerCourse.getYear()});
    }

    /**
     * <p>Triggered by the list to signal removal is directed to miscellaneousTable.</p>
     */
    public static void ridOf(Course summerCourse){
        miscModel.removeRow(miscModel.getRowOf(summerCourse.getCode()));
    }

    private void generateMiscTable(){
        miscModel = new KDefaultTableModel();
        miscModel.setColumnIdentifiers(new String[] {"CODE","NAME","LECTURER","GRADE","YEAR"});
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

                    jPopupMenu.show(miscTable,e.getX(),e.getY());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    final boolean necessary = miscTable.getSelectedRow() >= 0;

                    detailsItem.setEnabled(necessary);
                    editItem.setEnabled(necessary);
                    removeItem.setEnabled(necessary);
                    confirmItem.setEnabled(necessary);
                    newItem.setEnabled(miscTable.getRowCount() <= 15);

                    jPopupMenu.show(miscTable,e.getX(),e.getY());
                }
            }
        });
    }

    private void configurePopUp(){
        detailsItem = new JMenuItem(ModulesHandler.detailsString);
        detailsItem.setFont(KFontFactory.createPlainFont(15));
        detailsItem.addActionListener(e -> {
            SwingUtilities.invokeLater(()->{
                try {
                    Course.exhibit(Board.getRoot(), Objects.requireNonNull(ModulesHandler.getModuleByCode(miscModel.getValueAt(miscTable.getSelectedRow(), 0) + "")));
                } catch (NullPointerException nil){
                    App.silenceException("No such course in list");
                }
            });
        });

        editItem = new JMenuItem(ModulesHandler.editString);
        editItem.setFont(KFontFactory.createPlainFont(15));
        editItem.addActionListener(e -> {
            final Course t = ModulesHandler.getModuleByCode(String.valueOf(miscModel.getValueAt(miscTable.getSelectedRow(), 0)));
            if (t != null) {
                SwingUtilities.invokeLater(()->{
                    new MiscModuleEditor(t).setVisible(true);
                });
            }
        });

        removeItem = new JMenuItem(ModulesHandler.deleteString);
        removeItem.setFont(KFontFactory.createPlainFont(15));
        removeItem.addActionListener(e -> {
            if (App.showYesNoCancelDialog("Confirm Removal","Are you sure you did not do "+miscModel.getValueAt(miscTable.getSelectedRow(),1)+",\nand that you wish to remove it from your summer collection?")) {
                ModulesHandler.getModulesMonitor().remove(ModulesHandler.getModuleByCode(miscModel.getValueAt(miscTable.getSelectedRow(),0).toString()));
            }
        });

        confirmItem = new JMenuItem(ModulesHandler.confirmString);
        confirmItem.setFont(KFontFactory.createPlainFont(15));
        confirmItem.addActionListener(e -> {
            final Course t = ModulesHandler.getModuleByCode(String.valueOf(miscModel.getValueAt(miscTable.getSelectedRow(),0)));
            if (t == null) {
                return;
            }
            new Thread(()-> {
                ModulesHandler.launchVerification(t);
            }).start();
        });

        newItem = new JMenuItem(ModulesHandler.addString);
        newItem.setFont(KFontFactory.createPlainFont(15));
        newItem.addActionListener(e -> {
            SwingUtilities.invokeLater(()->{
                new MiscModuleAdder().setVisible(true);
            });
        });

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

                    jPopupMenu.show(miscScrollPane,e.getX(),e.getY());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    final boolean necessary = miscTable.getSelectedRow() >= 0;

                    detailsItem.setEnabled(necessary);
                    editItem.setEnabled(necessary);
                    removeItem.setEnabled(necessary);
                    confirmItem.setEnabled(necessary);

                    jPopupMenu.show(miscScrollPane,e.getX(),e.getY());
                }
            }
        });

        final KPanel present = new KPanel();
        present.setLayout(new BoxLayout(present, BoxLayout.Y_AXIS));
        present.add(ComponentAssistant.provideBlankSpace(800,10));
        present.add(ComponentAssistant.provideBlankSpace(800,10));
        present.add(miscScrollPane);
        present.add(ComponentAssistant.provideBlankSpace(800,15));

        return present;
    }


    public static class MiscModuleAdder extends ModulesHandler.ModuleAdder {
        JComboBox<String> semestersBox;

        private MiscModuleAdder(){
            super(null,null);
            this.setTitle("New Miscellaneous Course");

            this.semestersBox = new JComboBox<>(new String[] {Student.FIRST_SEMESTER,Student.SECOND_SEMESTER,Student.SUMMER_SEMESTER});
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
                    App.signalError("Invalid Format","Sorry, "+yearField.getText()+" is not a valid academic year. Dashboard reads years in the format: yyyy/yyyy, e.g 2016/2017");
                    yearField.requestFocusInWindow();
                    return;
                }
                if (givenYear.equals(Student.firstAcademicYear())) {
                    App.signalError("Error","Sorry, the year you provided ["+givenYear+"] corresponds your first academic year.\nBy convention, Dashboard restricts the addition of specific resources to the miscellaneous table.\nIf you've done this module in your first year, add it in the appropriate place, else\nconsider changing the year.");
                    yearField.requestFocusInWindow();
                    return;
                } else if (givenYear.equals(Student.secondAcademicYear())) {
                    App.signalError("Error","Sorry, the year you provided ["+givenYear+"] corresponds your second academic year.\nBy convention, Dashboard restricts the addition of specific resources to the miscellaneous table.\nIf you've done this module in your second year, add it in the appropriate place, else\nconsider changing the year.");
                    yearField.requestFocusInWindow();
                    return;
                } else if (givenYear.equals(Student.thirdAcademicYear())) {
                    App.signalError("Error","Sorry, the year you provided ["+givenYear+"] corresponds your third academic year.\nBy convention, Dashboard restricts the addition of specific resources to the miscellaneous table.\nIf you've done this module in your third year, add it in the appropriate place, else\nconsider changing the year.");
                    yearField.requestFocusInWindow();
                    return;
                } else if (givenYear.equals(Student.finalAcademicYear())) {
                    App.signalError("Error","Sorry, the year you provided ["+givenYear+"] corresponds your fourth academic year.\nBy convention, Dashboard restricts the addition of specific resources to the miscellaneous table.\nIf you've done this module in your fourth year, add it in the appropriate place, else\nconsider changing the year.");
                    yearField.requestFocusInWindow();
                    return;
                }

                if (Globals.isBlank(codeField.getText())) {
                    App.signalError(MiscModuleAdder.this.getRootPane(), "No Code","Please provide the code of the course.");
                    codeField.requestFocusInWindow();
                } else if (Globals.isBlank(nameField.getText())) {
                    App.signalError(MiscModuleAdder.this.getRootPane(),"No Name","Please provide the name of the course.");
                    nameField.requestFocusInWindow();
                } else if (Globals.isBlank(scoreField.getText())) {
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
                    final Course course = new Course(givenYear,String.valueOf(semestersBox.getSelectedItem()),codeField.getText(),nameField.getText(),
                            lecturerField.getText(),venueField.getText(),day,time,score,Integer.parseInt(String.valueOf(creditBox.getSelectedItem())),
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
                    App.signalError("Invalid Format","Sorry, "+yearField.getText()+" is not a valid academic year. Dashboard reads years in the format: yyyy/yyyy, e.g 2016/2017");
                    yearField.requestFocusInWindow();
                    return;
                }
                if (givenYear.equals(Student.firstAcademicYear())) {
                    App.signalError("Error","Sorry, the year you provided ["+givenYear+"] corresponds your first academic year.\nBy convention, Dashboard restricts the addition of specific resources to the miscellaneous table.\nIf you've done this module in your first year, add it in the appropriate place, else\nconsider changing the year.");
                    yearField.requestFocusInWindow();
                    return;
                } else if (givenYear.equals(Student.secondAcademicYear())) {
                    App.signalError("Error","Sorry, the year you provided ["+givenYear+"] corresponds your second academic year.\nBy convention, Dashboard restricts the addition of specific resources to the miscellaneous table.\nIf you've done this module in your second year, add it in the appropriate place, else\nconsider changing the year.");
                    yearField.requestFocusInWindow();
                    return;
                } else if (givenYear.equals(Student.thirdAcademicYear())) {
                    App.signalError("Error","Sorry, the year you provided ["+givenYear+"] corresponds your third academic year.\nBy convention, Dashboard restricts the addition of specific resources to the miscellaneous table.\nIf you've done this module in your third year, add it in the appropriate place, else\nconsider changing the year.");
                    yearField.requestFocusInWindow();
                    return;
                } else if (givenYear.equals(Student.finalAcademicYear())) {
                    App.signalError("Error","Sorry, the year you provided ["+givenYear+"] corresponds your fourth academic year.\nBy convention, Dashboard restricts the addition of specific resources to the miscellaneous table.\nIf you've done this module in your fourth year, add it in the appropriate place, else\nconsider changing the year.");
                    yearField.requestFocusInWindow();
                    return;
                }

                if (Globals.isBlank(codeField.getText())) {
                    App.signalError(this.getRootPane(),"No Code","Please provide the code of the course.");
                    codeField.requestFocusInWindow();
                } else if (Globals.isBlank(nameField.getText())) {
                    App.signalError(this.getRootPane(),"No Name","Please provide the name of the course.");
                    nameField.requestFocusInWindow();
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
                    final Course course = new Course(yearField.getText(),String.valueOf(semestersBox.getSelectedItem()), codeField.getText(),nameField.getText(),
                            "",venueField.getText(),day,time,score,Integer.parseInt(String.valueOf(creditBox.getSelectedItem())),
                            String.valueOf(requirementBox.getSelectedItem()), target.isVerified());
                    course.setLecturer(lecturerField.getText(), lecturerField.isEditable());
                    ModulesHandler.substitute(target, course);
                    this.dispose();
                }
            };
        }
    }

}
