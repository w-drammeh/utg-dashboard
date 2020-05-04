package main;

import customs.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * The course model with almost all the concrete attributes of a course.
 * It has a generalized constructor, passing all the fields at that very instance of creation.
 * A course may be verified in only two ways: those that are provided to the Memory type by
 * PrePortal are automatically "verified" set; those that are put into the table by the
 * user can be verified through the Portal.
 */
public class Course {
    /*
     * And this is how they're passed to the constructor. This order should remain religious
     * for backward-compatibility sake and de-serialization.
     */
    private String year;
    private String semester;
    private String code;
    private String name;
    private String lecturer;
    private String venue;
    private String day;
    private String time;
    private double score;
    private int creditHours;
    private String requirement;
    private boolean isValidated;
    private boolean tutorsNameIsCustomizable = true;
//    Requirement options
    public static final String MAJOR_OBLIGATORY = "Major Obligatory";
    public static final String MINOR_OBLIGATORY = "Minor Obligatory";
    public static final String MAJOR_OPTIONAL = "Major Elective";
    public static final String MINOR_OPTIONAL = "Minor Elective";
    public static final String DIVISIONAL_REQUIREMENT = "Divisional Requirement";
    public static final String GENERAL_REQUIREMENT = "General Requirement";
    public static final String NONE = Globals.NONE;
//    Known divisional codes
    public static final String DER = "DER";
    public static final String GER = "GER";
    /**
     * The unknown constant
     */
    public static final String UNKNOWN = "Unknown";
    /**
     * For separating multiple values in a single line serial data.
     */
    public static final String VALUE_SEPARATOR = "::";


    /**
     * Do not give null to these args., give the empty string instead.
     * At least for this implementation.
     */
    public Course(String year, String semester, String id, String name, String tutor, String place, String day, String time,
                  double score, int creditHours, String requirement, boolean validity){
        this.year = year;
        this.semester = semester;
        this.code = id.toUpperCase();
        this.name = name;
        this.lecturer = tutor;
        this.venue = place;
        this.day = day;
        this.time = time;
        this.score = score;
        this.creditHours = creditHours;
        this.requirement = Globals.isBlank(requirement) ? NONE : requirement;
        this.isValidated = validity;

        if (requirement.equals(NONE)) {
            try {
                final String vitalPart = code.substring(0, 3);
                if (vitalPart.equals(Student.getMajorCode())) {
                    this.setRequirement(MAJOR_OBLIGATORY);
                } else if (vitalPart.equals(Student.getMinorCode())) {
                    this.setRequirement(MINOR_OBLIGATORY);
                } else if (vitalPart.equals(DER)) {
                    this.setRequirement(DIVISIONAL_REQUIREMENT);
                } else if (vitalPart.equals(GER)) {
                    this.setRequirement(GENERAL_REQUIREMENT);
                }
            } catch (StringIndexOutOfBoundsException e){
                App.silenceException("Malformed code -"+code+". Requirement could not be determined.");
            }
        }
    }

    /**
     * This is a re-construction process of retrieving the course whose exportContent() is this dataLines.
     * Exceptions throwable by this operation must be handled with great care across implementations.
     */
    public static Course importFromSerial(String dataLines){
        final String[] data = dataLines.split("\n");
        double score = 0D;
        try {
            score = Double.parseDouble(data[8]);
        } catch (Exception e) {
            App.silenceException("Warning: error reading score for "+data[3]);
        }
        int credits = 3;
        try {
            credits = Integer.parseInt(data[9]);
        } catch (Exception e) {
            App.silenceException("Warning: error reading credit hours for "+data[3]);
        }
        boolean state = false;
        try {
            state = Boolean.parseBoolean(data[11]);
        } catch (Exception e) {
            App.silenceException("Warning: error reading validity of "+data[3]);
        }

        final String[] lectComponents = data[4].split(VALUE_SEPARATOR);
        final String lecturerName = lectComponents[0];
        final boolean lecturerNameEditable = Boolean.parseBoolean(lectComponents[1]);
        final Course serialCourse = new Course(data[0], data[1], data[2], data[3], lecturerName, data[5], data[6], data[7],
                score, credits, data[10], state);
        serialCourse.setLecturer(lecturerName, lecturerNameEditable);
        return serialCourse;
    }

    /**
     * Gets a grade based on the score.
     */
    private static String gradeOf(double score) {
        String grade = "F";//0-39, 0
        if (score > 39 && score < 50) {
            grade = "D";//40-49, 1
        } else if (score > 49 && score < 54) {
            grade = "C-";//50-53, 1.7
        } else if (score > 53 && score < 57) {
            grade = "C";//54-56, 2
        } else if (score > 56 && score < 60) {
            grade = "C+";//57-59, 2.3
        } else if (score > 59 && score < 64) {
            grade = "B-";//60-63, 2.7
        } else if (score > 63 && score < 67) {
            grade = "B";//64-66, 3
        } else if (score > 66 && score < 70) {
            grade = "B+";//67-69, 3.3
        } else if (score > 69 && score < 80) {
            grade = "A-";//70-79, 3.7
        } else if (score > 79 && score < 90) {
            grade = "A";//80-89, 4
        } else if (score > 89 && score < 101) {
            grade = "A+";//90-100, 4.3
        }

        return grade;
    }

    /**
     * Assigns a comment to the grade / score. E.g "Excellent", "Fails", etc.
     */
    private static String gradeCommentOf(double score){
        if (score >= 70) {
            return "Excellent";
        } else if (score >= 60) {
            return "Good";
        } else if (score >= 50) {
            return "Satisfactory";
        } else if (score >= 40) {
            return "Marginal Pass";
        } else {
            return "Fail";
        }
    }

    /**
     * Gets a point based on the grade.
     */
    private static double pointsOf(String grade){
        double point = 0;
        switch (grade) {
            case "D":
                point = 1.0;
                break;
            case "C-":
                point = 1.7;
                break;
            case "C":
                point = 2.0;
                break;
            case "C+":
                point = 2.3;
                break;
            case "B-":
                point = 2.7;
                break;
            case "B":
                point = 3.0;
                break;
            case "B+":
                point = 3.3;
                break;
            case "A-":
                point = 3.7;
                break;
            case "A":
                point = 4.0;
                break;
            case "A+":
                point = 4.3;
                break;
        }
        return point;
    }

    public static String[] availableCoursePeriods(){
        return new String[] {UNKNOWN, "8:00", "8:30", "9:00", "11:00", "11:30", "14:00", "14:30", "15:00", "17:00",
                "17:30", "20:00"};
    }

    public static String[] getWeekDays(){
        return new String[]{UNKNOWN, "Mondays", "Tuesdays", "Wednesdays", "Thursdays", "Fridays", "Saturdays", "Sundays"};
    }

    /**
     * All requirement boxes must delegate to this as their list of options.
     */
    public static String[] availableCourseRequirements(){
        return new String[] {MAJOR_OBLIGATORY, MAJOR_OPTIONAL, MINOR_OBLIGATORY, MINOR_OPTIONAL, DIVISIONAL_REQUIREMENT,
                GENERAL_REQUIREMENT, NONE};
    }

    /**
     * The elements returned herein are safe to be int-casted.
     */
    public static String[] availableCreditHours(){
        return new String[] {"3", "4"};
    }

    /**
     * When a course is from sync, it should be merged if its like existed
     */
    public static void merge(Course incoming, Course outgoing) {
        incoming.setDay(outgoing.getDay());
        incoming.setTime(outgoing.getTime());
        incoming.setVenue(outgoing.getVenue());
        incoming.setRequirement(outgoing.getRequirement());
        final boolean incomingIsLecturerSet = Globals.hasText(incoming.getLecturer());
        incoming.setLecturer(incomingIsLecturerSet ? incoming.getLecturer() : outgoing.getLecturer(),
                incomingIsLecturerSet);
    }

    /**
     * Nicely exhibits a course. This is to be an instance-call in a future-release.
     */
    public static void exhibit(Component base, Course course){
        final KDialog exhibitDialog = new KDialog(course.getName()+(course.isMisc() ? " - Miscellaneous" : ""));
        exhibitDialog.setResizable(true);
        exhibitDialog.setModalityType(KDialog.DEFAULT_MODALITY_TYPE);

        final Font hintFont = KFontFactory.createBoldFont(15);
        final Font valueFont = KFontFactory.createPlainFont(15);

        final KPanel codePanel = new KPanel(new BorderLayout());
        codePanel.add(KPanel.wantDirectAddition(new KLabel("Code:",hintFont)),BorderLayout.WEST);
        codePanel.add(KPanel.wantDirectAddition(new KLabel(course.getCode(),valueFont)),BorderLayout.CENTER);

        final KPanel namePanel = new KPanel(new BorderLayout());
        namePanel.add(KPanel.wantDirectAddition(new KLabel("Name:",hintFont)),BorderLayout.WEST);
        namePanel.add(KPanel.wantDirectAddition(new KLabel(course.getName(),valueFont)),BorderLayout.CENTER);

        final KPanel lectPanel = new KPanel(new BorderLayout());
        lectPanel.add(KPanel.wantDirectAddition(new KLabel("Tutor:",hintFont)),BorderLayout.WEST);
        lectPanel.add(KPanel.wantDirectAddition(new KLabel(course.getLecturer(),valueFont)),BorderLayout.CENTER);

        final KPanel yearPanel = new KPanel(new BorderLayout());
        yearPanel.add(KPanel.wantDirectAddition(new KLabel("Academic Year:",hintFont)),BorderLayout.WEST);
        yearPanel.add(KPanel.wantDirectAddition(new KLabel(course.getYear(),valueFont)),BorderLayout.CENTER);

        final KPanel semesterPanel = new KPanel(new BorderLayout());
        semesterPanel.add(KPanel.wantDirectAddition(new KLabel("Semester:",hintFont)),BorderLayout.WEST);
        semesterPanel.add(KPanel.wantDirectAddition(new KLabel(course.getSemester(),valueFont)),BorderLayout.CENTER);

        final KPanel typePanel = new KPanel(new BorderLayout());
        typePanel.add(KPanel.wantDirectAddition(new KLabel("Requirement:",hintFont)),BorderLayout.WEST);
        typePanel.add(KPanel.wantDirectAddition(new KLabel(course.getRequirement(),valueFont)),BorderLayout.CENTER);

        final KPanel schedulePanel = new KPanel(new BorderLayout());
        schedulePanel.add(KPanel.wantDirectAddition(new KLabel("Schedule:",hintFont)),BorderLayout.WEST);
        schedulePanel.add(KPanel.wantDirectAddition(new KLabel(course.schedule(),valueFont)),BorderLayout.CENTER);

        final KPanel venuePanel = new KPanel(new BorderLayout());
        venuePanel.add(KPanel.wantDirectAddition(new KLabel("Venue:",hintFont)),BorderLayout.WEST);
        venuePanel.add(KPanel.wantDirectAddition(new KLabel(course.getVenue(),valueFont)),BorderLayout.CENTER);

        final KPanel creditPanel = new KPanel(new BorderLayout());
        creditPanel.add(KPanel.wantDirectAddition(new KLabel("Credit Hours:",hintFont)),BorderLayout.WEST);
        creditPanel.add(KPanel.wantDirectAddition(new KLabel(course.getCreditHours()+"",valueFont)),BorderLayout.CENTER);

        final KPanel scorePanel = new KPanel(new BorderLayout());
        scorePanel.add(KPanel.wantDirectAddition(new KLabel("Final Score:",hintFont)),BorderLayout.WEST);
        scorePanel.add(KPanel.wantDirectAddition(new KLabel(course.getScore()+"",valueFont)),BorderLayout.CENTER);

        final KPanel gradePanel = new KPanel(new BorderLayout());
        gradePanel.add(KPanel.wantDirectAddition(new KLabel("Grade:",hintFont)),BorderLayout.WEST);
        gradePanel.add(KPanel.wantDirectAddition(new KLabel(course.getGrade()+"  ["+course.getGradeComment()+"]",valueFont)),BorderLayout.CENTER);

        final KPanel gradeValuePanel = new KPanel(new BorderLayout());
        gradeValuePanel.add(KPanel.wantDirectAddition(new KLabel("Grade Value:",hintFont)),BorderLayout.WEST);
        gradeValuePanel.add(KPanel.wantDirectAddition(new KLabel(course.getQualityPoint()+"",valueFont)),BorderLayout.CENTER);

        final KPanel statusPanel = new KPanel(new BorderLayout());
        statusPanel.add(KPanel.wantDirectAddition(new KLabel("Status:",hintFont)),BorderLayout.WEST);
        statusPanel.add(KPanel.wantDirectAddition(new KLabel(course.isVerified() ? "Confirmed" : "Unverified",valueFont,course.isVerified() ? Color.BLUE : Color.RED)),BorderLayout.CENTER);

        final KButton closeButton = new KButton("Close");
        closeButton.addActionListener(e -> exhibitDialog.dispose());

        final KPanel contentPanel = new KPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.addAll(codePanel,namePanel,lectPanel,yearPanel,semesterPanel,schedulePanel,venuePanel,typePanel,creditPanel,scorePanel,gradePanel,gradeValuePanel,statusPanel,
                ComponentAssistant.contentBottomGap(),KPanel.wantDirectAddition(new FlowLayout(FlowLayout.RIGHT),null,closeButton));

        exhibitDialog.getRootPane().setDefaultButton(closeButton);
        exhibitDialog.setContentPane(contentPanel);
        exhibitDialog.pack();
        exhibitDialog.setMinimumSize(exhibitDialog.getPreferredSize());
        exhibitDialog.setLocationRelativeTo(base == null ? Board.getRoot() : base);
        exhibitDialog.setVisible(true);
    }

    public static void exhibit(Course c){
        exhibit(Board.getRoot(), c);
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    /**
     * Returns a compound-string of the year and semester. This is useful, especially
     * in comparing if courses were done in the same semester.
     */
    public String getAbsoluteSemesterName(){
        return this.getYear()+" "+this.getSemester();
    }

    public boolean isFirstSemester(){
        return this.getSemester().equals(Student.FIRST_SEMESTER);
    }

    public boolean isSecondSemester(){
        return this.getSemester().equals(Student.SECOND_SEMESTER);
    }

    public boolean isSummerSemester(){
        return this.getSemester().equals(Student.SUMMER_SEMESTER);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbsoluteName() {
        return code+" "+name;
    }

    public String getLecturer() {
        return lecturer;
    }

    public void setLecturer(String lecturer, boolean allowChange) {
        this.lecturer = lecturer;
        this.tutorsNameIsCustomizable = allowChange;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public String getRequirement() {
        return requirement;
    }

    /**
     * The only options passable are those defined in herein the Course type.
     */
    public void setRequirement(String newRequirement) {
        this.requirement = newRequirement;
    }

    public boolean isMajor() {
        return this.getRequirement().contains("Major");
    }

    public boolean isMajorObligatory() {
        return this.getRequirement().equals(MAJOR_OBLIGATORY);
    }

    public boolean isMajorElective() {
        return this.getRequirement().equals(MAJOR_OPTIONAL);
    }

    public boolean isMinor() {
        return this.getRequirement().contains("Minor");
    }

    public boolean isMinorObligatory() {
        return this.getRequirement().equals(MINOR_OBLIGATORY);
    }

    public boolean isMinorElective() {
        return this.getRequirement().equals(MINOR_OPTIONAL);
    }

    public boolean isDivisional() {
        return this.getRequirement().contains("Divisional");
    }

    public boolean isGeneral() {
        return this.getRequirement().contains("General");
    }

    public boolean isUnclassified() {
        return this.getRequirement().equals(NONE);
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getCreditHours() {
        return creditHours;
    }

    public void setCreditHours(int creditHours) {
        this.creditHours = creditHours;
    }

    public boolean isVerified() {
        return isValidated;
    }

    public String getGrade() {
        return gradeOf(this.getScore());
    }

    public String getGradeComment() {
        return gradeCommentOf(this.getScore());
    }

    public double getQualityPoint(){
        return pointsOf(this.getGrade());
    }

    /**
     * A lecturer's name of a module is changeable iff it was not actually found on the portal.
     */
    public boolean isTutorsNameCustomizable(){
        return Globals.hasText(lecturer) || tutorsNameIsCustomizable;
    }

    /**
     * Gets the list-index of this course. This is useful for substitution and editing.
     */
    public int getListIndex(){
        final List<Course> list = ModulesHandler.getModulesMonitor();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).code.equals(this.code)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Exports the contents of this course to a string in value-line format.
     * E.g:
     * Calculus 1
     * Amadou Keita
     * ...
     *
     */
    public String exportContent(){
        return year+"\n" +
                semester+"\n" +
                code+"\n" +
                name+"\n" +
                lecturer+VALUE_SEPARATOR+isTutorsNameCustomizable()+"\n" +
                venue+"\n" +
                day+"\n" +
                time+"\n" +
                score+"\n" +
                creditHours+"\n" +
                requirement+"\n" +
                isValidated+"\n";
    }

    /**
     * A course is marked <strong>misc</strong> if its year falls out of the student's
     * four years bachelor's program specification.
     */
    public boolean isMisc(){
        final String y = this.getYear();
        return !(y.equals(Student.firstAcademicYear()) || y.equals(Student.secondAcademicYear()) || y.equals(Student.thirdAcademicYear()) || y.equals(Student.finalAcademicYear()));
    }

}
