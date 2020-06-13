package main;

import customs.*;

import javax.swing.*;
import java.awt.*;

/**
 * The Login type provides the UI for the user to provide details of email & password which it'll
 * parse to a static function of the PrePortal class, launchVerificationSequences(), for verification.
 */
public class Login extends KDialog {
    private KTextField emailField;
    private JPasswordField passwordField;
    private Welcome welcome;
    private static String initialHint;
    private static KButton loginButton, cancelButton;
    private static KPanel statusPanel;
    private static KPanel smallPanel;
    private static JRootPane rootPane;
    private static KScrollPane statusHolder;
    private static Login loginInstance;


    public Login(Welcome welcome){
        loginInstance = Login.this;
        rootPane = this.getRootPane();
        this.welcome = welcome;
        this.setSize(720, 425);
        this.setDefaultCloseOperation(Login.DO_NOTHING_ON_CLOSE);
        this.setUndecorated(true);

        final KLabel bigText = new KLabel("LOGIN TO GET THE MOST OUT OF YOUR STUDENT-HOOD!",
                KFontFactory.createPlainFont(18), Color.WHITE);
        bigText.setBounds(15, 10, 625, 30);
        bigText.setOpaque(false);

        final KLabel utgLabel = KLabel.wantIconLabel("UTGLogo.gif", 50, 50);
        utgLabel.setBounds(645, 0, 100, 50);
        utgLabel.setOpaque(false);

        final KLabel logHint = new KLabel("LOGIN",KFontFactory.createBoldFont(20),Color.BLACK);
        logHint.setBounds(205, 5, 100, 30);

        final KLabel studentLabel = KLabel.wantIconLabel("student.png",30,30);
        studentLabel.setBounds(50, 45, 40, 30);

        final KLabel passwordLabel = KLabel.wantIconLabel("padlock.png",40,40);
        passwordLabel.setBounds(50, 100, 40, 30);

        emailField = new KTextField();
        emailField.setBounds(105, 45, 315, 30);
        emailField.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2, true));
        emailField.setToolTipText("Insert your Email here");
        emailField.addActionListener(e-> passwordField.requestFocusInWindow());

        passwordField = new JPasswordField();
        passwordField.setBorder(emailField.getBorder());
        passwordField.setHorizontalAlignment(emailField.getHorizontalAlignment());
        passwordField.setFont(emailField.getFont());
        passwordField.setBounds(105, 100, 315, 30);
        passwordField.setToolTipText("Insert your Password here");
        passwordField.addActionListener(e-> loginButton.doClick());

        loginButton = new KButton("LOGIN");
        loginButton.setStyle(KFontFactory.createPlainFont(15), Color.BLUE);
        loginButton.setBounds(265, 160, 110, 30);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> {
            if (Globals.isBlank(emailField.getText())) {
                App.signalError(rootPane,"Error","Email Field cannot be blank. Please try again.");
                emailField.requestFocusInWindow();
            } else if (Globals.isBlank(new String(passwordField.getPassword()))) {
                App.signalError(rootPane,"Error", "Password Field cannot be blank. Please try again.");
                passwordField.requestFocusInWindow();
            } else {
                onNext();
            }
        });

        cancelButton = new KButton("CLOSE");
        cancelButton.setFont(loginButton.getFont());
        cancelButton.setBounds(140, 160, 110, 30);
        cancelButton.addActionListener(e -> {
            Login.this.dispose();
            SwingUtilities.invokeLater(() -> this.welcome.setVisible(true));
        });

        smallPanel = new KPanel();
        smallPanel.setBackground(new Color(240, 240, 240));
        smallPanel.setBorder(BorderFactory.createLineBorder(smallPanel.getBackground(),5,true));
        smallPanel.setBounds(120, 50, 500, 210);
        smallPanel.setLayout(null);
        smallPanel.addAll(logHint, studentLabel, passwordLabel, emailField, passwordField, loginButton, cancelButton);

        statusPanel = new KPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel,BoxLayout.Y_AXIS));
        statusPanel.setBackground(new Color(40, 40, 40));

        statusHolder = KScrollPane.getAutoScroller(statusPanel);
        statusHolder.setBounds(1, 270, 718, 154);

        final KPanel contentsPanel = new KPanel();
        contentsPanel.setBackground(new Color(40, 40, 40));
        contentsPanel.setLayout(null);
        contentsPanel.addAll(bigText, utgLabel, smallPanel, statusHolder);
        this.setContentPane(contentsPanel);

        appendToStatus(initialHint = "Enter your Email and Password in the fields provided above, respectively.");
        this.setLocationRelativeTo(null);
    }

    public static void appendToStatus(String update){
        final KLabel newLabel = new KLabel(update, KFontFactory.createPlainFont(15), Color.WHITE);
        newLabel.setOpaque(false);
        statusPanel.add(newLabel);
        ComponentAssistant.ready(statusPanel);
        try {
            Thread.sleep(250);
            if (update.equals("-")) {
                SwingUtilities.invokeLater(()-> KScrollPane.stopAutoScrollingOn(statusHolder));
                RunningCoursesGenerator.uploadInitials();
                ModulesHandler.uploadModules();
            }
        } catch (InterruptedException e) {
            App.silenceException(e);
        }
    }

    public static void replaceLastUpdate(String newUpdate){
        statusPanel.removeLastChild();
        appendToStatus(newUpdate);
    }

    public static void setInputsState(boolean enable){
        ComponentAssistant.repair(statusPanel);
        appendToStatus(enable ? initialHint : "Please hang on while you're verified");
        for (Component c : smallPanel.getComponents()) {
            c.setEnabled(enable);
        }
    }

    public static JRootPane getRoot(){
        return rootPane;
    }

    /**
     * PrePortal should call this after it's done all its tasks...
     */
    public static void notifyCompletion(){
        Login.appendToStatus("Now running Pre-Dashboard builds....... Please wait");
        final KButton enter = new KButton("Launch Dashboard");
        enter.setFocusable(true);
        SwingUtilities.invokeLater(()-> {
            final Board firstBoard = new Board();
            enter.addActionListener(e -> {
                loginInstance.dispose();
                firstBoard.setVisible(true);
            });
            rootPane.add(enter);
            rootPane.setDefaultButton(enter);
            replaceLastUpdate("Now running Pre-Dashboard builds....... Completed");
            appendToStatus("Your Dashboard is ready : Press \"Enter\" to launch");
            appendToStatus("----------------------------------------------------------------------------------------------------------------------------------------");
            appendToStatus("                                                <<<<------- Enter ------>>>>");
            appendToStatus("-");
        });
    }

    private void onNext(){
        if (!InternetAvailabilityChecker.isInternetAvailable()) {
            App.signalError(rootPane,"Internet Error","Internet connection is required to set up Dashboard.\n" +
                    "Please connect to the internet and try again.");
            return;
        }

        final boolean permission = App.showYesNoCancelDialog(rootPane,"Permission Checkpoint",
                "By clicking Login you hereby permit Dashboard to go through your portal\n" +
                "and acknowledge the safety of your data with it.");
        if (permission) {
            Login.setInputsState(false);
        } else {
            return;
        }
        new Thread(() -> PrePortal.launchVerificationSequences(emailField.getText(), new String(passwordField.getPassword()))).start();
    }

}
