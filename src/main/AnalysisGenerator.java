package main;

import customs.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class AnalysisGenerator implements ActivityAnswerer {
    private KLabel aPlusTellerLabel, aNeutralTellerLabel, aMinusTellerLabel, bPlusTellerLabel, bNeutralTellerLabel, bMinusTellerLabel,
            cPlusTellerLabel, cNeutralTellerLabel, cMinusTellerLabel, dTellerLabel, fTellerLabel;
    private KLabel highestScoreTellerLabel, lowestScoreTellerLabel, highestMajorScoreTellerLabel, lowestMajorScoreTellerLabel,
            highestMinorScoreTellerLabel, lowestMinorScoreTellerLabel, highestDERScoreTellerLabel, lowestDERScoreTellerLabel,
            highestGERScoreTellerLabel, lowestGERScoreTellerLabel;
    private KLabel majorListTellerLabel, minorListTellerLabel, DERListTellerLabel, GERListTellerLabel, unclassifiedListTellerLabel;
    private KLabel allTellerLabel;
    private ArrayList<Course> aPlusList, aNeutralList, aMinusList, bPlusList, bNeutralList, bMinusList,
            cPlusList, cNeutralList, cMinusList, dList, fList;
    private ArrayList<Course> majorList, minorList, DERList, GERList, unclassifiedList;
    private Course highestScoreCourse, lowestScoreCourse, highestMajorScoreCourse, lowestMajorScoreCourse,
            highestMinorScoreCourse, lowestMinorScoreCourse,
            highestDERScoreCourse, lowestDERScoreCourse, highestGERScoreCourse, lowestGERScoreCourse;
    private CardLayout cardLayout;
    private KPanel coursesBased, semestersBased, yearsBased;
    private static final Font VALUE_FONT = KFontFactory.createPlainFont(15);
    private static final Font HINT_FONT = KFontFactory.createBoldFont(15);
    private static final Font ON_FOCUS_FONT = KFontFactory.createBoldFont(16);
    private static final Cursor ON_FOCUS_CURSOR = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    private static ArrayList<String> semestersList, yearsList;
    private static ArrayList<Double> semesterScores;//as for the names, the 'semestersList' is enough - it's being static for such reason?


    public AnalysisGenerator(){
        final KPanel analysisContents = new KPanel(cardLayout = new CardLayout());
        cardLayout.addLayoutComponent(analysisContents.add(new KScrollPane(coursesAnalysisBasement(),false)), "course-basement");
        cardLayout.addLayoutComponent(analysisContents.add(new KScrollPane(initiateSemestersAnalysisBasement(),false)), "semester-basement");
        cardLayout.addLayoutComponent(analysisContents.add(new KScrollPane(initiateYearlyAnalysisBasement(),false)),"year-basement");

        final JComboBox<String> optionsCombo = new JComboBox<String>(new String[] {"My Courses", "Semesters", "Academic Years"}){
            @Override
            public JToolTip createToolTip() {
                return KLabel.preferredTip();
            }
        };
        optionsCombo.setFont(KFontFactory.createPlainFont(15));
        optionsCombo.setFocusable(false);
        optionsCombo.setToolTipText("Shift analysis type");
        optionsCombo.setCursor(ON_FOCUS_CURSOR);
        optionsCombo.addActionListener(e -> {
            if (optionsCombo.getSelectedIndex() == 0) {
                cardLayout.show(analysisContents, "course-basement");
            } else if (optionsCombo.getSelectedIndex() == 1) {
                cardLayout.show(analysisContents, "semester-basement");
            } else if (optionsCombo.getSelectedIndex() == 2) {
                cardLayout.show(analysisContents, "year-basement");
            }
        });

        final KPanel sensitivePanel = new KPanel(new FlowLayout(FlowLayout.RIGHT));
        sensitivePanel.addAll(new KLabel("Showing analysis based on:", KFontFactory.createPlainFont(15)), optionsCombo);

        final KPanel northPanel = new KPanel(new BorderLayout());
        northPanel.add(new KLabel("Analysis Center", KFontFactory.bodyHeaderFont()), BorderLayout.WEST);
        northPanel.add(sensitivePanel, BorderLayout.EAST);

        final KPanel analysisUI = new KPanel(new BorderLayout());
        analysisUI.add(northPanel, BorderLayout.NORTH);
        analysisUI.add(analysisContents, BorderLayout.CENTER);

        Board.addCard(analysisUI, "Analysis");
    }

    private JComponent coursesAnalysisBasement(){
        aPlusTellerLabel = newValueLabel();
        aPlusTellerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!aPlusTellerLabel.getText().equals("None")) {
                    SwingUtilities.invokeLater(()-> new GlassPrompt("A+ Grades", aPlusList).setVisible(true));
                }
            }
        });

        aNeutralTellerLabel = newValueLabel();
        aNeutralTellerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!aNeutralTellerLabel.getText().equals("None")) {
                    SwingUtilities.invokeLater(()-> new GlassPrompt("A Grades",aNeutralList).setVisible(true));
                }
            }
        });

        aMinusTellerLabel = newValueLabel();
        aMinusTellerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!aMinusTellerLabel.getText().equals("None")) {
                    SwingUtilities.invokeLater(()-> new GlassPrompt("A- Grades",aMinusList).setVisible(true));
                }
            }
        });

        bPlusTellerLabel = newValueLabel();
        bPlusTellerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!bPlusTellerLabel.getText().equals("None")) {
                    SwingUtilities.invokeLater(()-> new GlassPrompt("B+ Grades",bPlusList).setVisible(true));
                }
            }
        });

        bNeutralTellerLabel = newValueLabel();
        bNeutralTellerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!bNeutralTellerLabel.getText().equals("None")) {
                    SwingUtilities.invokeLater(()-> new GlassPrompt("B Grades",bNeutralList).setVisible(true));
                }
            }
        });

        bMinusTellerLabel = newValueLabel();
        bMinusTellerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!bMinusTellerLabel.getText().equals("None")) {
                    SwingUtilities.invokeLater(()-> new GlassPrompt("B- Grades",bMinusList).setVisible(true));
                }
            }
        });

        cPlusTellerLabel = newValueLabel();
        cPlusTellerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!cPlusTellerLabel.getText().equals("None")) {
                    SwingUtilities.invokeLater(()-> new GlassPrompt("C+ Grades",cPlusList).setVisible(true));
                }
            }
        });

        cNeutralTellerLabel = newValueLabel();
        cNeutralTellerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!cNeutralTellerLabel.getText().equals("None")) {
                    SwingUtilities.invokeLater(()-> new GlassPrompt("C Grades",cNeutralList).setVisible(true));
                }
            }
        });

        cMinusTellerLabel = newValueLabel();
        cMinusTellerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!cMinusTellerLabel.getText().equals("None")) {
                    SwingUtilities.invokeLater(()-> new GlassPrompt("C- Grades",cMinusList).setVisible(true));
                }
            }
        });

        dTellerLabel = newValueLabel();
        dTellerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!dTellerLabel.getText().equals("None")) {
                    SwingUtilities.invokeLater(()-> new GlassPrompt("D Grades",dList).setVisible(true));
                }
            }
        });

        fTellerLabel = newValueLabel();
        fTellerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!fTellerLabel.getText().equals("None")) {
                    SwingUtilities.invokeLater(()-> new GlassPrompt("F Grades",fList).setVisible(true));
                }
            }
        });

        highestScoreTellerLabel = newSingletonLabel();
        highestScoreTellerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!highestScoreTellerLabel.getText().equals("...")) {
                    SwingUtilities.invokeLater(()-> Course.exhibit(Board.getRoot(), highestScoreCourse));
                }
            }
        });

        lowestScoreTellerLabel = newSingletonLabel();
        lowestScoreTellerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!lowestScoreTellerLabel.getText().equals("...")) {
                    SwingUtilities.invokeLater(()-> Course.exhibit(Board.getRoot(),lowestScoreCourse));
                }
            }
        });

        highestMajorScoreTellerLabel = newSingletonLabel();
        highestMajorScoreTellerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!highestMajorScoreTellerLabel.getText().equals("...")) {
                    SwingUtilities.invokeLater(()-> Course.exhibit(Board.getRoot(),highestMajorScoreCourse));
                }
            }
        });

        lowestMajorScoreTellerLabel = newSingletonLabel();
        lowestMajorScoreTellerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!lowestMajorScoreTellerLabel.getText().equals("...")) {
                    SwingUtilities.invokeLater(()-> Course.exhibit(Board.getRoot(),lowestMajorScoreCourse));
                }
            }
        });

        highestMinorScoreTellerLabel = newSingletonLabel();
        highestMinorScoreTellerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!highestMinorScoreTellerLabel.getText().equals("...")) {
                    SwingUtilities.invokeLater(()-> Course.exhibit(Board.getRoot(),highestMinorScoreCourse));
                }
            }
        });

        lowestMinorScoreTellerLabel = newSingletonLabel();
        lowestMinorScoreTellerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!lowestMinorScoreTellerLabel.getText().equals("...")) {
                    SwingUtilities.invokeLater(()-> Course.exhibit(Board.getRoot(),lowestMinorScoreCourse));
                }
            }
        });

        highestDERScoreTellerLabel = newSingletonLabel();
        highestDERScoreTellerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!highestDERScoreTellerLabel.getText().equals("...")) {
                    SwingUtilities.invokeLater(()-> Course.exhibit(Board.getRoot(),highestDERScoreCourse));
                }
            }
        });

        lowestDERScoreTellerLabel = newSingletonLabel();
        lowestDERScoreTellerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!lowestDERScoreTellerLabel.getText().equals("...")) {
                    SwingUtilities.invokeLater(()-> Course.exhibit(Board.getRoot(),lowestDERScoreCourse));
                }
            }
        });

        highestGERScoreTellerLabel = newSingletonLabel();
        highestGERScoreTellerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!highestGERScoreTellerLabel.getText().equals("...")) {
                    SwingUtilities.invokeLater(()-> Course.exhibit(Board.getRoot(),highestGERScoreCourse));
                }
            }
        });

        lowestGERScoreTellerLabel = newSingletonLabel();
        lowestGERScoreTellerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!lowestGERScoreTellerLabel.getText().equals("...")) {
                    SwingUtilities.invokeLater(()-> Course.exhibit(Board.getRoot(),lowestGERScoreCourse));
                }
            }
        });

        majorListTellerLabel = newValueLabel();
        majorListTellerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!majorListTellerLabel.getText().equals("None")) {
                    SwingUtilities.invokeLater(()-> new GlassPrompt("My Majors",majorList).setVisible(true));
                }
            }
        });

        minorListTellerLabel = newValueLabel();
        minorListTellerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!minorListTellerLabel.getText().equals("None")) {
                    SwingUtilities.invokeLater(()-> new GlassPrompt("My Minors",minorList).setVisible(true));
                }
            }
        });

        DERListTellerLabel = newValueLabel();
        DERListTellerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!DERListTellerLabel.getText().equals("None")) {
                    SwingUtilities.invokeLater(()-> new GlassPrompt("Divisional Educational Requirements", DERList).setVisible(true));
                }
            }
        });

        GERListTellerLabel = newValueLabel();
        GERListTellerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!GERListTellerLabel.getText().equals("None")) {
                    SwingUtilities.invokeLater(()-> new GlassPrompt("General Education Requirements", GERList).setVisible(true));
                }
            }
        });

        unclassifiedListTellerLabel = newValueLabel();
        unclassifiedListTellerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!unclassifiedListTellerLabel.getText().equals("None")) {
                    SwingUtilities.invokeLater(()-> new GlassPrompt("Unknown Requirements", unclassifiedList).setVisible(true));
                }
            }
        });

        allTellerLabel = new KLabel("", VALUE_FONT, Color.BLUE);
        allTellerLabel.underline(true);
        allTellerLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        allTellerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SwingUtilities.invokeLater(()-> new GlassPrompt("My Modules",Memory.listRequested()).setVisible(true));
            }
        });

        coursesBased = new KPanel();
        coursesBased.setLayout(new BoxLayout(coursesBased, BoxLayout.Y_AXIS));
        return coursesBased;
    }

    private void finishModulesAnalysisBasement(){
        ComponentAssistant.empty(coursesBased);
        if (Memory.listRequested().isEmpty()) {
            coursesBased.addAll(new KPanel(), createNoAnalysisPanel(), new KPanel());
        } else {
            allTellerLabel.setText(Globals.checkPlurality(Memory.listRequested().size(),"Courses")+" ["+
                    Globals.checkPlurality(majorList.size(),"Majors")+", "+ Globals.checkPlurality(minorList.size(),
                    "Minors")+", "+ Globals.checkPlurality(DERList.size(),"DERs")+", "+ Globals.checkPlurality(GERList.size(),
                    "GERs")+", and "+ Globals.checkPlurality(unclassifiedList.size(),"Un-classifications")+"]");
            coursesBased.addAll(newAnalysisHeader("By Grade"),
                    newAnalysisPlate("A+",aPlusTellerLabel),
                    newAnalysisPlate("A",aNeutralTellerLabel),
                    newAnalysisPlate("A-",aMinusTellerLabel),
                    newAnalysisPlate("B+",bPlusTellerLabel),
                    newAnalysisPlate("B",bNeutralTellerLabel),
                    newAnalysisPlate("B-",bMinusTellerLabel),
                    newAnalysisPlate("C+",cPlusTellerLabel),
                    newAnalysisPlate("C",cNeutralTellerLabel),
                    newAnalysisPlate("C-",cMinusTellerLabel),
                    newAnalysisPlate("D",dTellerLabel),
                    newAnalysisPlate("F",fTellerLabel),
                    newAnalysisHeader("By Score"),
                    newAnalysisPlate("Best Score Overall",highestScoreTellerLabel),
                    newAnalysisPlate("Worst Score Overall",lowestScoreTellerLabel),
                    newAnalysisPlate("Best Major Score",highestMajorScoreTellerLabel),
                    newAnalysisPlate("Worst Major Score",lowestMajorScoreTellerLabel),
                    newAnalysisPlate("Best Minor Score",highestMinorScoreTellerLabel),
                    newAnalysisPlate("Worst Minor Score",lowestMinorScoreTellerLabel),
                    newAnalysisPlate("Best DER Score",highestDERScoreTellerLabel),
                    newAnalysisPlate("Worst DER Score",lowestDERScoreTellerLabel),
                    newAnalysisPlate("Best GER Score",highestGERScoreTellerLabel),
                    newAnalysisPlate("Worst GER Score",lowestGERScoreTellerLabel),
                    newAnalysisHeader("By List / Classification"),
                    newAnalysisPlate("Majors",majorListTellerLabel),
                    newAnalysisPlate("Minors",minorListTellerLabel),
                    newAnalysisPlate("DERs",DERListTellerLabel),
                    newAnalysisPlate("GERs",GERListTellerLabel),
                    newAnalysisPlate("Unclassified",unclassifiedListTellerLabel),
                    newAnalysisPlate("All together",allTellerLabel));
        }
        ComponentAssistant.ready(coursesBased);
    }

    private JComponent initiateSemestersAnalysisBasement(){
        semestersBased = new KPanel();
        semestersBased.setLayout(new BoxLayout(semestersBased, BoxLayout.Y_AXIS));
        return semestersBased;
    }

    private void finishSemestersAnalysisBasement(){
        semesterScores = new ArrayList<>();
        ComponentAssistant.empty(semestersBased);
        if (semestersList.isEmpty()) {
            semestersBased.addAll(new KPanel(), createNoAnalysisPanel(), new KPanel());
        } else {
            for (String semTex : semestersList) {
                final ArrayList<Course> fractionalSem = Memory.getFractionBySemester(semTex);
                final KLabel promptLabel = new KLabel("", VALUE_FONT, Color.BLUE);
                promptLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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
                    promptLabel.setText(Globals.checkPlurality(i,"Courses")+" ["+
                            Globals.checkPlurality(Memory.getMajorsBySemester(semTex).size(),"Majors")+", "+
                            Globals.checkPlurality(Memory.getMinorsBySemester(semTex).size(),"Minors")+", "+
                            Globals.checkPlurality(Memory.getDERsBySemester(semTex).size(),"DERs")+", "+
                            Globals.checkPlurality(Memory.getGERsBySemester(semTex).size(),"GERs")+", and "+
                            Globals.checkPlurality(Memory.getUnknownsBySemester(semTex).size(),"Un-classifications")+"]");
                }
                attachActiveOnFocus(promptLabel, null);

                final KLabel cgTeller = new KLabel(Globals.toFourth(Memory.getCGPABySemester(semTex)), VALUE_FONT,Color.BLUE);
                semesterScores.add(Double.valueOf(cgTeller.getText()));

                semestersBased.addAll(newAnalysisHeader(semTex),newAnalysisPlate("Courses Registered",promptLabel),
                        newAnalysisPlate("CGPA Earned",cgTeller));
            }
            final KLabel totalLabel = new KLabel(Globals.checkPlurality(semestersList.size(),"Semesters"), VALUE_FONT, Color.BLUE);
            totalLabel.underline(true);
            semestersBased.addAll(newAnalysisHeader("Overall"),
                    newAnalysisPlate("All together",totalLabel),
                    newAnalysisPlate("Best Semester",new KLabel(Memory.traceBestSemester(true), VALUE_FONT, Color.BLUE)),
                    newAnalysisPlate("Worst Semester",new KLabel(Memory.traceWorstSemester(true), VALUE_FONT, Color.BLUE)),
                    newAnalysisHeader("Performance Sketch"),new RoughSketch());
        }
        ComponentAssistant.ready(semestersBased);
    }

    private JComponent initiateYearlyAnalysisBasement(){
        yearsBased = new KPanel();
        yearsBased.setLayout(new BoxLayout(yearsBased, BoxLayout.Y_AXIS));
        return yearsBased;
    }

    private void finishYearsAnalysisBasement(){
        ComponentAssistant.empty(yearsBased);
        if (yearsList.isEmpty()) {
            yearsBased.addAll(new KPanel(), createNoAnalysisPanel(), new KPanel());
        } else {
            for (String yearTex : yearsList) {
                final ArrayList<Course> fractionalYear = Memory.getFractionByYear(yearTex);
                final KLabel allPromptLabel = new KLabel("", VALUE_FONT, Color.BLUE);
                allPromptLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                allPromptLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        SwingUtilities.invokeLater(()-> new GlassPrompt(yearTex,fractionalYear).setVisible(true));
                    }
                });
                final ArrayList<Course> yMajors = Memory.getMajorsByYear(yearTex), yMinors = Memory.getMinorsByYear(yearTex),
                        yDERs = Memory.getDERsByYear(yearTex), yGERs = Memory.getGERsByYear(yearTex), yNons = Memory.getUnknownsByYear(yearTex);
                if (fractionalYear.size() == 1) {
                    final Course onlyCourse = fractionalYear.get(0);
                    allPromptLabel.setText(onlyCourse.getAbsoluteName()+" ["+onlyCourse.getSemester()+"]");
                } else {
                    final int i = fractionalYear.size();
                    allPromptLabel.setText(Globals.checkPlurality(i,"Courses")+" ["+
                            Globals.checkPlurality(yMajors.size(),"Majors")+", "+
                            Globals.checkPlurality(yMinors.size(),"Minors")+", "+
                            Globals.checkPlurality(yDERs.size(),"DERs")+", "+
                            Globals.checkPlurality(yGERs.size(),"GERs")+", and "+
                            Globals.checkPlurality(yNons.size(),"Un-classifications")+"]");
                }
                attachActiveOnFocus(allPromptLabel, null);

                final ArrayList<String> yLectsList = Memory.getLecturersByYear(yearTex);
                final KLabel tutorsLabel = new KLabel(Globals.checkPlurality(yLectsList.size(),"Lecturers"), VALUE_FONT, Color.BLUE);
                tutorsLabel.setCursor(tutorsLabel.getText().equals("No Lecturers") ? null : Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                attachActiveOnFocus(tutorsLabel, "No Lecturers");
                tutorsLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (!tutorsLabel.getText().equals("No Lecturers")) {
                            SwingUtilities.invokeLater(()-> new GlassPrompt(yLectsList, yearTex).setVisible(true));
                        }
                    }
                });
                yearsBased.addAll(newAnalysisHeader(yearTex),newAnalysisPlate("Courses Registered",allPromptLabel),
                        newAnalysisPlate("Majors",specificYearLabel("Majors",yMajors,yearTex)),
                        newAnalysisPlate("Minors",specificYearLabel("Minors",yMinors,yearTex)),
                        newAnalysisPlate("DERs",specificYearLabel("DERs",yDERs,yearTex)),
                        newAnalysisPlate("GERs",specificYearLabel("GERs",yGERs,yearTex)),
                        newAnalysisPlate("Lecturers",tutorsLabel),
                        newAnalysisPlate("CGPA Earned",new KLabel(Globals.toFourth(Memory.getCGPAByYear(yearTex)), VALUE_FONT, Color.BLUE)));
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
            yearsBased.addAll(newAnalysisHeader("Overall"),
                    newAnalysisPlate("All together",totalLabel),
                    newAnalysisPlate("My Tutors",totalTutorsLabel),
                    newAnalysisPlate("Best Year",new KLabel(Memory.traceBestYear(true), VALUE_FONT, Color.BLUE)),
                    newAnalysisPlate("Worst Year",new KLabel(Memory.traceWorstYear(true), VALUE_FONT, Color.BLUE)),
                    newAnalysisPlate("Current CGPA",new KLabel(Student.getCGPA()+" ["+Student.upperDivision()+"]", VALUE_FONT, Color.BLUE)));
        }
        ComponentAssistant.ready(yearsBased);
    }

    /**
     * Created and added in place of the analysis-basements to signify that analysis is not available.
     * Note: Caller should not let this panel assume its entire size.
     * This can include an icon in a future release.
     */
    private KPanel createNoAnalysisPanel(){
        final KPanel noAnalysisPanel = new KPanel();
        noAnalysisPanel.setLayout(new BoxLayout(noAnalysisPanel, BoxLayout.Y_AXIS));
        noAnalysisPanel.addAll(new KPanel(new KLabel("Analysis is not available", KFontFactory.createPlainFont(20))),
                new KPanel(new KLabel("No Verified Course detected", KFontFactory.createPlainFont(15), Color.GRAY)));
        noAnalysisPanel.setMaximumSize(noAnalysisPanel.getPreferredSize());
        return noAnalysisPanel;
    }

    private KLabel specificYearLabel(String plText, ArrayList<Course> list, String specificName){
        final KLabel kLabel = new KLabel(getProperValueText(list), VALUE_FONT, Color.BLUE);
        kLabel.setCursor(kLabel.getText().equals("None") ? null : Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        kLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!kLabel.getText().equals("None")) {
                    SwingUtilities.invokeLater(()->{
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

    private KPanel newAnalysisPlate(String hint, KLabel aLabel){
        final KPanel aPanel = new KPanel(new FlowLayout(FlowLayout.LEFT));
        aPanel.add(new KPanel(new KLabel(hint, HINT_FONT)));
        aPanel.addAll(Box.createHorizontalStrut(25), aLabel);
        return aPanel;
    }

    private void attachActiveOnFocus(KLabel vLabel, String except){
        vLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                vLabel.setFont(vLabel.getText().equals(except) ? VALUE_FONT : ON_FOCUS_FONT);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                vLabel.setFont(VALUE_FONT);
            }
        });
    }

    private KLabel newValueLabel(){
        final KLabel vLabel = new KLabel("", VALUE_FONT, Color.BLUE){
            @Override
            public void setText(String newText) {
                super.setText(newText);
                super.setCursor(newText.equals("None") ? null : ON_FOCUS_CURSOR);
            }

        };
        attachActiveOnFocus(vLabel, "None");
        return vLabel;
    }

    private KLabel newSingletonLabel(){
        final KLabel singletonLabel = new KLabel("", VALUE_FONT, Color.BLUE){
            @Override
            public void setText(String newText) {
                super.setText(newText);
                super.setCursor(newText.equals("...") ? null : Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
        };
        attachActiveOnFocus(singletonLabel, "...");
        return singletonLabel;
    }

    private KPanel newAnalysisHeader(String headerText){
        final KPanel hPanel = new KPanel();
        hPanel.add(new KLabel(">>"+headerText,KFontFactory.createBoldFont(17),Color.RED));
        return hPanel;
    }

    private void resetLists(){
        aPlusList =  Memory.getFractionByGrade("A+");
        aNeutralList = Memory.getFractionByGrade("A");
        aMinusList = Memory.getFractionByGrade("A-");
        bPlusList = Memory.getFractionByGrade("B+");
        bNeutralList = Memory.getFractionByGrade("B");
        bMinusList = Memory.getFractionByGrade("B-");
        cPlusList = Memory.getFractionByGrade("C+");
        cNeutralList = Memory.getFractionByGrade("C");
        cMinusList = Memory.getFractionByGrade("C-");
        dList = Memory.getFractionByGrade("D");
        fList = Memory.getFractionByGrade("F");

        majorList = Memory.getMajors();
        minorList = Memory.getMinors();
        DERList = Memory.getDERs();
        GERList = Memory.getGERs();
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
        aPlusTellerLabel.setText(getProperValueText(aPlusList));
        aNeutralTellerLabel.setText(getProperValueText(aNeutralList));
        aMinusTellerLabel.setText(getProperValueText(aMinusList));
        bPlusTellerLabel.setText(getProperValueText(bPlusList));
        bNeutralTellerLabel.setText(getProperValueText(bNeutralList));
        bMinusTellerLabel.setText(getProperValueText(bMinusList));
        cPlusTellerLabel.setText(getProperValueText(cPlusList));
        cNeutralTellerLabel.setText(getProperValueText(cNeutralList));
        cMinusTellerLabel.setText(getProperValueText(cMinusList));
        dTellerLabel.setText(getProperValueText(dList));
        fTellerLabel.setText(getProperValueText(fList));

//        Singletons...
        highestScoreTellerLabel.setText(getProperValueText(highestScoreCourse));
        lowestScoreTellerLabel.setText(getProperValueText(lowestScoreCourse));
        highestMajorScoreTellerLabel.setText(getProperValueText(highestMajorScoreCourse));
        lowestMajorScoreTellerLabel.setText(getProperValueText(lowestMajorScoreCourse));
        highestMinorScoreTellerLabel.setText(getProperValueText(highestMinorScoreCourse));
        lowestMinorScoreTellerLabel.setText(getProperValueText(lowestMinorScoreCourse));
        highestDERScoreTellerLabel.setText(getProperValueText(highestDERScoreCourse));
        lowestDERScoreTellerLabel.setText(getProperValueText(lowestDERScoreCourse));
        highestGERScoreTellerLabel.setText(getProperValueText(highestGERScoreCourse));
        lowestGERScoreTellerLabel.setText(getProperValueText(lowestGERScoreCourse));

        majorListTellerLabel.setText(getProperValueText(majorList));
        minorListTellerLabel.setText(getProperValueText(minorList));
        DERListTellerLabel.setText(getProperValueText(DERList));
        GERListTellerLabel.setText(getProperValueText(GERList));
        unclassifiedListTellerLabel.setText(getProperValueText(unclassifiedList));
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
        return course == null ? "..." : course.getAbsoluteName()+" ["+course.getAbsoluteSemesterName()+"] : "+course.getScore()+"%";
    }

    @Override
    public void answerActivity() {
        SwingUtilities.invokeLater(()->{
            resetLists();
            finishModulesAnalysisBasement();
            finishSemestersAnalysisBasement();
            finishYearsAnalysisBasement();
            resetCourses();
            resetLabels();
        });
        Board.showCard("Analysis");
    }


    /**
     * The responsibility of this inner-class is to prompt up the contents of an analysis-list.
     */
    private static class GlassPrompt extends KDialog {
        public KPanel substancePanel;

        private GlassPrompt(String title, ArrayList<Course> courseList, Component ... relativeRoots) {
            super(title+" ["+ Globals.checkPlurality(courseList.size(),"Courses")+"]");
            this.setModalityType(KDialog.DEFAULT_MODALITY_TYPE);
            this.setResizable(true);

            substancePanel = new KPanel();
            substancePanel.setLayout(new BoxLayout(substancePanel, BoxLayout.Y_AXIS));
            for (Course tCourse : courseList) {
                join(tCourse);
            }

            final KScrollPane kScrollPane = new KScrollPane(substancePanel,false);
            if (courseList.size() > 15) {
                kScrollPane.setPreferredSize(new Dimension(kScrollPane.getPreferredSize().width+25,525));
            }

            this.setContentPane(KPanel.wantDirectAddition(new BorderLayout(), null, kScrollPane));
            this.pack();
            this.setLocationRelativeTo(relativeRoots.length == 0 ? Board.getRoot() : relativeRoots[0]);
        }

        private GlassPrompt(ArrayList<String> yTutorsList, String yName, Component ... relativeRoots) {
            super(yName+" ["+yTutorsList.size()+" Lecturers]");
            this.setModalityType(KDialog.DEFAULT_MODALITY_TYPE);
            this.setResizable(true);

            substancePanel = new KPanel();
            substancePanel.setLayout(new BoxLayout(substancePanel, BoxLayout.Y_AXIS));
            for (String tName : yTutorsList) {
                join(tName, yName);
            }

            final KScrollPane kScrollPane = new KScrollPane(substancePanel,false);
            if (yTutorsList.size() > 15) {
                kScrollPane.setPreferredSize(new Dimension(kScrollPane.getPreferredSize().width+25,525));
            }

            this.setContentPane(KPanel.wantDirectAddition(new BorderLayout(), null, kScrollPane));
            this.pack();
            this.setLocationRelativeTo(relativeRoots.length == 0 ? Board.getRoot() : relativeRoots[0]);
        }

        private GlassPrompt(ArrayList<String> lectsList, Component ... relativeRoots) {
            super("My Tutors ["+lectsList.size()+"]");
            this.setModalityType(KDialog.DEFAULT_MODALITY_TYPE);
            this.setResizable(true);

            substancePanel = new KPanel();
            substancePanel.setLayout(new BoxLayout(substancePanel, BoxLayout.Y_AXIS));
            for (String lName : lectsList) {
                join(lName);
            }

            final KScrollPane kScrollPane = new KScrollPane(substancePanel,false);
            if (lectsList.size() > 15) {
                kScrollPane.setPreferredSize(new Dimension(kScrollPane.getPreferredSize().width+25,525));
            }

            this.setContentPane(KPanel.wantDirectAddition(new BorderLayout(), null, kScrollPane));
            this.pack();
            this.setLocationRelativeTo(relativeRoots.length == 0 ? Board.getRoot() : relativeRoots[0]);
        }

        private void join(String lName) {
            final KButton dtlButton = KButton.getIconifiedButton("warn.png",25,25);
            dtlButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            dtlButton.addActionListener(e -> SwingUtilities.invokeLater(()-> new GlassPrompt(lName,
                    Memory.getFractionByLecturer(lName), this.getRootPane()).setVisible(true)));

            final KPanel joinPanel = new KPanel(new BorderLayout());
            joinPanel.add(new KPanel(new KLabel(lName,KFontFactory.createPlainFont(15))),BorderLayout.WEST);
            joinPanel.add(Box.createRigidArea(new Dimension(30, 30)), BorderLayout.CENTER);
            joinPanel.add(dtlButton,BorderLayout.EAST);

            substancePanel.add(joinPanel);
        }

        private void join(String tName, String yName) {
            final KButton dtlButton = KButton.getIconifiedButton("warn.png",25,25);
            dtlButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            dtlButton.addActionListener(e -> SwingUtilities.invokeLater(()->{
                final ArrayList<Course> tList = Memory.getFractionByLecturer(tName,yName);
                final GlassPrompt vPrompt = new GlassPrompt("", tList, this.getRootPane());
                vPrompt.setTitle(tName+" ["+ yName +": "+tList.size()+"]");
                vPrompt.setVisible(true);
            }));

            final KPanel joinPanel = new KPanel(new BorderLayout());
            joinPanel.add(new KPanel(new KLabel(tName,KFontFactory.createPlainFont(15))), BorderLayout.WEST);
            joinPanel.add(Box.createRigidArea(new Dimension(30, 30)),BorderLayout.CENTER);
            joinPanel.add(dtlButton,BorderLayout.EAST);

            substancePanel.add(joinPanel);
        }

        public void join(Course c){
            final KButton dtlButton = KButton.getIconifiedButton("warn.png",25,25);
            dtlButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            dtlButton.addActionListener(e -> Course.exhibit(this.getRootPane(), c));

            final KPanel joinPanel = new KPanel(new BorderLayout());
            joinPanel.add(new KPanel(new KLabel(c.getName(),KFontFactory.createPlainFont(15))), BorderLayout.WEST);
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
                this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
                this.addAll(new KPanel(), new KPanel(new KLabel("No Sketch Available", KFontFactory.createPlainFont(20))),
                        new KPanel(new KLabel("Student must complete at least two semesters", KFontFactory.createPlainFont(15), Color.GRAY)),
                        new KPanel());
                this.setMaximumSize(this.getPreferredSize());
            } else {
                this.setPreferredSize(new Dimension(900, 475));
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (semestersList.size() > 1) {
                final Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                final double xScale = ((double) (this.getWidth() - (2 * PADDING)) / (semesterScores.size() - 1));
                final double yScale = ((double) (this.getHeight() - (2 * PADDING)) / (Y_MAX - 1));

                final ArrayList<Point> points = new ArrayList<>();
                for (int i = 0; i < semesterScores.size(); i++) {
                    int x1 = (int) (i * xScale + PADDING);
                    int y1 = (int) ((Y_MAX - semesterScores.get(i)) * yScale + PADDING);
                    points.add(new Point(x1, y1));
                }

                g2.drawLine(PADDING, this.getHeight() - PADDING, this.getWidth() - PADDING, this.getHeight() - PADDING);
                g2.drawLine(PADDING, this.getHeight() - PADDING, PADDING, PADDING);

                g2.setFont(KFontFactory.createPlainFont(11));
                for (int i = 0; i < semesterScores.size() - 1; i++) {
                    int x0 = (i + 1) * (this.getWidth() - (PADDING * 2)) / (semesterScores.size() - 1) + PADDING;
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
