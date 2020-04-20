package main;

import customs.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * <h1>class Login</h1>
 * <p><b>Description</b>: The Login type provides the framework for the user to provide
 * details of email & password which it'll parse to a static function  of the {@link PrePortal} class, launchVerificationSequences,
 * for verification</p>
 * <p><b>GUI</b>: A sizable undecorated frame with a null-layout, as  specified by its contentPane
 * static instance - contents. </p>
 */
public class Login extends KDialog {
    private static final String initialHint = "Please enter your Email and Password in the fields provided above, respectively.";
    private static KButton loginButton, cancelButton;
    private static Container contents;
    private static KPanel statusPanel;
    private static KPanel smallPanel;
    private static JRootPane rootPane;
    private static KScrollPane statusHolder;
    private static Login loginInstance;
    private KTextField emailField;
    private JPasswordField passwordField;
    private Welcome welcome;


    public Login(Welcome welcome){
        this.welcome = welcome;
        this.setSize(720,425);
        this.setDefaultCloseOperation(Login.DO_NOTHING_ON_CLOSE);
        this.setUndecorated(true);

        final KLabel bigText = new KLabel("LOGIN TO GET THE MOST OUT OF YOUR STUDENT-HOOD!", KFontFactory.createPlainFont(18),Color.WHITE);
        bigText.setBounds(15, 10, 625, 30);
        bigText.setBackground(null);

        final KLabel utgLabel = KLabel.wantIconLabel("Logo_of_UTG.gif",50,50);
        utgLabel.setBounds(645, 0, 100, 50);
        utgLabel.setBackground(null);

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
        emailField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    passwordField.requestFocusInWindow();
                }
            }
        });

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
        passwordField.setToolTipText("Insert your Password here");
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    loginButton.doClick(0);
                }
            }
        });

        loginButton = new KButton("LOGIN");
        loginButton.setStyle(KFontFactory.createPlainFont(15), Color.BLUE);
        loginButton.setBounds(265, 160, 110, 30);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> {
            if (Globals.isBlank(emailField.getText())) {
                App.signalError(rootPane,"Error","Email field cannot be blank. Please try again.");
                emailField.requestFocusInWindow();
            } else if (Globals.isBlank(new String(passwordField.getPassword()))) {
                App.signalError(rootPane,"Error","Password field cannot be blank. Please try again.");
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
            SwingUtilities.invokeLater(() -> {
                this.welcome.setVisible(true);
            });
        });

        smallPanel = new KPanel();
        smallPanel.setBackground(new Color(240, 240, 240));
        smallPanel.setLayout(null);
        smallPanel.setBounds(120, 55, 500, 210);
        smallPanel.setBorder(BorderFactory.createLineBorder(smallPanel.getBackground(),5,true));
        smallPanel.add(logHint);
        smallPanel.add(studentLabel);
        smallPanel.add(passwordLabel);
        smallPanel.add(emailField);
        smallPanel.add(passwordField);
        smallPanel.add(loginButton);
        smallPanel.add(cancelButton);

        statusPanel = new KPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel,BoxLayout.Y_AXIS));
        statusPanel.setBackground(new Color(40, 40, 40));

        statusHolder = KScrollPane.getAutoScroller(statusPanel);
        statusHolder.setBounds(5,275,710,145);

        contents = this.getContentPane();
        contents.setLayout(null);
        contents.setBackground(new Color(40, 40, 40));
        contents.add(bigText);
        contents.add(utgLabel);
        contents.add(smallPanel);
        contents.add(statusHolder);
        rootPane = this.getRootPane();

        appendToStatus(initialHint);

        loginInstance = Login.this;
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
        if (enable) {
            ComponentAssistant.repair(statusPanel);
            appendToStatus(initialHint);
        }

        for (Component c : smallPanel.getComponents()) {
            c.setEnabled(enable);
        }
    }

    public static JRootPane getRoot(){
        return rootPane;
    }

    /**
     * <p>PrePortal should call this after it's done all its tasks...</p>
     */
    public static void notifyCompletion(){
        Login.appendToStatus("Now running Pre-Dashboard builds....... Please wait");
        final KButton enter = new KButton("Launch Dashboard", true);
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
            App.signalError(rootPane,"Internet Error","Sorry, internet connection is required to set up Dashboard.\n" +
                    "Please connect to the internet and try again.");
            return;
        }

        final boolean permission = App.showYesNoCancelDialog(rootPane,"Permission Checkpoint","By clicking 'Login' you hereby permit Dashboard to go through your portal,\n" +
                "and acknowledge the safety of your data with it. Continue?");
        if (permission) {
            replaceLastUpdate("Thank you for your trust!");
            appendToStatus("Please hang-on while you're verified");
        }else {
            return;
        }

        Login.setInputsState(false);
        new Thread(() -> {
            PrePortal.launchVerificationSequences(emailField.getText(),new String(passwordField.getPassword()));
        }).start();
    }

}
