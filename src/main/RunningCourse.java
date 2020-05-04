package main;

import customs.*;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

/**
 * A model for the registered courses.
 * A weaker form with limited resources.
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


    public RunningCourse(String code, String name, String lName, String vName, String rName, String day, String time, boolean onPortal){
        this.code = code.toUpperCase();
        this.name = name;
        this.lecturer = lName;
        this.venue = vName;
        this.room = rName;
        this.day = day.equals(Course.UNKNOWN) ? "" : day;
        this. time = time.equals(Course.UNKNOWN) ? "" : time;
        this.isConfirmed = onPortal;
    }

    /**
     * See Course.importFromSerial(String)
     */
    public static RunningCourse importFromSerial(String dataLines){
        final String[] data = dataLines.split("\n");
        boolean onPortalState = false;
        try {
            onPortalState = Boolean.parseBoolean(data[7]);
        } catch (Exception e) {
            App.silenceException("Warning: error reading validity of the registered course "+data[3]);
        }

        return new RunningCourse(data[0], data[1], data[2], data[3], data[4], data[5], data[6], onPortalState);
    }

    public static void exhibit(Component base, RunningCourse activeCourse){
        final KDialog exhibitDialog = new KDialog(activeCourse.getName());
        exhibitDialog.setResizable(true);
        exhibitDialog.setModalityType(KDialog.DEFAULT_MODALITY_TYPE);

        final Font hintFont = KFontFactory.createBoldFont(15);
        final Font valueFont = KFontFactory.createPlainFont(15);

        final KPanel codePanel = new KPanel(new BorderLayout());
        codePanel.add(KPanel.wantDirectAddition(new KLabel("Code:",hintFont)),BorderLayout.WEST);
        codePanel.add(KPanel.wantDirectAddition(new KLabel(activeCourse.code,valueFont)),BorderLayout.CENTER);

        final KPanel namePanel = new KPanel(new BorderLayout());
        namePanel.add(KPanel.wantDirectAddition(new KLabel("Name:",hintFont)),BorderLayout.WEST);
        namePanel.add(KPanel.wantDirectAddition(new KLabel(activeCourse.name,valueFont)),BorderLayout.CENTER);

        final KPanel lectPanel = new KPanel(new BorderLayout());
        lectPanel.add(KPanel.wantDirectAddition(new KLabel("Lecturer:",hintFont)),BorderLayout.WEST);
        lectPanel.add(KPanel.wantDirectAddition(new KLabel(activeCourse.lecturer,valueFont)),BorderLayout.CENTER);

        final KPanel schedulePanel = new KPanel(new BorderLayout());
        schedulePanel.add(KPanel.wantDirectAddition(new KLabel("Schedule:",hintFont)),BorderLayout.WEST);
        schedulePanel.add(KPanel.wantDirectAddition(new KLabel(activeCourse.schedule(),valueFont)),BorderLayout.CENTER);

        final KPanel venuePanel = new KPanel(new BorderLayout());
        venuePanel.add(KPanel.wantDirectAddition(new KLabel("Venue:",hintFont)),BorderLayout.WEST);
        venuePanel.add(KPanel.wantDirectAddition(new KLabel(activeCourse.venue,valueFont)),BorderLayout.CENTER);

        final KPanel roomPanel = new KPanel(new BorderLayout());
        roomPanel.add(KPanel.wantDirectAddition(new KLabel("Room:",hintFont)),BorderLayout.WEST);
        roomPanel.add(KPanel.wantDirectAddition(new KLabel(activeCourse.room,valueFont)),BorderLayout.CENTER);

        final KPanel statusPanel = new KPanel(new BorderLayout());
        statusPanel.add(KPanel.wantDirectAddition(new KLabel("Status:",hintFont)),BorderLayout.WEST);
        statusPanel.add(KPanel.wantDirectAddition(new KLabel((activeCourse.isConfirmed ? "Confirmed" : "Unknown"), valueFont, (activeCourse.isConfirmed ? Color.BLUE : Color.RED))),BorderLayout.CENTER);

        final KButton closeButton = new KButton("Close");
        closeButton.addActionListener(e -> exhibitDialog.dispose());

        final KPanel contentPanel = new KPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.addAll(codePanel, namePanel, lectPanel, schedulePanel, venuePanel, roomPanel, statusPanel,
                ComponentAssistant.contentBottomGap(), KPanel.wantDirectAddition(new FlowLayout(FlowLayout.RIGHT),null,closeButton));

        exhibitDialog.getRootPane().setDefaultButton(closeButton);
        exhibitDialog.setContentPane(contentPanel);
        exhibitDialog.pack();
        exhibitDialog.setMinimumSize(exhibitDialog.getPreferredSize());
        exhibitDialog.setLocationRelativeTo(base == null ? Board.getRoot() : base);
        exhibitDialog.setVisible(true);
    }

    public static void exhibit(RunningCourse runningCourse){
        exhibit(null, runningCourse);
    }

    /**
     * See Course.exportContent()
     */
    public String exportContent(){
        return code+"\n" +
                name+"\n" +
                lecturer+"\n" +
                venue+"\n" +
                room+"\n" +
                day+"\n" +
                time+"\n" +
                isConfirmed+"\n";
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

    public String schedule(){
        if (Globals.hasText(day) && Globals.hasText(time)) {
            return String.join(" ", day, time);
        } else if (Globals.hasText(day) && !Globals.hasText(time)) {
            return String.join(" - ", day, "Unknown time");
        } else if (!Globals.hasText(day) && Globals.hasText(time)) {
            return String.join(" - ", time, "Unknown day");
        } else {
            return "";
        }
    }

    public boolean isConfirmed(){
        return isConfirmed;
    }

    public void setConfirmed(boolean onPortal){
        this.isConfirmed = onPortal;
    }

}
