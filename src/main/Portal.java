package main;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * The dashboard physical portal representative.
 * The functionality of this class come to life after the student is verified.
 * Never forget to use it on a different thread!
 */
public class Portal {
    public static final String LOGIN_PAGE = "https://www.utg.gm/login";
    //do not call the contentsPage or profilePage on a driver that has not yet entered....!
    public static final String CONTENTS_PAGE = "https://www.utg.gm/course-registrations";
    public static final String PROFILE_PAGE = "https://www.utg.gm/profile";
    /**
     * It's where the admission notice is.
     * Notice: The same class reference of the admission-notice will change to registrationNotice on the contentsPage.
     */
    public static final String HOME_PAGE = "https://www.utg.gm/home";
    private static String registrationNotice = "Waiting for a successful sync...";
    private static String admissionNotice = registrationNotice;
    private static Date lastNoticeUpdate;
    private static boolean autoSync = false;
    private static Date lastLogin;//currently not readable, as its get function is not used
    private static FirefoxDriver portalDriver;


    static {
        new Timer(Globals.DAY_IN_MILLI, e -> {
            /*
            keeps checking every day whether to automate or not. Error generated herein must not come
            to notice. And it should be internet-availability sensitive.
             */
            if (autoSync) {
                RunningCoursesGenerator.startMatching(false);
                ModulesGenerator.triggerRefresh(false);
                NotificationGenerator.updateNotices(false);
            }
        }).start();
    }

    public static void userRequestsOpenPortal(Component button){
        new Thread(() -> {
            button.setEnabled(false);
            if (InternetAvailabilityChecker.isInternetAvailable()) {
                final int vInt = App.verifyUser("To access your portal, enter your matriculation number below, so Dashboard confirms its you.\n" +
                        "-\n" +
                        "By using this process, do not bother write your Email & Password when the browser is opened - \n" +
                        "leave everything to Dashboard! In a short-while, the contents of your portal will be shown to you.\n");
                if (vInt == App.VERIFICATION_TRUE) {
                    showOpenPortal(button);
                } else if (vInt == App.VERIFICATION_FALSE) {
                    App.reportMatError();
                    button.setEnabled(true);
                } else {//whatever
                    button.setEnabled(true);
                }
            } else {
                App.reportNoInternet();
                button.setEnabled(true);
            }
        }).start();
    }

    /**
     * Meant to launch the portal for the student, and that's all.
     * To remedy a situation of multiple clicks, it accepts a nullable button, which if possible,
     * will enable it after completing the pending charges.
     *
     */
    public static void showOpenPortal(Component kButton){
        try {
            if (portalDriver == null) {
                portalDriver = DriversPack.forgeNew(false);
            }
            if (portalDriver == null) {
                App.reportMissingDriver();
                return;
            }

            final int loginAttempt = DriversPack.attemptLogin(portalDriver);
            if (loginAttempt == DriversPack.ATTEMPT_SUCCEEDED) {
                portalDriver.navigate().to(Portal.CONTENTS_PAGE);
            }
        } finally {
            if (!(kButton == null)) {
                kButton.setEnabled(true);
            }
        }
    }

    /**
     * Should always be checked to make sure that the portal is not busy.
     */
    public static boolean isPortalBusy(FirefoxDriver driver){
        try {
            driver.findElementByCssSelector("div.gritter-item-wrapper:nth-child(4)");
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * Successful operation herein shall induce all the notices to be re-assigned, also do the 'lastNoticeUpdate' todate.
     */
    public static void startRenewingNotices(FirefoxDriver noticeDriver, boolean userRequested){
        if (isPortalBusy(noticeDriver)) {
            if (userRequested) {
                App.reportBusyPortal();
            }
            return;
        }

        try {
            noticeDriver.navigate().to(HOME_PAGE);
            new WebDriverWait(noticeDriver,50).until(ExpectedConditions.presenceOfElementLocated(By.className("media-heading")));
            final WebElement admissionElement = new WebDriverWait(noticeDriver,59).until(ExpectedConditions.presenceOfElementLocated(By.className("gritter-title")));
            setNotices(null, admissionElement.getText());
        } catch (Exception e) {
            if (userRequested) {
                App.reportConnectionLost();
            }
            return;
        }

        try {
            noticeDriver.navigate().to(CONTENTS_PAGE);
            WebElement registrationElement = new WebDriverWait(noticeDriver,59).until(ExpectedConditions.presenceOfElementLocated(By.className("gritter-title")));
            Portal.setNotices(registrationElement.getText(),null);
        } catch (Exception e) {
            if (userRequested) {
                App.reportConnectionLost();
            }
        }
    }

    /**
     * Method call does not attempt to renew notice - hence returns the recently found notice.
     */
    public static String getBufferedNotice_Admission(){
        return admissionNotice;
    }

    /**
     * Method call does not attempt to renew notice - hence returns the recently found notice.
     */
    public static String getBufferedNotice_Registration(){
        return registrationNotice;
    }

    /**
     * Returns the last time a notice was updated.
     */
    public static String getLastNoticeUpdate(){
        return lastNoticeUpdate == null ? "Never" : MDate.formatFully(lastNoticeUpdate);
    }

    /**
     * Remains public for the sake of only PrePortal to set them during build.
     * The non-null param(s) will be renewed to their respective types, and notify the components holding those.
     */
    public static void setNotices(String registration, String admission){
        if (registration != null) {
            registrationNotice = registration;
            RunningCoursesGenerator.noticeLabel.setText(registration+("    [Last updated: "+getLastNoticeUpdate()+"]"));
        }

        if (admission != null) {
            admissionNotice = admission;
        }

        lastNoticeUpdate = new Date();
    }

    /**
     * Auto-sync shall mean that courses will be verified as they are locally added to the tables,
     * notices will be renewed in the background, news updates, etc.
     *
     * The default is daily, but there should be options for the user in next compilations.
     */
    public static void  setAutoSync(boolean sync){
        autoSync = sync;
    }

    public static boolean isAutoSynced(){
        return autoSync;
    }

    public static Date getLastLogin() {
        return lastLogin;
    }

    private static void setLastLogin(Date lastLogin) {
        Portal.lastLogin = lastLogin;
    }

    /**
     * This call is intended, purposely, for pre-scrapping actions.
     * These include setting of semesters, levels, and other dynamic details.
     */
    public static void nowOnPortal(FirefoxDriver nowDriver){
        if (nowDriver.getCurrentUrl().equals(CONTENTS_PAGE)) {
            final List<WebElement> iGroup = nowDriver.findElementsByClassName("info-group");
            Student.setLevel(iGroup.get(2).getText().split("\n")[1]);
            Student.setState(iGroup.get(3).getText().split("\n")[1]);

            final String[] findingSemester = iGroup.get(6).getText().split("\n")[0].split(" ");
            final String ongoingSemester = findingSemester[0]+" "+findingSemester[1]+" "+findingSemester[2];
            Student.setSemester(ongoingSemester);
        }

        setLastLogin(new Date());
    }

    public static void receiveDriver(FirefoxDriver prePortalDriver) {
        Portal.portalDriver = prePortalDriver;
    }

    public static void serialize(){
        System.out.print("Serializing portal data... ");
        final HashMap<String, Object> portalHash = new HashMap<>();
        portalHash.put("rNotice", registrationNotice);
        portalHash.put("aNotice", admissionNotice);
        portalHash.put("autoSync", autoSync);
        portalHash.put("lastNoticeUpdate", lastNoticeUpdate);
        portalHash.put("lastLogin", lastLogin);
        MyClass.serialize(portalHash, "portal.ser");
        System.out.println("Completed.");
    }

    public static void deSerialize(){
        System.out.print("Deserializing portal data... ");
        final HashMap<String, Object> savedState = (HashMap<String, Object>) MyClass.deserialize("portal.ser");
        registrationNotice = savedState.get("rNotice").toString();
        admissionNotice = savedState.get("aNotice").toString();
        autoSync = Boolean.parseBoolean(savedState.get("autoSync").toString());
        lastNoticeUpdate = (Date) savedState.get("lastNoticeUpdate");
        lastLogin = (Date) savedState.get("lastLogin");
        System.out.println("Completed.");
    }

}
