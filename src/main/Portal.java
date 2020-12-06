package main;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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
    public static final String LOGOUT_PAGE = "https://www.utg.gm/logout";
    //do not call the contentsPage or profilePage on a driver that has not yet entered....!
    public static final String CONTENTS_PAGE = "https://www.utg.gm/course-registrations";
    public static final String PROFILE_PAGE = "https://www.utg.gm/profile";
    public static final int MAXIMUM_WAIT_TIME = 30;//intended in seconds
    public static final int MINIMUM_WAIT_TIME = 5;
    /**
     * It's where the admission notice is.
     * Notice: The same class reference of the admission-notice will change to registrationNotice on the contentsPage.
     */
    public static final String HOME_PAGE = "https://www.utg.gm/home";
    private static String registrationNotice = "Waiting for a successful sync...";
    private static String admissionNotice = registrationNotice;
    private static Date lastAdmissionNoticeUpdate, lastRegistrationNoticeUpdate;
    private static boolean autoSync = false;
    private static Date lastLogin;//currently not readable, as its get function is not used
    private static FirefoxDriver portalDriver;


    public static void openPortal(Component clickable){
        new Thread(() -> {
            clickable.setEnabled(false);
            if (Internet.isInternetAvailable()) {
                final int vInt = App.verifyUser("To access your portal, kindly enter your Matriculation Number below:");
                if (vInt == App.VERIFICATION_TRUE) {
                    launchPortal(clickable);
                } else if (vInt == App.VERIFICATION_FALSE) {
                    App.reportMatError();
                    clickable.setEnabled(true);
                } else {
                    clickable.setEnabled(true);
                }
            } else {
                App.reportNoInternet();
                clickable.setEnabled(true);
            }
        }).start();
    }

    /**
     * Meant to launch the portal for the student, and that's all.
     * To remedy a situation of multiple clicks, it accepts a nullable button, which if possible,
     * will enable it after completing the pending charges.
     *
     */
    private static void launchPortal(Component clickable){
        try {
            if (portalDriver == null) {
                portalDriver = MDriver.forgeNew(false);
                if (portalDriver == null) {
                    App.reportMissingDriver();
                    return;
                }
            }

            final int loginAttempt = MDriver.attemptLogin(portalDriver);
            if (loginAttempt == MDriver.ATTEMPT_SUCCEEDED) {
                portalDriver.navigate().to(Portal.CONTENTS_PAGE);
            }
        } finally {
            if (!(clickable == null)) {
                clickable.setEnabled(true);
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
    public static boolean startRenewingNotices(FirefoxDriver noticeDriver, boolean userRequested){
        if (isPortalBusy(noticeDriver)) {
            if (userRequested) {
                App.reportBusyPortal();
            }
            return false;
        }

        try {
            noticeDriver.navigate().to(HOME_PAGE);
            new WebDriverWait(noticeDriver, 50).until(ExpectedConditions.presenceOfElementLocated(By.className("media-heading")));
            final WebElement admissionElement = new WebDriverWait(noticeDriver, 59).until(ExpectedConditions.presenceOfElementLocated(By.className("gritter-title")));
            setAdmissionNotice(admissionElement.getText());
        } catch (Exception e) {
            if (userRequested) {
                App.reportConnectionLost();
            }
            return false;
        }

        try {
            noticeDriver.navigate().to(CONTENTS_PAGE);
            WebElement registrationElement = new WebDriverWait(noticeDriver,59).until(ExpectedConditions.presenceOfElementLocated(By.className("gritter-title")));
            Portal.setRegistrationNotice(registrationElement.getText());
            return true;
        } catch (Exception e) {
            if (userRequested) {
                App.reportConnectionLost();
            }
            return false;
        }
    }

    public static String getAdmissionNotice(){
        return admissionNotice;
    }

    public static void setAdmissionNotice(String admissionNotice){
        Portal.admissionNotice = admissionNotice;
        lastAdmissionNoticeUpdate = new Date();
    }

    public static String getLastAdmissionNoticeUpdate(){
        return lastAdmissionNoticeUpdate == null ? "Never" : MDate.format(lastAdmissionNoticeUpdate);
    }

    public static String getRegistrationNotice(){
        return registrationNotice;
    }

    public static void setRegistrationNotice(String registrationNotice){
        Portal.registrationNotice = registrationNotice;
        lastRegistrationNoticeUpdate = new Date();
        if (Board.isAppReady()) {
            RunningCourseActivity.effectNoticeUpdate();
        } else {
            Board.postProcesses.add(RunningCourseActivity::effectNoticeUpdate);
        }
    }

    public static String getLastRegistrationNoticeUpdate(){
        return lastRegistrationNoticeUpdate == null ? "Never" : MDate.format(lastRegistrationNoticeUpdate);
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
            Student.setStatus(iGroup.get(3).getText().split("\n")[1]);

            final String[] findingSemester = iGroup.get(6).getText().split("\n")[0].split(" ");
            final String ongoingSemester = findingSemester[0]+" "+findingSemester[1]+" "+findingSemester[2];
            Student.setSemester(ongoingSemester);
        }

        setLastLogin(new Date());
    }


    public static void serialize(){
        final HashMap<String, Object> portalHash = new HashMap<>();
        portalHash.put("rNotice", registrationNotice);
        portalHash.put("aNotice", admissionNotice);
        portalHash.put("autoSync", autoSync);
        portalHash.put("lastAdmissionNoticeUpdate", lastAdmissionNoticeUpdate);
        portalHash.put("lastRegistrationNoticeUpdate", lastRegistrationNoticeUpdate);
        portalHash.put("lastLogin", lastLogin);
        Serializer.toDisk(portalHash, "portal.ser");
    }

    public static void deSerialize(){
        final HashMap<String, Object> savedState = (HashMap<String, Object>) Serializer.fromDisk("portal.ser");
        if (savedState == null) {
            App.silenceException("Error reading Portal Data.");
            return;
        }
        registrationNotice = savedState.get("rNotice").toString();
        admissionNotice = savedState.get("aNotice").toString();
        autoSync = Boolean.parseBoolean(savedState.get("autoSync").toString());
        lastAdmissionNoticeUpdate = (Date) savedState.get("lastAdmissionNoticeUpdate");
        lastRegistrationNoticeUpdate = (Date) savedState.get("lastRegistrationNoticeUpdate");
        lastLogin = (Date) savedState.get("lastLogin");
    }

}
