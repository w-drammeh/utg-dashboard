package main;

import customs.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * <h1>class Welcome</h1>
 * <p>The Welcome class shows an overview of the project at glance.</p>
 * <p><i>It should contain useful info. of the dashboard.</i></p>
 *
 * <p><i>It causes a new instance of Login to be visible.</i></p>
 */
public class Welcome extends KDialog {
    private static final int PREFERRED_WIDTH = 675;


    public Welcome() {
        super("Welcome");
        this.setSize(PREFERRED_WIDTH + 50, 575);

        final KPanel topPanel = KPanel.wantDirectAddition(new KLabel("Personal Dashboard", KFontFactory.createBoldFont(25)));
        topPanel.setBackground(Color.WHITE);

        final String broughtString = "Proudly brought to you by the <b>Dashboard Project</b>. Dashboard comes with solutions long-anticipated by the UTG Students, " +
                "so use it to <i>organize yourself</i>! Before proceeding, we vehemently recommend that you go through the disclaimer below.";

        final String dedicationText = "Dashboard is developed by the Students for the Students. Whether you're an <b>Undergraduate</b>, <b>Postgraduate</b>, or a student <b>deferring</b>, Dashboard got you covered. " +
                "Dashboard grasp your fundamental details at every successful login - so you don't " +
                "need to be manually specifying your level, or status on the go.";

        final String requirementText = "Dashboard, as a project, is written completely in the Java (<i>platform-independent language</i>), allowing it to run gently on virtually all operating systems. " +
                "The system-dependent compilations are as a result of the <b>Selenium Web-Driver Specification</b> across platforms." +
                "<p>Dashboard uses the traditional <em>Firefox Browser</em> to get the better of your portal. " +
                "This is wholly background, and does no way interfere with the normal usage of your browser.</p>" +
                "<p>For uniformity reasons, Dashboard is not <b>tested compatible</b> with any other browser. " +
                "So <i>if Firefox is not installed</i>, please make sure it is installed and ready before start - " +
                "as we offer no guarantee as to how the app behaves with other browsers.</p>";

        final String securityText = "<em>Whatever happens in your Dashboard, stays in your Dashboard</em>. However, keep the following points in mind as long as the portal is concerned:" +
                "<h3 style='font-size: 14px;'>What Dashboard cannot do</h3>" +
                "<b>Dashboard does not write your portal</b>! Dashboard is not legalized to do so just yet, therefore it cannot <i>register or drop courses, neither can it apply deferment for you, nor can it be used as a mean of application for yet-enrolled students</i>." +
                "<h3 style='font-size: 14px;'>What Dashboard can do</h3>" +
                "Just a glimpse of what Dashboard is capable of carving out for you:" +
                "<ul>" +
                "<li>You'll be able to <b>Print / Export your transcript</b> in a Portable Document Format</li>" +
                "<li>Dashboard provides powerful analyzation system of filtering your courses based on grades, scores, requirements, etc; your attended-lecturers, CGPAs earned, to mention a few</li>" +
                "<li>Sketches your semester-to-semester performance with respect to the CGPA earned per semester</li>" +
                "<li>Dashboard possesses rich customization property, letting you to keep track of even <b>non-academic related details</b>. Customizations include changing the <b>Look & Feel</b> " +
                "to your native system's Look. So feel free to assume you're using a typical system application</li>" +
                "<li>Enjoy the carefully spelled-out answers to some <b>FAQs</b> of UTG</li>" +
                "<li>Dashboard keeps you up to date with the News from UTG's official news site in a more presentable manner</li>" +
                "<li>Dashboard effectively organizes your <b>personal tasks, group works, assignments, and other student-related cares</b>, all at your fingertips</li>" +
                "<li>Dashboard reads, if necessary, every last detail of your portal which you can access anytime offline!</li></ul>";

        final String importantText = "The analysis provided to you by Dashboard is entirely <i>portal-independent</i>.<br>" +
                "But analysis is based on data, and their is no better source of your data than your portal. Therefore, unexpected details from therein can induce <i>misbehavior of your Dashboard</i>!" +
                "<p>We urged every student victim of <b>wrong</b>, or <b>incomplete details</b> from their portals to refer to their respective departments " +
                "for help before proceeding.</p>" +
                "<p>We however handle, gracefully, the common issue of <b>missing-grades</b>, but cannot afford to loose core details like your <b>name or matriculation number</b>. " +
                "Dashboard may halt build, if such details are missing, or however not readable.</p>" +
                "<p>Besides missing details, some students have <b>conflicting information</b> in their portals. " +
                "This can let you have the <b>worst possible experience</b> from your usage of Dashboard! For instance, a student admitted in 2016 may have his/her year-of-admission 2019 in the portal. " +
                "To mention a few consequences of this is that, obviously, a wrong computation will be returned when Dashboard is asked to predict the <b>expected-year-of-graduation, or the current-level of the student</b>. " +
                "Plus, <i>mis-indexing of modules' years</i> will occur which, in turn, will cause <i>analysis-by-year problems, and addition of modules to the inappropriate tables</i>.</p>" +
                "<p>The good news is: all these, if occurred, can be fixed at any point in time even after build - as Dashboard effortlessly <b>re-indexes</b> your resources after every successful login.</p>";

        final String nextText = "<p>To continue, acknowledge adherence to these terms by selecting the <b>CheckBox</b> below.</p>";

        final KPanel separatorPanel = KPanel.wantDirectAddition(new KSeparator(Color.RED, new Dimension(PREFERRED_WIDTH, 1)));
        separatorPanel.setBackground(Color.WHITE);

        final KPanel welcomePanel = new KPanel();
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
        welcomePanel.addAll(topPanel, write(broughtString, 70),
                head("Dedication"),write(dedicationText, 90),head("System Requirement"),write(requirementText, 210), head("Portal & Privacy"),write(securityText, 565),
                head("Important"),write(importantText, 455), separatorPanel, write(nextText, 75));

        final KScrollPane kScrollPane = new KScrollPane(welcomePanel, false);
//        kScrollPane.setPreferredSize(new Dimension(PREFERRED_WIDTH, 525));

        final KButton exitButton = new KButton("Exit");
        exitButton.setFont(KFontFactory.createPlainFont(15));
        exitButton.addActionListener(e-> System.exit(0));

        final KButton nextButton = new KButton("Next");
        nextButton.setFont(KFontFactory.createPlainFont(15));
        nextButton.addActionListener(e -> {
            Welcome.this.dispose();
            SwingUtilities.invokeLater(() -> {
                new Login(this).setVisible(true);
            });
        });
        nextButton.setEnabled(false);

        final KCheckBox checkBox = new KCheckBox("I hereby read, understood, and consent to these terms.");
        checkBox.setFont(KFontFactory.createPlainFont(15));
        checkBox.setFocusable(true);
        checkBox.setForeground(Color.RED);
        checkBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        checkBox.addItemListener(e -> {
            nextButton.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
        });

        final KPanel lowerPanel = new KPanel(new Dimension(PREFERRED_WIDTH, 50));
        lowerPanel.setLayout(new BoxLayout(lowerPanel, BoxLayout.X_AXIS));
        lowerPanel.addAll(checkBox, KPanel.wantDirectAddition(new FlowLayout(FlowLayout.RIGHT), null, exitButton, nextButton));

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        this.getRootPane().setDefaultButton(nextButton);
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(kScrollPane, BorderLayout.CENTER);
        this.getContentPane().add(lowerPanel, BorderLayout.SOUTH);
        this.setLocationRelativeTo(null);
    }

    public static KTextPane write(String manyText, int tHeight){
        final KTextPane textPane = KTextPane.wantHtmlFormattedPane(manyText);
        textPane.setBackground(Color.WHITE);
        textPane.setPreferredSize(new Dimension(PREFERRED_WIDTH, tHeight));

        return textPane;
    }

    private KPanel head(String head){
        final KPanel headerPanel = new KPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.add(new KLabel(head, KFontFactory.createBoldFont(17), Color.BLUE));

        return headerPanel;
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (b) {
            SwingUtilities.invokeLater(()->{
                ((KScrollPane) this.getContentPane().getComponent(0)).toTop();
            });
        }
    }

}
