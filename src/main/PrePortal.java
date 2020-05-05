package main;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;

/**
 * As the name suggests, PrePortal is a once and for all class, technically
 * reserved for verification and collection of start-up data from the Portal which it'll send to the
 * Student, Memory, and Course types.
 */
public class PrePortal {
    private static ArrayList<String> portalDetails = new ArrayList<>();
    private static String email, password, temporaryName;
    private static FirefoxDriver driver;//Should be delivered to Portal after task
    private static WebDriverWait loadWaiter;


    public static void launchVerificationSequences(String email, String password){
        PrePortal.email = email;
        PrePortal.password = password;

        Login.appendToStatus("Setting up the web driver....... Please wait");
        startFixingDriver();
        if (driver == null) {
            App.reportMissingDriver(Login.getRoot());
            Login.setInputsState(true);
            return;
        }

        loadWaiter = new WebDriverWait(driver, 59);
        Login.replaceLastUpdate("Setting up the driver....... Successful");

        Login.appendToStatus("Now contacting utg.......");
        try {
            driver.navigate().to(Portal.LOGIN_PAGE);
            Login.replaceLastUpdate("Now contacting utg....... Okay");
            driver.findElement(By.name("email")).sendKeys(email);
            driver.findElement(By.name("password")).sendKeys(password);
            driver.findElement(By.className("form-group")).submit();
        } catch (Exception e1) {
            driver.navigate().back();
            App.reportConnectionLost(Login.getRoot());
            Login.setInputsState(true);
            return;
        }

        Login.appendToStatus("Connections established successfully with utg.gm");
        Login.appendToStatus("Waiting for the server respond to "+email+".......");
        try {
            new WebDriverWait(driver,5).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".alert-danger")));
            Login.appendToStatus("Verification failed : No student found matching");
            driver.navigate().back();
            App.signalError(Login.getRoot(),"Invalid Credentials","The information you provided:\n" +
                    "Email - "+email+"\nPassword - "+password+"\n" +
                    "do not match any student. Please try again.");
            Login.setInputsState(true);
            return;
        } catch (TimeoutException out){//it's what I wanted
            //if the time is up yet there's no danger sign, 1 condition?
            //1. Let the next attempt wait for the name element, throw timeout, otherwise proceed
        }

        try {
            temporaryName = loadWaiter.until(ExpectedConditions.presenceOfElementLocated(By.className("media-heading"))).getText();
            Login.appendToStatus("Login successful : "+temporaryName);
            gateOpened();
        } catch (TimeoutException out){
            driver.navigate().back();
            App.reportConnectionLost(Login.getRoot());
            Login.setInputsState(true);
        }
    }

    public static synchronized void startFixingDriver(){
        if (driver != null) {
            return;
        }
        driver = DriversPack.forgeNew(true);
    }

    //Since gate is opened, and the driver enters with the student details, if an error occurs, it'll quit before returning
    private static void gateOpened(){
        String firstName="", lastName="", matNumber="", program="", major="", school="", department="", nationality="", MOA="", YOA="", address="", mStatus="", DOB="", tel="", ongoingSemester="", level="", state="";

        if (Portal.isPortalBusy(driver)) {
            Login.appendToStatus("Busy portal : Course Evaluation needed");
            App.reportBusyPortal(Login.getRoot());
            driver = null;
            Login.setInputsState(true);
            return;
        }

        //Before navigating to the contents-page, lets extract the admission notice herein the home-page
        try {
            WebElement admissionAlert = loadWaiter.until(ExpectedConditions.presenceOfElementLocated(By.className("gritter-title")));
            Portal.setNotices(null,admissionAlert.getText());
        } catch (TimeoutException out){
            App.silenceException("Admission Notice not found, because of time-out.");
        }

        Login.appendToStatus("Now processing details.......");
        Login.appendToStatus("Operation may take longer based on your internet signal or temporary server issues");
        try {
            driver.navigate().to(Portal.CONTENTS_PAGE);
            WebElement registrationAlert = loadWaiter.until(ExpectedConditions.presenceOfElementLocated(By.className("gritter-title")));
            Portal.setNotices(registrationAlert.getText(),null);
        } catch (Exception e1) {
            driver = null;
            App.reportConnectionLost(Login.getRoot());
            Login.setInputsState(true);
            return;
        }

        try {
            final String[] allNames = temporaryName.split(" ");
            firstName = allNames[allNames.length - 1];
            lastName = allNames[0];
            if (allNames.length == 3) {
                lastName += " " + allNames[1];
            } else if (allNames.length == 4) {
                lastName += " " + allNames[1] + " " + allNames[2];
            }
        } catch (Exception e){
            App.silenceException("Problem setting the name");
        }

        final List<WebElement> iGroup = driver.findElementsByClassName("info-group");
        level = iGroup.get(2).getText().split("\n")[1];
        state = iGroup.get(3).getText().split("\n")[1];

        program = driver.findElementByXPath("/html/body/section/div[2]/div/div[1]/div/div[2]/div[2]/div[1]/div/h4").getText();
        major = program.contains("Unknown") ? "Unknown" : program.split(" ")[4];
        try {
            school = iGroup.get(1).getText().split("\n")[1];
        } catch (Exception e) {
            App.silenceException("School not found");
        }
        try {
            department = iGroup.get(0).getText().split("\n")[1];
        } catch (Exception e) {
            App.silenceException("Department not found");
        }

        final String[] findingSemester = iGroup.get(6).getText().split("\n")[0].split(" ");
        ongoingSemester = findingSemester[0] + " " + findingSemester[1] + " " + findingSemester[2];

        try {
            driver.navigate().to(Portal.PROFILE_PAGE);
        } catch (Exception e1) {
            driver = null;
            App.reportConnectionLost(Login.getRoot());
            Login.setInputsState(true);
            return;
        }

        matNumber = driver.findElementByCssSelector("initialModules, strong").getText().split(" ")[1];

        final List<WebElement> detail = driver.findElementsByClassName("info-group");
        try {
            address = detail.get(0).getText().split("\n")[1];
        } catch (Exception e) {
            App.silenceException("Address not found");
        }
        try {
            tel = detail.get(2).getText().split("\n")[1];
        } catch (Exception e) {
            App.silenceException("Telephone not found");
        }
        try {
            mStatus = detail.get(3).getText().split("\n")[1];
        } catch (Exception e) {
            App.silenceException("Marital status not found");
        }
        try {
            final String[] DOBParts = detail.get(4).getText().split("\n")[1].split(" ");
            DOB = DOBParts[0]+" "+DOBParts[1]+" "+DOBParts[2];
        } catch (Exception e) {
            App.silenceException("Date of birth not found");
        }
        try {
            nationality = detail.get(5).getText().split("\n")[1];
        } catch (Exception e) {
            App.silenceException("Nationality not found");
        }

        final String[] admissionDate = detail.get(6).getText().split("\n");
        try {
            YOA = admissionDate[1].split("-")[0];
        } catch (Exception e) {
            App.silenceException("Year of admission not found");
        }
        try {
            MOA = admissionDate[1].split("-")[1];
        } catch (Exception e) {
            App.silenceException("Month of admission not found");
        }

        try {
            driver.navigate().to(Portal.CONTENTS_PAGE);
            loadWaiter.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.tagName("table")));
        } catch (Exception e) {
            driver = null;
            App.reportConnectionLost(Login.getRoot());
            Login.setInputsState(true);
            return;
        }

        //adding in predefined order... cgpa will be added with transcript
        Login.appendToStatus("Processing details....... Completed");
        Login.appendToStatus("#####");
        enlistDetail("First Name",firstName);//they're added as they're listed
        enlistDetail("Last Name",lastName);
        enlistDetail("Program",program);
        enlistDetail("Mat#",matNumber);
        enlistDetail("Major",major);
        enlistDetail("School",school);
        enlistDetail("Department",department);
        enlistDetail("Nationality",nationality);
        enlistDetail("Month of Admission",MOA);
        enlistDetail("Year of Admission",YOA);
        enlistDetail("Address",address);
        enlistDetail("Marital status",mStatus);
        enlistDetail("Birth Day",DOB);
        enlistDetail("Telephone",tel);
        enlistDetail("Email",email);
        enlistDetail("password",password);
        enlistDetail("On-going Semester",ongoingSemester);
        enlistDetail("Level",level);
        enlistDetail("State",state);

        Login.appendToStatus("#####");
        Login.appendToStatus("Collecting up all your courses....... This may take a while");
        List<WebElement> tabs;
        try {
            tabs = loadWaiter.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".nav-tabs > li")));
        } catch (Exception e){
            driver = null;
            App.reportConnectionLost(Login.getRoot());
            Login.setInputsState(true);
            return;
        }

        //Firstly, code, name, year, semester, and credit hour at transcript tab
        //Addition to startupCourses is only here; all the following loops only updates the details. this eradicates the possibility of adding running courses at tab-4
        tabs.get(7).click();
        final WebElement transcriptTable = driver.findElementByCssSelector(".table-bordered");
        final WebElement transBody = transcriptTable.findElement(By.tagName("tbody"));
        final List<WebElement> transRows = transBody.findElements(By.tagName("tr"));
        final List<WebElement> semCaptions = transBody.findElements(By.className("warning"));
        String vYear = null;
        String vSemester = null;
        for (WebElement transRow : transRows) {
            if (transRow.getText().contains("Semester")) {
                vYear = transRow.getText().split(" ")[0];
                vSemester = transRow.getText().split(" ")[1]+" Semester";
            } else {
                final List<WebElement> data = transRow.findElements(By.tagName("td"));
                ModulesHandler.STARTUP_COURSES.add(new Course(vYear, vSemester, data.get(1).getText(), data.get(2).getText(),
                        "", "", "", "", 0.0, Integer.parseInt(data.get(3).getText()),"",true));
            }
        }
        final WebElement surrounds = driver.findElementsByCssSelector(".pull-right").get(3);
        final String cgpa = surrounds.findElements(By.tagName("th")).get(1).getText();
        enlistDetail("cgpa", cgpa);

        //Secondly, add scores at grades tab
        tabs.get(6).click();
        final WebElement gradesTable = driver.findElementsByCssSelector(".table-warning").get(1);
        final WebElement tBody = gradesTable.findElement(By.tagName("tbody"));
        final List<WebElement> rows = tBody.findElements(By.tagName("tr"));
        for(WebElement t : rows){
            final List<WebElement> data = t.findElements(By.tagName("td"));
            for (Course c : ModulesHandler.STARTUP_COURSES) {
                if (c.getCode().equals(data.get(0).getText())) {
                    c.setScore(Double.parseDouble(data.get(6).getText()));
                }
            }
        }

        //Finally, available lecturer names at all-registered tab
        tabs.get(4).click();
        final WebElement allRegisteredTable = driver.findElementByCssSelector(".table-warning");
        final WebElement tableBody = allRegisteredTable.findElement(By.tagName("tbody"));
        final List<WebElement> allRows = tableBody.findElements(By.tagName("tr"));
        int l = 0;
        while (l < allRows.size()) {
            final List<WebElement> instantRow = allRows.get(l).findElements(By.tagName("td"));
            for (Course c : ModulesHandler.STARTUP_COURSES) {
                if (c.getCode().equals(instantRow.get(0).getText())) {
                    c.setLecturer(instantRow.get(2).getText(), false);
                }
            }
            l++;
        }

        //Check for running courses? add
        final List<WebElement> captions = tableBody.findElements(By.cssSelector("b, strong"));
        final boolean running = captions.get(captions.size()-1).getText().equalsIgnoreCase(ongoingSemester);
        int runningCount = 0;
        if (running) {
            int match = allRows.size() -1;
            while (!allRows.get(match).getText().equalsIgnoreCase(ongoingSemester)){
                final List<WebElement> data = allRows.get(match).findElements(By.tagName("td"));
                RunningCoursesGenerator.STARTUP_REGISTRATIONS.add(new RunningCourse(data.get(0).getText(),data.get(1).getText(),data.get(2).getText(),
                        data.get(3).getText(),data.get(4).getText(),"","",true));
                match--;
                runningCount++;
            }
        }

        final int courseCount = rows.size();
        final int semesterCount = semCaptions.size();
        Login.appendToStatus("Successfully found "+ Globals.checkPlurality(courseCount, "courses")+" in "+ Globals.checkPlurality(semesterCount, "semesters"));
        if (runningCount == 0) {
            Login.appendToStatus("No registration detected this semester");
        } else if (runningCount == 1) {
            Login.appendToStatus("Plus 1 registration this semester");
        } else {
            Login.appendToStatus("Plus "+runningCount+" registered courses this semester");
        }
        Login.appendToStatus("#####");

        Student.receiveDetails(portalDetails.toArray());

        Portal.receiveDriver(driver);
        Login.notifyCompletion();
    }

    private static void enlistDetail(String key, String value){
        if (Globals.isBlank(value) || value.contains("Unknown")) {
            Login.appendToStatus("***Warning ..... " + key + " not found!");
        } else if(!(key.equalsIgnoreCase("password") || key.equalsIgnoreCase("cgpa"))) {
            Login.appendToStatus(key+": "+value);
        }
        //It must however be.....
        portalDetails.add(value);
    }

}
