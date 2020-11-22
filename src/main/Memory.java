package main;

import java.util.ArrayList;

/**
 * It's the root-center of student analysis.
 * Among the things it does is to keep track of verified courses to be supplied in the Transcript
 * and Analysis.
 */
public class Memory {
    /**
     * There should be no chance of adding unverified modules.
     * Accessors of this list - noticeably, the Transcript and Analysis - should refresh calls
     * prior to any activity-answer.
     * This list must remain updated by the monitor in ModulesHandler.
     * Never directly add or withdraw from this list - all such must be directed
     * by the monitor at main.ModulesHandler
     */
    private static final ArrayList<Course> VERIFIED_LIST = new ArrayList<>() {
        @Override
        public boolean add(Course course) {
            return course.isVerified() && super.add(course);
        }
    };

    /**
     * May be called only by the modulesMonitor at ModulesHandler
     */
    public static void mayRemember(Course course){
        if (VERIFIED_LIST.add(course)) {
            TranscriptGenerator.TRANSCRIPT_MODEL.addRow(new String[] {course.getCode(), course.getName(),
                    Integer.toString(course.getCreditHours()), course.getGrade(), Double.toString(course.getQualityPoint())});
        }
    }

    /**
     * May be called only by the modulesMonitor at ModulesHandler
     */
    public static void mayForget(Course course){
        if (VERIFIED_LIST.remove(course)) {
            TranscriptGenerator.TRANSCRIPT_MODEL.removeRow(TranscriptGenerator.TRANSCRIPT_MODEL.getRowOf(course.getCode()));
        }
    }

    /**
     * May be called only by the modulesMonitor at ModulesHandler
     */
    public static void mayReplace(Course outgoing, Course incoming){
        final int t = indexOf(outgoing.getCode());
        if (t >= 0) {
            VERIFIED_LIST.set(t, incoming);
        }
    }

    public static int indexOf(String code){
        for (int i = 0; i < VERIFIED_LIST.size(); i++) {
            if (VERIFIED_LIST.get(i).getCode().equalsIgnoreCase(code)) {
                return i;
            }
        }
        return -1;
    }

    public static ArrayList<Course> listRequested(){
        return VERIFIED_LIST;
    }

    /**
     * Returns a course which can be used for experimentation by this class of experimentation.
     */
    private static Course newDefaultCourse(){
        return new Course("", "", "", "", "", "", "", "", 0.0, 0, "", true);
    }

//    Filterers -
    public static ArrayList<String> filterAcademicYears(){
        final ArrayList<String> requiredList = new ArrayList<>();
        for (Course c : VERIFIED_LIST) {
            if (!requiredList.contains(c.getYear())) {
                requiredList.add(c.getYear());
            }
        }
        return requiredList;
    }

    public static ArrayList<String> filterSemesters(){
        final ArrayList<String> requiredList = new ArrayList<>();
        for (String yearName : filterAcademicYears()) {
            for (Course course : VERIFIED_LIST) {
                if (course.isFirstSemester() && course.getYear().equals(yearName)) {
                    requiredList.add(yearName+" "+Student.FIRST_SEMESTER);
                    break;
                }
            }
            for (Course course : VERIFIED_LIST) {
                if (course.isSecondSemester() && course.getYear().equals(yearName)) {
                    requiredList.add(yearName+" "+Student.SECOND_SEMESTER);
                    break;
                }
            }
            for (Course course : VERIFIED_LIST) {
                if (course.isSummerSemester() && course.getYear().equals(yearName)) {
                    requiredList.add(yearName+" "+Student.SUMMER_SEMESTER);
                    break;
                }
            }
        }
        return requiredList;
    }

    public static ArrayList<String> filterLecturers(){
        final ArrayList<String> requiredList = new ArrayList<>();
        for (Course c : VERIFIED_LIST) {
            if (!(requiredList.contains(c.getLecturer()) || Globals.isBlank(c.getLecturer()))) {
                requiredList.add(c.getLecturer());
            }
        }
        return requiredList;
    }

//    Partitioners - return a fraction of the list fitting a condition
//    Some are, of course, little bit more specific. They may return empty-lists, never null
    public static ArrayList<Course> getFractionByGrade(String grade){
        final ArrayList<Course> requiredList = new ArrayList<>();
        for (Course c : VERIFIED_LIST) {
            if (c.getGrade().equals(grade)) {
                requiredList.add(c);
            }
        }
        return requiredList;
    }

    public static ArrayList<Course> getMajors(){
        final ArrayList<Course> requiredList = new ArrayList<>();
        for (Course c : VERIFIED_LIST) {
            if (c.isMajor()) {
                requiredList.add(c);
            }
        }
        return requiredList;
    }

    public static ArrayList<Course> getMajorsBySemester(String sName){
        final ArrayList<Course> requiredList = new ArrayList<>();
        for (Course c : getFractionBySemester(sName)) {
            if (c.isMajor()) {
                requiredList.add(c);
            }
        }
        return requiredList;
    }

    public static ArrayList<Course> getMajorsByYear(String yName){
        final ArrayList<Course> requiredList = new ArrayList<>();
        for (Course c : getFractionByYear(yName)) {
            if (c.isMajor()) {
                requiredList.add(c);
            }
        }
        return requiredList;
    }

    public static ArrayList<Course> getMinors(){
        final ArrayList<Course> requiredList = new ArrayList<>();
        for (Course c : VERIFIED_LIST) {
            if (c.isMinor()) {
                requiredList.add(c);
            }
        }
        return requiredList;
    }

    public static ArrayList<Course> getMinorsBySemester(String sName){
        final ArrayList<Course> requiredList = new ArrayList<>();
        for (Course c : getFractionBySemester(sName)) {
            if (c.isMinor()) {
                requiredList.add(c);
            }
        }
        return requiredList;
    }

    public static ArrayList<Course> getMinorsByYear(String yName){
        final ArrayList<Course> requiredList = new ArrayList<>();
        for (Course c : getFractionByYear(yName)) {
            if (c.isMinor()) {
                requiredList.add(c);
            }
        }
        return requiredList;
    }

    public static ArrayList<Course> getDERs(){
        final ArrayList<Course> requiredList = new ArrayList<>();
        for (Course c : VERIFIED_LIST) {
            if (c.isDivisional()) {
                requiredList.add(c);
            }
        }
        return requiredList;
    }

    public static ArrayList<Course> getDERsBySemester(String sName){
        final ArrayList<Course> requiredList = new ArrayList<>();
        for (Course c : getFractionBySemester(sName)) {
            if (c.isDivisional()) {
                requiredList.add(c);
            }
        }
        return requiredList;
    }

    public static ArrayList<Course> getDERsByYear(String yName){
        final ArrayList<Course> requiredList = new ArrayList<>();
        for (Course c : getFractionByYear(yName)) {
            if (c.isDivisional()) {
                requiredList.add(c);
            }
        }
        return requiredList;
    }

    public static ArrayList<Course> getGERs(){
        final ArrayList<Course> requiredList = new ArrayList<>();
        for (Course c : VERIFIED_LIST) {
            if (c.isGeneral()) {
                requiredList.add(c);
            }
        }
        return requiredList;
    }

    public static ArrayList<Course> getGERsBySemester(String sName){
        final ArrayList<Course> requiredList = new ArrayList<>();
        for (Course c : getFractionBySemester(sName)) {
            if (c.isGeneral()) {
                requiredList.add(c);
            }
        }
        return requiredList;
    }

    public static ArrayList<Course> getGERsByYear(String yName){
        final ArrayList<Course> requiredList = new ArrayList<>();
        for (Course c : getFractionByYear(yName)) {
            if (c.isGeneral()) {
                requiredList.add(c);
            }
        }
        return requiredList;
    }

    public static ArrayList<Course> getUnknowns(){
        final ArrayList<Course> requiredList = new ArrayList<>();
        for (Course c : VERIFIED_LIST) {
            if (c.isUnclassified()) {
                requiredList.add(c);
            }
        }
        return requiredList;
    }

    public static ArrayList<Course> getUnknownsBySemester(String sName){
        final ArrayList<Course> requiredList = new ArrayList<>();
        for (Course c : getFractionBySemester(sName)) {
            if (c.isUnclassified()) {
                requiredList.add(c);
            }
        }
        return requiredList;
    }

    public static ArrayList<Course> getUnknownsByYear(String yName){
        final ArrayList<Course> requiredList = new ArrayList<>();
        for (Course c : getFractionByYear(yName)) {
            if (c.isUnclassified()) {
                requiredList.add(c);
            }
        }
        return requiredList;
    }

    public static ArrayList<String> getLecturersByYear(String yearName){
        final ArrayList<String> requiredList = new ArrayList<>();
        for (Course c : getFractionByYear(yearName)) {
            if (!(requiredList.contains(c.getLecturer()) || Globals.isBlank(c.getLecturer()))) {
                requiredList.add(c.getLecturer());
            }
        }
        return requiredList;
    }

    public static ArrayList<Course> getFractionByLecturer(String lName){
        final ArrayList<Course> requiredList = new ArrayList<>();
        for (Course c : VERIFIED_LIST) {
            if (!Globals.isBlank(c.getLecturer()) && c.getLecturer().equals(lName)) {
                requiredList.add(c);
            }
        }
        return requiredList;
    }

    public static ArrayList<Course> getFractionByLecturer(String lName, String yName){
        final ArrayList<Course> requiredList = new ArrayList<>();
        for (Course c : getFractionByYear(yName)) {
            if (c.getLecturer().equals(lName)) {
                requiredList.add(c);
            }
        }
        return requiredList;
    }

    public static ArrayList<Course> getFractionBySemester(String sName){
        final ArrayList<Course> requiredList = new ArrayList<>();
        for (Course c : VERIFIED_LIST) {
            if (c.getAbsoluteSemesterName().equals(sName)) {
                requiredList.add(c);
            }
        }
        return requiredList;
    }

    public static ArrayList<Course> getFractionByYear(String yName){
        final ArrayList<Course> requiredList = new ArrayList<>();
        for (Course c : VERIFIED_LIST) {
            if (c.getYear().equals(yName)) {
                requiredList.add(c);
            }
        }
        return requiredList;
    }


//    Tracers -
//    These can give back null
    public static Course traceHighestScore_Overall(){
        if (VERIFIED_LIST.isEmpty()) {
            return null;
        } else {
            Course requiredCourse = newDefaultCourse();
            for (Course someCourse : VERIFIED_LIST) {
                if (someCourse.getScore() > requiredCourse.getScore()) {
                    requiredCourse = someCourse;
                }
            }
            return requiredCourse;
        }
    }

    public static Course traceLowestScore_Overall(){
        if (VERIFIED_LIST.isEmpty()) {
            return null;
        } else {
            Course requiredCourse = newDefaultCourse();
            requiredCourse.setScore(100D);
            for (Course instantCourse : VERIFIED_LIST) {
                if (instantCourse.getScore() < requiredCourse.getScore()) {
                    requiredCourse = instantCourse;
                }
            }
            return requiredCourse;
        }
    }

    public static Course traceHighestScore_Major(){
        if (getMajors().isEmpty()) {
            return null;
        } else {
            Course requiredCourse = newDefaultCourse();
            for (Course someCourse : getMajors()) {
                if (someCourse.getScore() > requiredCourse.getScore()) {
                    requiredCourse = someCourse;
                }
            }
            return requiredCourse;
        }
    }

    public static Course traceLowestScore_Major(){
        if (getMajors().isEmpty()) {
            return null;
        } else {
            Course requiredCourse = newDefaultCourse();
            requiredCourse.setScore(100D);
            for (Course someCourse : getMajors()) {
                if (someCourse.getScore() < requiredCourse.getScore()) {
                    requiredCourse = someCourse;
                }
            }
            return requiredCourse;
        }
    }

    public static Course traceHighestScore_Minor(){
        if (getMinors().isEmpty()) {
            return null;
        } else {
            Course requiredCourse = newDefaultCourse();
            for (Course someCourse : getMinors()) {
                if (someCourse.getScore() > requiredCourse.getScore()) {
                    requiredCourse = someCourse;
                }
            }
            return requiredCourse;
        }
    }

    public static Course traceLowestScore_Minor(){
        if (getMinors().isEmpty()) {
            return null;
        } else {
            Course requiredCourse = newDefaultCourse();
            requiredCourse.setScore(100D);
            for (Course someCourse : getMinors()) {
                if (someCourse.getScore() < requiredCourse.getScore()) {
                    requiredCourse = someCourse;
                }
            }
            return requiredCourse;
        }
    }

    public static Course traceHighestScore_DER(){
        if (getDERs().isEmpty()) {
            return null;
        } else {
            Course requiredCourse = newDefaultCourse();
            for (Course someCourse : getDERs()) {
                if (someCourse.getScore() > requiredCourse.getScore()) {
                    requiredCourse = someCourse;
                }
            }
            return requiredCourse;
        }
    }

    public static Course traceLowestScore_DER(){
        if (getDERs().isEmpty()) {
            return null;
        } else {
            Course requiredCourse = newDefaultCourse();
            requiredCourse.setScore(100D);
            for (Course someCourse : getDERs()) {
                if (someCourse.getScore() < requiredCourse.getScore()) {
                    requiredCourse = someCourse;
                }
            }
            return requiredCourse;
        }
    }

    public static Course traceHighestScore_GER(){
        if (getGERs().isEmpty()) {
            return null;
        } else {
            Course requiredCourse = newDefaultCourse();
            for (Course someCourse : getGERs()) {
                if (someCourse.getScore() > requiredCourse.getScore()) {
                    requiredCourse = someCourse;
                }
            }
            return requiredCourse;
        }
    }

    public static Course traceLowestScore_GER(){
        if (getGERs().isEmpty()) {
            return null;
        } else {
            Course requiredCourse = newDefaultCourse();
            requiredCourse.setScore(100D);
            for (Course someCourse : getGERs()) {
                if (someCourse.getScore() < requiredCourse.getScore()) {
                    requiredCourse = someCourse;
                }
            }
            return requiredCourse;
        }
    }

    public static double getCGPAByYear(String yName){
        if (filterAcademicYears().isEmpty()) {
            return 0D;
        } else {
            double totalCG = 0D;
            int i = 0;
            for (Course c : VERIFIED_LIST) {
                if (c.getYear().equals(yName)) {
                    totalCG += c.getQualityPoint();
                    i++;
                }
            }
            return totalCG/i;
        }
    }

    public static double getCGPABySemester(String sName){
        if (filterSemesters().isEmpty()) {
            return 0D;
        } else {
            double totalCG = 0D;
            int i = 0;
            for (Course c : VERIFIED_LIST) {
                if (c.getAbsoluteSemesterName().equals(sName)) {
                    totalCG += c.getQualityPoint();
                    i++;
                }
            }
            return totalCG / i;
        }
    }

    public static String traceBestSemester(boolean getCG){
        final ArrayList<String> list = filterSemesters();
        String bestSem = "...";
        double bestCG = 0D;
        for (String semName : list) {
            if (getCGPABySemester(semName) > bestCG) {
                bestCG = getCGPABySemester(semName);
                bestSem = semName;
            }
        }
        return getCG ? bestSem+" [CGPA="+ Globals.toFourth(bestCG)+"]" : bestSem;
    }

    public static String traceWorstSemester(boolean getCG){
        final ArrayList<String> list = filterSemesters();
        String worstSem = "...";
        double worstCG = 5D;
        for (String semName : list) {
            if (getCGPABySemester(semName) < worstCG) {
                worstCG = getCGPABySemester(semName);
                worstSem = semName;
            }
        }
        return getCG ? worstSem+" [CGPA="+ Globals.toFourth(worstCG)+"]" : worstSem;
    }

    public static String traceBestYear(boolean getCG){
        final ArrayList<String> list = filterAcademicYears();
        String bestAcdYr = "...";
        double bestCG = 0D;
        for (String yrName : list) {
            if (getCGPAByYear(yrName) > bestCG) {
                bestCG = getCGPAByYear(yrName);
                bestAcdYr = yrName;
            }
        }
        return getCG ? bestAcdYr+" [CGPA="+ Globals.toFourth(bestCG)+"]" : bestAcdYr;
    }

    public static String traceWorstYear(boolean getCG){
        final ArrayList<String> list = filterAcademicYears();
        String worstAcdYr = "...";
        double worstCG = 5D;
        for (String yrName : list) {
            if (getCGPAByYear(yrName) < worstCG) {
                worstCG = getCGPAByYear(yrName);
                worstAcdYr = yrName;
            }
        }
        return getCG ? worstAcdYr+" [CGPA="+ Globals.toFourth(worstCG)+"]" : worstAcdYr;
    }

}
