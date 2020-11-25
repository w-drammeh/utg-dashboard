package main;

import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class TranscriptExporter {
    private Document document;
    private Rectangle pageSize;
    private int maxUsableWidth;
    final int[] columnWidths = new int[] {5, 15, 40, 15, 10, 15};
    final Font captionFont = new Font(Font.HELVETICA, 10, Font.BOLD);
    final Font bodyFont = new Font(Font.HELVETICA, 10, Font.NORMAL);
    private static final String[] HEAD = {"#", "COURSE CODE", "COURSE DESCRIPTION", "CREDIT VALUE", "GRADE", "QUALITY POINT"};


    public TranscriptExporter(){
        document = new Document();
        pageSize = document.getPageSize();
        maxUsableWidth = (int)(pageSize.getWidth() - pageSize.getWidth()/25) - 5;
    }

    public void exportNow() throws IOException, DocumentException {
        String savePath;
        final String homeDir = System.getProperty("user.home"),
                documentsDir = homeDir+"/Documents";
        final JFileChooser fileChooser = new JFileChooser(new File(documentsDir).exists() ? documentsDir : homeDir);
        fileChooser.setDialogTitle("Select Destination");
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fileChooser.showOpenDialog(Board.getRoot()) == JFileChooser.APPROVE_OPTION) {
            savePath = fileChooser.getSelectedFile().getPath();
        } else {
            return;
        }

        final File outputFile = new File(savePath+Serializer.FILE_SEPARATOR+"transcript.pdf");
        final FileOutputStream outputStream = new FileOutputStream(outputFile);
        PdfWriter.getInstance(document, outputStream);

        document.open();
        addMetaData();
        addUTGLabels();
        addUserData();
        addTable();
        document.close();
        outputStream.close();
        SwingUtilities.invokeLater(()-> App.promptPlain("Successful",
                "Your Transcript is been exported successfully to "+savePath));
    }

    private void addMetaData(){
        document.addAuthor("Muhammed W. Drammeh");
        document.addTitle("UTG Student Transcript");
        document.addCreator("UTG Student Dashboard");
        document.addCreationDate();
    }

    private void addUTGLabels() throws IOException, DocumentException {
        final Paragraph line1 = new Paragraph(new Phrase("THE UNIVERSITY OF THE GAMBIA",
                new Font(Font.TIMES_ROMAN, 18, Font.BOLD)));
        line1.setAlignment(Paragraph.ALIGN_CENTER);
        line1.setSpacingAfter(10);

        final Paragraph line2 = new Paragraph(new Phrase("STUDENT ACADEMIC RECORDS",
                new Font(Font.TIMES_ROMAN, 13, Font.BOLD)));
        line2.setAlignment(Paragraph.ALIGN_CENTER);
        line2.setSpacingAfter(45);

        final Image logo = new Jpeg(App.getIconURL("UTGLogo.jpg"));
        logo.scaleAbsolute(65, 80);
        logo.setAbsolutePosition(pageSize.getWidth() - 90, pageSize.getHeight() - 129);

        document.add(line1);
        document.add(line2);
        document.add(logo);
    }

    private void addUserData() throws DocumentException {
        final int detailTableWidth = (int)(pageSize.getWidth()/2) - 50;
        final int longCellHeight = 30;
        final int shortCellHeight = 15;
        final Font hintFont = new Font(Font.HELVETICA, 10, Font.BOLD);
        final Font valueFont = new Font(Font.HELVETICA, 10, Font.NORMAL);

        final PdfPTable leftTable = new PdfPTable(2);
        leftTable.setHorizontalAlignment(PdfPTable.ALIGN_LEFT);
        leftTable.setTotalWidth(detailTableWidth);
        leftTable.setLockedWidth(true);
        leftTable.setWidths(new int[] {35, 50});
        leftTable.addCell(newDataCell("STUDENT NAME", hintFont, longCellHeight));
        leftTable.addCell(newDataCell(Student.getFullNamePostOrder().toUpperCase(), valueFont, longCellHeight));
        leftTable.addCell(newDataCell("STUDENT NUMBER", hintFont, longCellHeight));
        leftTable.addCell(newDataCell(Student.getMatNumber(), valueFont, longCellHeight));
        leftTable.addCell(newDataCell("YEAR ENROLLED", hintFont, shortCellHeight));
        final String admissionText = Student.getMonthOfAdmissionName()+", "+Student.getYearOfAdmission();
        leftTable.addCell(newDataCell(admissionText, valueFont, shortCellHeight));

        final PdfPTable rightTable = new PdfPTable(2);
        rightTable.setHorizontalAlignment(PdfPTable.ALIGN_RIGHT);
        rightTable.setTotalWidth(detailTableWidth);
        rightTable.setLockedWidth(true);
        rightTable.setWidths(new int[] {30, 50});
        rightTable.addCell(newDataCell("YEAR GRADUATED", hintFont, longCellHeight));
        rightTable.addCell(newDataCell(Student.getYearOfGraduation(), valueFont, longCellHeight));
        rightTable.addCell(newDataCell("MAJOR", hintFont, longCellHeight));
        rightTable.addCell(newDataCell(Student.getProgram(), valueFont, longCellHeight));
        rightTable.addCell(newDataCell("MINOR", hintFont, shortCellHeight));
        final String minorText = Student.isDoingMinor() ? Student.getMinor() : "N/A";
        rightTable.addCell(newDataCell(minorText, valueFont, shortCellHeight));

        final PdfPTable detailsTable = new PdfPTable(2);
        detailsTable.getDefaultCell().setBorderColor(Color.WHITE);//desperately trying to hide the border
        detailsTable.setTotalWidth(maxUsableWidth);
        detailsTable.setLockedWidth(true);
        detailsTable.addCell(leftTable);
        detailsTable.addCell(rightTable);
        detailsTable.setSpacingAfter(25);
        document.add(detailsTable);
    }

    private PdfPCell newDataCell(String text, Font font, int height){
        final PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        cell.setBorderWidth(1F);
        cell.setFixedHeight(height);
        return cell;
    }

    public void addTable() throws DocumentException {
        final PdfPTable headTable = new PdfPTable(HEAD.length);
        headTable.setTotalWidth(maxUsableWidth);
        headTable.setLockedWidth(true);
        headTable.setWidths(columnWidths);
        for (String header : HEAD) {
            final PdfPCell headCell = new PdfPCell(new Phrase(header, new Font(Font.HELVETICA, 9, Font.BOLD)));
            headCell.setBackgroundColor(Color.LIGHT_GRAY);
            headCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            headCell.setPadding(5F);
            headTable.addCell(headCell);
        }
        document.add(headTable);

        final ArrayList<String> semesters = Memory.filterSemesters();
        for (String semester : semesters) {
            document.add(newSemesterHeadTable(semester));
            document.add(newSemesterBodyTable(Memory.getFractionBySemester(semester)));
        }

        final PdfPTable gpaTable = new PdfPTable(3);
        gpaTable.setTotalWidth(maxUsableWidth);
        gpaTable.setLockedWidth(true);
        gpaTable.setWidths(new int[] {columnWidths[0] + columnWidths[1] + columnWidths[2] + columnWidths[3],
                columnWidths[4], columnWidths[5]});
        final PdfPCell gpHintCell = new PdfPCell(new Phrase("AVERAGE QUALITY POINT", captionFont));
        gpHintCell.setPadding(10F);
        gpHintCell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        final PdfPCell gpValueCell = new PdfPCell(new Phrase(String.valueOf(Student.getCGPA()), captionFont));
        gpValueCell.setPadding(10F);
        gpValueCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        gpaTable.addCell(gpHintCell);
        gpaTable.addCell(gpValueCell);
        final PdfPCell hiddenCell = new PdfPCell(new Phrase(" "));
        hiddenCell.setBorderWidthRight(0.0F);
        hiddenCell.setBorderWidthBottom(0.0F);
        gpaTable.addCell(hiddenCell);
        document.add(gpaTable);
    }

    private PdfPTable newSemesterHeadTable(String semesterName){
        final PdfPCell semesterCell = new PdfPCell(new Phrase(semesterName, captionFont));
        semesterCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        semesterCell.setPadding(7F);
        final PdfPTable captionTable = new PdfPTable(1);
        captionTable.setTotalWidth(maxUsableWidth);
        captionTable.setLockedWidth(true);
        captionTable.addCell(semesterCell);
        return captionTable;
    }

    private PdfPTable newSemesterBodyTable(ArrayList<Course> list) throws DocumentException {
        final PdfPTable bodyTable = new PdfPTable(HEAD.length);
        bodyTable.setTotalWidth(maxUsableWidth);
        bodyTable.setLockedWidth(true);
        bodyTable.setWidths(columnWidths);
        int count = 1;
        for (Course c : list) {
            bodyTable.addCell(newTableCell(String.valueOf(count)));
            final PdfPCell codeCell = newTableCell(c.getCode());
            codeCell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            bodyTable.addCell(codeCell);
            bodyTable.addCell(newTableCell(c.getName()));
            bodyTable.addCell(newTableCell(Integer.toString(c.getCreditHours())));
            bodyTable.addCell(newTableCell(c.getGrade()));
            final PdfPCell pointCell = newTableCell(String.valueOf(c.getQualityPoint()));
            pointCell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            bodyTable.addCell(pointCell);
            count++;
        }
        return bodyTable;
    }

    private PdfPCell newTableCell(String text){
        final PdfPCell cell = new PdfPCell(new Phrase(text, bodyFont));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setPadding(5);
        return cell;
    }

}
