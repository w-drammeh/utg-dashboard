package main;

import org.apache.commons.io.FileUtils;
import utg.Dashboard;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Formatter;

/**
 * As the name implies, MyClass is a extra class for functions that
 * are not classifiable to the main classes.
 */
public class MyClass {
	public static final String fileSeparator = System.getProperty("file.separator");
	public static final String rootDir = System.getProperty("user.home")+fileSeparator+"Dashboard";
	public static final String serialsDir = rootDir+fileSeparator+"serials";
	public static final String outputDir = rootDir+fileSeparator+"outputs";

	/**
	 * Note! the exception generative by this call is silenced.
	 * Under serious cases, where it requires attention,
	 * take advantage of the fact that it returns 'null' when an exception occur
	 */
	public static ImageIcon scaleForMe(URL url, int width, int height) {
		ImageIcon icon = null;
		try {
			final BufferedImage buf = ImageIO.read(url);
			icon = new ImageIcon(buf.getScaledInstance(width, height, Image.SCALE_SMOOTH));
		} catch (Exception e) {
			App.silenceException(e);
		}

		return icon;
	}

	public static void serialize(Object o, String serName){
		try {
			final File path = new File(serialsDir);
			if (!path.exists()) {
				path.mkdirs();
				placeReadMeFile();
				placeUserDetails();
			}
			final FileOutputStream fileOutputStream = new FileOutputStream(path+"/"+serName);
			final ObjectOutputStream out = new ObjectOutputStream(fileOutputStream);
			out.writeObject(o);
			out.close();
		} catch (Exception e) {
			App.silenceException("Error serializing file "+serName);
		}
	}

	/**
	 * Dashboard depends, greatly, on this piece of call.
	 * It handles its potential exceptions.
	 * So caller must after-check if the returned-object is null before an attempt to use -
	 * such is the convention.
	 */
    public static Object deserialize(String serName){
        Object serObject = null;
        try {
        	final FileInputStream fileInputStream = new FileInputStream(serialsDir +"/"+serName);
            final ObjectInputStream in = new ObjectInputStream(fileInputStream);
			serObject = in.readObject();
            in.close();
        } catch (Exception e) {
			App.silenceException("Error deserializing file "+serName);
        }
        return serObject;
    }

    public static void placeReadMeFile(){
		final String readMe = "This jar file, or its derivatives (Linux Executables, Windows Executables, etc.)\n" +
				"were compiled and distributed by Muhammed W. Drammeh <wakadrammeh@gmail.com> Tue Jan 28, 2020 10:33:15 GMT.\n\n" +
				"Kindly report all problems to "+Mailer.DEVELOPERS_MAIL+".\n\n" +
				"Do not modify or delete this file, or any other files in the \"serials\" directory.\n" +
				"Modifying files in the \"serials\" path can interrupt the 'Launch Sequences' which might cause Dashboard\n" +
				"to force a new instance, removing all your details and setting preferences. Thus, you'll have to login again.\n\n" +
				"This project is a FOSS [Free & Open Source Software]. So, you are hereby permitted to make changes provided\n" +
				"you very well know and can make those changes.\n\n" +
				"--Compilation Version = "+Dashboard.VERSION;
		try {
			final Formatter formatter = new Formatter(System.getProperty("user.home") + "/Dashboard/README.txt");
			formatter.format(readMe);
			formatter.close();
		} catch (FileNotFoundException e) {
			App.silenceException(e);
		}
	}

	public static void placeUserDetails(){
		final File path = new File(outputDir);
		if (!path.exists()) {
			path.mkdirs();
		}
		final String data = "\t\t<-----This file shall contain the fundamental details of the student / user----->\n" +
				"Do not border modify this file - all modifications are discarded at every \"collapse\".\n\n" +
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
				"Department: Department of "+Student.getDepartment()+"\n" +
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
			final Formatter formatter = new Formatter(outputDir+"/"+Student.getLastName()+".txt");
			formatter.format(data);
			formatter.close();
		} catch (FileNotFoundException e) {
			App.silenceException(e);
		}
	}

	public static void mountUserData(){
		serialize(System.getProperty("user.name"), "userName.ser");
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
			FileUtils.deleteDirectory(new File(rootDir));
			return true;
		} catch (IOException ioe) {
			final File userName = new File(MyClass.serialsDir+MyClass.fileSeparator+"userName.ser");
			return userName.delete();
		}
	}

}
