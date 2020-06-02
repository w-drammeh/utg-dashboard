package main;

import customs.*;
import utg.Dashboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author Muhammed W. Drammeh
 *
 * The ultimate class of UI.
 * When using Dashboard, the user actually interacts with an instance of this
 * child of KFrame, an hier to JFrame.
 *
 * I'll assume a height of about 20 - 30 pixels is consumed by the decoration of the platform.
 * thoraxPanel shall cover 230 pixels of height
 * bodyLayer (Panel) 450
 * Thus, the total dimension is roughly 1_000 x (680 - 700)
 */
public final class Board extends KFrame {
    /**
     * The standard root for dialogs. This is assigned to the rootPane.
     */
    private static JRootPane boardRoot;
    /**
     * The container (actually, a KPanel) onto which the 2 layers are placed.
     * The contentPane is set to this.
     */
    private static KPanel boardContent;
    /**
     * Has the cardLayout and responsible for bringing and discarding the main activities.
     */
    private static KPanel bodyLayer;
    /**
     * The layout which shifts between the four(4) main activities:
     * 'Home', 'News', 'Tasks', and 'Notifications'.
     */
    private static CardLayout cardBoard;
    private static KPanel imagePanel;//Left-most top, lies the image
    private static KLabel stateIndicator;//This is locally triggered
    private static KLabel levelIndicator;//Very dormant in changes anyway
    private static KLabel semesterIndicator;
    private static KLabel nameLabel;
    private static KButton toPortalButton;
    private static KButton notificationButton;
    private static Board appInstance;
    public static final ArrayList<Runnable> postProcesses = new ArrayList<Runnable>();
    public static final Thread shutDownThread = new Thread(MyClass::mountUserData);


//    Collaborators declaration.
//    The order in which these will be initialized does matter!
    private RunningCoursesGenerator runningCoursesGenerator;
    private ModulesGenerator modulesGenerator;
    private SettingsUI settingsUI;
    private TranscriptGenerator transcriptGenerator;
    private AnalysisGenerator analysisGenerator;
    private HelpGenerator faqsGenerator;
    private MyDashboard myDashboard;


    public Board(){
        appInstance = Board.this;
        this.setTitle("UTG-Student Dashboard");
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (SettingsCore.confirmExit) {
                    if (App.showYesNoCancelDialog("Confirm Exit", "Do you really mean to close the Dashboard?")) {
                        setVisible(false);
                    }
                } else {
                    setVisible(false);
                }
            }
        });

        boardContent = new KPanel();
        boardContent.setLayout(new BoxLayout(boardContent, BoxLayout.Y_AXIS));
        this.setUpThorax();
        this.setUpBody();
        this.setContentPane(boardContent);

        boardRoot = this.getRootPane();
        SettingsCore.allLooksInfo = UIManager.getInstalledLookAndFeels();

        runningCoursesGenerator = new RunningCoursesGenerator();
        modulesGenerator = new ModulesGenerator();
        settingsUI = new SettingsUI();
        transcriptGenerator = new TranscriptGenerator();
        analysisGenerator = new AnalysisGenerator();
        faqsGenerator = new HelpGenerator();
        myDashboard = new MyDashboard();

        final Timer dayTimer = new Timer(Globals.DAY_IN_MILLI, e-> anotherDay());
        dayTimer.setInitialDelay(Globals.DAY_IN_MILLI - MDate.getTimeValue(new Date()));

        this.pack();
        this.setMinimumSize(this.getPreferredSize());
        this.setLocationRelativeTo(null);
        this.attachUniversalKeys();
        this.completeBuild();
    }

    public static JRootPane getRoot(){
        return boardRoot;
    }

    /**
     * Provides the runtime instance of the Dashboard.
     */
    public static Board getInstance(){
        return appInstance;
    }

    public static boolean isAppReady(){
        return appInstance != null && appInstance.isShowing();
    }

    public static void addCard(Component card, String name){
        cardBoard.addLayoutComponent(bodyLayer.add(card), name);
    }

    public static void showCard(String cardName){
        cardBoard.show(bodyLayer, cardName);
    }

    /**
     * As used by NotificationGenerator to adjust the toolTip.
     */
    public static KButton getNotificationButton(){
        return notificationButton;
    }

    public static KPanel getImagePanel(){
        return imagePanel;
    }

    public static void effectRegistrationState(boolean oneIs){
        if (oneIs) {
            stateIndicator.setText("Active");
            stateIndicator.setStyle(KFontFactory.createItalicFont(15), Color.BLUE);
            stateIndicator.setToolTipText("This means you have registered for this semester");
        } else {
            stateIndicator.setText("Inactive");
            stateIndicator.setStyle(KFontFactory.createPlainFont(15), Color.RED);
            stateIndicator.setToolTipText("Please register at least one course");
        }
    }

    public static void effectSemesterUpgrade(String upgrade){
        if (isAppReady()) {
            semesterIndicator.setText(upgrade);
            RunningCoursesGenerator.semesterBigLabel.setText(upgrade);
        } else {
            postProcesses.add(()->{
                semesterIndicator.setText(upgrade);
                RunningCoursesGenerator.semesterBigLabel.setText(upgrade);
            });
        }
    }

    public static void effectLevelUpgrade(){
        if (levelIndicator != null) {
            levelIndicator.setText(Student.getLevel());
        } else {
            postProcesses.add(()-> levelIndicator.setText(Student.getLevel()));
        }
    }

    public static void effectNameFormatChanges(){
        if (isAppReady()) {
            nameLabel.setText(Student.requiredNameForFormat());
        } else {
            postProcesses.add(()-> nameLabel.setText(Student.requiredNameForFormat()));
        }
    }

    /**
     * This call sets up the thorax-region of the Dashboard.
     *
     * Total height to be covered is 230, and the 30 is for the bigButtons to lie beneath.
     * This is horizontally partitioned into 3 sections with widths as follows:
     * imagePart 300
     * detailsPart 375 and
     * midPart unset
     */
    private void setUpThorax(){
        final KMenuItem resetOption = new KMenuItem("Reset", e -> Student.fireIconReset());
        final KMenuItem defaultOption = new KMenuItem("Set Default", e -> Student.fireIconDefaultSet());

        final JPopupMenu imageOptionsPop = new JPopupMenu();
        imageOptionsPop.add(new KMenuItem("Change", e -> Student.startSettingImage()));
        imageOptionsPop.add(resetOption);
        imageOptionsPop.add(defaultOption);

        imagePanel = new KPanel(new BorderLayout(), new Dimension(275,200));
        imagePanel.setBorder(BorderFactory.createLineBorder(Color.BLUE, 1, true));
        imagePanel.add(new KLabel(Student.getIcon()));
        imagePanel.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {//For linux (or unix-liked) systems
                    resetOption.setEnabled(!Student.isNoIconSet());
                    defaultOption.setEnabled(!Student.isDefaultIconSet());
                    imageOptionsPop.show(imagePanel, e.getX(), e.getY());
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {//Windows systems
                mousePressed(e);
            }
        });

        levelIndicator = new KLabel(Student.getLevel(), KFontFactory.createPlainFont(15), Color.BLUE);
        final KPanel levelPanel = new KPanel(new FlowLayout(FlowLayout.LEFT), new Dimension(325,25));
        levelPanel.addAll(new KLabel("Level:", KFontFactory.createPlainFont(15)), levelIndicator);

        nameLabel = new KLabel(Student.requiredNameForFormat(), KFontFactory.createBoldFont(20));
        nameLabel.setToolTipText(Student.getLevelNumber()+" Level "+Student.getMajor()+" Major");

        final KLabel programLabel = new KLabel(Student.getProgram(), KFontFactory.createPlainFont(17));

        toPortalButton = KButton.getIconifiedButton("goArrow.png",25,25);
        toPortalButton.setText("Go Portal");
        toPortalButton.setMaximumSize(new Dimension(145, 35));
        toPortalButton.setFont(KFontFactory.createBoldItalic(15));
        toPortalButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        toPortalButton.setToolTipText("Visit your portal on a browser");
        toPortalButton.addActionListener(actionEvent -> Portal.userRequestsOpenPortal(toPortalButton));

        final KPanel midPart = new KPanel(300, 200);
        midPart.setLayout(new GridLayout(7, 1, 5, 0));
        midPart.addAll(levelPanel, Box.createRigidArea(new Dimension(100, 10)),
                KPanel.wantDirectAddition(nameLabel), KPanel.wantDirectAddition(programLabel),
                Box.createRigidArea(new Dimension(100, 10)));
        final KPanel horizontalWrapper = new KPanel();
        horizontalWrapper.setLayout(new BoxLayout(horizontalWrapper, BoxLayout.X_AXIS));
        horizontalWrapper.addAll(new KPanel(), toPortalButton, new KPanel());
        midPart.add(horizontalWrapper);//Notice how the last space is automatically left blank.
        // Besides, the height and the spaces do not seem to matter

        final KButton aUtgButton = new KButton("About UTG");
        aUtgButton.undress();
        aUtgButton.setStyle(KFontFactory.createBoldFont(14), Color.BLUE);
        aUtgButton.setToolTipText("Learn more about UTG");
        aUtgButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        aUtgButton.addActionListener(e -> new Thread(() -> {
            aUtgButton.setEnabled(false);
            try {
                Desktop.getDesktop().browse(URI.create(NewsGenerator.HOME_SITE));
            } catch (Exception e1) {
                App.signalError(e1);
            } finally {
                aUtgButton.setEnabled(true);
            }
        }).start());

        stateIndicator = new KLabel();
        effectRegistrationState(false);//If you rather bring the registered courses also during build, then remove this from here

        final KPanel statePanel = new KPanel();
        statePanel.addAll(new KLabel("Status:", KFontFactory.createPlainFont(15)), stateIndicator);

        final KPanel upperDetails = new KPanel(new BorderLayout());
        upperDetails.add(statePanel, BorderLayout.WEST);
        upperDetails.add(aUtgButton, BorderLayout.EAST);

        final KLabel labelIcon = KLabel.wantIconLabel("UTGLogo.gif", 125, 85);

        final KLabel schoolLabel = new KLabel("School of "+Student.getSchool(), KFontFactory.createBoldFont(17));
        final KLabel divLabel = new KLabel("Department of "+Student.getDepartment(), KFontFactory.createBoldFont(17));
        if (Student.getSchool().contains("Unknown")) {
            schoolLabel.setText("Unknown School");
        }
        if (Student.getDepartment().contains("Unknown")) {
            divLabel.setText("Unknown Department");
        }

        semesterIndicator = new KLabel(Student.getSemester(), KFontFactory.createBoldFont(17));

        final KPanel moreDetails = new KPanel();
        moreDetails.setLayout(new BoxLayout(moreDetails, BoxLayout.Y_AXIS));
        moreDetails.addAll(schoolLabel,divLabel, semesterIndicator);

        final KPanel detailsPart = new KPanel(375, 200);
        detailsPart.setLayout(new BorderLayout());
        detailsPart.add(upperDetails, BorderLayout.NORTH);
        detailsPart.add(labelIcon, BorderLayout.CENTER);
        detailsPart.add(moreDetails, BorderLayout.SOUTH);

        final KButton toHome = new KButton("HOME");
        toHome.setPreferredSize(new Dimension(225, toHome.getPreferredSize().height));
        toHome.setFont(KFontFactory.createBoldFont(16));
        toHome.addActionListener(e -> cardBoard.show(bodyLayer,"Home"));

        final KButton toTasks = new KButton("MY TASKS+");
        toTasks.setPreferredSize(new Dimension(225, toTasks.getPreferredSize().height));
        toTasks.setFont(toHome.getFont());
        toTasks.addActionListener(e -> cardBoard.show(bodyLayer,"Tasks"));

        final KButton toNews = new KButton("NEWS");
        toNews.setPreferredSize(new Dimension(225, toNews.getPreferredSize().height));
        toNews.setFont(toHome.getFont());
        toNews.addActionListener(e -> cardBoard.show(bodyLayer,"News"));

        notificationButton = new KButton("NOTIFICATIONS"){
            @Override
            public void setToolTipText(String text) {
                super.setToolTipText(text);
                super.setForeground(text == null ? null : Color.RED);
                super.setCursor(text == null ? null : Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
        };
        notificationButton.setPreferredSize(new Dimension(225, notificationButton.getPreferredSize().height));
        notificationButton.setFont(toHome.getFont());
        notificationButton.addActionListener(e -> cardBoard.show(bodyLayer,"Notifications"));

        final KPanel bigButtonsPanel = new KPanel(1_000,30);
        bigButtonsPanel.addAll(toHome, toTasks, toNews, notificationButton);

        final KPanel thoraxPanel = new KPanel(1_000,230);
        thoraxPanel.setLayout(new BorderLayout());
        thoraxPanel.add(imagePanel, BorderLayout.WEST);
        thoraxPanel.add(midPart, BorderLayout.CENTER);
        thoraxPanel.add(detailsPart, BorderLayout.EAST);
        thoraxPanel.add(bigButtonsPanel, BorderLayout.SOUTH);

        boardContent.add(thoraxPanel, BorderLayout.NORTH);
    }

    private void setUpBody(){
        cardBoard = new CardLayout();

        bodyLayer = new KPanel(cardBoard);
        bodyLayer.setPreferredSize(new Dimension(1_000, 450));

        cardBoard.addLayoutComponent(bodyLayer.add(this.generateHomePage()),"Home");
        cardBoard.addLayoutComponent(bodyLayer.add(this.generateTaskPage()),"Tasks");
        cardBoard.addLayoutComponent(bodyLayer.add(this.generateNewsPage()),"News");
        cardBoard.addLayoutComponent(bodyLayer.add(this.generateNotificationPage()),"Notifications");

        boardContent.add(new KScrollPane(bodyLayer), BorderLayout.CENTER);
    }

    private void attachUniversalKeys(){
        final KButton comeHomeButton = new KButton("Come Home");
        comeHomeButton.setFocusable(true);
        comeHomeButton.addActionListener(e -> cardBoard.show(bodyLayer,"Home"));
        comeHomeButton.setMnemonic(KeyEvent.VK_H);
        boardRoot.add(comeHomeButton);
    }

    @Override
    public void setVisible(boolean b) {
        if (b) {
            super.setVisible(true);
            if (Dashboard.isFirst()) {
                new FirstLaunch().setVisible(true);
            }
            new Thread(() -> {
                for (Runnable runnable : postProcesses) {
                    runnable.run();
                }
                Student.mayReportIncoming();
            }).start();
        } else {
            collapse();
        }
    }

    /**
     * Calling collapse() means shutdown is requested by the user and this is normal.
     */
    private void collapse(){
        dispose();
        Runtime.getRuntime().exit(0);
    }

    private void completeBuild(){
        Runtime.getRuntime().addShutdownHook(shutDownThread);
        if (Dashboard.isFirst()) {//Is there anything to remember?
            postProcesses.add(SettingsUI::loadDefaults);
        } else {
            SettingsUI.rememberPreferences();
        }
        postProcesses.add(()->{
            RunningCoursesGenerator.uploadInitials();
            ModulesHandler.uploadModules();
        });
    }

    private JComponent generateHomePage(){
        final KPanel runPanel = provideJumperPanel("This Semester","current.png",200,170);
        runPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                runningCoursesGenerator.answerActivity();
            }
        });

        final KPanel completedPanel = provideJumperPanel("Module Collection","collection.png",200,170);
        completedPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                modulesGenerator.answerActivity();
            }
        });

        final KPanel settingPanel = provideJumperPanel("Privacy & Settings","personalization.png",200,170);
        settingPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                settingsUI.answerActivity();
            }
        });

        final KPanel transcriptPanel = provideJumperPanel("My Transcript","transcript.png",190,155);
        transcriptPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                transcriptGenerator.answerActivity();
            }
        });

        final KPanel analysisPanel = provideJumperPanel("Analysis","analysis.png",200,190);
        analysisPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                analysisGenerator.answerActivity();
            }
        });

        final KPanel helpPanel = provideJumperPanel("FAQs & Help","help.png",200,170);
        helpPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                faqsGenerator.answerActivity();
            }
        });

        final KPanel aboutPanel = provideJumperPanel("About","about.png",200,170);
        aboutPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                myDashboard.setVisible(true);
            }
        });

        final KPanel homePage = new KPanel(new GridLayout(2, 4, 30, 15));
        homePage.addAll(runPanel, completedPanel, settingPanel, transcriptPanel, analysisPanel, helpPanel, aboutPanel);
        return homePage;
    }

    private JComponent generateTaskPage(){
        return new TasksGenerator().presentedContainer();
    }

    private JComponent generateNewsPage(){
        final KPanel palace = new KPanel();
        palace.setLayout(new BoxLayout(palace, BoxLayout.Y_AXIS));
        palace.add(Box.createVerticalStrut(10));

        final NewsGenerator newsGenerator = new NewsGenerator();

        final KButton refreshButton = new KButton("Refresh News");
        refreshButton.setFont(KFontFactory.createPlainFont(15));
        refreshButton.setToolTipText("Update news feeds (Alt+R)");
        refreshButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refreshButton.setMnemonic(KeyEvent.VK_R);
        refreshButton.addActionListener(e -> new Thread(() -> {
            refreshButton.setEnabled(false);
            newsGenerator.packAllIn(palace, true);
            refreshButton.setEnabled(true);
        }).start());
        if (Dashboard.isFirst()) {
            postProcesses.add(() -> newsGenerator.packAllIn(palace, false));
        } else {
            EventQueue.invokeLater(()-> newsGenerator.pushNewsIfAny(palace));
        }

        final KLabel hintLabel = new KLabel("News Feeds", KFontFactory.bodyHeaderFont());

        final KPanel northernPanel = new KPanel(new BorderLayout());
        northernPanel.add(hintLabel, BorderLayout.WEST);
        northernPanel.add(KPanel.wantDirectAddition(new FlowLayout(FlowLayout.RIGHT), null, refreshButton), BorderLayout.EAST);

        final KScrollPane newsPlaceScrollPane = new KScrollPane(palace, false);

        final KPanel pageItself = new KPanel(new BorderLayout());
        pageItself.add(northernPanel,BorderLayout.NORTH);
        pageItself.add(newsPlaceScrollPane,BorderLayout.CENTER);

        return pageItself;
    }

    private JComponent generateNotificationPage(){
        return new NotificationGenerator().presentedContainer();
    }

    //Provides the look for the panels in the homepage. Mouse click will be added later.
    private static KPanel provideJumperPanel(String label, String iPathName, int iWidth, int iHeight){
        final KLabel jumpLabel = new KLabel(label, KFontFactory.createPlainFont(16));

        final KPanel jumperPanel = new KPanel(new BorderLayout());
        jumperPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jumperPanel.add(KPanel.wantDirectAddition(null, new Dimension(225,30), jumpLabel), BorderLayout.NORTH);
        jumperPanel.add(KLabel.wantIconLabel(iPathName, iWidth,iHeight), BorderLayout.CENTER);
        jumperPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                jumpLabel.setFont(KFontFactory.createBoldFont(17));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                jumpLabel.setFont(KFontFactory.createPlainFont(16));
            }
        });
        return jumperPanel;
    }

    /**
     * To-do things that should be done at the beginning of every day.
     */
    private void anotherDay(){
        Mailer.REVIEWS_COUNT_TODAY = 0;
    }

}
