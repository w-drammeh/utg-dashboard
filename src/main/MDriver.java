package main;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Handles driver-related operations, including setting and donating them.
 */
public class MDriver {
    public static final int CONNECTION_LOST = 0;
    public static final int ATTEMPT_FAILED = 1;
    public static final int ATTEMPT_SUCCEEDED = 2;


    /**
     * Returns a fresh driver as appropriate for the currently running OS.
     * Nullability must be checked prior to any attempt of usage.
     *
     * Todo: separate the setup from the assignment
     */
    public static synchronized FirefoxDriver forgeNew(boolean headless) {
        FirefoxDriver firefoxDriver = null;
        try {
            WebDriverManager.firefoxdriver().setup();
            firefoxDriver = new FirefoxDriver(new FirefoxOptions().setHeadless(headless));
        } catch (Exception e) {
            App.silenceException("Error building Driver");
        }
        return firefoxDriver;
    }

    /**
     * Attempts to log this driver in to the Portal using the given email and password.
     * A successful attempt leaves the driver at the home page
     */
    public static int attemptLogin(FirefoxDriver driver, String email, String password) {
        if (isIn(driver)) {
            final int logoutAttempt = attemptLogout(driver);
            if (logoutAttempt == CONNECTION_LOST) {
                return CONNECTION_LOST;
            }
        } else {
            try {
                driver.navigate().to(Portal.LOGIN_PAGE);
                driver.findElement(By.name("email")).sendKeys(email);
                driver.findElement(By.name("password")).sendKeys(password);
                driver.findElement(By.className("form-group")).submit();
            } catch (Exception lost) {
                return CONNECTION_LOST;
            }
        }

        try {
            new WebDriverWait(driver, Portal.MINIMUM_WAIT_TIME).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".alert-danger")));
            return ATTEMPT_FAILED;
        } catch (TimeoutException ignored) {
        }

        try {
            new WebDriverWait(driver, Portal.MAXIMUM_WAIT_TIME).until(ExpectedConditions.presenceOfElementLocated(By.className("media-heading")));
            return ATTEMPT_SUCCEEDED;
        } catch (TimeoutException e) {
            return CONNECTION_LOST;
        }
    }

    public static int attemptLogin(FirefoxDriver driver) {
        return attemptLogin(driver, Student.getPortalMail(), Student.getPortalPassword());
    }

    /**
     * Attempts to log this driver out of the Portal.
     * By the time this call returns, the driver will be at the login page.
     */
    public static int attemptLogout(FirefoxDriver driver) {
        if (isIn(driver)) {
            try {
                driver.navigate().to(Portal.LOGOUT_PAGE);
                return ATTEMPT_SUCCEEDED;
            } catch (Exception failed) {
                return ATTEMPT_FAILED;
            }
        } else {
            try {
                driver.navigate().to(Portal.LOGIN_PAGE);
                return ATTEMPT_SUCCEEDED;
            } catch (Exception lost) {
                return CONNECTION_LOST;
            }
        }
    }

    /**
     * A driver is considered in the Portal if its url matches the home, content, or profile page
     */
    public static boolean isIn(FirefoxDriver driver) {
        final String url = driver.getCurrentUrl();
        return url.equals(Portal.HOME_PAGE) || url.equals(Portal.CONTENTS_PAGE) || url.equals(Portal.PROFILE_PAGE);
    }

}
