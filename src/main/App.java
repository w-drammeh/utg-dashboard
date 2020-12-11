package main;


import proto.KFontFactory;
import proto.KLabel;
import proto.KPanel;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
/**
 * It is the center for generalized concepts.
 * Any attempt to point to an icon, a sheet, etc, must directly call this class.
 * As analogous to the Globals type which globalizes code operations, this class universalizes the
 * input-output operations.
 */
public class App {
    public static final int DIALOG_DISMISSED = 0;
    public static final int INPUT_BLANK = 1;
    public static final int VERIFICATION_FALSE = 2;
    public static final int VERIFICATION_TRUE = 3;


    /**
     * This method is stagnant. For uniformity only.
     * The given iconName must be a pre-existing name in the icons dir., the URL-pointer to which is to be returned.
     */
    public static URL getIconURL(String iconName){
        return App.class.getResource("/icons/"+iconName);
    }

    /**
     * A centralized point for user verification. This obsoletes the call verifyUser(String)
     */
    public static int verifyUser(Component parent, String vString){
        if (Settings.noVerifyNeeded || Student.isTrial()) {
            return VERIFICATION_TRUE;
        } else {
            final String input = requestInput(parent == null ? Board.getRoot() : parent, "Confirm ID", vString);
            if (input == null) {
                return DIALOG_DISMISSED;
            } else if (Globals.hasNoText(input)) {
                return INPUT_BLANK;
            } else if (input.equals(Student.getMatNumber())) {
                return VERIFICATION_TRUE;
            } else {
                return VERIFICATION_FALSE;
            }
        }
    }

    public static int verifyUser(String vString){
        return verifyUser(null, vString);
    }

    public static void reportMatError(){
        reportMatError(null);
    }

    public static void reportMatError(Component parent){
        signalError(parent == null ? Board.getRoot() : parent,"Mat Error","That matriculation number does not match. Try again.");
    }

    //The showXxx() calls:
    //Simplified methods that returns a boolean indicating that the user consents with a JOptionPane's dialog
    public static boolean showYesNoCancelDialog(Component parent, String title, String text){
        return JOptionPane.showConfirmDialog(parent == null ? Board.getRoot() : parent, dialogTextPanel(text),
                title, JOptionPane.YES_NO_CANCEL_OPTION) == JOptionPane.YES_OPTION;
    }

    public static boolean showYesNoCancelDialog(String title, String text){
        return showYesNoCancelDialog(null, title, text);
    }

    public static boolean showOkCancelDialog(Component parent, String title, String text){
        return JOptionPane.showConfirmDialog(parent == null ? Board.getRoot() : parent, dialogTextPanel(text),
                title, JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION;
    }

    public static boolean showOkCancelDialog(String title, String text){
        return showOkCancelDialog(null, title, text);
    }

    /**
     * Requests an input from the user.
     * Seemingly gives null if the dialog is dismissed.
     */
    public static String requestInput(Component parent, String title, String text){
        return JOptionPane.showInputDialog(parent == null ? Board.getRoot() : parent, dialogTextPanel(text),
                title, JOptionPane.PLAIN_MESSAGE);
    }

    public static String requestInput(String title, String text){
        return requestInput(null, title, text);
    }

    public static void signalError(Component parent, String title, String message){
        JOptionPane.showMessageDialog(parent == null ? Board.getRoot() : parent, dialogTextPanel(message),
                title, JOptionPane.ERROR_MESSAGE);
    }

    public static void signalError(Component parent, Exception e){
        signalError(parent == null ? Board.getRoot() : parent, e.getClass().getSimpleName(), e.getMessage());
    }

    public static void signalError(String title, String message){
        signalError(null, title, message);
    }

    public static void signalError(Exception e){
        signalError(e.getClass().getName(), e.getMessage());
    }

    /**
     * Should be called in the catch block of tries the failure of which dashboard cannot dispense.
     * This subsequently causes the VM to quit.
     */
    public static void signalFatalError(Component parent, String title, String detail){
        signalError(parent == null ? Board.getRoot() : parent, title, detail);
        System.exit(0);
    }

    /**
     * A convenient way of calling signalFatalError(Component, String, String);
     */
    public static void signalFatalError(Component c, Exception e){
        signalFatalError(c == null ? Board.getRoot() : c, "Fatal Error", "Fatal Error Occurred: "+e.getMessage());
    }

    public static void reportMissingDriver(Component parent){
        signalError(parent == null ? Board.getRoot() : parent, "No Driver",
                "Sorry, setting up the driver doesn't complete normally.\n" +
                "Please make sure 'Firefox Browser' is installed and try again.");
    }

    public static void reportMissingDriver(){
        reportMissingDriver(null);
    }

    public static void reportConnectionLost(Component parent) {
        signalError(parent == null ? Board.getRoot() : parent,"Connection Lost",
                "Sorry, we are having troubles connecting to the Portal.\n" +
                "Please try again later.");
    }

    public static void reportConnectionLost() {
        reportConnectionLost(null);
    }

    public static void reportNoInternet(Component parent) {
        signalError(parent == null ? Board.getRoot() : parent, "Internet Error",
                "Sorry, we're having troubles connecting to the internet.\n" +
                "Please connect and try again.");
    }

    public static void reportNoInternet() {
        reportNoInternet(null);
    }

    public static void reportLoginAttemptFailed() {
        signalError(Board.getRoot(), "Login Failed", "Dashboard has been denied access to your portal.\n" +
                "Please go to 'Home | Privacy & Settings | Customize Profile' tab to make sure the right credentials are given.");
    }

    /**
     * Dashboard's standard way of prompting a plain message, typically, successful ones.
     */
    public static void promptPlain(Component parent, String title, String information) {
        JOptionPane.showMessageDialog(parent == null ? Board.getRoot() : parent, dialogTextPanel(information),
                title, JOptionPane.PLAIN_MESSAGE);
    }

    public static void promptPlain(String title, String information){
        promptPlain(null, title, information);
    }

    public static void promptWarning(Component parent, String title, String message){
        JOptionPane.showMessageDialog(parent == null ? Board.getRoot() : parent, dialogTextPanel(message),
                title, JOptionPane.WARNING_MESSAGE);
    }

    public static void promptWarning(String title, String message){
        promptWarning(null, title, message);
    }

    public static void reportBusyPortal(Component parent){
        promptWarning(parent == null ? Board.getRoot() : parent, "Busy Portal",
                "Your portal is currently requesting 'Course Evaluations'.\n" +
                "Please visit your portal to perform the evaluation and try again.");
    }

    public static void reportBusyPortal(){
        reportBusyPortal(null);
    }

    public static void silenceException(Exception e){
        e.printStackTrace();
    }

    /**
     * Convenient way of ignoring exceptions, and write the passed string to the console using the
     * standard error-reporter.
     */
    public static void silenceException(String note){
        System.err.println(note);
    }

    /**
     * Provides the text used by option-dialogs on a panel.
     */
    private static KPanel dialogTextPanel(String text){
        final KPanel dialogPanel = new KPanel();
        dialogPanel.setOpaque(false);
        dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.Y_AXIS));
        for (String line : text.split("\n")) {
            final KLabel lineLabel = new KLabel(line, KFontFactory.createPlainFont(15));
            lineLabel.setOpaque(false);
            dialogPanel.add(lineLabel);
        }
        return dialogPanel;
    }

}
