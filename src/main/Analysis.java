package main;

import customs.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Analysis implements Activity {
    private KLabel APlusLabel, ANeutralLabel, AMinusLabel, BPlusLabel, BNeutralLabel, BMinusLabel, CPlusLabel,
            CNeutralLabel, CMinusLabel, DLabel, FLabel;

    private KLabel highestScoreLabel, lowestScoreLabel, highestMajorScoreLabel, lowestMajorScoreLabel,
            highestMinorScoreLabel, lowestMinorScoreLabel, highestDERScoreLabel, lowestDERScoreLabel,
            highestGERScoreLabel, lowestGERScoreLabel;

    private KLabel majorsLabel, minorsLabel, DERsLabel, GERsLabel, unclassifiedListLabel;
    private KLabel allModulesLabel;

    private ArrayList<Course> APlusList, ANeutralList, AMinusList, BPlusList, BNeutralList, BMinusList,
            CPlusList, CNeutralList, CMinusList, DList, FList;

    private ArrayList<Course> majorsList, minorsList, DERsList, GERsList, unclassifiedList;

    private Course highestScoreCourse, lowestScoreCourse, highestMajorScoreCourse, lowestMajorScoreCourse,
            highestMinorScoreCourse, lowestMinorScoreCourse, highestDERScoreCourse, lowestDERScoreCourse,
            highestGERScoreCourse, lowestGERScoreCourse;

    private static ArrayList<String> semestersList, yearsList;
    private static ArrayList<Double> semesterScores;

    private CardLayout cardLayout;
    private KPanel modulesBasement, semestersBasement, yearsBasement;
    private static final Font HINT_FONT = KFontFactory.createBoldFont(16);
    private static final Font VALUE_FONT = KFontFactory.createPlainFont(15);
    private static final Font ON_FOCUS_FONT = KFontFactory.createPlainFont(17);
    private static final Cursor ON_FOCUS_CURSOR = MComponent.HAND_CURSOR;


    public Analysis(){
        cardLayout = new CardLayout();
        final KPanel analysisContents = new KPanel(cardLayout);
        cardLayout.addLayoutComponent(analysisContents.add(getModulesBasement()), "courses");
        cardLayout.addLayoutComponent(analysisContents.add(getSemestersBasement()), "semesters");
        cardLayout.addLayoutComponent(analysisContents.add(getYearsBasement()),"years");

        final KComboBox<String> optionsCombo = new KComboBox<>(new String[]{"My Courses", "Semesters", "Academic Years"});
        optionsCombo.setToolTipText("Shift Analysis");
        optionsCombo.addActionListener(e-> {
            if (optionsCombo.getSelectedIndex() == 0) {
                cardLayout.show(analysisContents, "courses");
            } else if (optionsCombo.getSelectedIndex() == 1) {
                cardLayout.show(analysisContents, "semesters");
            } else if (optionsCombo.getSelectedIndex() == 2) {
                cardLayout.show(analysisContents, "years");
            }
        });

        final KPanel sensitivePanel = new KPanel(new FlowLayout(FlowLayout.RIGHT));
        sensitivePanel.addAll(new KLabel("Showing analysis based on:", KFontFactory.createPlainFont(15)), optionsCombo);

        final KPanel northPanel = new KPanel(new BorderLayout());
        northPanel.add(new KPanel(new KLabel("Analysis Center", KFontFactory.bodyHeaderFont())), BorderLayout.WEST);
        northPanel.add(sensitivePanel, BorderLayout.EAST);

        final KPanel analysisUI = new KPanel(new BorderLayout());
        analysisUI.add(northPanel, BorderLayout.NORTH);
        analysisUI.add(analysisContents, BorderLayout.CENTER);

        Board.addCard(analysisUI, "Analysis");
    }

    @Override
    public void answerActivity() {
        SwingUtilities.invokeLater(()-> {
            resetLists();
            completeModulesBasement();
            completeSemestersBasement();
            completeYearsBasement();
            resetCourses();
            resetLabels();
        });
        Board.showCard("Analysis");
    }

    private JComponent getModulesBasement(){
//        by grade
        APlusLabel = newValueLabel();
        APlusLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!APlusLabel.getText().equals("None")) {
                    SwingUtilities.invokeLater(()-> new GlassPrompt("A+ Grades", APlusList).setVisible(true));
                }
            }
        });

        ANeutralLabel = newValueLabel();
        ANeutralLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!ANeutralLabel.getText().equals("None")) {
                    SwingUtilities.invokeLater(()-> new GlassPrompt("A Grades", ANeutralList).setVisible(true));
                }
            }
        });

        AMinusLabel = newValueLabel();
        AMinusLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!AMinusLabel.getText().equals("None")) {
                    SwingUtilities.invokeLater(()-> new GlassPrompt("A- Grades", AMinusList).setVisible(true));
                }
            }
        });

        BPlusLabel = newValueLabel();
        BPlusLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!BPlusLabel.getText().equals("None")) {
                    SwingUtilities.invokeLater(()-> new GlassPrompt("B+ Grades", BPlusList).setVisible(true));
                }
            }
        });

        BNeutralLabel = newValueLabel();
        BNeutralLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!BNeutralLabel.getText().equals("None")) {
                    SwingUtilities.invokeLater(()-> new GlassPrompt("B Grades", BNeutralList).setVisible(true));
                }
            }
        });

        BMinusLabel = newValueLabel();
        BMinusLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!BMinusLabel.getText().equals("None")) {
                    SwingUtilities.invokeLater(()-> new GlassPrompt("B- Grades", BMinusList).setVisible(true));
                }
            }
        });

        CPlusLabel = newValueLabel();
        CPlusLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!CPlusLabel.getText().equals("None")) {
                    SwingUtilities.invokeLater(()-> new GlassPrompt("C+ Grades", CPlusList).setVisible(true));
                }
            }
        });

        CNeutralLabel = newValueLabel();
        CNeutralLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!CNeutralLabel.getText().equals("None")) {
                    SwingUtilities.invokeLater(()-> new GlassPrompt("C Grades", CNeutralList).setVisible(true));
                }
            }
        });

        CMinusLabel = newValueLabel();
        CMinusLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!CMinusLabel.getText().equals("None")) {
                    SwingUtilities.invokeLater(()-> new GlassPrompt("C- Grades", CMinusList).setVisible(true));
                }
            }
        });

        DLabel = newValueLabel();
        DLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!DLabel.getText().equals("None")) {
                    SwingUtilities.invokeLater(()-> new GlassPrompt("D Grades", DList).setVisible(true));
                }
            }
        });

        FLabel = newValueLabel();
        FLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!FLabel.getText().equals("None")) {
                    SwingUtilities.invokeLater(()-> new GlassPrompt("F Grades", FList).setVisible(true));
                }
            }
        });

//        by score
        highestScoreLabel = newSingletonLabel();
        highestScoreLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!highestScoreLabel.getText().equals("...")) {
                    Course.exhibit(highestScoreCourse);
                }
            }
        });

        lowestScoreLabel = newSingletonLabel();
        lowestScoreLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!lowestScoreLabel.getText().equals("...")) {
                    Course.exhibit(lowestScoreCourse);
                }
            }
        });

        highestMajorScoreLabel = newSingletonLabel();
        highestMajorScoreLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!highestMajorScoreLabel.getText().equals("...")) {
                    Course.exhibit(highestMajorScoreCourse);
                }
            }
        });

        lowestMajorScoreLabel = newSingletonLabel();
        lowestMajorScoreLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!lowestMajorScoreLabel.getText().equals("...")) {
                    Course.exhibit(lowestMajorScoreCourse);
                }
            }
        });

        highestMinorScoreLabel = newSingletonLabel();
        highestMinorScoreLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!highestMinorScoreLabel.getText().equals("...")) {
                    Course.exhibit(highestMinorScoreCourse);
                }
            }
        });

        lowestMinorScoreLabel = newSingletonLabel();
        lowestMinorScoreLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!lowestMinorScoreLabel.getText().equals("...")) {
                    Course.exhibit(lowestMinorScoreCourse);
                }
            }
        });

        highestDERScoreLabel = newSingletonLabel();
        highestDERScoreLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!highestDERScoreLabel.getText().equals("...")) {
                    Course.exhibit(highestDERScoreCourse);
                }
            }
        });

        lowestDERScoreLabel = newSingletonLabel();
        lowestDERScoreLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!lowestDERScoreLabel.getText().equals("...")) {
                    Course.exhibit(lowestDERScoreCourse);
                }
            }
        });

        highestGERScoreLabel = newSingletonLabel();
        highestGERScoreLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!highestGERScoreLabel.getText().equals("...")) {
                    Course.exhibit(highestGERScoreCourse);
                }
            }
        });

        lowestGERScoreLabel = newSingletonLabel();
        lowestGERScoreLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!lowestGERScoreLabel.getText().equals("...")) {
                    Course.exhibit(lowestGERScoreCourse);
                }
            }
        });

//        by requirement
        majorsLabel = newValueLabel();
        majorsLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!majorsLabel.getText().equals("None")) {
                    SwingUtilities.invokeLater(()-> new GlassPrompt("My Majors", majorsList).setVisible(true));
                }
            }
        });

        minorsLabel = newValueLabel();
        minorsLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!minorsLabel.getText().equals("None")) {
                    SwingUtilities.invokeLater(()-> new GlassPrompt("My Minors", minorsList).setVisible(true));
                }
            }
        });

        DERsLabel = newValueLabel();
        DERsLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!DERsLabel.getText().equals("None")) {
                    SwingUtilities.invokeLater(()-> new GlassPrompt("Divisional Educational Requirements", DERsList).setVisible(true));
                }
            }
        });

        GERsLabel = newValueLabel();
        GERsLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!GERsLabel.getText().equals("None")) {
                    SwingUtilities.invokeLater(()-> new GlassPrompt("General Education Requirements", GERsList).setVisible(true));
                }
            }
        });

        unclassifiedListLabel = newValueLabel();
        unclassifiedListLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!unclassifiedListLabel.getText().equals("None")) {
                    SwingUtilities.invokeLater(()-> new GlassPrompt("Unknown Requirements", unclassifiedList).setVisible(true));
                }
            }
        });

        allModulesLabel = new KLabel("", VALUE_FONT, Color.BLUE);
        allModulesLabel.underline(true);
        allModulesLabel.setCursor(ON_FOCUS_CURSOR);
        allModulesLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SwingUtilities.invokeLater(()-> new GlassPrompt("My Modules", Memory.listRequested()).setVisible(true));
            }
        });

        modulesBasement = new KPanel();
        modulesBasement.setLayout(new BoxLayout(modulesBasement, BoxLayout.Y_AXIS));
        return new KScrollPane(modulesBasement);
    }

    private void completeModulesBasement(){
        MComponent.empty(modulesBasement);
        if (Memory.listRequested().isEmpty()) {
            modulesBasement.addAll(new KPanel(), createNoAnalysisPanel(), new KPanel());
        } else {
            modulesBasement.addAll(newAnalysisHeader("By Grade"),
                    newAnalysisPlate("A+", APlusLabel),
                    newAnalysisPlate("A", ANeutralLabel),
                    newAnalysisPlate("A-", AMinusLabel),
                    newAnalysisPlate("B+", BPlusLabel),
                    newAnalysisPlate("B", BNeutralLabel),
                    newAnalysisPlate("B-", BMinusLabel),
                    newAnalysisPlate("C+", CPlusLabel),
                    newAnalysisPlate("C", CNeutralLabel),
                    newAnalysisPlate("C-", CMinusLabel),
                    newAnalysisPlate("D", DLabel),
                    newAnalysisPlate("F", FLabel),
                    newAnalysisHeader("By Score"),
                    newAnalysisPlate("Best Score Overall", highestScoreLabel),
                    newAnalysisPlate("Worst Score Overall", lowestScoreLabel),
                    newAnalysisPlate("Best Major Score", highestMajorScoreLabel),
                    newAnalysisPlate("Worst Major Score", lowestMajorScoreLabel),
                    newAnalysisPlate("Best Minor Score", highestMinorScoreLabel),
                    newAnalysisPlate("Worst Minor Score", lowestMinorScoreLabel),
                    newAnalysisPlate("Best DER Score", highestDERScoreLabel),
                    newAnalysisPlate("Worst DER Score", lowestDERScoreLabel),
                    newAnalysisPlate("Best GER Score", highestGERScoreLabel),
                    newAnalysisPlate("Worst GER Score", lowestGERScoreLabel),
                    newAnalysisHeader("By Requirement"),
                    newAnalysisPlate("Majors", majorsLabel),
                    newAnalysisPlate("Minors", minorsLabel),
                    newAnalysisPlate("DERs", DERsLabel),
                    newAnalysisPlate("GERs", GERsLabel),
                    newAnalysisPlate("Unclassified", unclassifiedListLabel),
                    newAnalysisPlate("All together", allModulesLabel));
            allModulesLabel.setText(Globals.checkPlurality(Memory.listRequested().size(),"Courses")+" ["+
                    Globals.checkPlurality(majorsList.size(),"Majors")+", "+ Globals.checkPlurality(minorsList.size(),
                    "Minors")+", "+ Globals.checkPlurality(DERsList.size(),"DERs")+", "+ Globals.checkPlurality(GERsList.size(),
                    "GERs")+", and "+ Globals.checkPlurality(unclassifiedList.size(),"Un-classifications")+"]");
        }

        MComponent.ready(modulesBasement);
    }

    private JComponent getSemestersBasement(){
        semestersBasement = new KPanel();
        semestersBasement.setLayout(new BoxLayout(semestersBasement, BoxLayout.Y_AXIS));
        return new KScrollPane(semestersBasement);
    }

    private void completeSemestersBasement(){
        semesterScores = new ArrayList<>();
        MComponent.empty(semestersBasement);
        if (semestersList.isEmpty()) {
            semestersBasement.addAll(new KPanel(), createNoAnalysisPanel(), new KPanel());
        } else {
            for (String semTex : semestersList) {
                final ArrayList<Course> fractionalSem = Memory.getFractionBySemester(semTex);
                final KLabel promptLabel = new KLabel("", VALUE_FONT, Color.BLUE);
                promptLabel.setCursor(ON_FOCUS_CURSOR);
                promptLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        SwingUtilities.invokeLater(()-> new GlassPrompt(semTex, fractionalSem).setVisible(true));
                    }
                });
                if (fractionalSem.size() == 1) {
                    promptLabel.setText(fractionalSem.get(0).getAbsoluteName());
                } else {
                    final int i = fractionalSem.size();
                    promptLabel.setText(Globals.checkPlurality(i, "Courses")+" ["+
                            Globals.checkPlurality(Memory.getMajorsBySemester(semTex).size(),"Majors")+", "+
                            Globals.checkPlurality(Memory.getMinorsBySemester(semTex).size(),"Minors")+", "+
                            Globals.checkPlurality(Memory.getDERsBySemester(semTex).size(),"DERs")+", "+
                            Globals.checkPlurality(Memory.getGERsBySemester(semTex).size(),"GERs")+", and "+
                            Globals.checkPlurality(Memory.getUnknownsBySemester(semTex).size(),"Un-classifications")+"]");
                }
                attachActiveOnFocus(promptLabel, null);

                final KLabel CGLabel = new KLabel(Globals.toFourth(Memory.getCGPABySemester(semTex)), VALUE_FONT, Color.BLUE);
                semesterScores.add(Double.valueOf(CGLabel.getText()));

                semestersBasement.addAll(newAnalysisHeader(semTex), newAnalysisPlate("Courses Registered", promptLabel),
                        newAnalysisPlate("CGPA Earned", CGLabel));
            }

            final KLabel totalLabel = new KLabel(Globals.checkPlurality(semestersList.size(),"Semesters"), VALUE_FONT, Color.BLUE);
            totalLabel.underline(true);
            semestersBasement.addAll(newAnalysisHeader("Overall"),
                    newAnalysisPlate("All together", totalLabel),
                    newAnalysisPlate("Best Semester", new KLabel(Memory.traceBestSemester(true), VALUE_FONT, Color.BLUE)),
                    newAnalysisPlate("Worst Semester", new KLabel(Memory.traceWorstSemester(true), VALUE_FONT, Color.BLUE)),
                    newAnalysisHeader("Performance Sketch"), new RoughSketch());
        }

        MComponent.ready(semestersBasement);
    }

    private JComponent getYearsBasement(){
        yearsBasement = new KPanel();
        yearsBasement.setLayout(new BoxLayout(yearsBasement, BoxLayout.Y_AXIS));
        return new KScrollPane(yearsBasement);
    }

    private void completeYearsBasement(){
        MComponent.empty(yearsBasement);
        if (yearsList.isEmpty()) {
            yearsBasement.addAll(new KPanel(), createNoAnalysisPanel(), new KPanel());
        } else {
            for (String yearTex : yearsList) {
                final ArrayList<Course> fractionalYear = Memory.getFractionByYear(yearTex);
                final KLabel allPromptLabel = new KLabel("", VALUE_FONT, Color.BLUE);
                allPromptLabel.setCursor(ON_FOCUS_CURSOR);
                allPromptLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        SwingUtilities.invokeLater(()-> new GlassPrompt(yearTex, fractionalYear).setVisible(true));
                    }
                });
                final ArrayList<Course> yMajors = Memory.getMajorsByYear(yearTex), yMinors = Memory.getMinorsByYear(yearTex),
                        yDERs = Memory.getDERsByYear(yearTex), yGERs = Memory.getGERsByYear(yearTex), yNons = Memory.getUnknownsByYear(yearTex);
                if (fractionalYear.size() == 1) {
                    final Course onlyCourse = fractionalYear.get(0);
                    allPromptLabel.setText(onlyCourse.getAbsoluteName()+" ["+onlyCourse.getSemester()+"]");
                } else {
                    final int i = fractionalYear.size();
                    allPromptLabel.setText(Globals.checkPlurality(i, "Courses")+" ["+
                            Globals.checkPlurality(yMajors.size(), "Majors")+", "+
                            Globals.checkPlurality(yMinors.size(), "Minors")+", "+
                            Globals.checkPlurality(yDERs.size(), "DERs")+", "+
                            Globals.checkPlurality(yGERs.size(), "GERs")+", and "+
                            Globals.checkPlurality(yNons.size(), "Un-classifications")+"]");
                }
                attachActiveOnFocus(allPromptLabel, null);

                final ArrayList<String> yLectsList = Memory.getLecturersByYear(yearTex);
                final KLabel tutorsLabel = new KLabel(Globals.checkPlurality(yLectsList.size(),"Lecturers"), VALUE_FONT, Color.BLUE);
                tutorsLabel.setCursor(tutorsLabel.getText().equals("No Lecturers") ? null : ON_FOCUS_CURSOR);
                attachActiveOnFocus(tutorsLabel, "No Lecturers");
                tutorsLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (!tutorsLabel.getText().equals("No Lecturers")) {
                            SwingUtilities.invokeLater(()-> new GlassPrompt(yLectsList, yearTex).setVisible(true));
                        }
                    }
                });
                yearsBasement.addAll(newAnalysisHeader(yearTex),newAnalysisPlate("Courses Registered", allPromptLabel),
                        newAnalysisPlate("Majors", specificYearLabel("Majors", yMajors, yearTex)),
                        newAnalysisPlate("Minors", specificYearLabel("Minors", yMinors, yearTex)),
                        newAnalysisPlate("DERs", specificYearLabel("DERs", yDERs, yearTex)),
                        newAnalysisPlate("GERs", specificYearLabel("GERs", yGERs, yearTex)),
                        newAnalysisPlate("Lecturers", tutorsLabel),
                        newAnalysisPlate("CGPA Earned", new KLabel(Globals.toFourth(Memory.getCGPAByYear(yearTex)), VALUE_FONT, Color.BLUE)));
            }

            final ArrayList<String> lectsList = Memory.filterLecturers();
            final KLabel totalTutorsLabel = new KLabel(Globals.checkPlurality(lectsList.size(),"distinguished lecturers"), VALUE_FONT, Color.BLUE);
            totalTutorsLabel.setCursor(totalTutorsLabel.getText().equals("No distinguished lecturers") ? null :
                    Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            attachActiveOnFocus(totalTutorsLabel, "No distinguished lecturers");
            totalTutorsLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (!totalTutorsLabel.getText().equals("No distinguished lecturers")) {
                        SwingUtilities.invokeLater(()-> new GlassPrompt(lectsList).setVisible(true));
                    }
                }
            });

            final KLabel totalLabel = new KLabel(Globals.checkPlurality(yearsList.size(),"Academic years"), VALUE_FONT, Color.BLUE);
            totalLabel.underline(true);
            yearsBasement.addAll(newAnalysisHeader("Overall"),
                    newAnalysisPlate("All together", totalLabel),
                    newAnalysisPlate("My Tutors", totalTutorsLabel),
                    newAnalysisPlate("Best Year", new KLabel(Memory.traceBestYear(true), VALUE_FONT, Color.BLUE)),
                    newAnalysisPlate("Worst Year", new KLabel(Memory.traceWorstYear(true), VALUE_FONT, Color.BLUE)),
                    newAnalysisPlate("Current CGPA", new KLabel(Student.getCGPA()+" ["+Student.upperClassDivision()+"]", VALUE_FONT, Color.BLUE)));
        }

        MComponent.ready(yearsBasement);
    }

    private KLabel newValueLabel() {
        final KLabel label = new KLabel("", VALUE_FONT, Color.BLUE) {
            @Override
            public void setText(String text) {
                super.setText(text);
                setCursor(text.equals("None") ? null : ON_FOCUS_CURSOR);
            }
        };
        attachActiveOnFocus(label, "None");
        return label;
    }

    private KLabel specificYearLabel(String plText, ArrayList<Course> list, String specificName) {
        final KLabel kLabel = new KLabel(getProperValueText(list), VALUE_FONT, Color.BLUE);
        kLabel.setText(kLabel.getText().replace(specificName+" ", ""));
        kLabel.setCursor(kLabel.getText().equals("None") ? null : ON_FOCUS_CURSOR);
        kLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!kLabel.getText().equals("None")) {
                    SwingUtilities.invokeLater(()-> {
                        final GlassPrompt violatedTitledPrompt = new GlassPrompt(null, list);
                        violatedTitledPrompt.setTitle(specificName+" ["+plText+": "+list.size()+"]");
                        violatedTitledPrompt.setVisible(true);
                    });
                }
            }
        });
        attachActiveOnFocus(kLabel, "None");
        return kLabel;
    }

    private KPanel newAnalysisPlate(String hint, KLabel label){
        final KPanel panel = new KPanel(new FlowLayout(FlowLayout.LEFT));
        panel.addAll(new KPanel(new KLabel(hint, HINT_FONT)), Box.createHorizontalStrut(20), label);
        return panel;
    }

    private void attachActiveOnFocus(KLabel label, String except) {
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!label.getText().equals(except)) {
                    label.setFont(ON_FOCUS_FONT);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                label.setFont(VALUE_FONT);
            }
        });
    }

    private KLabel newSingletonLabel(){
        final KLabel label = new KLabel("", VALUE_FONT, Color.BLUE){
            @Override
            public void setText(String text) {
                super.setText(text);
                if (!text.equals("...")) {
                    super.setCursor(ON_FOCUS_CURSOR);
                }
            }
        };
        attachActiveOnFocus(label, "...");
        return label;
    }

    private KPanel newAnalysisHeader(String headerText){
        final KPanel hPanel = new KPanel();
        hPanel.add(new KLabel(headerText, KFontFactory.createBoldFont(18), Color.RED));
        return hPanel;
    }

    private void resetLists(){
        APlusList =  Memory.getFractionByGrade("A+");
        ANeutralList = Memory.getFractionByGrade("A");
        AMinusList = Memory.getFractionByGrade("A-");
        BPlusList = Memory.getFractionByGrade("B+");
        BNeutralList = Memory.getFractionByGrade("B");
        BMinusList = Memory.getFractionByGrade("B-");
        CPlusList = Memory.getFractionByGrade("C+");
        CNeutralList = Memory.getFractionByGrade("C");
        CMinusList = Memory.getFractionByGrade("C-");
        DList = Memory.getFractionByGrade("D");
        FList = Memory.getFractionByGrade("F");

        majorsList = Memory.getMajors();
        minorsList = Memory.getMinors();
        DERsList = Memory.getDERs();
        GERsList = Memory.getGERs();
        unclassifiedList = Memory.getUnknowns();

        semestersList = Memory.filterSemesters();
        yearsList = Memory.filterAcademicYears();
    }

    private void resetCourses(){
        highestScoreCourse = Memory.traceHighestScore_Overall();
        lowestScoreCourse = Memory.traceLowestScore_Overall();
        highestMajorScoreCourse = Memory.traceHighestScore_Major();
        lowestMajorScoreCourse = Memory.traceLowestScore_Major();
        highestMinorScoreCourse = Memory.traceHighestScore_Minor();
        lowestMinorScoreCourse = Memory.traceLowestScore_Minor();
        highestDERScoreCourse = Memory.traceHighestScore_DER();
        lowestDERScoreCourse = Memory.traceLowestScore_DER();
        highestGERScoreCourse = Memory.traceHighestScore_GER();
        lowestGERScoreCourse = Memory.traceLowestScore_GER();
    }

    private void resetLabels(){
        APlusLabel.setText(getProperValueText(APlusList));
        ANeutralLabel.setText(getProperValueText(ANeutralList));
        AMinusLabel.setText(getProperValueText(AMinusList));
        BPlusLabel.setText(getProperValueText(BPlusList));
        BNeutralLabel.setText(getProperValueText(BNeutralList));
        BMinusLabel.setText(getProperValueText(BMinusList));
        CPlusLabel.setText(getProperValueText(CPlusList));
        CNeutralLabel.setText(getProperValueText(CNeutralList));
        CMinusLabel.setText(getProperValueText(CMinusList));
        DLabel.setText(getProperValueText(DList));
        FLabel.setText(getProperValueText(FList));

//        Singletons...
        highestScoreLabel.setText(getProperValueText(highestScoreCourse));
        lowestScoreLabel.setText(getProperValueText(lowestScoreCourse));
        highestMajorScoreLabel.setText(getProperValueText(highestMajorScoreCourse));
        lowestMajorScoreLabel.setText(getProperValueText(lowestMajorScoreCourse));
        highestMinorScoreLabel.setText(getProperValueText(highestMinorScoreCourse));
        lowestMinorScoreLabel.setText(getProperValueText(lowestMinorScoreCourse));
        highestDERScoreLabel.setText(getProperValueText(highestDERScoreCourse));
        lowestDERScoreLabel.setText(getProperValueText(lowestDERScoreCourse));
        highestGERScoreLabel.setText(getProperValueText(highestGERScoreCourse));
        lowestGERScoreLabel.setText(getProperValueText(lowestGERScoreCourse));

        majorsLabel.setText(getProperValueText(majorsList));
        minorsLabel.setText(getProperValueText(minorsList));
        DERsLabel.setText(getProperValueText(DERsList));
        GERsLabel.setText(getProperValueText(GERsList));
        unclassifiedListLabel.setText(getProperValueText(unclassifiedList));
    }

    private String getProperValueText(ArrayList<Course> list){
        if (list.isEmpty()) {
            return "None";
        } else if (list.size() == 1) {
            return list.get(0).getName()+" ["+list.get(0).getAbsoluteSemesterName()+"]";
        } else if (list.size() == 2) {
            return list.get(0).getName() + " & " + list.get(1).getName();
        } else if (list.size() == 3) {
            return list.get(0).getName() + ", and 2 others...";
        } else {
            return list.get(0).getName()+", "+list.get(1).getName()+", and "+(list.size() - 2)+" others...";
        }
    }

    private String getProperValueText(Course course){
        return course == null ? "..." : course.getName()+" ["+course.getAbsoluteSemesterName()+"] : "+course.getScore()+"%";
    }

    /**
     * Created and added in place of the analysis-basements to signify that analysis is not available.
     * Note: Caller should not let this panel assume its entire size.
     * This can include an icon in a future release.
     */
    private KPanel createNoAnalysisPanel() {
        final KPanel panel = new KPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.addAll(new KPanel(new KLabel("Analysis is not available", KFontFactory.createPlainFont(20))),
                new KPanel(new KLabel("No Verified Course detected", KFontFactory.createPlainFont(15), Color.GRAY)));
        panel.setMaximumSize(panel.getPreferredSize());
        return panel;
    }


    /**
     * The responsibility of this inner-class is to prompt up the contents of an analysis-list.
     */
    private static class GlassPrompt extends KDialog {
        public KPanel substancePanel;

        private GlassPrompt(String title, ArrayList<Course> courseList, Component root) {
            super(title+" ["+ Globals.checkPlurality(courseList.size(),"Courses")+"]");
            setModalityType(KDialog.DEFAULT_MODALITY_TYPE);
            setResizable(true);

            substancePanel = new KPanel();
            substancePanel.setLayout(new BoxLayout(substancePanel, BoxLayout.Y_AXIS));
            for (Course tCourse : courseList) {
                join(tCourse);
            }

            final KScrollPane kScrollPane = new KScrollPane(substancePanel);
            if (courseList.size() > 15) {
                kScrollPane.setPreferredSize(new Dimension(kScrollPane.getPreferredSize().width + 25,525));
            }

            setContentPane(new KPanel(new BorderLayout(), kScrollPane));
            pack();
            setLocationRelativeTo(root == null ? Board.getRoot() : root);
        }

        private GlassPrompt(String title, ArrayList<Course> courseList){
            this(title, courseList, null);
        }

        private GlassPrompt(ArrayList<String> yTutorsList, String yName) {
            super(yName+" ["+yTutorsList.size()+" Lecturers]");
            setModalityType(KDialog.DEFAULT_MODALITY_TYPE);
            setResizable(true);

            substancePanel = new KPanel();
            substancePanel.setLayout(new BoxLayout(substancePanel, BoxLayout.Y_AXIS));
            for (String tName : yTutorsList) {
                join(tName, yName);
            }

            final KScrollPane kScrollPane = new KScrollPane(substancePanel);
            if (yTutorsList.size() > 15) {
                kScrollPane.setPreferredSize(new Dimension(kScrollPane.getPreferredSize().width + 25, 525));
            }

            setContentPane(new KPanel(new BorderLayout(), kScrollPane));
            pack();
            setLocationRelativeTo(Board.getRoot());
        }

        private GlassPrompt(ArrayList<String> lectsList) {
            super("My Tutors ["+lectsList.size()+"]");
            setModalityType(KDialog.DEFAULT_MODALITY_TYPE);
            setResizable(true);

            substancePanel = new KPanel();
            substancePanel.setLayout(new BoxLayout(substancePanel, BoxLayout.Y_AXIS));
            for (String lName : lectsList) {
                join(lName);
            }

            final KScrollPane kScrollPane = new KScrollPane(substancePanel);
            if (lectsList.size() > 15) {
                kScrollPane.setPreferredSize(new Dimension(kScrollPane.getPreferredSize().width + 25, 525));
            }

            setContentPane(new KPanel(new BorderLayout(), kScrollPane));
            pack();
            setLocationRelativeTo(Board.getRoot());
        }

        private void join(String lName) {
            final KButton dtlButton = KButton.getIconifiedButton("warn.png",25,25);
            dtlButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            dtlButton.addActionListener(e -> SwingUtilities.invokeLater(()->
                    new GlassPrompt(lName, Memory.getFractionByLecturer(lName), getRootPane()).setVisible(true)));

            final KPanel joinPanel = new KPanel(new BorderLayout());
            joinPanel.add(new KPanel(new KLabel(lName, KFontFactory.createPlainFont(15))), BorderLayout.WEST);
            joinPanel.add(Box.createRigidArea(new Dimension(30, 30)), BorderLayout.CENTER);
            joinPanel.add(dtlButton, BorderLayout.EAST);
            substancePanel.add(joinPanel);
        }

        private void join(String tName, String yName) {
            final KButton dtlButton = KButton.getIconifiedButton("warn.png",25,25);
            dtlButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            dtlButton.addActionListener(e-> SwingUtilities.invokeLater(()->{
                final ArrayList<Course> tList = Memory.getFractionByLecturer(tName,yName);
                final GlassPrompt vPrompt = new GlassPrompt("", tList, getRootPane());
                vPrompt.setTitle(tName+" ["+ yName +": "+tList.size()+"]");
                vPrompt.setVisible(true);
            }));

            final KPanel joinPanel = new KPanel(new BorderLayout());
            joinPanel.add(new KPanel(new KLabel(tName,KFontFactory.createPlainFont(15))), BorderLayout.WEST);
            joinPanel.add(Box.createRigidArea(new Dimension(30, 30)), BorderLayout.CENTER);
            joinPanel.add(dtlButton, BorderLayout.EAST);
            substancePanel.add(joinPanel);
        }

        public void join(Course c) {
            final KButton dtlButton = KButton.getIconifiedButton("warn.png",25,25);
            dtlButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            dtlButton.addActionListener(e-> Course.exhibit(getRootPane(), c));

            final KPanel joinPanel = new KPanel(new BorderLayout());
            joinPanel.add(new KPanel(new KLabel(c.getName(), KFontFactory.createPlainFont(15))), BorderLayout.WEST);
            joinPanel.add(dtlButton, BorderLayout.EAST);
            substancePanel.add(joinPanel);
        }
    }


    private static class RoughSketch extends KPanel {
        private static final int PADDING = 50;
        private static final int POINTS_WIDTH = 10;
        private static final int Y_COUNT = 20;
        private static final double Y_MAX = 4.3;

        private RoughSketch(){
            if (semestersList.size() <= 1) {
                setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
                addAll(new KPanel(), new KPanel(new KLabel("No Sketch Available", KFontFactory.createPlainFont(20))),
                        new KPanel(new KLabel("Student must complete at least two semesters", KFontFactory.createPlainFont(15), Color.GRAY)),
                        new KPanel());
                setMaximumSize(this.getPreferredSize());
            } else {
                setPreferredSize(new Dimension(900, 475));
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (semestersList.size() > 1) {
                final Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                final double xScale = ((double) (getWidth() - (2 * PADDING)) / (semesterScores.size() - 1));
                final double yScale = ((double) (getHeight() - (2 * PADDING)) / (Y_MAX - 1));

                final ArrayList<Point> points = new ArrayList<>();
                for (int i = 0; i < semesterScores.size(); i++) {
                    int x1 = (int) (i * xScale + PADDING);
                    int y1 = (int) ((Y_MAX - semesterScores.get(i)) * yScale + PADDING);
                    points.add(new Point(x1, y1));
                }

                g2.drawLine(PADDING, getHeight() - PADDING, getWidth() - PADDING, getHeight() - PADDING);
                g2.drawLine(PADDING, getHeight() - PADDING, PADDING, PADDING);

                g2.setFont(KFontFactory.createPlainFont(11));
                for (int i = 0; i < semesterScores.size() - 1; i++) {
                    int x0 = (i + 1) * (getWidth() - (PADDING * 2)) / (semesterScores.size() - 1) + PADDING;
                    int x1 = x0;
                    int y0 = this.getHeight() - PADDING;
                    int y1 = y0 - POINTS_WIDTH;
                    g2.drawLine(x0, y0, x1, y1);

                    final String[] nameParts = semestersList.get(i + 1).split(" ");
                    g2.drawString(nameParts[0], x0 - 40, y0 + 20);
                    g2.drawString(nameParts[1] + " " + nameParts[2], x0 - 55, y0 + 35);
                }

                for (int i = 0; i < Y_COUNT; i++) {
                    int x0 = PADDING;
                    int x1 = POINTS_WIDTH + PADDING;
                    int y0 = getHeight() - (((i + 1) * (getHeight() - PADDING * 2)) / Y_COUNT + PADDING);
                    int y1 = y0;
                    g2.drawLine(x0, y0, x1, y1);

                    if (i == Y_COUNT - 1) {
                        g2.drawString("4.3",25,y0 + 5);
                    }
                }

                g2.setStroke(new BasicStroke(3F));

                for (int i = 0; i < points.size() - 1; i++) {
                    int x1 = points.get(i).x;
                    int y1 = points.get(i).y;
                    int x2 = points.get(i + 1).x;
                    int y2 = points.get(i + 1).y;
                    g2.drawLine(x1, y1, x2, y2);
                }

                final Stroke oldStroke = g2.getStroke();
                g2.setStroke(oldStroke);
                g2.setColor(Color.BLUE);

                for (int i = 0; i < points.size(); i++) {
                    int x = points.get(i).x - POINTS_WIDTH / 2;
                    int y = points.get(i).y - POINTS_WIDTH / 2;
                    int ovalW = POINTS_WIDTH;
                    int ovalH = POINTS_WIDTH;
                    g2.fillOval(x, y, ovalW, ovalH);
                    g2.drawString(semesterScores.get(i)+"",x - 10,y - 10);
                }
            }
        }
    }

}
