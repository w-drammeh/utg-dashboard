package main;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * <h1>class DriversPack</h1>
 * <p><b>Description</b>: Handles driver-related operations, including setting and donating them.</p>
 */
public class DriversPack {
    public static final int CONNECTION_LOST = 0;
    public static final int ATTEMPT_FAILED = 1;
    public static final int ATTEMPT_SUCCEEDED = 2;

    /**
     * <p>Returns a fresh driver as appropriate for the currently running OS.</p>
     * <p><i>Be careful in making this call, it always try to return something. So external callers must
     * afterwards check for nullability.</i></p>
     * <p><i>This can be done like: <b>if(DriversPack.getInstance == null){return / if possible}</b></i></p>
     *
     * <p><i>Passing null as the container implies errors generated in this block will not be reported.
     * You may wish to report this later.</i></p>
     */
    public static synchronized FirefoxDriver forgeNew(boolean headless){
        FirefoxDriver fDriver = null;

        try {
            WebDriverManager.firefoxdriver().setup();
            fDriver = new FirefoxDriver(new FirefoxOptions().setHeadless(headless));
        } catch (Exception e) {
            App.silenceException("Building driver failed: "+e.getMessage());
        }

        return fDriver;
    }

    /**
     * <p>By convention, this and the co are to be used only after a successful build,
     * as they're intended to login/out the current user only..</p>
     * <p><i>It's checked before even proceeding to the 'content-page'</i></p>
     * <p><i>{@link PrePortal} does not use this whence.</i></p>
     */
    public static int attemptLogin(FirefoxDriver foxDriver){
        if (isIn(foxDriver)) {
            return ATTEMPT_SUCCEEDED;
        }

        try {
            foxDriver.navigate().to(Portal.LOGIN_PAGE);
            foxDriver.findElement(By.name("email")).sendKeys(Student.getPortalMail());
            foxDriver.findElement(By.name("password")).sendKeys(Student.getPortalPassword());
            foxDriver.findElement(By.className("form-group")).submit();
        } catch (Exception lost) {
            return CONNECTION_LOST;
        }

        try {
            new WebDriverWait(foxDriver, 5).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".alert-danger")));
            return ATTEMPT_FAILED;
        } catch (TimeoutException out){//it's what I wanted
            //if the time is up yet there's no danger sign, 1 condition?
            //1. Let the next attempt wait for the name element, throw timeout, otherwise proceed
        }

        try {
            new WebDriverWait(foxDriver, 50).until(ExpectedConditions.presenceOfElementLocated(By.className("media-heading")));
            return ATTEMPT_SUCCEEDED;
        } catch (Exception e) {
            return CONNECTION_LOST;
        }
    }

    public static void attemptLogout(FirefoxDriver foxDriver){

    }

    /**
     * <p>Will say yes (return true) as long as the driver is in - no matter whether is
     * at the home or contents-page</p>
     */
    public static boolean isIn(FirefoxDriver foxDriver){
        return foxDriver.getCurrentUrl().equals(Portal.HOME_PAGE) || foxDriver.getCurrentUrl().equals(Portal.CONTENTS_PAGE);
    }

}
