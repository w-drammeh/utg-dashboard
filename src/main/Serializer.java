package main;

import org.apache.commons.io.FileUtils;
import utg.Dashboard;

import java.io.*;
import java.util.Formatter;

public class Serializer {
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
    public static final String ROOT_DIR = System.getProperty("user.home") + FILE_SEPARATOR + "Dashboard";
    public static final String SERIALS_DIR = ROOT_DIR + FILE_SEPARATOR + "serials";
    public static final String OUTPUT_DIR = ROOT_DIR + FILE_SEPARATOR + "outputs";


    /**
     * This does the ultimate serialization.
     */
    public static void toDisk(Object obj, String name){
        try {
            final File serialsPath = new File(SERIALS_DIR);
            if (serialsPath.exists() || serialsPath.mkdirs()) {
                final FileOutputStream fileOutputStream = new FileOutputStream(serialsPath + "/" + name);
                final ObjectOutputStream out = new ObjectOutputStream(fileOutputStream);
                out.writeObject(obj);
                out.close();
            } else {
                App.silenceException("Error serialize file "+name+"; could not mount directory "+serialsPath);
            }
        } catch (Exception e) {
            App.silenceException("Error serializing file "+name);
        }
    }

    /**
     * This does the ultimate deserialization.
     * This call also handles potential exceptions.
     * So caller must after-check if the returned-object is null before an attempt to use.
     */
    public static Object fromDisk(String serName){
        Object serObject = null;
        try {
            final FileInputStream fileInputStream = new FileInputStream(SERIALS_DIR +"/"+serName);
            final ObjectInputStream in = new ObjectInputStream(fileInputStream);
            serObject = in.readObject();
            in.close();
        } catch (Exception e) {
            App.silenceException("Error deserializing file "+serName);
        }
        return serObject;
    }

    public static void placeReadMeFile(){
        final String readMeText = "This jar file, or its derivatives (Linux Executables, Windows Executables, etc.)\n" +
                "were compiled and distributed by Muhammed W. Drammeh <wakadrammeh@gmail.com> Tue Jan 28, 2020 10:33:15 GMT.\n\n" +
                "Kindly report all problems to "+Mailer.DEVELOPERS_MAIL+".\n\n" +
                "Do not modify or delete this file, or any other files in the \"serials\" directory.\n" +
                "Modifying files in the \"serials\" path can interrupt the 'Launch Sequences' which might cause Dashboard\n" +
                "to force a new instance, removing all your details and setting preferences. Thus, you'll have to login again.\n\n" +
                "This project is a FOSS [Free & Open Source Software]. So, you are hereby permitted to make changes provided\n" +
                "you very well know and can make those changes.\n\n" +
                "--Compilation Version = "+ Dashboard.VERSION;
        try {
            final Formatter formatter = new Formatter(ROOT_DIR + "/README.txt");
            formatter.format(readMeText);
            formatter.close();
        } catch (FileNotFoundException e) {
            App.silenceException("Error: unable to place README.txt file");
        }
    }

    public static void placeUserDetails(){
        final String data = "\t\t----This file shall contain the fundamental details of the student / user----\n" +
                "Do not bother modify this file - all modifications are discarded at every \"collapse\".\n\n" +
                "Month of Admission: "+ Student.getMonthOfAdmission_Extended()+"\n" +
                "Year of Admission: "+Student.getYearOfAdmission()+"\n" +
                "Current Semester: "+Student.getSemester().toUpperCase()+"\n" +
                "First Name: "+Student.getFirstName()+"\n" +
                "Last Name: "+Student.getLastName()+"\n" +
                "Mat. Number: "+Student.getMatNumber()+"\n" +
                "Major: "+Student.getMajor()+"\n" +
                "Major Code: "+Student.getMajorCode()+"\n" +
                "Minor: "+Student.getMinor()+"\n" +
                "Minor Code: "+Student.getMinorCode()+"\n" +
                "Program: "+Student.getProgram()+"\n" +
                "School: School of "+Student.getSchool()+"\n" +
                "Department: Division of "+Student.getDivision()+"\n" +
                "Address: "+Student.getAddress()+"\n" +
                "Telephone: "+Student.getTelephone()+"\n" +
                "Nationality: "+Student.getNationality()+"\n" +
                "Date of Birth: "+Student.getDateOfBirth()+"\n" +
                "Portal Email: "+Student.getPortalMail()+"\n" +
                "Student Mail: "+Student.getStudentMail()+"\n" +
                "Marital Status: "+Student.getMaritalStatue()+"\n" +
                "Place of Birth: "+Student.getPlaceOfBirth()+"\n" +
                "Level: "+Student.getLevel()+"\n" +
                "State: "+Student.getState()+"\n" +
                "Last Dashboard Launch: "+ MDate.now()+"\n" +
                "Dashboard Version: "+ Dashboard.VERSION;
        try {
            final Formatter formatter = new Formatter(OUTPUT_DIR + "/user.txt");
            formatter.format(data);
            formatter.close();
        } catch (FileNotFoundException e) {
            App.silenceException(e);
        }
    }

    public static void mountUserData(){
        toDisk(System.getProperty("user.name"), "userName.ser");
        placeReadMeFile();
        placeUserDetails();
        Student.serializeData();
        Portal.serialize();
        SettingsCore.serialize();
        SettingsUI.serialize();
        RunningCoursesGenerator.serializeModules();
        ModulesHandler.serializeData();
        TaskSelf.serializeAll();
        Notification.serializeAll();
        NewsGenerator.serializeData();
    }

    public static boolean unMountUserData(){
        try {
            FileUtils.deleteDirectory(new File(ROOT_DIR));
            return true;
        } catch (IOException ioe) {
            final File userName = new File(SERIALS_DIR + FILE_SEPARATOR + "userName.ser");
            return userName.delete();
        }
    }

}
