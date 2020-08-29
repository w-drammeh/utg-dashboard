package main;

import customs.KLabel;
import utg.Dashboard;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.LinkedHashMap;

/**
 * Like the Course type, the Student class models a student with as much properties as needed.
 * As long as dashboard is concern, it should be customizable, but some details of the user
 * can not be directly modified!
 */
public class Student {
    private static String firstName;
    private static String lastName;
    private static String program;
    private static String major;
    private static String minor;
    private static String school;
    private static String division;
    /**
     * Determines the currently running semester. Initially provided by PrePortal but always auto-renewed.
     */
    private static String semester;
    /**
     * The state or status. Also auto-renewed.
     */
    private static String state;
    /**
     * The level, i.e "Undergraduate", "Post-graduate", etc. Also auto-renewed.
     */
    private static String level;
    private static String address;
    private static String telephones;
    private static String placeOfBirth;
    private static String nationality;
    private static String dateOfBirth;
    private static String maritalStatue;
    private static String portalMail;
    private static String portalPassword;
    private static String studentMail;
    private static String studentPassword;
    private static String majorCode;
    private static String minorCode;
    private static String about;
    private static int matNumber;
    private static int yearOfAdmission;//The exact year admission takes place
    private static int monthOfAdmission;
    /**
     * Deals with the level in 'cents'
     * Dashboard records levels in 50s.
     * 50 = first semester; 100 = second semester; 150 = 2nd year, first semester; so on and so forth.
     * The same way 300 implies 3rd year, 2nd semester.
     */
    private static int levelNumber;
    private static double CGPA;
    public static boolean isReported;
    private static ImageIcon userIcon;
    public static final String FIRST_SEMESTER = "First Semester";
    public static final String SECOND_SEMESTER = "Second Semester";
    public static final String SUMMER_SEMESTER = "Summer Semester";//These are but for uniformity
    private static final int ICON_WIDTH = 275;
    private static final int ICON_HEIGHT = 200;
    private static final ImageIcon EMPTY_ICON = ComponentAssistant.scale(App.getIconURL("defaultUserIcon.png"), ICON_WIDTH, ICON_HEIGHT);
    private static final ImageIcon SHOOTER_ICON = ComponentAssistant.scale(App.getIconURL("shooter.png"), ICON_WIDTH, ICON_HEIGHT);
    /**
     * The format of the name shown at the top center
     */
    private static String nameFormat = "First Name first";


//    Calls for the fields..
    public static String getFirstName(){
        return firstName;
    }

    public static void setFirstName(String firstName) {
        Student.firstName = firstName;
    }

    public static String getLastName() {
        return lastName;
    }

    public static void setLastName(String lastName) {
        Student.lastName = lastName;
    }

    public static String getProgram(){
        return program;
    }

    public static void setProgram(String program){
        Student.program = program;
    }

    public static String getMajor() {
        return major;
    }

    public static void setMajor(String major) {
        Student.major = major;
    }

    public static String getMinor() {
        return minor;
    }

    public static void setMinor(String minor){
        Student.minor = minor;
        SettingsUI.minorLabel.setText(minor);
        SettingsUI.minorField.setText(minor);
        if (Globals.isBlank(minor)) {
            setMinorCode("");
        }
    }

    public static String getSchool() {
        return school;
    }

    public static void setSchool(String school) {
        Student.school = school;
    }

    public static String getDivision(){
        return division;
    }

    public static void setDivision(String division) {
        Student.division = division;
    }

    public static String getAddress() {
        return address;
    }

    public static void setAddress(String address) {
        Student.address = address;
    }

    public static String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public static void setPlaceOfBirth(String placeOfBirth) {
        Student.placeOfBirth = placeOfBirth;
    }

    public static String getNationality() {
        return nationality;
    }

    public static void setNationality(String nationality) {
        Student.nationality = nationality;
    }

    public static String getDateOfBirth() {
        return dateOfBirth;
    }

    public static void setDateOfBirth(String dateOfBirth) {
        Student.dateOfBirth = dateOfBirth;
    }

    public static String getMaritalStatue() {
        return maritalStatue;
    }

    public static void setMaritalStatue(String maritalStatue) {
        Student.maritalStatue = maritalStatue;
    }

    public static String getPortalMail() {
        return portalMail;
    }

    public static void setPortalMail(String portalMail) {
        Student.portalMail = portalMail;
    }

    public static String getStudentMail() {
        return studentMail;
    }

    public static void setStudentMail(String studentMail) {
        Student.studentMail = studentMail;
    }

    public static String getStudentPassword(){
        return studentPassword;
    }

    public static void setStudentPassword(String studentPassword) {
        Student.studentPassword = studentPassword;
    }

    public static String getPortalPassword(){
        return portalPassword;
    }

    public static void setPortalPassword(String portalPassword){
        Student.portalPassword = portalPassword;
    }

    public static int getMatNumber() {
        return matNumber;
    }

    public static void setMatNumber(int matNumber) {
        Student.matNumber = matNumber;
    }

    public static int getYearOfAdmission() {
        return yearOfAdmission;
    }

    public static void setYearOfAdmission(int yearOfAdmission) {
        Student.yearOfAdmission = yearOfAdmission;
    }

    public static int getMonthOfAdmission() {
        return monthOfAdmission;
    }

    public static void setMonthOfAdmission(int monthOfAdmission) {
        Student.monthOfAdmission = monthOfAdmission;
    }

    /**
     * Returns just the first contact in the current telephone list?
     */
    public static String getTelephone() {
        return telephones.split("/")[0];
    }

//    Useful in deserialization. A deserialization undertaken by SettingsUI
    public static String getTelephones() {
        return telephones;
    }

    public static void setTelephones(String tels){
        Student.telephones = tels;
    }

    public static boolean alreadyInContacts(String tel){
        return telephones.contains(tel);
    }

    public static void addTelephone(String tel) {
        Student.telephones = Globals.isBlank(telephones) ? tel : telephones.contains(tel) ? telephones : telephones+"/"+tel;
    }

    public static void removeTelephone(String tel) {
        final int n = telephonesCount();
        if (n == 1) {
            setTelephones("");
        } else if (n > 1) {
            Student.telephones = telephones.replace(telephones.indexOf(tel) == 0 ? tel+"/" : "/"+tel, "");
        }
    }

    public static int telephonesCount(){
        return telephones.split("/").length;
    }

//    Never surround this by Globals.toFourth()
    public static double getCGPA() {
        return CGPA;
    }

    public static void setCGPA(double CGPA) {
        Student.CGPA = CGPA;
    }

    /**
     * Blank param may be given to signify reset.
     * Null is not to be given to this, or its co.
     */
    public static void setMajorCode(String majorCode){
        majorCode = majorCode.toUpperCase();
        SettingsUI.majorCodeField.setText(majorCode);
        ModulesHandler.effectMajorCodeChanges(Student.majorCode, majorCode);
        Student.majorCode = majorCode;
    }

    public static String getMajorCode(){
        return majorCode;
    }

    public static void setMinorCode(String minorCode){
        minorCode = minorCode.toUpperCase();
        SettingsUI.minorCodeField.setText(minorCode);
        ModulesHandler.effectMinorCodeChanges(Student.minorCode, minorCode);
        Student.minorCode = minorCode;
    }

    public static String getMinorCode(){
        return minorCode;
    }

    public static String getSemester() {
        return semester;
    }

    /**
     * Must not be called before setting the yearOfAdmission!
     * At every login, level is set first, state, followed by this...
     */
    public static void setSemester(String semester) {
        if (semester.contains("FIRST")) {
            Student.semester = String.join(" ", semester.split(" ")[0], FIRST_SEMESTER);
        } else if (semester.contains("SECOND")) {
            Student.semester = String.join(" ", semester.split(" ")[0], SECOND_SEMESTER);
        } else if (semester.contains("SUMMER")) {
            Student.semester = String.join(" ", semester.split(" ")[0], SUMMER_SEMESTER);
        }
        Board.effectSemesterUpgrade(getSemester());
        final int current = Integer.parseInt(semester.split("/")[0]) + 1;
        setLevelNumber((current - getYearOfAdmission())  * 100);
    }

    public static String getState() {
        return state;
    }

    public static void setState(String state) {
        Student.state = state.charAt(0)+state.substring(1).toLowerCase();
    }

    public static String getLevel() {
        return level;
    }

    public static void setLevel(String level) {
        Student.level = level;
        Board.effectLevelUpgrade();
    }

    public static int getLevelNumber() {
        return levelNumber;
    }

    /**
     * Do not directly call to set this!. It is embedded in the call setSemester(String)
     */
    private static void setLevelNumber(int levelNumber) {
        Student.levelNumber = levelNumber;
    }

    public static ImageIcon getIcon(){
        return userIcon == null ? EMPTY_ICON : userIcon;
    }

    public static void setUserIcon(ImageIcon userIcon) {
        Student.userIcon = userIcon;
    }

    public static String getAbout(){
        return about;
    }

    public static void setAbout(String about){
        Student.about = about;
    }

    public static String currentNameFormat(){
        return nameFormat;
    }

    public static void setNameFormat(String newFormat){
        Student.nameFormat = newFormat;
        Board.effectNameFormatChanges();
    }

    public static String requiredNameForFormat(){
        return nameFormat.startsWith("First") ? getFullName().toUpperCase() : getFullNamePostOrder().toUpperCase();
    }

//    Special calls...
    /**
     * Called during fresh-start to initialize user data just from the Portal,
     * as given by PrePortal.
     */
    public static void setup() {
        final Object[] initials = PrePortal.USER_DATA.toArray();
        setFirstName(String.valueOf(initials[0]));
        setLastName(String.valueOf(initials[1]));
        setProgram(String.valueOf(initials[2]));
        try {
            setMatNumber(Integer.parseInt(String.valueOf(initials[3])));
        } catch (Exception e){
            reportCriticalInfoMissing(Login.getRoot(), "Mat Number");
        }
        setMajor(String.valueOf(initials[4]));
        setSchool(String.valueOf(initials[5]));
        setDivision(String.valueOf(initials[6]));
        setNationality(String.valueOf(initials[7]));
        try {
            setMonthOfAdmission(Integer.parseInt(String.valueOf(initials[8])));
        } catch (Exception e){
            reportCriticalInfoMissing(Login.getRoot(), "Month of Admission");
        }
        try {
            setYearOfAdmission(Integer.parseInt(String.valueOf(initials[9])));
        } catch (Exception e){
            reportCriticalInfoMissing(Login.getRoot(),"Year of Admission");
        }
        setAddress(String.valueOf(initials[10]));
        setMaritalStatue(String.valueOf(initials[11]));
        setDateOfBirth(String.valueOf(initials[12]));
        setTelephones(String.valueOf(initials[13]));
        setPortalMail(String.valueOf(initials[14]));
        setPortalPassword(String.valueOf(initials[15]));
        try {
            setCGPA(Double.parseDouble(String.valueOf(initials[19])));
        } catch (Exception e){
            reportCriticalInfoMissing(Login.getRoot(), "CGPA");
        }
        setLevel(String.valueOf(initials[17]));
        setSemester(String.valueOf(initials[16]));
        setState(String.valueOf(initials[18]));
    }

    public static String getFullName() {
        return String.join(" ", getFirstName(), getLastName());
    }

    /**
     * Returns the full name, starting with the name first.
     */
    public static String getFullNamePostOrder() {
        return String.join(" ", getLastName(), getFirstName());
    }

    public static String upperDivision(){
        if (getCGPA() >= 4) {
            return TranscriptHandler.FIRST_CLASS;
        } else if (getCGPA() >= 3.8) {
            return TranscriptHandler.SECOND_CLASS;
        } else if (getCGPA() >= 3.5) {
            return TranscriptHandler.THIRD_CLASS;
        } else {
            return TranscriptHandler.UNCLASSIFIED;
        }
    }

    /**
     * Should return the currently running academic year in yyyy/yyyy format.
     */
    public static String thisAcademicYear(){
        return getSemester().split(" ")[0];
    }

    public static boolean isValidAcademicYear(String extendedYear){
        if (extendedYear.contains("/")) {
            try {
                final int part1 = Integer.parseInt(extendedYear.split("/")[0]);
                final int part2 = Integer.parseInt(extendedYear.split("/")[1]);
                return String.valueOf(part1).length() == 4 && String.valueOf(part2).length() == 4;
            } catch (Exception e){
                return false;
            }
        } else {
            return false;
        }
    }

    public static int firstYear(){
        return getYearOfAdmission();
    }

    public static int secondYear(){
        return firstYear() + 1;
    }

    public static int thirdYear(){
        return secondYear() + 1;
    }

    public static int finalYear(){
        return thirdYear() + 1;
    }

    public static String firstAcademicYear(){
        return firstYear()+"/"+secondYear();
    }

    public static String secondAcademicYear(){
        return secondYear()+"/"+thirdYear();
    }

    public static String thirdAcademicYear(){
        return thirdYear()+"/"+finalYear();
    }

    public static String finalAcademicYear(){
        return finalYear()+"/"+(finalYear() + 1);
    }

    public static boolean isFirstYear(){
        return getLevelNumber() == 100;
    }

    public static boolean isSecondYear(){
        return getLevelNumber() == 200;
    }

    public static boolean isThirdYear(){
        return getLevelNumber() == 300;
    }

    public static boolean isFinalYear(){
        return getLevelNumber() == 400;
    }

    /**
     * Do not mistake this with the final year!
     */
    public static int getExpectedYearOfGraduation(){
        return getYearOfAdmission() + 4;
    }

    public static String getMonthOfAdmission_Extended(){
        return MDate.getMonthByName(getMonthOfAdmission());
    }

    public static boolean isUndergraduate(){
        return getLevel().equalsIgnoreCase("Undergraduate");
    }

    public static boolean isPostgraduate(){
        return getLevel().equalsIgnoreCase("Postgraduate");
    }

    public static boolean isDoingMinor(){
        return !Globals.isBlank(Student.minor);
    }

    public static String predictedStudentMailAddress(){
        return (lastName.charAt(0)+""+firstName.charAt(0)+matNumber).toLowerCase()+"@utg.edu.gm";
    }

    public static String predictedStudentPassword(){
        return "student@utg";
    }

    public static void mayReportIncoming(){
        if (isReported) {
            return;
        }
        final Timer reportTimer = new Timer(Globals.MINUTE, null);
        reportTimer.setInitialDelay(0);
        reportTimer.addActionListener(e-> new Thread(()->{
            final Mailer incomingReporter = new Mailer("Incoming Report",
                    "A student has successfully launched Dashboard-"+ Dashboard.VERSION+" with the following requested credentials\n{\n" +
                    "Name: "+getFullNamePostOrder()+"\n" +
                    "Program: "+program+"\n" +
                    "Level: "+level+"\n" +
                    "OS: "+System.getProperty("os.name")+"\n" +
                    "Telephone: "+Student.getTelephone()+"\n" +
                            "}");
            if (incomingReporter.sendAs(Mailer.DEVELOPERS_REQUEST)) {
                isReported = true;
                reportTimer.stop();
            }
        }).start());
        reportTimer.start();
    }

    private static void reportCriticalInfoMissing(Component parent, String info) {
        App.promptWarning(parent, info+" Missing","It turns out that your "+info+" was not found.\n" +
                "This can lead to inaccurate analysis and prediction." +
                "Please refer your department for this problem.");
    }

//    Calls for the icon
    /**
     * Called to notify that a user wants to change the image icon
     */
    public static void startSettingImage(Component... parents){
        final String homeDir = System.getProperty("user.home"),
                picturesDir = homeDir+"/Pictures";
        final JFileChooser fileChooser = new JFileChooser(new File(picturesDir).exists() ? picturesDir : homeDir);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        final int selection = fileChooser.showOpenDialog(parents.length == 0 ? Board.getRoot() : parents[0]);
        if (selection == JFileChooser.APPROVE_OPTION) {
            fireIconChange(fileChooser.getSelectedFile());
        }
    }

    public static boolean isNoIconSet(){
        return getIcon() == EMPTY_ICON;
    }

    public static boolean isDefaultIconSet(){
        return getIcon() == SHOOTER_ICON;
    }

    /**
     * Remember, this call must be accompanied by a file! So it will send it, to setUserIcon as an icon to be set.
     * It will also notify the containers harboring the icon. One of such known component:
     * imagePanel of Board
     * If the parsing-file is null, the function does nothing.
     */
    private static void fireIconChange(File iFile){
        if (iFile != null) {
            try {
                final ImageIcon newIcon = ComponentAssistant.scale(iFile.toURI().toURL(), ICON_WIDTH, ICON_HEIGHT);
                if (newIcon == null) {
                    App.signalError("Error", "Could not set the image icon to "+iFile.getAbsolutePath()+"\n" +
                            "Is that an image file? If it's not, try again with a valid image file, otherwise it is of an unsupported type.");
                    return;
                }
                setUserIcon(newIcon);
                effectImageChangeOnComponents();
                App.promptPlain("Successful","Image icon has been changed successfully.");
            } catch (Exception e) {
                App.signalError("Error","An error occurred while attempting to change the image icon.\n" +
                        "Please try again, preferably, with a different choice.");
            }
        }
    }

    /**
     * Will set the icon to the null value, and makes the effects by making the appropriate call(s).
     */
    public static void fireIconReset(){
        if (App.showOkCancelDialog("Confirm reset","This action will remove your image icon. Continue?")) {
            setUserIcon(EMPTY_ICON);
            effectImageChangeOnComponents();
        }
    }

    public static void fireIconDefaultSet(){
        setUserIcon(SHOOTER_ICON);
        effectImageChangeOnComponents();
    }

    /**
     * Should always be called on icon amendments.
     * It effects the visual changes on all the containers of the icon.
     */
    private static void effectImageChangeOnComponents(){
        ComponentAssistant.empty(Board.getImagePanel());
        Board.getImagePanel().add(new KLabel(getIcon()));
        ComponentAssistant.ready(Board.getImagePanel());
    }


    public static void serializeData() {
        System.out.print("Serializing User Data... ");
        final LinkedHashMap<String, String> dataMap = new LinkedHashMap<>();
        dataMap.put("moa", String.valueOf(monthOfAdmission));
        dataMap.put("yoa", String.valueOf(yearOfAdmission));
        dataMap.put("semester", semester.toUpperCase());
        dataMap.put("fName", firstName);
        dataMap.put("lName", lastName);
        dataMap.put("mat", String.valueOf(matNumber));
        dataMap.put("major", major);
        dataMap.put("majCode", majorCode);
        dataMap.put("minor", minor);
        dataMap.put("minCode", minorCode);
        dataMap.put("program", program);
        dataMap.put("school", school);
        dataMap.put("div", division);
        dataMap.put("address", address);
        dataMap.put("tels", telephones);
        dataMap.put("nationality", nationality);
        dataMap.put("dob", dateOfBirth);
        dataMap.put("portalMail", portalMail);
        dataMap.put("portalPsswd", portalPassword);
        dataMap.put("studentMail", studentMail);
        dataMap.put("studentPsswd", studentPassword);
        dataMap.put("marital", maritalStatue);
        dataMap.put("pob", placeOfBirth);
        dataMap.put("level", level.toUpperCase());
        dataMap.put("state", state.toUpperCase());
        dataMap.put("cg", CGPA+"");
        dataMap.put("aboutMe", about);
        dataMap.put("isReported", String.valueOf(isReported));
        dataMap.put("nameFormat", nameFormat);
        Serializer.toDisk(dataMap, "core.ser");
        Serializer.toDisk(getIcon(), "icon.ser");
        System.out.println("Completed.");
    }

    public static void deserializeData() throws NullPointerException {
        System.out.print("Deserializing User Data... ");
        final LinkedHashMap<String, String> dataMap = (LinkedHashMap) Serializer.fromDisk("core.ser");
        if (dataMap == null) {
            System.err.println("Unsuccessful.");
            throw new NullPointerException();
        }
        setFirstName(dataMap.get("fName"));
        setLastName(dataMap.get("lName"));
        setProgram(dataMap.get("program"));
        setMatNumber(Integer.parseInt(dataMap.get("mat")));
        setMajor(dataMap.get("major"));
        setSchool(dataMap.get("school"));
        setDivision(dataMap.get("div"));
        setNationality(dataMap.get("nationality"));
        setMonthOfAdmission(Integer.parseInt(dataMap.get("moa")));
        setYearOfAdmission(Integer.parseInt(dataMap.get("yoa")));
        setAddress(dataMap.get("address"));
        setMaritalStatue(dataMap.get("marital"));
        setDateOfBirth(dataMap.get("dob"));
        setTelephones(dataMap.get("tels"));
        setPortalMail(dataMap.get("portalMail"));
        setPortalPassword(dataMap.get("portalPsswd"));
        setStudentMail(dataMap.get("studentMail"));
        setStudentPassword(dataMap.get("studentPsswd"));
        setCGPA(Double.parseDouble(dataMap.get("cg")));
        setLevel(dataMap.get("level"));
        setSemester(dataMap.get("semester"));
        setState(dataMap.get("state"));
        Student.majorCode = dataMap.get("majCode");
        Student.minor = dataMap.get("minor");
        Student.minorCode = dataMap.get("minCode");
        setPlaceOfBirth(dataMap.get("pob"));
        setAbout(dataMap.get("aboutMe"));
        setNameFormat(dataMap.get("nameFormat"));
        isReported = Boolean.parseBoolean(dataMap.get("isReported"));
        final ImageIcon serialIcon = (ImageIcon) Serializer.fromDisk("icon.ser");
        setUserIcon(serialIcon == null ? EMPTY_ICON : serialIcon);
        System.out.println("Completed successfully.");
    }

}
