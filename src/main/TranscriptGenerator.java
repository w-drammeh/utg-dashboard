package main;

import customs.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;

/**
 * Dashboard does not make any effort(s) to calculate the CGPA. So, the cgpa, the upperDivision,
 * as well as other generated resources of this type should be renewed anytime it answerActivity.
 * And the cgpa is always set before the upperDivision is re-checked!
 * Feel free of what this class presents, because what's printed is independent of it.
 */
public class TranscriptGenerator implements ActivityAnswerer {
    public static final String[] HEADS = {"COURSE CODE", "COURSE DESCRIPTION", "CREDIT VALUE", "GRADE", "QUALITY POINT"};
    private static final KLabel classificationLabel = KLabel.getPredefinedLabel("Current Upper Class Division: ",
            SwingConstants.LEFT);
    private static KPanel transcriptFace;
    /**
     * Hold the logo, labels, as well as the details.
     */
    private static KPanel detailsPanelPlus;
    private static KDefaultTableModel tModel;
    private static KTable tTable;
    private static KScrollPane tWrapper;
    private static KLabel cgpaLabel;
    private static KLabel minorLabel;


    public TranscriptGenerator(){
        cgpaLabel = new KLabel(Student.getCGPA()+"",KFontFactory.createPlainFont(16));
        minorLabel = new KLabel(Student.getMinor().toUpperCase(),KFontFactory.createPlainFont(15));
        classificationLabel.setFont(KFontFactory.createPlainFont(15));

        buildDetailsPanelPlus();

        tModel = new KDefaultTableModel();
        tModel.setColumnIdentifiers(HEADS);

        tTable = new KTable(tModel);
        tTable.setRowHeight(30);
        tTable.setFont(KFontFactory.createPlainFont(15));
        tTable.getColumnModel().getColumn(1).setPreferredWidth(350);
        tTable.getColumnModel().getColumn(3).setPreferredWidth(40);
        tTable.getTableHeader().setFont(KFontFactory.createBoldFont(16));
        tTable.getTableHeader().setPreferredSize(new Dimension(tTable.getPreferredSize().width,35));
        tTable.centerAlignColumns(2, 3, 4);
        tTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    final int selectedRow = tTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        SwingUtilities.invokeLater(()-> Course.exhibit(ModulesHandler.getModuleByCode(
                                String.valueOf(tModel.getValueAt(selectedRow, 0)))));
                    }
                    e.consume();
                }
            }
        });

        tWrapper = KScrollPane.getSizeMatchingScrollPane(tTable, 3);

        generateTranscriptFace();

        final KPanel transcriptUI = new KPanel(new BorderLayout());
        transcriptUI.add(headLiner(),BorderLayout.NORTH);
        transcriptUI.add(new KScrollPane(transcriptFace), BorderLayout.CENTER);
        transcriptUI.add(bottomLiner(),BorderLayout.SOUTH);

        Board.addCard(transcriptUI, "Transcript");
    }

    private static KPanel headLiner(){
        final KButton downloadButton = new KButton("Export Transcript");
        downloadButton.setPreferredSize(new Dimension(175, 30));
        downloadButton.setStyle(KFontFactory.createPlainFont(15), Color.BLUE);
        downloadButton.undress();
        downloadButton.underline(null, false);
        downloadButton.setToolTipText("Export Transcript to PDF");
        downloadButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        downloadButton.addActionListener(actionEvent -> {
            downloadButton.setEnabled(false);
            TranscriptHandler.exportNow();
            downloadButton.setEnabled(true);
        });

        final KCheckBox detailsCheck = new KCheckBox("Show Details",false);
        detailsCheck.setFont(KFontFactory.createPlainFont(15));
        detailsCheck.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        detailsCheck.setOpaque(true);
        detailsCheck.addItemListener(itemEvent -> {
            if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                detailsPanelPlus.setVisible(true);
            } else {
                detailsPanelPlus.setVisible(false);
            }
        });

        final KPanel helloPanel = new KPanel(new FlowLayout(FlowLayout.RIGHT));
        helloPanel.addAll(detailsCheck, downloadButton);
        return helloPanel;
    }

    private static void generateTranscriptFace(){
        transcriptFace = new KPanel();
        transcriptFace.setLayout(new BoxLayout(transcriptFace, BoxLayout.Y_AXIS));
        transcriptFace.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        transcriptFace.addAll(Box.createVerticalStrut(10), detailsPanelPlus, Box.createVerticalStrut(20),
                tWrapper, Box.createVerticalStrut(10), getPointPanel());
    }

    private static void buildDetailsPanelPlus(){
        final KLabel l1 = new KLabel("THE UNIVERSITY OF THE GAMBIA",KFontFactory.createBoldFont(25));
        final KLabel l2 = new KLabel("STUDENT ACADEMIC RECORDS",l1.getFont());

        final KPanel l1l2_Panel = new KPanel(new Dimension(500,100));
        l1l2_Panel.addAll(l1,l2);

        final KPanel lParted = new KPanel();
        lParted.addAll(KLabel.wantIconLabel("UTGLogo.gif",125,125),
                Box.createRigidArea(new Dimension(50, 100)),l1l2_Panel);

        detailsPanelPlus = new KPanel();
        detailsPanelPlus.setLayout(new BoxLayout(detailsPanelPlus, BoxLayout.Y_AXIS));
        detailsPanelPlus.addAll(lParted,Box.createVerticalStrut(30),packAllSeparatedPanels());
        detailsPanelPlus.setVisible(false);
    }

    private static KPanel packAllSeparatedPanels(){
        final KPanel leftSide = new KPanel();
        leftSide.setLayout(new BoxLayout(leftSide, BoxLayout.Y_AXIS));
        leftSide.add(provideRectangularPanel(130,30,"STUDENT NAME", Student.getFullNamePostOrder()));
        leftSide.add(Box.createVerticalStrut(10));
        leftSide.add(provideRectangularPanel(130,60,"YEAR","ENROLLED",
                Integer.toString(Student.getYearOfAdmission())));
        leftSide.add(Box.createVerticalStrut(10));
        leftSide.add(provideRectangularPanel(130,30,"MINOR", ""));

        final KPanel rightSide = new KPanel();
        rightSide.setLayout(new BoxLayout(rightSide,BoxLayout.Y_AXIS));
        rightSide.add(provideRectangularPanel(165,30,"STUDENT NUMBER", Student.getMatNumber()+""));
        rightSide.add(Box.createVerticalStrut(10));
        rightSide.add(provideRectangularPanel(165,60,"YEAR EXPECTED", "TO GRADUATE",
                Student.getExpectedYearOfGraduation()+""));
        rightSide.add(Box.createVerticalStrut(10));
        rightSide.add(provideRectangularPanel(165,30,"MAJOR", Student.getMajor()));

        final KPanel returningPanel = new KPanel(new BorderLayout());
        returningPanel.add(leftSide, BorderLayout.WEST);
        returningPanel.add(rightSide, BorderLayout.EAST);
        return returningPanel;
    }

    private static KPanel provideRectangularPanel(int leftMostWidth, int leftMostHeight, String... strings){
        final Font hereFont = KFontFactory.createPlainFont(15);

        final KPanel l = new KPanel(new Dimension(leftMostWidth, leftMostHeight));
        final KPanel r = new KPanel();

        if (strings.length == 2) {
            l.add(new KLabel(strings[0], hereFont));
            r.add(Objects.equals(strings[0], "MINOR") ? minorLabel : new KLabel(strings[1].toUpperCase(), hereFont));
        } else if (strings.length == 3) {
            l.setLayout(new BoxLayout(l, BoxLayout.Y_AXIS));
            l.add(KPanel.wantDirectAddition(new KLabel(strings[0], hereFont)));
            l.add(KPanel.wantDirectAddition(new KLabel(strings[1].toUpperCase(), hereFont)));

            r.setLayout(new BoxLayout(r, BoxLayout.X_AXIS));
            r.addAll(new KPanel(), new KLabel(strings[2].toUpperCase(), hereFont), new KPanel());
        }

        final KPanel lr = new KPanel(new BorderLayout());
        lr.setBorder(BorderFactory.createLineBorder(Color.BLACK,1, false));
        lr.add(l, BorderLayout.WEST);
        lr.add(new KSeparator(KSeparator.VERTICAL), BorderLayout.CENTER);
        lr.add(r, BorderLayout.EAST);
        return lr;
    }

    private static KPanel getPointPanel(){
        final KPanel leftHand = new KPanel();
        leftHand.setLayout(new BoxLayout(leftHand, BoxLayout.Y_AXIS));
        leftHand.addAll(KPanel.wantDirectAddition(new KLabel("AVERAGE", KFontFactory.createPlainFont(15))),
                KPanel.wantDirectAddition(new KLabel("QUALITY POINT",KFontFactory.createPlainFont(15))));

        final KPanel avgPanel = new KPanel(new Dimension(255,55));
        avgPanel.setLayout(new BoxLayout(avgPanel, BoxLayout.X_AXIS));
        avgPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK,1,false));
        avgPanel.add(leftHand);
        avgPanel.add(new KSeparator(KSeparator.VERTICAL));
        final KPanel numberHolder = new KPanel();
        numberHolder.setLayout(new BoxLayout(numberHolder, BoxLayout.X_AXIS));
        numberHolder.addAll(new KPanel(), cgpaLabel, new KPanel());
        avgPanel.add(numberHolder);

        final KPanel kPanel = new KPanel(new BorderLayout());
        kPanel.add(avgPanel, BorderLayout.EAST);
        return kPanel;
    }

    private static KPanel bottomLiner(){
        return KPanel.wantDirectAddition(classificationLabel);
    }

    private static void reIndexTableResources(){
        for (int i = 0; i < tTable.getRowCount(); i++){
            tModel.removeRow(i);
            tWrapper.setPreferredSize(new Dimension(tWrapper.getPreferredSize().width, tWrapper.getPreferredSize().height - 30));
        }

        for (Course tCourse : Memory.listRequested()) {
            tModel.addRow(new String[] {tCourse.getCode(),tCourse.getName(),tCourse.getCreditHours()+"", tCourse.getGrade(),
                    tCourse.getQualityPoint()+""});
            tWrapper.setPreferredSize(new Dimension(tWrapper.getPreferredSize().width, tWrapper.getPreferredSize().height + 30));
        }
    }

    public static String getMinorState(){
        return minorLabel.getText();
    }

    @Override
    public void answerActivity() {
        minorLabel.setText(Student.isDoingMinor()? Student.getMinor().toUpperCase() : "NONE");
        classificationLabel.setText(Student.upperDivision());
        classificationLabel.setForeground(classificationLabel.getText().contains("None") ? Color.RED : Color.BLUE);
        cgpaLabel.setText(Double.toString(Student.getCGPA()));

        SwingUtilities.invokeLater(TranscriptGenerator::reIndexTableResources);
        Board.showCard("Transcript");
    }

}
