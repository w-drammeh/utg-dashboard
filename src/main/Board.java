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
 * <h1>class Board</h1>
 * The ultimate class of UI. When using Dashboard, the user actually interacts with an instance of this
 * child of {@link KFrame}, a child of {@link JFrame}.
 *
 * <p><b>GUI</b>: I'll assume a height of about 20 - 30 pixels is consumed by the decoration of the platform.
 * <b>topLayer</b> shall cover 230 pixels of height; <b>bodyScroller</b> 450. Thus, the total dimension: 1000 x 680</p>
 * <p><i>By time, even the layer will not be preferred size set.</i></p>
 * <p><i>The container should remain private; dialogs use the root, and for the body - its getter method should be called once
 * as the representative in the destination class.</i></p>
 *
 * <p>Clear the fields before building Dashboard. And clearing must be followed by settings.</p>
 * <p>Dashboard builds with the student's details. At which state the coponents are ready, so add resources
 * before, or after made visible</p>
 */
public final class Board extends KFrame {
    public static final ArrayList<Runnable> postProcesses = new ArrayList<>();
    public static final Thread shutDownThread = new Thread(MyClass::mountUserData);
    /**
     * <p>This dimension let the bodyLayer to assume a size with which its parent bars will be hidden</p>
     * <p><i>By convention, the horizontal bar should always be hidden.</i></p>
     */
    public static final Dimension BODYSIZE_NOSCROLLBARS = new Dimension(975, 425);
    /**
     * <p>The layout which shifts between the four(4) main activities: 'home', 'news', 'tasks', and 'notifications',
     * as well as the assignments made by the activity answerers.</p>
     */
    private static final CardLayout cardBoard = new CardLayout();
    private static Board appInstance;
    /**
     * <p>The standard root for dialogs.</p>
     * <p><i>This is assigned to the rootPane</i></p>
     */
    private static JRootPane root;
    /**
     * <p>The container onto which the 2 layers are placed.</p>
     * <p><i>This is assigned to the contentPane</i></p>
     */
    private static KPanel container;
    private static KPanel imagePanel;//left-most top, lies the image
    private static KLabel stateIndicator;//this is locally triggered!
    private static KLabel levelIndicator;//very dormant in changes anyway
    private static KLabel semesterIndicator;
    /**
     * <p>It's the same panel in the scroll fixed as the 2nd layer of the frame. Can be used by classes outside.
     * Note that the bodyScroller spans for 1000 x 450 pixels.</p>
     * <p><b><i>Do not mess between the container and the bodyLayer! container=getContentPane, while bodyLayer='a KPanel'</i></b></p>
     */
    private static KPanel bodyLayer;
    private static KButton toNotifications;
    private static KButton toPortalButton;
    private static KLabel nameLabel;

    //Collaborators declaration. The order in which these classes are instantiated after one-another does matter!
    private RunningCoursesGenerator runningCoursesGenerator;
    private ModulesGenerator modulesGenerator;
    private SettingsUI settingsUI;
    private TranscriptGenerator transcriptGenerator;
    private AnalysisGenerator analysisGenerator;
    private HelpGenerator faqsGenerator;
    private MyDashboard myDashboard;


    public Board(){
        super("UTG-Student Dashboard");
        this.setDefaultCloseOperation(KFrame.DO_NOTHING_ON_CLOSE);
        container = new KPanel(new BorderLayout());
        this.setContentPane(container);
        root = this.getRootPane();
        appInstance = Board.this;
        SettingsCore.allLooksInfo = UIManager.getInstalledLookAndFeels();
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
        this.fixTopLayer();
        this.fixBodyLayer();

        runningCoursesGenerator = new RunningCoursesGenerator();
        modulesGenerator = new ModulesGenerator();
        settingsUI = new SettingsUI();
        transcriptGenerator = new TranscriptGenerator();
        analysisGenerator = new AnalysisGenerator();
        faqsGenerator = new HelpGenerator();
        myDashboard = new MyDashboard();

        final Timer dayTimer = new Timer(Globals.DAY_IN_MILLI, null);
        dayTimer.setInitialDelay(Globals.DAY_IN_MILLI - MDate.getTimeValue(new Date()));
        dayTimer.addActionListener(e-> anotherDay());

        this.pack();
        this.setMinimumSize(getPreferredSize());
        this.setLocationRelativeTo(null);
        this.attachUniversalKeys();
        this.completeBuild();
    }

    /**
     * <p>The standard Dashboard rootPane for dialogs, and, or the likes.</p>
     * <p><i>Returns an instance which was initialized to the rootPane</i></p>
     */
    public static JRootPane getRoot(){
        return root;
    }

    /**
     * <p>Provides the runtime instance of the Dashboard</p>
     */
    public static Board getAppInstance(){
        return appInstance;
    }

    public static boolean isAppReady(){
        return appInstance != null && appInstance.isShowing();
    }

    /**
     * <p>Use it as the body representative as a panel.</p>
     * <p><i>No! All body representatives join their activity to the card instead.</i></p>
     */
    public static KPanel getBody(){
        return bodyLayer;
    }

    public static void addCard(Component card, String name){
        cardBoard.addLayoutComponent(bodyLayer.add(card), name);
    }

    public static void showCard(String cardName){
        cardBoard.show(bodyLayer, cardName);
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

    public static void effectLevelUpgrade(String upgrade){
        if (levelIndicator != null) {
            levelIndicator.setText(upgrade);
        } else {
            postProcesses.add(()-> levelIndicator.setText(upgrade));
        }
    }

    public static void effectNameFormatChanges(){
        if (isAppReady()) {
            nameLabel.setText(Student.requiredNameForFormat());
        } else {
            postProcesses.add(()->{
                nameLabel.setText(Student.requiredNameForFormat());
            });
        }
    }

    /**
     * <p>As used by {@link NotificationGenerator} to set the toolTip</p>
     */
    public static KButton getNotificationButton(){
        return toNotifications;
    }

    /**
     * <p>The image container.</p>
     */
    public static KPanel getImagePanel(){
        return imagePanel;
    }

    /**
     * <p>This call fixes the layer just below the window-decoration.</p>
     *
     * <p><i>Total height to be covered is 230, and the 30 is for the bigButtons to lie beneath.</i>.
     * Notice 30 was left for the decoration! Which may not count munch - board is 'packed'.</p>
     * <p><i>All of the image, mid, and details part constitute about 200 of height, leaving 30 for the <b>mainButtons</b>.
     * </i>And their width is as follows: <b>imagePart = 300; detailsPart = 375; midPart is unset</b></p>
     */
    private void fixTopLayer(){
        final JMenuItem changeOption = new JMenuItem("Change");
        changeOption.setFont(KFontFactory.createPlainFont(15));
        changeOption.addActionListener(e -> {
            Student.startSettingImage();
        });

        final JMenuItem resetOption = new JMenuItem("Reset");
        resetOption.setFont(KFontFactory.createPlainFont(15));
        resetOption.addActionListener(e -> Student.fireIconReset());

        final JMenuItem defaultOption = new JMenuItem("Set Default");
        defaultOption.setFont(KFontFactory.createPlainFont(15));
        defaultOption.addActionListener(e -> Student.fireIconDefaultSet());

        final JPopupMenu imageOptionsPop = new JPopupMenu();
        imageOptionsPop.add(changeOption);
        imageOptionsPop.add(resetOption);
        imageOptionsPop.add(defaultOption);

        imagePanel = new KPanel(new BorderLayout(),new Dimension(275,200));
        imagePanel.setBorder(BorderFactory.createLineBorder(Color.BLUE,1,true));
        imagePanel.add(new KLabel(Student.getIcon()));
        imagePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {//As for linux systems
                    resetOption.setEnabled(!Student.isNoIconSet());
                    defaultOption.setEnabled(!Student.isDefaultIconSet());
                    imageOptionsPop.show(imagePanel,e.getX(),e.getY());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {//Windows systems
                mousePressed(e);
            }
        });

        levelIndicator = new KLabel(Student.getLevel(),KFontFactory.createPlainFont(15),Color.BLUE);
        final KPanel levelPanel = new KPanel(new FlowLayout(FlowLayout.LEFT),new Dimension(325,25));
        levelPanel.addAll(new KLabel("Level:",KFontFactory.createPlainFont(15)),levelIndicator);

        nameLabel = new KLabel(Student.requiredNameForFormat(),KFontFactory.createBoldFont(20));
        nameLabel.setToolTipText(Student.getLevelNumber()+" Level "+Student.getMajor()+" Major");

        final KLabel programLabel = new KLabel(Student.getProgram(),KFontFactory.createPlainFont(17));

        toPortalButton = KButton.getIconifiedButton("portal-arrow.png",25,25);
        toPortalButton.setText("Go Portal");
        toPortalButton.setMaximumSize(new Dimension(145, 35));
        toPortalButton.setFont(KFontFactory.createBoldItalic(15));
        toPortalButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        toPortalButton.setToolTipText("Visit your portal on a browser");
        toPortalButton.addActionListener(actionEvent -> Portal.userRequestsOpenPortal(toPortalButton));

        final KPanel midPart = new KPanel(300, 200);
        midPart.setLayout(new GridLayout(7, 1, 5, 0));
        midPart.addAll(levelPanel, ComponentAssistant.provideBlankSpace(100, 10),
                KPanel.wantDirectAddition(nameLabel), KPanel.wantDirectAddition(programLabel), ComponentAssistant.provideBlankSpace(100, 10));
        final KPanel horizontalWrapper = new KPanel();
        horizontalWrapper.setLayout(new BoxLayout(horizontalWrapper, BoxLayout.X_AXIS));
        horizontalWrapper.addAll(new KPanel(), toPortalButton, new KPanel());
        midPart.add(horizontalWrapper);//notice how the last space is automatically left blank. besides, the height the the spaces does not seems to matter

        final KButton aUtgButton = new KButton("About UTG...");
        aUtgButton.undress();
        aUtgButton.setStyle(KFontFactory.createBoldFont(14), Color.BLUE);
        aUtgButton.setToolTipText("Learn more about the University of The Gambia");
        aUtgButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        aUtgButton.addActionListener(e -> {
            new Thread(() -> {
                aUtgButton.setEnabled(false);
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(URI.create(NewsGenerator.HOME_SITE));
                    } catch (Exception e1) {
                        App.signalError(e1);
                    }
                } else {
                    App.signalError("Unsupported Desktop","Sorry, launching of default-browser by Swing is not supported by your desktop.");
                }

                try {
                    Thread.sleep(10_000);
                } catch (InterruptedException ie) {
                    App.silenceException(ie);
                } finally{
                    aUtgButton.setEnabled(true);
                }
            }).start();
        });

        stateIndicator = new KLabel();
        effectRegistrationState(false);//if you rather bring the registered courses also during build, then remove this from here

        final KPanel statePanel = new KPanel();
        statePanel.addAll(new KLabel("Status:",KFontFactory.createPlainFont(15)),stateIndicator);

        final KPanel upperDetails = new KPanel(new BorderLayout());
        upperDetails.add(statePanel,BorderLayout.WEST);
        upperDetails.add(aUtgButton,BorderLayout.EAST);

        final KLabel labelIcon = KLabel.wantIconLabel("Logo_of_UTG.gif",125,85);

        final KLabel schoolLabel = new KLabel("School of "+Student.getSchool(),KFontFactory.createBoldFont(17));
        final KLabel divLabel = new KLabel("Department of "+Student.getDepartment(),KFontFactory.createBoldFont(17));
        if (Student.getSchool().contains("Unknown")) {
            schoolLabel.setText("Unknown School");
        }
        if (Student.getDepartment().contains("Unknown")) {
            divLabel.setText("Unknown Department");
        }

        semesterIndicator = new KLabel(Student.getSemester(),KFontFactory.createBoldFont(17));

        final KPanel moreDetails = new KPanel();
        moreDetails.setLayout(new BoxLayout(moreDetails,BoxLayout.Y_AXIS));
        moreDetails.addAll(schoolLabel,divLabel, semesterIndicator);

        final KPanel detailsPart = new KPanel(375, 200);
        detailsPart.setLayout(new BorderLayout());
        detailsPart.add(upperDetails,BorderLayout.NORTH);
        detailsPart.add(labelIcon,BorderLayout.CENTER);
        detailsPart.add(moreDetails,BorderLayout.SOUTH);

        final KButton toHome = new KButton("HOME");
        toHome.setPreferredSize(new Dimension(225, toHome.getPreferredSize().height));
        toHome.setFont(KFontFactory.createBoldFont(16));
        toHome.addActionListener(e -> {
            bodyLayer.setPreferredSize(BODYSIZE_NOSCROLLBARS);
            cardBoard.show(bodyLayer,"Home");
        });

        final KButton toTasks = new KButton("MY TASKS+");
        toTasks.setPreferredSize(new Dimension(225, toTasks.getPreferredSize().height));
        toTasks.setFont(toHome.getFont());
        toTasks.addActionListener(e -> {
            bodyLayer.setPreferredSize(BODYSIZE_NOSCROLLBARS);
            cardBoard.show(bodyLayer,"Tasks");
        });

        final KButton toNews = new KButton("NEWS");
        toNews.setPreferredSize(new Dimension(225, toNews.getPreferredSize().height));
        toNews.setFont(toHome.getFont());
        toNews.addActionListener(e -> {
            bodyLayer.setPreferredSize(BODYSIZE_NOSCROLLBARS);
            cardBoard.show(bodyLayer,"News");
        });

        toNotifications = new KButton("NOTIFICATIONS"){
            @Override
            public void setToolTipText(String text) {
                super.setToolTipText(text);
                super.setForeground(text == null ? null : Color.RED);
                super.setCursor(text == null ? null : Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
        };
        toNotifications.setPreferredSize(new Dimension(225, toNotifications.getPreferredSize().height));
        toNotifications.setFont(toHome.getFont());
        toNotifications.addActionListener(e -> {
            bodyLayer.setPreferredSize(BODYSIZE_NOSCROLLBARS);
            cardBoard.show(bodyLayer,"Notifications");
        });

        final KPanel bigButtonsPanel = new KPanel(1_000,30);
        bigButtonsPanel.addAll(toHome,toTasks,toNews,toNotifications);

        final KPanel topLayer = new KPanel(1_000,230);
        topLayer.setLayout(new BorderLayout());
        topLayer.add(imagePanel, BorderLayout.WEST);
        topLayer.add(midPart, BorderLayout.CENTER);
        topLayer.add(detailsPart, BorderLayout.EAST);
        topLayer.add(bigButtonsPanel, BorderLayout.SOUTH);

        container.add(topLayer, BorderLayout.NORTH);
    }

    /**
     * <p>The 2nd-layer is actually a scroll-pane, spanning for about 450 pixels of height.</p>
     */
    private void fixBodyLayer(){
        bodyLayer = new KPanel(cardBoard, BODYSIZE_NOSCROLLBARS);
        /*
         *Lets get the hell out of this! Since the 'add' function returns the argument, then
         */
        cardBoard.addLayoutComponent(bodyLayer.add(generateHomePage()),"Home");
        cardBoard.addLayoutComponent(bodyLayer.add(generateTaskPage()),"Tasks");
        cardBoard.addLayoutComponent(bodyLayer.add(generateNewsPage()),"News");
        cardBoard.addLayoutComponent(bodyLayer.add(generateNotificationPage()),"Notifications");

        final KScrollPane bodyScroller = new KScrollPane(bodyLayer);
        bodyScroller.setPreferredSize(new Dimension(1_000,450));
        container.add(bodyScroller, BorderLayout.CENTER);
    }

    private void attachUniversalKeys(){
        final KButton comeHomeButton = new KButton("Come Home", true);
        comeHomeButton.addActionListener(e -> cardBoard.show(bodyLayer,"Home"));
        comeHomeButton.setMnemonic(KeyEvent.VK_H);

        root.add(comeHomeButton);
    }

    @Override
    public void setVisible(boolean b) {
        if (b) {
            super.setVisible(true);
            if (Dashboard.isFirst()) {
                SwingUtilities.invokeLater(() -> new FirstLaunch().setVisible(true));
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
        MyClass.mountUserData();
        Runtime.getRuntime().removeShutdownHook(shutDownThread);
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

        final KPanel completedPanel = provideJumperPanel("Module Collection","all-registered.png",200,170);
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

        final KPanel pageItself = new KPanel(975,500);
        pageItself.setLayout(new GridLayout(2,4,30,15));
        pageItself.addAll(runPanel,completedPanel,settingPanel,transcriptPanel,analysisPanel,helpPanel,aboutPanel);

        return pageItself;
    }

    private JComponent generateTaskPage(){
        return new TasksGenerator().presentedContainer();
    }

    private JComponent generateNewsPage(){
        final KPanel palace = new KPanel();
        palace.setLayout(new BoxLayout(palace,BoxLayout.Y_AXIS));
        palace.add(ComponentAssistant.provideBlankSpace(500,10));

        final NewsGenerator newsGenerator = new NewsGenerator();

        final KButton refreshButton = new KButton("Refresh News");
        refreshButton.setFont(KFontFactory.createPlainFont(15));
        refreshButton.setToolTipText("Update news feeds (Alt+R)");
        refreshButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refreshButton.setMnemonic(KeyEvent.VK_R);
        refreshButton.addActionListener(e -> new Thread(() -> {
            refreshButton.setEnabled(false);
            newsGenerator.packAllIn(palace,true);
            refreshButton.setEnabled(true);
        }).start());
        EventQueue.invokeLater(()->{
            newsGenerator.pushNewsIfAny(palace);
            postProcesses.add(()-> newsGenerator.packAllIn(palace,false));
        });

        final KLabel hintLabel = new KLabel("News Feeds",KFontFactory.bodyHeaderFont());

        final KPanel northernPanel = new KPanel(new BorderLayout());
        northernPanel.add(hintLabel,BorderLayout.WEST);
        northernPanel.add(KPanel.wantDirectAddition(new FlowLayout(FlowLayout.RIGHT),null,refreshButton),BorderLayout.EAST);

        final KScrollPane newsPlaceScrollPane = new KScrollPane(palace, false);

        final KPanel pageItself = new KPanel(new BorderLayout());
        pageItself.add(northernPanel,BorderLayout.NORTH);
        pageItself.add(newsPlaceScrollPane,BorderLayout.CENTER);

        return pageItself;
    }

    private JComponent generateNotificationPage(){
        return new NotificationGenerator().presentedContainer();
    }

    //Provides the look for the panels in the homepage. Mouse click will be added later
    private KPanel provideJumperPanel(String label, String iPathName, int iWidth, int iHeight){
        final KLabel jumpLabel = new KLabel(label, KFontFactory.createPlainFont(16));

        final KPanel jumper = new KPanel(new BorderLayout());
        jumper.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jumper.add(KPanel.wantDirectAddition(null, new Dimension(225,30), jumpLabel), BorderLayout.NORTH);
        jumper.add(KLabel.wantIconLabel(iPathName, iWidth,iHeight), BorderLayout.CENTER);
        jumper.addMouseListener(new MouseAdapter() {
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

        return jumper;
    }

    /**
     * <p>To-do things that should be done at the beginning of every day</p>
     */
    private void anotherDay(){
        Mailer.REVIEWS_COUNT_TODAY = 0;
    }

}
