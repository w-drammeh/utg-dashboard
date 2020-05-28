package main;

import customs.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

public class FirstLaunch extends KDialog {
    private final Font bigFont = KFontFactory.createBoldFont(18);
    private CardLayout CARDS = new CardLayout(){
        @Override
        public void show(Container parent, String name) {
            super.show(parent, name);
            FirstLaunch.this.setTitle("Startup Settings - "+name);
        }
    };
    private KPanel contentPane;


    public FirstLaunch(){
        super("Startup Settings - Major code");
        setModalityType(KDialog.DEFAULT_MODALITY_TYPE);
        setDefaultCloseOperation(KDialog.DO_NOTHING_ON_CLOSE);
        contentPane = new KPanel(CARDS);
        setContentPane(contentPane);
        CARDS.addLayoutComponent(contentPane.add(majorCodeComponent()), "Major code");
        CARDS.addLayoutComponent(contentPane.add(minorComponent()), "Minor");
        CARDS.addLayoutComponent(contentPane.add(emailComponent()), "Student mail");
        CARDS.addLayoutComponent(contentPane.add(imageComponent()), "Image icon");
        CARDS.addLayoutComponent(contentPane.add(welcomeComponent()), "Welcome");
        setPreferredSize(new Dimension(600, 500));
        pack();
        setLocationRelativeTo(Board.getRoot());
    }

    private static void mountDataPlus(){
        MyClass.mountUserData();
        Student.setAbout("My name is "+Student.getFullNamePostOrder()+"\n" +
                "The University of the Gambia\n" +
                "School of "+Student.getSchool()+"\n" +
                "Department of "+Student.getDepartment()+"\n" +
                Student.getMajor()+" program\n" +
                String.join(" - ", String.valueOf(Student.getYearOfAdmission()),
                        String.valueOf(Student.getExpectedYearOfGraduation())));
        SettingsUI.descriptionArea.setText(Student.getAbout());
    }

    private Component majorCodeComponent(){
        final String majorCodeText = "<p>Yes, your major code. In short, it means the 3-letter prefix of the course-codes of your major courses.</p>" +
                "<p>Dashboard performs analysis on you from a variety of angles. One of the most important is performing personalized " +
                "analysis on your <b>major courses</b>, and for that it uses this code to auto-index and filter out the courses " +
                "that are your majors.</p>" +
                "<p><b>If you are not sure, do not write anything in the field below!</b> Changes can always be made in Settings.</p>" +
                "<p>For example, the known major-code for Mathematics program is <b>MTH</b>; Computer, <b>CPS</b>; Economics, <b>ECO</b>; " +
                "Chemistry, <b>CHM</b>; Biology, <b>BIO</b>, etc.</p>";
        final KTextPane textPane = KTextPane.wantHtmlFormattedPane(majorCodeText);
        textPane.setBackground(Color.WHITE);

        final KButton nextButton = new KButton("Next");
        final KTextField majorCodeField = KTextField.rangeControlField(3);
        majorCodeField.setPreferredSize(new Dimension(150, 35));
        majorCodeField.setFont(bigFont);
        majorCodeField.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2, true));
        majorCodeField.addActionListener(e-> nextButton.doClick());

        nextButton.setFont(KFontFactory.createPlainFont(15));
        nextButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        nextButton.addActionListener(e-> {
            final String majorCode = majorCodeField.getText().toUpperCase();
            Student.setMajorCode(majorCode, true);
            SettingsUI.majorCodeField.setText(majorCode);
            CARDS.show(contentPane, "Minor");
        });

        final KPanel majorPanel = new KPanel();
        majorPanel.setLayout(new BoxLayout(majorPanel, BoxLayout.Y_AXIS));
        majorPanel.addAll(KPanel.wantDirectAddition(new KLabel("What's Your Major Code?", bigFont)), textPane,
                KPanel.wantDirectAddition(majorCodeField),
                ComponentAssistant.contentBottomGap(), KPanel.wantDirectAddition(new FlowLayout(FlowLayout.RIGHT),null,nextButton));
        return majorPanel;
    }

    private Component minorComponent(){
        final String minorText = "<p>If you are also doing a minor, then Dashboard has even better ways of organizing your modules.</p>" +
                "<p>Write, in the fields below, the <b>name</b> and <b>course-code</b> of the program you are minoring.<br>" +
                "<i>Remember, the minor-code must also match the minor program in a way as specified earlier for the major-code.</i></p>" +
                "<p>If you are not minoring a program, select the corresponding button below and continue.</p>";
        final KTextPane textPane = KTextPane.wantHtmlFormattedPane(minorText);
        textPane.setBackground(Color.WHITE);

        final KTextField minorCodeField = KTextField.rangeControlField(3);
        final KButton nextButton = new KButton("Next");
        final KTextField minorNameField = new KTextField();
        minorNameField.setPreferredSize(new Dimension(350, 30));
        minorNameField.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2, true));
        minorNameField.setEditable(false);
        minorNameField.addActionListener(e->minorCodeField.requestFocusInWindow());

        minorCodeField.setPreferredSize(new Dimension(125, 30));
        minorCodeField.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2, true));
        minorCodeField.setEditable(false);
        minorCodeField.addActionListener(e->{
            if (minorNameField.hasText()) {
                nextButton.doClick();
            } else {
                minorNameField.requestFocusInWindow();
            }
        });

        final JRadioButton iDoButton = new JRadioButton("Am Doing Minor");
        iDoButton.setFont(KFontFactory.createPlainFont(15));
        iDoButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        iDoButton.addItemListener(e -> {
            minorNameField.setEditable(e.getStateChange() == ItemEvent.SELECTED);
            minorCodeField.setEditable(e.getStateChange() == ItemEvent.SELECTED);
        });
        final JRadioButton iDontButton = new JRadioButton("Am Not Doing Minor", true);
        iDontButton.setFont(KFontFactory.createPlainFont(15));
        iDontButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        final ButtonGroup choicesGroup = new ButtonGroup();
        choicesGroup.add(iDoButton);
        choicesGroup.add(iDontButton);

        final KPanel kPanel = new KPanel();
        kPanel.setLayout(new BoxLayout(kPanel, BoxLayout.Y_AXIS));
        kPanel.addAll(textPane, KPanel.wantDirectAddition(iDoButton, iDontButton),
                KPanel.wantDirectAddition(new KLabel("Minor Program: ",KFontFactory.createBoldFont(16)),minorNameField),
                KPanel.wantDirectAddition(new KLabel("Code: ",KFontFactory.createBoldFont(16)), minorCodeField));

        final KButton prevButton = new KButton("Back");
        prevButton.setFont(KFontFactory.createPlainFont(15));
        prevButton.addActionListener(e-> CARDS.show(contentPane, "Major code"));

        nextButton.setFont(KFontFactory.createPlainFont(15));
        nextButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        nextButton.addActionListener(e-> {
            if (iDoButton.isSelected()) {
                final String minorName = minorNameField.getText();
                final String minorCode = minorCodeField.getText().toUpperCase();
                if (Globals.isBlank(minorName)) {
                    App.signalError("Error", "Sorry, the name of the minor program cannot be blank.");
                    return;
                } else if (Globals.isBlank(minorCode)) {
                    App.promptWarning("Warning", "You have not set the code for your minor program - "+minorName+".\n" +
                            "Set this later in the Settings for Dashboard to detect your minor courses.");
                }

                SettingsUI.minorLabel.setText(minorName);
                SettingsUI.minorField.setText(minorName);

                Student.setMinorCode(minorCode, true);
                SettingsUI.minorCodeField.setText(minorCode);
            } else {
                Student.setMinor("");
            }
            CARDS.show(contentPane, "Student mail");
        });

        final KPanel minorPanel = new KPanel();
        minorPanel.setLayout(new BoxLayout(minorPanel, BoxLayout.Y_AXIS));
        minorPanel.addAll(KPanel.wantDirectAddition(new KLabel("Do You Minor a Program?", bigFont)), kPanel,
                ComponentAssistant.contentBottomGap(), KPanel.wantDirectAddition(new FlowLayout(FlowLayout.RIGHT),
                        null,prevButton, nextButton));
        return minorPanel;
    }

    private Component emailComponent(){
        final String mailText = "Well, every enrolled student is automatically assigned an email address known as the <b>Student mail</b>." +
                "<p>It is a mean through which UTG reaches out to you. Unfortunately, some students will came to know about this overdue. " +
                "So, Dashboard has loaded the predicted credentials in the fields below. It's through this Student mail that " +
                "you can contact the developers, send reviews, and give feedbacks.</p>" +
                "<p>If you don't want Dashboard to keep track of your Student mail, <b>skip</b> this dialog.</p>" +
                "<p>If you've already being using your Student mail, and made changes to either the Email or Password, " +
                "then make the changes to the fields below, and click <b>Set</b>.</p>";
        final KTextPane textPane = KTextPane.wantHtmlFormattedPane(mailText);
        textPane.setBackground(Color.WHITE);

        final KTextField emailField = new KTextField(new Dimension(325, 30));
        emailField.setText(Student.predictedStudentMailAddress());
        emailField.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2, true));
        final KTextField psswdField = new KTextField(new Dimension(325, 30));
        psswdField.setText(Student.predictedStudentPassword());
        psswdField.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2, true));

        final KButton setButton = new KButton("Set");
        setButton.setFont(KFontFactory.createPlainFont(15));
        setButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setButton.addActionListener(e->{
            if (!emailField.hasText()) {
                App.signalError("No Email", "To set a student mail, please provide it in the email field.");
                emailField.requestFocusInWindow();
                return;
            }
            if (!psswdField.hasText()) {
                App.signalError("No Email", "Please provide the password in the password field.");
                psswdField.requestFocusInWindow();
                return;
            }

            Student.setStudentMail(emailField.getText());
            SettingsUI.studentMailField.setText(emailField.getText());
            Student.setStudentPassword(psswdField.getText());
            SettingsUI.studentPsswdField.setText(psswdField.getText());
            CARDS.show(contentPane, "Image icon");
            Student.mayReportIncoming();
        });

        final KPanel kPanel = new KPanel();
        kPanel.setLayout(new BoxLayout(kPanel, BoxLayout.Y_AXIS));
        kPanel.addAll(textPane, KPanel.wantDirectAddition(new KLabel("Email: ",KFontFactory.createBoldFont(16)), emailField),
                KPanel.wantDirectAddition(new KLabel("Password: ",KFontFactory.createBoldFont(16)), psswdField));

        final KButton prevButton = new KButton("Back");
        prevButton.setFont(KFontFactory.createPlainFont(15));
        prevButton.addActionListener(e-> CARDS.show(contentPane, "Minor"));

        final KButton skipButton = new KButton("Skip");
        skipButton.setStyle(KFontFactory.createPlainFont(15), Color.RED);
        skipButton.addActionListener(e-> CARDS.show(contentPane, "Image icon"));

        final KPanel emailPanel = new KPanel();
        emailPanel.setLayout(new BoxLayout(emailPanel, BoxLayout.Y_AXIS));
        emailPanel.addAll(KPanel.wantDirectAddition(new KLabel("Do you know you had a Student Mail?", bigFont)), kPanel,
                ComponentAssistant.contentBottomGap(), KPanel.wantDirectAddition(new FlowLayout(FlowLayout.RIGHT),
                        null,prevButton,skipButton,setButton));
        return emailPanel;
    }

    private Component imageComponent(){
        final String imgText = "With such a nice look, you cannot wait to behold your glittering face right at the top-left " +
                "of your dashboard. Set an optional image icon now to get started with your <b>Personal Dashboard</b>, or anytime later " +
                "under Settings.<br>You can also change your image by simply right-clicking the blue-bordered box at the top-left.";
        final KTextPane textPane = KTextPane.wantHtmlFormattedPane(imgText);
        textPane.setBackground(Color.WHITE);

        final KPanel iPanel = new KPanel();
        iPanel.add(new KLabel(Student.getIcon()));

        final KButton setButton = new KButton("Set Now");
        setButton.setFocusable(true);
        setButton.setFont(KFontFactory.createPlainFont(14));
        setButton.setForeground(Color.BLUE);
        setButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setButton.addActionListener(e-> {
            Student.startSettingImage(this.getRootPane());
            ComponentAssistant.repair(iPanel);
            iPanel.add(new KLabel(Student.getIcon()));
            ComponentAssistant.ready(iPanel);
        });

        final KPanel nicePanel = new KPanel();
        nicePanel.setLayout(new BoxLayout(nicePanel, BoxLayout.Y_AXIS));
        nicePanel.addAll(textPane, iPanel, KPanel.wantDirectAddition(setButton));

        final KButton prevButton = new KButton("Back");
        prevButton.setFont(KFontFactory.createPlainFont(15));
        prevButton.addActionListener(e-> CARDS.show(contentPane, "Student mail"));
        final KButton finishButton = new KButton("Finish");
        finishButton.setFont(KFontFactory.createPlainFont(15));
        finishButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        finishButton.addActionListener(e-> {
            CARDS.show(contentPane, "Welcome");
            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            new Thread(FirstLaunch::mountDataPlus).start();
        });

        final KPanel imgPanel = new KPanel();
        imgPanel.setLayout(new BoxLayout(imgPanel, BoxLayout.Y_AXIS));
        imgPanel.addAll(KPanel.wantDirectAddition(new KLabel("You Look Nice!", bigFont)), nicePanel,
                ComponentAssistant.contentBottomGap(), KPanel.wantDirectAddition(new FlowLayout(FlowLayout.RIGHT),
                        null,prevButton,finishButton));
        return imgPanel;
    }

    private Component welcomeComponent(){
        final String mailText = "<b>"+Student.getLastName()+", you are all set... Dashboard is all yours!</b>" +
                "<p>"+Student.getFullName()+", you've just started a new journey into your student-hood at <i>The University of the Gambia</i>. " +
                "By seeing this dialog means that you've completed setting up your <b>Personal Dashboard</b>. Dashboard is flexible: it does so much " +
                "while asking you almost nothing.</p>" +
                "<p>Please see <b>Home | FAQs & Help | Dashboard Tips</b> to get yourself quickly familiar with the vast features " +
                "of Dashboard.</p>" +
                "<p>Finally, by seeing this dialog, it means Dashboard has already created a root folder in your home directory - " +
                MyClass.rootDir+". You may want to check out for this folder and the <b>README.txt</b> file therein.</p>" +
                "<p style='text-align: center;'>Thank you for using <b>Dashboard</b></p>";
        final KTextPane textPane = KTextPane.wantHtmlFormattedPane(mailText);
        textPane.setBackground(Color.WHITE);

        final KButton startButton = new KButton("Start using Dashboard");
        startButton.setFocusable(true);
        startButton.setStyle(KFontFactory.createPlainFont(15), Color.BLUE);
        startButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        startButton.addActionListener(e-> FirstLaunch.this.dispose());

        final KPanel welcomePanel = new KPanel();
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
        welcomePanel.addAll(KPanel.wantDirectAddition(new KLabel("Voila!", bigFont)), textPane,
                KPanel.wantDirectAddition(new FlowLayout(),null,startButton), Box.createVerticalStrut(50));
        return welcomePanel;
    }

}
