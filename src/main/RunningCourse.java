package main;

import proto.*;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

/**
 * A model for the registered courses.
 */
public class RunningCourse implements Serializable {
    private String code;
    private String name;
    private String lecturer;
    private String venue;
    private String room;
    private String day;
    private String time;
    private boolean isConfirmed;


    public RunningCourse(String code, String name, String lName, String vName, String rName, String day, String time,
                         boolean onPortal){
        this.code = code.toUpperCase();
        this.name = name;
        this.lecturer = lName;
        this.venue = vName;
        this.room = rName;
        this.day = day.equals(Course.UNKNOWN) ? "" : day;
        this. time = time.equals(Course.UNKNOWN) ? "" : time;
        this.isConfirmed = onPortal;
    }

    public String getCode(){
        return code;
    }

    public void setCode(String newCode){
        this.code = newCode;
    }

    public String getName(){
        return name;
    }

    public void setName(String newName){
        this.name = newName;
    }

    public String getLecturer(){
        return lecturer;
    }

    public void setLecturer(String newLecturer){
        this.lecturer = newLecturer;
    }

    public String getVenue(){
        return venue;
    }

    public void setVenue(String newVenue){
        this.venue = newVenue;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getDay(){
        return day;
    }

    public void setDay(String newDay){
        this.day = newDay;
    }

    public String getTime(){
        return time;
    }

    public void setTime(String newTime){
        this.time = newTime;
    }

    public boolean isConfirmed(){
        return isConfirmed;
    }

    public void setConfirmed(boolean onPortal){
        this.isConfirmed = onPortal;
    }

    public String getAbsoluteName(){
        return String.join(" ", code, name);
    }

    public String getSchedule(){
        if (Globals.hasText(day) && Globals.hasText(time)) {
            return String.join(" ", day, time);
        } else if (Globals.hasText(day) && Globals.isBlank(time)) {
            return String.join(" - ", day, "Unknown time");
        } else if (Globals.isBlank(day) && Globals.hasText(time)) {
            return String.join(" - ", time, "Unknown day");
        } else {
            return "";
        }
    }

    public String exportContent(){
        return code + "\n" +
                name + "\n" +
                lecturer + "\n" +
                venue + "\n" +
                room + "\n" +
                day + "\n" +
                time + "\n" +
                isConfirmed;
    }

    public static RunningCourse importFromSerial(String dataLines){
        final String[] data = dataLines.split("\n");
        boolean validity = false;
        try {
            validity = Boolean.parseBoolean(data[7]);
        } catch (Exception e) {
            App.silenceException("Error reading validity of registered course "+data[3]);
        }
        return new RunningCourse(data[0], data[1], data[2], data[3], data[4], data[5], data[6], validity);
    }

    public static void exhibit(RunningCourse course, Component base) throws NullPointerException {
        final KDialog dialog = new KDialog(course.name);
        dialog.setResizable(true);
        dialog.setModalityType(KDialog.DEFAULT_MODALITY_TYPE);

        final Font hintFont = KFontFactory.createBoldFont(15);
        final Font valueFont = KFontFactory.createPlainFont(15);

        final KPanel codePanel = new KPanel(new BorderLayout());
        codePanel.add(new KPanel(new KLabel("Code:", hintFont)), BorderLayout.WEST);
        codePanel.add(new KPanel(new KLabel(course.code, valueFont)), BorderLayout.CENTER);

        final KPanel namePanel = new KPanel(new BorderLayout());
        namePanel.add(new KPanel(new KLabel("Name:", hintFont)), BorderLayout.WEST);
        namePanel.add(new KPanel(new KLabel(course.name, valueFont)), BorderLayout.CENTER);

        final KPanel lectPanel = new KPanel(new BorderLayout());
        lectPanel.add(new KPanel(new KLabel("Lecturer:", hintFont)), BorderLayout.WEST);
        lectPanel.add(new KPanel(new KLabel(course.lecturer, valueFont)), BorderLayout.CENTER);

        final KPanel schedulePanel = new KPanel(new BorderLayout());
        schedulePanel.add(new KPanel(new KLabel("Schedule:", hintFont)), BorderLayout.WEST);
        schedulePanel.add(new KPanel(new KLabel(course.getSchedule(), valueFont)), BorderLayout.CENTER);

        final KPanel venuePanel = new KPanel(new BorderLayout());
        venuePanel.add(new KPanel(new KLabel("Venue:", hintFont)), BorderLayout.WEST);
        venuePanel.add(new KPanel(new KLabel(course.venue, valueFont)), BorderLayout.CENTER);

        final KPanel roomPanel = new KPanel(new BorderLayout());
        roomPanel.add(new KPanel(new KLabel("Room:", hintFont)), BorderLayout.WEST);
        roomPanel.add(new KPanel(new KLabel(course.room, valueFont)), BorderLayout.CENTER);

        final KPanel statusPanel = new KPanel(new BorderLayout());
        statusPanel.add(new KPanel(new KLabel("Status:", hintFont)), BorderLayout.WEST);
        final KLabel vLabel = course.isConfirmed ? new KLabel("Confirmed", valueFont, Color.BLUE) :
                new KLabel("Unknown", valueFont, Color.RED);
        statusPanel.add(new KPanel(vLabel), BorderLayout.CENTER);

        final KButton closeButton = new KButton("Close");
        closeButton.addActionListener(e-> dialog.dispose());

        final KPanel contentPanel = new KPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.addAll(codePanel, namePanel, lectPanel, schedulePanel, venuePanel, roomPanel, statusPanel,
                MComponent.contentBottomGap(), new KPanel(closeButton));

        dialog.getRootPane().setDefaultButton(closeButton);
        dialog.setContentPane(contentPanel);
        dialog.pack();
        dialog.setMinimumSize(dialog.getPreferredSize());
        dialog.setLocationRelativeTo(base == null ? Board.getRoot() : base);
        SwingUtilities.invokeLater(()-> dialog.setVisible(true));
    }

    public static void exhibit(RunningCourse runningCourse) throws NullPointerException {
        exhibit(runningCourse, null);
    }

}
