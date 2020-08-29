package main;

import customs.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * The Login type provides the UI for the user to provide details of email & password
 * which it'll parse to a static function in PrePortal, launchVerification(String, String),
 * purposely for verification.
 */
public class Login extends KDialog {
    private Welcome welcome;
    private static KTextField emailField;
    private static JPasswordField passwordField;
    private static KButton loginButton;
    private static KButton closeButton;
    private static String initialHint;
    private static KPanel statusPanel;
    private static JRootPane rootPane;
    private static KScrollPane statusHolder;
    private static Login instance;
    private static final ActionListener CLOSE_LISTENER = e-> {
        instance.dispose();
        instance.welcome.setVisible(true);
    };


    public Login(Welcome welcome){
        instance = Login.this;
        instance.welcome = welcome;
        rootPane = this.getRootPane();
        this.setUndecorated(true);
        this.setSize(720, 425);
        this.setDefaultCloseOperation(Login.DO_NOTHING_ON_CLOSE);

        final KLabel bigText = new KLabel("LOGIN TO GET THE MOST OUT OF YOUR STUDENT-HOOD!",
                KFontFactory.createPlainFont(18), Color.WHITE);
        bigText.setBounds(15, 10, 625, 30);
        bigText.setOpaque(false);

        final KLabel utgLogo = KLabel.wantIconLabel("UTGLogo.gif", 50, 50);
        utgLogo.setBounds(645, 0, 100, 50);
        utgLogo.setOpaque(false);

        final KLabel loginHint = new KLabel("LOGIN", KFontFactory.createBoldFont(20), Color.BLACK);
        loginHint.setBounds(205, 5, 100, 30);

        final KLabel studentLogo = KLabel.wantIconLabel("student.png",30,30);
        studentLogo.setBounds(50, 45, 40, 30);

        final KLabel passwordLogo = KLabel.wantIconLabel("padlock.png",40,40);
        passwordLogo.setBounds(50, 100, 40, 30);

        emailField = new KTextField();
        emailField.setBounds(105, 45, 315, 30);
        emailField.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2, true));
        emailField.setToolTipText("Insert email here");
        emailField.addActionListener(e-> passwordField.requestFocusInWindow());

        passwordField = new JPasswordField(){
            @Override
            public JToolTip createToolTip() {
                return KLabel.preferredTip();
            }
        };
        passwordField.setBorder(emailField.getBorder());
        passwordField.setHorizontalAlignment(emailField.getHorizontalAlignment());
        passwordField.setFont(emailField.getFont());
        passwordField.setBounds(105, 100, 315, 30);
        passwordField.setToolTipText("Insert password here");
        passwordField.addActionListener(e-> loginTriggered());

        loginButton = new KButton("LOGIN");
        loginButton.setStyle(KFontFactory.createPlainFont(15), Color.BLUE);
        loginButton.setBounds(265, 160, 110, 30);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e-> loginTriggered());

        closeButton = new KButton("CLOSE");
        closeButton.setFont(loginButton.getFont());
        closeButton.setBounds(140, 160, 110, 30);
        closeButton.addActionListener(CLOSE_LISTENER);

        final KPanel smallPanel = new KPanel();
        smallPanel.setBackground(new Color(240, 240, 240));
        smallPanel.setBorder(BorderFactory.createLineBorder(smallPanel.getBackground(),5,true));
        smallPanel.setBounds(120, 50, 500, 210);
        smallPanel.setLayout(null);
        smallPanel.addAll(loginHint, studentLogo, passwordLogo, emailField, passwordField, loginButton, closeButton);

        statusPanel = new KPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.setBackground(new Color(40, 40, 40));

        statusHolder = KScrollPane.getAutoScroller(statusPanel);
        statusHolder.setBounds(1, 270, 718, 154);

        final KPanel contentsPanel = new KPanel();
        contentsPanel.setBackground(new Color(40, 40, 40));
        contentsPanel.setLayout(null);
        contentsPanel.addAll(bigText, utgLogo, smallPanel, statusHolder);
        this.setContentPane(contentsPanel);
        this.setLocationRelativeTo(null);

        appendToStatus(initialHint = "Enter your Email and Password in the fields provided above, respectively.");
    }

    private void loginTriggered(){
        if (Globals.isBlank(emailField.getText())) {
            App.signalError(rootPane, "No Email", "Email Field cannot be blank. Please insert an email address.");
            emailField.requestFocusInWindow();
        } else if (Globals.isBlank(String.valueOf(passwordField.getPassword()))) {
            App.signalError(rootPane,"No Password", "Password Field cannot be blank. Please insert a password.");
            passwordField.requestFocusInWindow();
        } else {
            if (!InternetAvailabilityChecker.isInternetAvailable()) {
                App.signalError(rootPane,"Internet Error","Internet connection is required to set up Dashboard.\n" +
                        "Please connect to the internet and try again.");
                return;
            }
            final boolean permission = App.showOkCancelDialog(rootPane,"Permission Checkpoint",
                    "By clicking Login, you hereby permit Dashboard to go through your portal\n" +
                            "and acknowledge the safety of your data with it.");
            if (permission) {
                Login.setInputsState(false);
            } else {
                return;
            }
            new Thread(()-> PrePortal.launchVerification(emailField.getText(), String.valueOf(passwordField.getPassword()))).start();
        }
    }

    public static void appendToStatus(String update){
        final KLabel newLabel = new KLabel(update, KFontFactory.createPlainFont(15), Color.WHITE);
        newLabel.setOpaque(false);
        statusPanel.add(newLabel);
        ComponentAssistant.ready(statusPanel);
        try {
            Thread.sleep(250);
            if (update.equals("-")) {
                statusHolder.stopAutoScrolling();
                RunningCoursesGenerator.uploadInitials();
                ModulesHandler.uploadModules();
                Student.setup();
            }
        } catch (InterruptedException ignored) {
        }
    }

    public static void replaceLastUpdate(String newUpdate){
        statusPanel.removeLastChild();
        appendToStatus(newUpdate);
    }

    public static void setInputsState(boolean state){
        ComponentAssistant.empty(statusPanel);
        emailField.setEditable(state);
        passwordField.setEditable(state);
        loginButton.setEnabled(state);
        if (state) {
            closeButton.removeActionListener(PrePortal.CANCEL_LISTENER);
            closeButton.addActionListener(CLOSE_LISTENER);
            closeButton.setText("CLOSE");
            closeButton.setForeground(null);
            appendToStatus(initialHint);
        } else {
            closeButton.removeActionListener(CLOSE_LISTENER);
            closeButton.addActionListener(PrePortal.CANCEL_LISTENER);
            closeButton.setText("CANCEL");
            closeButton.setForeground(Color.RED);
            appendToStatus("Please hang on while you're verified");
        }
    }

    public static JRootPane getRoot(){
        return rootPane;
    }

    public static Login getInstance(){
        return instance;
    }

    /**
     * PrePortal should call this after it's done all its tasks.
     */
    public static void notifyCompletion(){
        appendToStatus("#####");
        loginButton.setEnabled(false);
        Login.appendToStatus("Now running Pre-Dashboard builds....... Please wait");
        final KButton enter = new KButton("Launch Dashboard");
        enter.setFocusable(true);
        SwingUtilities.invokeLater(()-> {
            final Board firstBoard = new Board();
            enter.addActionListener(e-> {
                instance.dispose();
                firstBoard.setVisible(true);
            });
            rootPane.add(enter);
            rootPane.setDefaultButton(enter);
            replaceLastUpdate("Now running Pre-Dashboard builds....... Completed");
            appendToStatus("Your Dashboard is ready : Press 'Enter' to launch");
            appendToStatus("----------------------------------------------------------------------------------------------------------------------------------------");
            appendToStatus("                                                <<<<------- Enter ------>>>>");
            appendToStatus("-");
        });
    }

}
