package main;

import proto.KLabel;
import utg.Dashboard;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Student {
    private static String firstName;
    private static String lastName;
    private static String matNumber;//has no arithmetic use since;
    private static String program;
    private static String major;
    private static String minor;
    private static String school;
    private static String division;
    /**
     * Determines the currently running semester.
     * Initially provided by PrePortal but always auto-renewed.
     */
    private static String semester;
    /**
     * The state / status. Also auto-renewed.
     * "RUNNING", "UNKNOWN",
     */
    private static String status;
    /**
     * The level, i.e "Undergraduate", "Post-graduate", etc. Also auto-renewed.
     */
    private static String level;
    private static String address;
    /**
     * Telephones: at any point in time, the first partition is returned
     * as the current telephone.
     */
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
    private static int yearOfAdmission;//the exact year admission takes place
    private static int monthOfAdmission;
    /**
     * Deals with the level in 'cents'
     * Dashboard records levels in 50s.
     * 50 = first semester; 100 = second semester; 150 = 2nd year, first semester; so on and so forth.
     * The same way 300 implies 3rd year, 2nd semester.
     */
    private static int levelNumber;
    private static double CGPA;
    private static ImageIcon userIcon;
    private static LinkedHashMap<String, String> additionalData;
    public static final String FIRST_SEMESTER = "First Semester";
    public static final String SECOND_SEMESTER = "Second Semester";
    public static final String SUMMER_SEMESTER = "Summer Semester";
    private static final int ICON_WIDTH = 275;
    private static final int ICON_HEIGHT = 200;
    private static final ImageIcon DEFAULT_ICON = MComponent.scale(App.getIconURL("default-icon.png"),
            ICON_WIDTH, ICON_HEIGHT);
    private static final ImageIcon SHOOTER_ICON = MComponent.scale(App.getIconURL("shooter.png"),
            ICON_WIDTH, ICON_HEIGHT);
    public static final String UNCLASSIFIED = "None";
    public static final String THIRD_CLASS = "\"Cum laude\" - With Praise!";
    public static final String SECOND_CLASS = "\"Magna Cum laude\" - With Great Honor!";
    public static final String FIRST_CLASS = "\"Summa Cum laude\" - With Greatest Honor!";
    private static String nameFormat = "First Name first";


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

    public static String getVisiblePortalMail(){
        final int l = portalMail.split("@")[0].length();
        String t = "*";
        while (t.length() < (l - 3)) {
            t += t;
        }
        return portalMail.substring(0, 2)+t+portalMail.charAt(l - 1)+"@utg.edu.gm";
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

    public static String getMatNumber() {
        return matNumber;
    }

    public static void setMatNumber(String matNumber) {
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
     * Returns just the first contact in the current telephones list
     * This may give 'Unknown' (when given by PrePortal),
     * or the empty-string (when all are removed)
     */
    public static String getTelephone() {
        return telephones.split("/")[0];
    }

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
        Student.telephones = Globals.isBlank(telephones) ? tel : telephones.contains(tel) ?
                telephones : telephones + "/" + tel;
    }

    public static void removeTelephone(String tel) {
        final int n = telephonesCount();
        if (n == 1) {
            setTelephones("");
        } else if (n > 1) {
            Student.telephones = telephones.replace(telephones.indexOf(tel) == 0 ? tel + "/" : "/" + tel, "");
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
     */
    public static void setMajorCode(String majorCode) {
        majorCode = majorCode.toUpperCase();
        SettingsUI.majorCodeField.setText(majorCode);
        ModuleHandler.effectMajorCodeChanges(Student.majorCode, majorCode);
        Student.majorCode = majorCode;
    }

    public static String getMajorCode(){
        return majorCode;
    }

    public static void setMinorCode(String minorCode){
        minorCode = minorCode.toUpperCase();
        SettingsUI.minorCodeField.setText(minorCode);
        ModuleHandler.effectMinorCodeChanges(Student.minorCode, minorCode);
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
     * At every login, level is set first, then state, followed by this.
     */
    public static void setSemester(String semester) {
        if (semester.contains("FIRST")) {
            Student.semester = String.join(" ", semester.split(" ")[0], FIRST_SEMESTER);
        } else if (semester.contains("SECOND")) {
            Student.semester = String.join(" ", semester.split(" ")[0], SECOND_SEMESTER);
        } else if (semester.contains("SUMMER")) {
            Student.semester = String.join(" ", semester.split(" ")[0], SUMMER_SEMESTER);
        }
        Board.effectSemesterUpgrade();
        final int current = Integer.parseInt(semester.split("/")[0]) + 1;
        levelNumber = (current - yearOfAdmission)  * 100;
    }

    public static String getStatus() {
        return status;
    }

    public static void setStatus(String status) {
        Student.status = status;
        Board.effectStatusUpgrade();
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

    public static ImageIcon getIcon(){
        return userIcon == null ? DEFAULT_ICON : userIcon;
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

    public static LinkedHashMap<String, String> getAdditional(){
        return additionalData;
    }

    public static String currentNameFormat(){
        return nameFormat;
    }

    public static void setNameFormat(String format){
        Student.nameFormat = format;
        Board.effectNameFormatChanges();
    }

    public static String requiredNameForFormat(){
        return nameFormat.startsWith("First") ? getFullName() : getFullNamePostOrder();
    }

    public static void initialize() {
        if (Dashboard.isFirst()) {
            final Object[] initials = PrePortal.USER_DATA.toArray();
            firstName = (String) initials[0];
            lastName = (String) initials[1];
            program = (String) initials[2];
            try {
                matNumber = (String) initials[3];
            } catch (Exception e){
                reportCriticalInfoMissing(Login.getRoot(), "Mat Number");
            }
            major = (String) initials[4];
            school = (String) initials[5];
            division = (String) initials[6];
            nationality = (String) initials[7];
            try {
                monthOfAdmission = Integer.parseInt((String) initials[8]);
            } catch (Exception e){
                reportCriticalInfoMissing(Login.getRoot(), "Month of Admission");
            }
            try {
                yearOfAdmission = Integer.parseInt((String) initials[9]);
            } catch (Exception e){
                reportCriticalInfoMissing(Login.getRoot(),"Year of Admission");
            }
            address = (String) initials[10];
            maritalStatue = (String) initials[11];
            dateOfBirth = (String) initials[12];
            telephones = (String) initials[13];
            portalMail = (String) initials[14];
            portalPassword = (String) initials[15];
            try {
                CGPA = Double.parseDouble((String) initials[19]);
            } catch (Exception e){
                reportCriticalInfoMissing(Login.getRoot(), "CGPA");
            }
            setLevel((String)(initials[17]));
            setSemester((String)(initials[16]));
            setStatus((String)(initials[18]));
//
            minor = majorCode = minorCode = "";
            additionalData = new LinkedHashMap<>();
        } else {
            deserializeData();
        }
    }

    public static String getFullName() {
        return String.join(" ", firstName, lastName);
    }

    /**
     * Returns the full name, starting with the name first.
     */
    public static String getFullNamePostOrder() {
        return String.join(" ", lastName, firstName);
    }

    public static String upperClassDivision() {
        if (CGPA >= 4) {
            return FIRST_CLASS;
        } else if (getCGPA() >= 3.8) {
            return SECOND_CLASS;
        } else if (getCGPA() >= 3.5) {
            return THIRD_CLASS;
        } else {
            return UNCLASSIFIED;
        }
    }

    /**
     * Should return the currently running academic year in yyyy/yyyy format.
     */
    public static String thisAcademicYear() {
        return semester.split(" ")[0];
    }

    public static boolean isValidAcademicYear(String extendedYear) {
        if (extendedYear.contains("/")) {
            try {
                final int part1 = Integer.parseInt(extendedYear.split("/")[0]);
                final int part2 = Integer.parseInt(extendedYear.split("/")[1]);
                return Integer.toString(part1).length() == 4 && Integer.toString(part2).length() == 4;
            } catch (Exception e){
                return false;
            }
        } else {
            return false;
        }
    }

    private static int firstYear(){
        return yearOfAdmission;
    }

    private static int secondYear(){
        return firstYear() + 1;
    }

    private static int thirdYear(){
        return secondYear() + 1;
    }

    private static int fourthYear(){
        return thirdYear() + 1;
    }

//    if this is readable from the portal, then be it.
    public static boolean isGraduated(){
        return levelNumber > 400;
    }

    public static String firstAcademicYear(){
        return firstYear() + "/" + secondYear();
    }

    public static String secondAcademicYear(){
        return secondYear() + "/" + thirdYear();
    }

    public static String thirdAcademicYear(){
        return thirdYear() + "/" + fourthYear();
    }

    public static String fourthAcademicYear(){
        return fourthYear() + "/" + (fourthYear() + 1);
    }

    public static boolean isFirstYear(){
        return levelNumber == 100;
    }

    public static boolean isSecondYear(){
        return levelNumber == 200;
    }

    public static boolean isThirdYear(){
        return levelNumber == 300;
    }

    public static boolean isFourthYear(){
        return levelNumber == 400;
    }

    public static int getExpectedYearOfGraduation(){
        return yearOfAdmission + 4;
    }

    public static String getMonthOfAdmissionName(){
        return MDate.getMonthByName(monthOfAdmission);
    }

    public static boolean isUndergraduate(){
        return level.equals("Undergraduate");
    }

    public static boolean isPostgraduate(){
        return level.equals("Postgraduate");
    }

    public static boolean isDoingMinor(){
        return Globals.hasText(Student.minor);
    }

    public static String getNameAcronym(){
        return (lastName.charAt(0)+""+firstName.charAt(0)).toLowerCase();
    }

    public static String predictedStudentMailAddress(){
        return getNameAcronym()+matNumber+"@utg.edu.gm";
    }

    public static String predictedStudentPassword(){
        return matNumber;//"student@utg"?
    }

    private static void reportCriticalInfoMissing(Component parent, String info) {
        App.promptWarning(parent, info+" Missing","It turns out that your \""+info+"\" was not found.\n" +
                "This can lead to inaccurate analysis and prediction.\nPlease refer your department for this problem.");
    }

    public static void startSettingImage(Component parent){
        final Component actualParent = parent == null ? Board.getRoot() : parent;
        final String homeDir = System.getProperty("user.home");
        final String picturesDir = homeDir + "/Pictures";
        final JFileChooser fileChooser = new JFileChooser(new File(picturesDir).exists() ? picturesDir : homeDir);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        final int selection = fileChooser.showOpenDialog(actualParent);
        if (selection == JFileChooser.APPROVE_OPTION) {
            fireIconChange(fileChooser.getSelectedFile(), actualParent);
        }
    }

    public static void startSettingImage(){
        startSettingImage(null);
    }

    /**
     * It will also notify the containers harboring the icon. One of such known component:
     * imagePanel at main.Board
     * If the parsing-file is null, nothing is done.
     */
    private static void fireIconChange(File imageFile, Component c){
        if (imageFile != null) {
            try {
                final ImageIcon newIcon = MComponent.scale(imageFile.toURI().toURL(), ICON_WIDTH, ICON_HEIGHT);
                if (newIcon == null) {
                    App.signalError(c, "Error", "Could not set the image icon to "+imageFile.getAbsolutePath()+".\n" +
                            "Is that an image file? If it's not, try again with a valid image file, otherwise it is of an unsupported type.");
                    return;
                }
                userIcon = newIcon;
                effectIconChanges();
            } catch (Exception e) {
                App.signalError(c, "Error","An error occurred while attempting to change the image icon.\n" +
                        "Please try again, preferably, with a different choice.");
            }
        }
    }

    /**
     * Effects visual changes on the containers holding the icon.
     */
    private static void effectIconChanges() {
        MComponent.empty(Board.getImagePanel());
        Board.getImagePanel().add(new KLabel(userIcon));
        MComponent.ready(Board.getImagePanel());
    }

    public static void fireIconReset(){
        if (App.showYesNoCancelDialog("Confirm reset","This action will remove your image icon. Continue?")) {
            userIcon = DEFAULT_ICON;
            effectIconChanges();
        }
    }

    public static void fireIconDefaultSet(){
        userIcon = SHOOTER_ICON;
        effectIconChanges();
    }

    /**
     * Algorithm generates true even if no icon is manually set.
     * See getIcon()
     */
    public static boolean isDefaultIconSet(){
        return getIcon() == DEFAULT_ICON;
    }

    public static boolean isShooterIconSet(){
        return getIcon() == SHOOTER_ICON;
    }


    public static void serializeData() {
        final HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put("moa", monthOfAdmission);
        dataMap.put("yoa", yearOfAdmission);
        dataMap.put("semester", semester.toUpperCase());
        dataMap.put("fName", firstName);
        dataMap.put("lName", lastName);
        dataMap.put("mat", matNumber);
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
        dataMap.put("status", status.toUpperCase());
        dataMap.put("cg", CGPA);
        dataMap.put("aboutMe", about);
        dataMap.put("nameFormat", nameFormat);
        dataMap.put("extra", additionalData);
        if (!isDefaultIconSet()) {
            if (isShooterIconSet()) {
                dataMap.put("shooterIcon", "Shooter");
            } else {
                dataMap.put("userIcon", "User Specific");
                dataMap.put("iconSelf", getIcon());
            }
        }
        Serializer.toDisk(dataMap, "core.ser");
    }

    private static void deserializeData() throws NullPointerException {
        final HashMap<String, Object> dataMap = (HashMap) Serializer.fromDisk("core.ser");
        if (dataMap == null) {
            throw new NullPointerException();
        }
        firstName = (String) (dataMap.get("fName"));
        lastName = (String) (dataMap.get("lName"));
        program = (String) (dataMap.get("program"));
        matNumber = (String) dataMap.get("mat");
        major = (String) (dataMap.get("major"));
        school = (String) (dataMap.get("school"));
        division = (String) (dataMap.get("div"));
        nationality = (String) (dataMap.get("nationality"));
        monthOfAdmission = (int) (dataMap.get("moa"));
        yearOfAdmission = (int) (dataMap.get("yoa"));
        address = (String) (dataMap.get("address"));
        maritalStatue = (String) (dataMap.get("marital"));
        dateOfBirth = (String) (dataMap.get("dob"));
        telephones = (String) (dataMap.get("tels"));
        portalMail = (String) dataMap.get("portalMail");
        portalPassword = (String) dataMap.get("portalPsswd");
        studentMail = (String) dataMap.get("studentMail");
        studentPassword = (String) dataMap.get("studentPsswd");
        CGPA = (double) dataMap.get("cg");
        setLevel((String) dataMap.get("level"));
        setSemester((String) dataMap.get("semester"));
        status = (String) dataMap.get("status");
        majorCode = (String) dataMap.get("majCode");
        minor = (String) dataMap.get("minor");
        minorCode = (String) dataMap.get("minCode");
        placeOfBirth = (String) dataMap.get("pob");
        about = (String) dataMap.get("aboutMe");
        setNameFormat((String) dataMap.get("nameFormat"));
        additionalData = (LinkedHashMap<String, String>) dataMap.get("extra");
        if (dataMap.containsKey("shooterIcon")) {
            Board.postProcesses.add(Student::fireIconDefaultSet);
        } else if (dataMap.containsKey("userIcon")) {
            userIcon = (ImageIcon) dataMap.get("iconSelf");
        }
    }

}
