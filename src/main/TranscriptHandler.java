package main;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import customs.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Objects;

/**
 * While the generator type gets the components used by Board,
 * this handles the print and export actions.
 */
public class TranscriptHandler {
    public static final String UNCLASSIFIED = "None";
    public static final String THIRD_CLASS = "\"Cum laude\" - With Praise!";
    public static final String SECOND_CLASS = "\"Magna Cum laude\" - With Great Honor!";
    public static final String FIRST_CLASS = "\"Summa Cum laude\" - With Greatest Honor!";
    private static KDialog exportDialog;
    private static KPanel exportPanel;
    private static boolean secondaryExportNeeded;
    private static int secondaryStartIndex;


    private static void setUpPrimaryExportation(){
        repairPanel();
        attachTitlePlus();
        addDetails();

        final KDefaultTableModel primaryModel = new KDefaultTableModel();
        primaryModel.setColumnIdentifiers(TranscriptGenerator.HEADS);
        final List<Course> l = Memory.listRequested();
        for (int i = 0; i < l.size(); i++) {
            if (i == 26) {
                secondaryExportNeeded = true;
                secondaryStartIndex = i;
                break;
            }
            final Course c = l.get(i);
            primaryModel.addRow(new String[] {c.getCode(),c.getName(),Integer.toString(c.getCreditHours()),
                    c.getGrade(),Double.toString(c.getQualityPoint())});
        }

        if (secondaryExportNeeded) {
            final int r = l.size() - secondaryStartIndex;
            if (r > 5) {
                int t = secondaryStartIndex;
                for (; t < (secondaryStartIndex + 4); t++) {
                    final Course c = l.get(t);
                    primaryModel.addRow(new String[] {c.getCode(),c.getName(),Integer.toString(c.getCreditHours()),
                            c.getGrade(),Double.toString(c.getQualityPoint())});
                }
                secondaryStartIndex = t;
            }
        }
        final KTable primaryTable = new KTable(primaryModel);
        primaryTable.setFont(KFontFactory.createPlainFont(11));
        primaryTable.getColumnModel().getColumn(0).setPreferredWidth(90);
        primaryTable.getColumnModel().getColumn(1).setPreferredWidth(235);
        primaryTable.getColumnModel().getColumn(2).setPreferredWidth(90);
        primaryTable.getColumnModel().getColumn(3).setPreferredWidth(50);
        primaryTable.getColumnModel().getColumn(4).setPreferredWidth(95);
        primaryTable.getTableHeader().setPreferredSize(new Dimension(primaryTable.getPreferredSize().width,35));
        primaryTable.getTableHeader().setBackground(Color.WHITE);//sounds needed
        primaryTable.getTableHeader().setFont(KFontFactory.createBoldFont(10));
        primaryTable.centerAlignColumns(2, 3, 4);
//        primaryTable.getColumnModel().getColumn(2).setHeaderValue(KTextPane.wantHtmlFormattedPane("CREDIT<br>VALUE"));

        final KScrollPane scrollPane = KScrollPane.getSizeMatchingScrollPane(primaryTable,3);
        scrollPane.setBounds(20, 285, scrollPane.getPreferredSize().width, scrollPane.getPreferredSize().height);//which were set earlier
        exportPanel.add(scrollPane);

        if (!secondaryExportNeeded) {
            final KPanel lastNotLeast = newDetailPanel(375, (scrollPane.getY()+scrollPane.getHeight()+10),
                    125, 200, 45,"AVERAGE","QUALITY POINT", " "+Student.getCGPA()+" ");
            lastNotLeast.setBounds(lastNotLeast.getX(),lastNotLeast.getY(),lastNotLeast.getWidth(),50);
            exportPanel.add(lastNotLeast);
        }//Then it shall be added later... (in secondary) to the secondaryExportPanel
        repairDialog();
    }

    private static void repairPanel(){
        exportPanel = new KPanel();
        exportPanel.setBackground(Color.WHITE);
        exportPanel.addContainerListener(new ContainerAdapter() {
            @Override
            public void componentAdded(ContainerEvent e) {
                e.getChild().setBackground(Color.WHITE);
            }
        });
        exportPanel.setLayout(null);
    }

    private static void repairDialog(){
        exportDialog = new KDialog();
        exportDialog.setSize(594, 842);
        exportDialog.setUndecorated(true);
        exportDialog.setContentPane(exportPanel);
        exportDialog.setLocationRelativeTo(null);
    }

    private static void attachTitlePlus(){
        final KLabel uLogo = KLabel.wantIconLabel("UTGLogo.gif",75,100);
        uLogo.setBounds(50,30,100,100);

        final KPanel labelsPanel = new KPanel();
        labelsPanel.setBounds(175,40,375,75);
        labelsPanel.add(new KLabel("THE UNIVERSITY OF THE GAMBIA", KFontFactory.createBoldFont(17)));
        labelsPanel.add(new KLabel("STUDENT ACADEMIC RECORDS", KFontFactory.createBoldFont(17)));
        exportPanel.addAll(uLogo, labelsPanel);
    }

    private static void addDetails(){
        exportPanel.add(newDetailPanel(20,155,90,255,25,"STUDENT NAME", " "+Student.getFullNamePostOrder().toUpperCase()+" "));
        exportPanel.add(newDetailPanel(20,190,90,255,45,"YEAR","ENROLLED", " "+Student.getYearOfAdmission()+" "));
        exportPanel.add(newDetailPanel(20,245,90,255,25," MINOR ", Student.isDoingMinor() ? " "+Student.getMinor().toUpperCase()+" " : "NONE"));

        exportPanel.add(newDetailPanel(325,155,110,255,25,"STUDENT NUMBER", " "+Student.getMatNumber()+" "));
        exportPanel.add(newDetailPanel(325,190,110,255,45,"YEAR EXPECTED"," TO GRADUATE ", " "+Student.getExpectedYearOfGraduation()+" "));
        exportPanel.add(newDetailPanel(325,245,110,255,25,"MAJOR", " "+Student.getMajor().toUpperCase()+" "));
    }

    private static KPanel newDetailPanel(int x, int y, int leftMostWidth, int totalWidth, int totalHeight, String... strings){
        final Font hereFont = KFontFactory.createPlainFont(10);

        final KPanel l = new KPanel(new Dimension(leftMostWidth, totalHeight)){
            @Override
            public Component add(Component comp) {
                comp.setBackground(Color.WHITE);
                return super.add(comp);
            }
        };
        l.setBackground(Color.WHITE);
        final KPanel r = new KPanel(){
            @Override
            public Component add(Component comp) {
                comp.setBackground(Color.WHITE);
                return super.add(comp);
            }
        };
        r.setBackground(Color.WHITE);

        if (strings.length == 2) {
            l.add(new KLabel(strings[0], hereFont));
            r.add(Objects.equals(strings[0], "MINOR") ? new KLabel(TranscriptGenerator.getMinorState(), hereFont) :
                    new KLabel(strings[1].toUpperCase(), hereFont));
        } else if (strings.length == 3) {
            l.setLayout(new BoxLayout(l, BoxLayout.Y_AXIS));
            l.addAll(KPanel.wantDirectAddition(new KLabel(strings[0], hereFont)), KPanel.wantDirectAddition(new KLabel(strings[1].toUpperCase(), hereFont)));

            r.setLayout(new BoxLayout(r, BoxLayout.X_AXIS));
            r.addAll(new KPanel(), new KLabel(strings[2].toUpperCase(), hereFont), new KPanel());
        }

        final KPanel lr = new KPanel(new BorderLayout());
        lr.setBackground(Color.WHITE);
        lr.setBounds(x, y, totalWidth, totalHeight);
        lr.setBorder(BorderFactory.createLineBorder(null,1, false));
        lr.add(l, BorderLayout.WEST);
        lr.add(new KSeparator(KSeparator.VERTICAL, Color.BLACK), BorderLayout.CENTER);
        lr.add(r, BorderLayout.EAST);

        return lr;
    }


    public static void exportNow(){
        setUpPrimaryExportation();

        File savePath;
        final String homeDir = System.getProperty("user.home"),
                documentsDir = homeDir+"/Documents";
        final JFileChooser fileChooser = new JFileChooser(new File(documentsDir).exists() ? documentsDir : homeDir);
        fileChooser.setDialogTitle("Select Destination");
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fileChooser.showOpenDialog(Board.getRoot()) == JFileChooser.APPROVE_OPTION) {
            savePath = fileChooser.getSelectedFile();
        } else {
            return;
        }

        exportDialog.setVisible(true);
        final Document document = new Document();
        try{
            final PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(savePath+"/transcript.pdf"));
            document.open();

            final PdfContentByte contentByte = writer.getDirectContent();
            final PdfTemplate template = contentByte.createTemplate(595.0F, 842.0F);
            final Graphics2D graphics = template.createGraphicsShapes(595.0F, 842.0F);
            contentByte.addTemplate(template,0,0);

            exportPanel.printAll(graphics);
            graphics.dispose();
            document.close();
            exportDialog.dispose();
            if (secondaryExportNeeded) {
                launchSecondaryExport(savePath);
            } else {
                App.promptPlain("Export Successful","Your Transcript exported successfully to "+savePath);
            }
        } catch (Exception e){
            reportExportError(e);
        } finally {
            exportDialog.dispose();
        }
    }

    private static void launchSecondaryExport(File savePath){
        final KDefaultTableModel secondaryModel = new KDefaultTableModel();
        secondaryModel.setColumnIdentifiers(TranscriptGenerator.HEADS);
        final List<Course> l = Memory.listRequested();
        for (int i = secondaryStartIndex; i < l.size(); i++) {
            final Course c = l.get(i);
            secondaryModel.addRow(new String[] {c.getCode(),c.getName(),Integer.toString(c.getCreditHours()),c.getGrade(),
                    Double.toString(c.getQualityPoint())});
        }

        final KTable secondaryTable = new KTable(secondaryModel);
        secondaryTable.setFont(KFontFactory.createPlainFont(11));
        secondaryTable.getColumnModel().getColumn(0).setPreferredWidth(90);
        secondaryTable.getColumnModel().getColumn(1).setPreferredWidth(235);
        secondaryTable.getColumnModel().getColumn(2).setPreferredWidth(90);
        secondaryTable.getColumnModel().getColumn(3).setPreferredWidth(50);
        secondaryTable.getColumnModel().getColumn(4).setPreferredWidth(95);
        secondaryTable.getTableHeader().setPreferredSize(new Dimension(0,0));
        secondaryTable.getTableHeader().setVisible(false);
        secondaryTable.centerAlignColumns(2, 3, 4);

        final KScrollPane scrollPane = KScrollPane.getSizeMatchingScrollPane(secondaryTable,3);
        scrollPane.setBounds(20,30, scrollPane.getPreferredSize().width, scrollPane.getPreferredSize().height);

        final KPanel lastNotLeast = newDetailPanel(350,(scrollPane.getY()+scrollPane.getHeight()+10),115,
                230, 45, "AVERAGE","QUALITY POINT", " "+Student.getCGPA()+" ");
        lastNotLeast.setBounds(lastNotLeast.getX(),lastNotLeast.getY(),lastNotLeast.getWidth(),50);

        repairPanel();
        exportPanel.addAll(scrollPane, lastNotLeast);
        repairDialog();

        exportDialog.setVisible(true);
        final Document document = new Document();
        try {
            final PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(savePath+"/transcript-part-2.pdf"));
            document.open();

            final PdfContentByte contentByte = writer.getDirectContent();
            final PdfTemplate template = contentByte.createTemplate(595.0F, 842.0F);
            final Graphics2D graphics = template.createGraphicsShapes(595.0F, 842.0F);
            contentByte.addTemplate(template, 0, 0);

            exportPanel.printAll(graphics);
            graphics.dispose();
            document.close();
            exportDialog.dispose();
            App.promptPlain("Export Successful", "Your Transcript is been exported successfully to "+savePath);
        } catch (Exception e) {
            reportExportError(e);
        } finally {
            exportDialog.dispose();
        }
    }

    private static void reportExportError(Exception e){
        App.signalError("Error", "Sorry, we experienced unusual problems during the export.\n" +
                "Please, try again.\nError Message = " + e.getMessage());
    }

}