package main;

import proto.*;
import utg.Dashboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * @author Muhammed W. Drammeh <wakadrammeh@gmail.com>
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
    private static KPanel imagePanel;//left-most top, lies the image
    private static KLabel stateIndicator;//this is locally triggered
    private static KLabel levelIndicator;//very dormant in changes anyway
    private static KLabel semesterIndicator;
    private static KLabel nameLabel;
    private static KButton toPortalButton;
    private static KButton notificationButton;
    private static Board appInstance;
    private static boolean isReady;
    public static final ArrayList<Runnable> postProcesses = new ArrayList<Runnable>() {
        @Override
        public boolean add(Runnable runnable) {
            if (isReady()) {
                App.silenceException("Dashboard is done building; task may never execute.");
            }
            return super.add(runnable);
        }
    };
    public static final Thread shutDownThread = new Thread(Serializer::mountUserData);

//    Collaborators declaration. The order in which these will be initialized does matter!
    private RunningCourseActivity runningCourseActivity;
    private ModuleActivity moduleActivity;
    private SettingsUI settingsUI;
    private TranscriptActivity transcriptActivity;
    private Analysis analysisGenerator;
    private Tips faqsGenerator;
    private About myDashboard;
    private TaskActivity taskActivity;
    private News newsPresent;
    private NotificationActivity alertActivity;


    public Board() {
        super("UTG-Student Dashboard");
        appInstance = Board.this;
        boardRoot = getRootPane();
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (!Settings.confirmExit || App.showYesNoCancelDialog("Confirm Exit",
                        "Do you really mean to quit the Dashboard?")) {
                    setVisible(false);
                }
            }
        });

        Settings.allLooksInfo = UIManager.getInstalledLookAndFeels();
        if (Dashboard.isFirst()) {
            Serializer.unMountUserData();
        } else {
            Settings.deSerialize();
            for (UIManager.LookAndFeelInfo lookAndFeelInfo : Settings.allLooksInfo) {
                if (lookAndFeelInfo.getName().equals(Settings.lookName)) {
                    try {
                        UIManager.setLookAndFeel(lookAndFeelInfo.getClassName());
                    } catch (Exception e) {
                        postProcesses.add(()-> App.signalError(e));
                    }
                    break;
                }
            }
        }

        boardContent = new KPanel();
        boardContent.setLayout(new BoxLayout(boardContent, BoxLayout.Y_AXIS));
        setContentPane(boardContent);
        setUpThorax();
        setUpBody();

        if (!(Dashboard.isFirst() || Student.isTrial())) {
            Portal.deSerialize();
        }
        runningCourseActivity = new RunningCourseActivity();
        moduleActivity = new ModuleActivity();
        settingsUI = new SettingsUI();
        transcriptActivity = new TranscriptActivity();
        analysisGenerator = new Analysis();
        faqsGenerator = new Tips();
        myDashboard = new About();
//        outlined / big buttons
        taskActivity = new TaskActivity();
        newsPresent = new News();
        alertActivity = new NotificationActivity();

        pack();
        setMinimumSize(getPreferredSize());
        setLocationRelativeTo(null);
        attachUniversalKeys();
        completeBuild();
    }

    /**
     * This call sets up the thorax-region of the Dashboard.
     * Total height to be covered is 230, and the 30 is for the bigButtons to lie beneath.
     * This is horizontally partitioned into 3 sections with widths as follows:
     * imagePart 300
     * detailsPart 375 and
     * midPart unset
     */
    private void setUpThorax() {
        final KMenuItem resetOption = new KMenuItem("Reset", e -> Student.fireIconReset());
        final KMenuItem shooterOption = new KMenuItem("Set Default", e -> Student.fireIconDefaultSet());

        final JPopupMenu imageOptionsPop = new JPopupMenu();
        imageOptionsPop.add(new KMenuItem("Change", e-> Student.startSettingImage()));
        imageOptionsPop.add(resetOption);
        imageOptionsPop.add(shooterOption);

        imagePanel = new KPanel(new BorderLayout(), new Dimension(275,200));
        imagePanel.add(new KLabel(Student.getIcon()));
        imagePanel.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {//For unix-based systems
                    resetOption.setEnabled(!Student.isDefaultIconSet());
                    shooterOption.setEnabled(!Student.isShooterIconSet());
                    imageOptionsPop.show(imagePanel, e.getX(), e.getY());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {//Windows systems
                mousePressed(e);
            }
        });

        levelIndicator = new KLabel(Student.isTrial() ? "" : Student.getLevel(), KFontFactory.createPlainFont(15), Color.BLUE);
        final KPanel levelPanel = new KPanel(new FlowLayout(FlowLayout.LEFT), new Dimension(325,25));
        if (!Student.isTrial()) {
            levelPanel.addAll(new KLabel("Level:", KFontFactory.createPlainFont(15)), levelIndicator);
        }

        nameLabel = new KLabel(Student.requiredNameForFormat().toUpperCase(), KFontFactory.createBoldFont(20));
        if (!Student.isTrial()) {
            final String nameTip = Student.getLevelNumber()+" Level "+Student.getMajor()+" Major";
            nameLabel.setToolTipText(nameTip);
        }

        final KLabel programLabel = new KLabel(Student.isTrial() ? "" : Student.getProgram(),
                KFontFactory.createPlainFont(17));

        toPortalButton = KButton.getIconifiedButton("go-arrow.png",25,25);
        toPortalButton.setText("Go Portal");
        toPortalButton.setMaximumSize(new Dimension(145, 35));
        toPortalButton.setFont(KFontFactory.createBoldItalic(15));
        toPortalButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        toPortalButton.setToolTipText(String.format("Visit %s Portal", Student.isTrial() ? "UTG" : "your"));
        toPortalButton.addActionListener(actionEvent-> new Thread(()-> Portal.openPortal(toPortalButton)).start());

        final KPanel midPart = new KPanel(300, 200);
        midPart.setLayout(new GridLayout(7, 1, 5, 0));
        midPart.addAll(levelPanel, Box.createRigidArea(new Dimension(100, 10)),
                new KPanel(nameLabel), new KPanel(programLabel),
                Box.createRigidArea(new Dimension(100, 10)));
        final KPanel horizontalWrapper = new KPanel();
        horizontalWrapper.setLayout(new BoxLayout(horizontalWrapper, BoxLayout.X_AXIS));
        horizontalWrapper.addAll(new KPanel(), toPortalButton, new KPanel());
        midPart.add(horizontalWrapper);//notice how the last space is automatically left blank.
        //besides, the height and the spaces do not seem to count

        final KButton aboutUTGButton = new KButton("About UTG");
        aboutUTGButton.setStyle(KFontFactory.createBoldFont(15), Color.BLUE);
        aboutUTGButton.undress();
        aboutUTGButton.underline(false);
        aboutUTGButton.setPreferredSize(new Dimension(125, 30));
        aboutUTGButton.setToolTipText("Learn more about UTG");
        aboutUTGButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        aboutUTGButton.addActionListener(e-> new Thread(()-> {
            aboutUTGButton.setEnabled(false);
            try {
                Internet.visit(News.HOME_SITE);
            } catch (Exception ex) {
                App.signalError(ex);
            } finally {
                aboutUTGButton.setEnabled(true);
            }
        }).start());

        stateIndicator = new KLabel(Student.isTrial() ? "" : Student.getStatus(), KFontFactory.createBoldFont(15));
        stateIndicator.setForeground(Color.GRAY);

        final KPanel statePanel = new KPanel();
        if (!Student.isTrial()) {
            statePanel.addAll(new KLabel("Status:", KFontFactory.createPlainFont(15)), stateIndicator);
        }

        final KPanel upperDetails = new KPanel(new BorderLayout());
        upperDetails.add(statePanel, BorderLayout.WEST);
        upperDetails.add(aboutUTGButton, BorderLayout.EAST);

        final KLabel utgIcon = KLabel.createIcon("UTGLogo.gif", 125, 85);

        final KLabel schoolLabel = createLabelFor("School of", Student.getSchool());
        final KLabel divLabel = createLabelFor("Department of", Student.getDivision());
        semesterIndicator = new KLabel(Student.isTrial() ? "" : Student.getSemester(),
                KFontFactory.createBoldFont(17));

        final KPanel moreDetails = new KPanel();
        moreDetails.setLayout(new BoxLayout(moreDetails, BoxLayout.Y_AXIS));
        moreDetails.addAll(schoolLabel, divLabel, semesterIndicator);

        final KPanel detailsPart = new KPanel(375, 200);
        detailsPart.setLayout(new BorderLayout());
        detailsPart.add(upperDetails, BorderLayout.NORTH);
        detailsPart.add(utgIcon, BorderLayout.CENTER);
        detailsPart.add(moreDetails, BorderLayout.SOUTH);

        final int outlinesWidth = 215;
        final Font outlinesFont = KFontFactory.createBoldFont(15);

        final KButton toHome = new KButton("HOME");
        toHome.setFont(outlinesFont);
        toHome.setPreferredSize(new Dimension(outlinesWidth, toHome.getPreferredSize().height));
        toHome.addActionListener(e-> showCard("Home"));

        final KButton toTasks = new KButton("MY TASKS+");
        toTasks.setFont(outlinesFont);
        toTasks.setPreferredSize(new Dimension(outlinesWidth, toTasks.getPreferredSize().height));
        toTasks.addActionListener(e-> taskActivity.answerActivity());

        final KButton toNews = new KButton("NEWS");
        toNews.setFont(outlinesFont);
        toNews.setPreferredSize(new Dimension(outlinesWidth, toNews.getPreferredSize().height));
        toNews.addActionListener(e-> newsPresent.answerActivity());

        notificationButton = new KButton("NOTIFICATIONS"){
            @Override
            public void setToolTipText(String text) {
                super.setToolTipText(text);
                if (text == null) {
                    super.setForeground(null);
                    super.setCursor(null);
                } else {
                    super.setForeground(Color.RED);
                    super.setCursor(MComponent.HAND_CURSOR);
                    //Todo: signal a desktop notification
                }
            }
        };
        notificationButton.setFont(outlinesFont);
        notificationButton.setPreferredSize(new Dimension(outlinesWidth, notificationButton.getPreferredSize().height));
        notificationButton.addActionListener(e-> alertActivity.answerActivity());

        final KPanel bigButtonsPanel = new KPanel(new FlowLayout(FlowLayout.CENTER, 10, 5), new Dimension(1_000, 30));
        bigButtonsPanel.addAll(toHome, toTasks, toNews, notificationButton);

        final KPanel thoraxPanel = new KPanel(1_000,230);
        thoraxPanel.setLayout(new BorderLayout());
        thoraxPanel.add(imagePanel, BorderLayout.WEST);
        thoraxPanel.add(midPart, BorderLayout.CENTER);
        thoraxPanel.add(detailsPart, BorderLayout.EAST);
        thoraxPanel.add(bigButtonsPanel, BorderLayout.SOUTH);
        boardContent.add(thoraxPanel, BorderLayout.NORTH);
    }

    private KLabel createLabelFor(String h, String t){
        final String text = Globals.hasNoText(t) ? "" : t.contains("Unknown") ? t : h+" "+t;
        return new KLabel(text, KFontFactory.createBoldFont(17));
    }

    private void setUpBody() {
        cardBoard = new CardLayout();
        bodyLayer = new KPanel(cardBoard);
        bodyLayer.setPreferredSize(new Dimension(1_000, 450));
        cardBoard.addLayoutComponent(bodyLayer.add(generateHomePage()),"Home");
        boardContent.add(new KScrollPane(bodyLayer), BorderLayout.CENTER);
    }

    private void attachUniversalKeys(){
        final KButton comeHomeButton = new KButton("Home Page");
        comeHomeButton.setFocusable(true);
        comeHomeButton.addActionListener(e-> cardBoard.show(bodyLayer, "Home"));
        comeHomeButton.setMnemonic(KeyEvent.VK_H);
        boardRoot.add(comeHomeButton);
        boardRoot.setDefaultButton(comeHomeButton);
    }

    @Override
    public void setVisible(boolean b) {
        if (b) {
            super.setVisible(true);
            if (Dashboard.isFirst() && !Student.isTrial()) {
                SwingUtilities.invokeLater(()-> new FirstLaunch().setVisible(true));
            }
        } else {
            collapse();
        }
    }

    private void collapse(){
        System.exit(0);
    }

    private void completeBuild() {
        isReady = true;
        Runtime.getRuntime().addShutdownHook(shutDownThread);
        if (Dashboard.isFirst()) {
            SettingsUI.loadDefaults();
        }
        for (Runnable r : postProcesses) {
            r.run();
        }
    }

    private JComponent generateHomePage(){
        final KPanel runPanel = provideJumperPanel("This Semester","current.png",200,170);
        runPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                runningCourseActivity.answerActivity();
            }
        });

        final KPanel completedPanel = provideJumperPanel("Module Collection","collection.png",200,170);
        completedPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                moduleActivity.answerActivity();
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
                transcriptActivity.answerActivity();
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

//    Provides the look for the panels in the homepage. Mouse click will be added later on.
    private static KPanel provideJumperPanel(String labelText, String iconName, int iWidth, int iHeight){
        final KLabel jumpLabel = new KLabel(labelText, KFontFactory.createPlainFont(16));

        final KPanel jumperPanel = new KPanel(new BorderLayout());
        jumperPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jumperPanel.add(new KPanel(new Dimension(225,30), jumpLabel), BorderLayout.NORTH);
        jumperPanel.add(KLabel.createIcon(iconName, iWidth, iHeight), BorderLayout.CENTER);
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

    public static void addCard(Component card, String name){
        cardBoard.addLayoutComponent(bodyLayer.add(card), name);
    }

    public static void showCard(String cardName){
        cardBoard.show(bodyLayer, cardName);
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

    public static boolean isReady(){
        return isReady;
    }

    public static void setReady(boolean ready){
        isReady = ready;
    }

//    As used by NotificationActivity to adjust the toolTip
    public static KButton getNotificationButton() {
        return notificationButton;
    }

    public static KPanel getImagePanel() {
        return imagePanel;
    }

    public static void effectSemesterUpgrade() {
        final String semester = Student.getSemester();
        if (isReady()) {
            semesterIndicator.setText(semester);
            RunningCourseActivity.semesterBigLabel.setText(semester);
        } else {
            postProcesses.add(()-> {
                semesterIndicator.setText(semester);
                RunningCourseActivity.semesterBigLabel.setText(semester);
            });
        }
    }

    public static void effectLevelUpgrade() {
        if (isReady()) {
            levelIndicator.setText(Student.getLevel());
        } else {
            postProcesses.add(()-> levelIndicator.setText(Student.getLevel()));
        }
    }

    public static void effectNameFormatChanges(){
        final String requiredName = Student.requiredNameForFormat().toUpperCase();
        if (isReady()) {
            nameLabel.setText(requiredName);
        } else {
            postProcesses.add(()-> nameLabel.setText(requiredName));
        }
    }

    public static void effectStatusUpgrade(){
        if (isReady()) {
            stateIndicator.setText(Student.getStatus());
        } else {
            postProcesses.add(()-> stateIndicator.setText(Student.getStatus()));
        }
    }

//    already forged on a thread
    public static void online() {
        if (Portal.isAutoSynced() && !Student.isTrial()) {
            RunningCourseActivity.startMatching(false);
            ModuleHandler.startThoroughSync(false, null);
            NotificationActivity.updateNotices(false);
        }
    }

}
