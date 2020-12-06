package main;

import proto.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * The Course type models a course.
 * It has a generalized constructor, passing all the fundamental attributes
 * at that very instance of creation.
 * A course may be verified in two ways:
 * 1) those that are provided to the main.Memory type by main.PrePortal through
 * main.ModulesHandler.STARTUP_COURSES are automatically "verified" set;
 * 2) those that are put into the table by the user can be verified (checked-out) in the Portal
 * by main.ModulesHandler.
 */
public class Course {
    /*
     * This order should remain religious for backward-compatibility and deserialization sake.
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
    private boolean isVerified;
    private boolean lecturerNameChangeability;
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
    public static final String UNKNOWN = Globals.UNKNOWN;


    public Course(String year, String semester, String code, String name, String tutor, String place, String day, String time,
                  double score, int creditHours, String requirement, boolean verified) {
        this.year = year;
        this.semester = semester;
        this.code = code.toUpperCase();
        this.name = name;
        this.lecturer = tutor;
        this.venue = place;
        this.day = day.equals(UNKNOWN) ? "" : day;
        this.time = time.equals(UNKNOWN) ? "" : time;
        this.score = score;
        this.creditHours = creditHours;
        this.isVerified = verified;
        this.lecturerNameChangeability = !verified;
        this.requirement = Globals.hasText(requirement) ? requirement : NONE;
        if (this.requirement.equals(NONE)) {
            try {
                final String requirementPart = this.code.substring(0, 3);
                if (requirementPart.equals(Student.getMajorCode())) {
                    this.setRequirement(MAJOR_OBLIGATORY);
                } else if (requirementPart.equals(Student.getMinorCode())) {
                    this.setRequirement(MINOR_OBLIGATORY);
                } else if (requirementPart.equals(DER)) {
                    this.setRequirement(DIVISIONAL_REQUIREMENT);
                } else if (requirementPart.equals(GER)) {
                    this.setRequirement(GENERAL_REQUIREMENT);
                }
            } catch (StringIndexOutOfBoundsException ignored){
            }
        }
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

    public String getLecturer() {
        return lecturer;
    }

    public void setLecturer(String lecturer, boolean changeable) {
        this.lecturer = lecturer;
        this.lecturerNameChangeability = changeable;
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

    public String getRequirement() {
        return requirement;
    }

    /**
     * The only options passable are those defined in herein
     */
    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        this.isVerified = verified;
    }

//    field assistants

    /**
     * A compound-string of the code and name.
     */
    public String getAbsoluteName() {
        return String.join(" ", code, name);
    }

    /**
     * A compound-string of the year and semester. This is useful, especially
     * in comparing if courses were done in the same semester.
     */
    public String getAbsoluteSemesterName(){
        return String.join(" ", year, semester);
    }

    public boolean isFirstSemester(){
        return semester.equals(Student.FIRST_SEMESTER);
    }

    public boolean isSecondSemester(){
        return semester.equals(Student.SECOND_SEMESTER);
    }

    public boolean isSummerSemester(){
        return semester.equals(Student.SUMMER_SEMESTER);
    }

    public boolean isFirstYear(){
        return year.equals(Student.firstAcademicYear());
    }

    public boolean isSecondYear(){
        return year.equals(Student.secondAcademicYear());
    }

    public boolean isThirdYear(){
        return year.equals(Student.thirdAcademicYear());
    }

    public boolean isFourthYear(){
        return year.equals(Student.fourthAcademicYear());
    }

    /**
     * A course is marked miscellaneous if its year falls out of the student's
     * four years bachelor's program specification.
     */
    public boolean isMisc() {
        final String y = year;
        return !(y.equals(Student.firstAcademicYear()) || y.equals(Student.secondAcademicYear()) ||
                y.equals(Student.thirdAcademicYear()) || y.equals(Student.fourthAcademicYear()));
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

    public boolean isMajor() {
        return requirement.contains("Major");
    }

    public boolean isMajorObligatory() {
        return requirement.equals(MAJOR_OBLIGATORY);
    }

    public boolean isMajorElective() {
        return requirement.equals(MAJOR_OPTIONAL);
    }

    public boolean isMinor() {
        return requirement.contains("Minor");
    }

    public boolean isMinorObligatory() {
        return requirement.equals(MINOR_OBLIGATORY);
    }

    public boolean isMinorElective() {
        return requirement.equals(MINOR_OPTIONAL);
    }

    public boolean isDivisional() {
        return requirement.contains("Divisional");
    }

    public boolean isGeneral() {
        return requirement.contains("General");
    }

    public boolean isUnclassified() {
        return requirement.equals(NONE);
    }

    public String getGrade() {
        return gradeOf(score);
    }

    public String getGradeComment() {
        return gradeCommentOf(score);
    }

    public double getQualityPoint() {
        return pointsOf(getGrade());
    }

    /**
     * A lecturer's name of a module is changeable iff it was not actually found on the Portal
     */
    public boolean canEditTutorName() {
        return Globals.isBlank(lecturer) || lecturerNameChangeability;
    }

    /**
     * Gets the list-index of this course. This is useful for substitution and editing
     */
    public int getListIndex() {
        final List<Course> monitor = ModulesHandler.getModulesMonitor();
        for (int i = 0; i < monitor.size(); i++) {
            if (code.equals(monitor.get(i).code)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Gets a grade based on the given score
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
     * Gets a point based on the grade
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

    /**
     * Exports the contents of this course to a string
     * E.g:
     * 2016/2017
     * First Semester
     * MTH002
     * Calculus 1
     * Amadou Keita
     * ...
     *
     */
    public String exportContent(){
        return year + "\n" +
                semester + "\n" +
                code + "\n" +
                name + "\n" +
                lecturer + "\n" +
                venue + "\n" +
                day + "\n" +
                time + "\n" +
                score + "\n" +
                creditHours + "\n" +
                requirement + "\n" +
                isVerified + "\n" +
                lecturerNameChangeability;
    }

    /**
     * This is a re-construction process of retrieving the course whose exportContent() is this dataLines.
     * Exceptions throwable by this operation must be handled with great care across implementations.
     */
    public static Course importFromSerial(String dataLines) {
        final String[] data = dataLines.split("\n");
        double score = 0D;
        try {
            score = Double.parseDouble(data[8]);
        } catch (Exception e) {
            App.silenceException("Error reading score for "+data[3]);
        }
        int creditsHours = 3;
        try {
            creditsHours = Integer.parseInt(data[9]);
        } catch (Exception e) {
            App.silenceException("Error reading credit hours for "+data[3]);
        }
        boolean isConfirmed = false;
        try {
            isConfirmed = Boolean.parseBoolean(data[11]);
        } catch (Exception e) {
            App.silenceException("Error reading validity of " + data[3]);
        }
        boolean tutorNameChangeability = false;
        try {
            tutorNameChangeability = Boolean.parseBoolean(data[12]);
        } catch (Exception e) {
            App.silenceException("Error reading lecturer name's status of " + data[3]);
        }

        final Course serialCourse = new Course(data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7],
                score, creditsHours, data[10], isConfirmed);
        serialCourse.lecturerNameChangeability = tutorNameChangeability;
        return serialCourse;
    }

    /**
     * When a course is from sync, merge if its like existed
     */
    public static void merge(Course incoming, Course outgoing) {
        incoming.setDay(outgoing.day);
        incoming.setTime(outgoing.time);
        incoming.setVenue(outgoing.venue);
        incoming.setRequirement(outgoing.requirement);
        if (incoming.canEditTutorName()) {
            incoming.setLecturer(outgoing.getLecturer(), true);
        }
    }

    public static String[] availableCoursePeriods(){
        return new String[] {UNKNOWN, "8:00", "8:30", "9:00", "11:00", "11:30", "14:00", "14:30", "15:00", "17:00",
                "17:30", "20:00"};
    }

    public static String[] getWeekDays(){
        return new String[] {UNKNOWN, "Mondays", "Tuesdays", "Wednesdays", "Thursdays", "Fridays", "Saturdays", "Sundays"};
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
     * Nicely exhibits a course.
     * If the course is null, nothing is done.
     */
    public static void exhibit(Component base, Course course){
        if (course == null) {
            return;
        }

        final KDialog dialog = new KDialog(course.name+(course.isMisc() ? " - Miscellaneous" : ""));
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

        final KPanel yearPanel = new KPanel(new BorderLayout());
        yearPanel.add(new KPanel(new KLabel("Academic Year:", hintFont)), BorderLayout.WEST);
        yearPanel.add(new KPanel(new KLabel(course.year, valueFont)), BorderLayout.CENTER);

        final KPanel semesterPanel = new KPanel(new BorderLayout());
        semesterPanel.add(new KPanel(new KLabel("Semester:", hintFont)), BorderLayout.WEST);
        semesterPanel.add(new KPanel(new KLabel(course.semester,valueFont)), BorderLayout.CENTER);

        final KPanel typePanel = new KPanel(new BorderLayout());
        typePanel.add(new KPanel(new KLabel("Requirement:", hintFont)), BorderLayout.WEST);
        typePanel.add(new KPanel(new KLabel(course.requirement, valueFont)), BorderLayout.CENTER);

        final KPanel schedulePanel = new KPanel(new BorderLayout());
        schedulePanel.add(new KPanel(new KLabel("Schedule:", hintFont)), BorderLayout.WEST);
        schedulePanel.add(new KPanel(new KLabel(course.getSchedule(), valueFont)), BorderLayout.CENTER);

        final KPanel venuePanel = new KPanel(new BorderLayout());
        venuePanel.add(new KPanel(new KLabel("Venue:", hintFont)), BorderLayout.WEST);
        venuePanel.add(new KPanel(new KLabel(course.venue, valueFont)), BorderLayout.CENTER);

        final KPanel creditPanel = new KPanel(new BorderLayout());
        creditPanel.add(new KPanel(new KLabel("Credit Hours:", hintFont)), BorderLayout.WEST);
        creditPanel.add(new KPanel(new KLabel(Integer.toString(course.creditHours), valueFont)), BorderLayout.CENTER);

        final KPanel scorePanel = new KPanel(new BorderLayout());
        scorePanel.add(new KPanel(new KLabel("Final Score:", hintFont)), BorderLayout.WEST);
        scorePanel.add(new KPanel(new KLabel(Double.toString(course.score), valueFont)), BorderLayout.CENTER);

        final KPanel gradePanel = new KPanel(new BorderLayout());
        gradePanel.add(new KPanel(new KLabel("Grade:", hintFont)), BorderLayout.WEST);
        gradePanel.add(new KPanel(new KLabel(course.getGrade()+"  ("+course.getGradeComment()+")", valueFont)), BorderLayout.CENTER);

        final KPanel gradeValuePanel = new KPanel(new BorderLayout());
        gradeValuePanel.add(new KPanel(new KLabel("Grade Value:", hintFont)), BorderLayout.WEST);
        gradeValuePanel.add(new KPanel(new KLabel(Double.toString(course.getQualityPoint()), valueFont)), BorderLayout.CENTER);

        final KPanel statusPanel = new KPanel(new BorderLayout());
        statusPanel.add(new KPanel(new KLabel("Status:", hintFont)), BorderLayout.WEST);
        final KLabel vLabel = course.isVerified ? new KLabel("Confirmed", valueFont, Color.BLUE) :
                new KLabel("Unknown", valueFont, Color.RED);
        statusPanel.add(new KPanel(vLabel), BorderLayout.CENTER);

        final KButton closeButton = new KButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());

        final KPanel contentPanel = new KPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.addAll(codePanel, namePanel, lectPanel, yearPanel, semesterPanel, schedulePanel, venuePanel,
                typePanel, creditPanel, scorePanel, gradePanel, gradeValuePanel, statusPanel,
                MComponent.contentBottomGap(), new KPanel(new FlowLayout(FlowLayout.RIGHT), closeButton));
        dialog.getRootPane().setDefaultButton(closeButton);
        dialog.setContentPane(contentPanel);
        dialog.pack();
        dialog.setMinimumSize(dialog.getPreferredSize());
        dialog.setLocationRelativeTo(base == null ? Board.getRoot() : base);
        SwingUtilities.invokeLater(()-> dialog.setVisible(true));
    }

    public static void exhibit(Course c){
        exhibit(null, c);
    }

}
