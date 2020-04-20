package main;

import customs.*;
import org.openqa.selenium.firefox.FirefoxDriver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * <h1>class NotificationGenerator</h1>
 */
public class NotificationGenerator {
    private static int unreadCount;//with a technical mechanism of incrementing and decrementing it
    private static KPanel dashboardPanel, portalPanel;
    private static KButton clearingButton;
    private static FirefoxDriver portalNoticeDriver;
    private static KButton portalRefresher;
    //and its related-rubbish comps...
    private static KLabel admissionLabel, registrationLabel;
    private final KLabel hint = KLabel.getPredefinedLabel("Notifications ", SwingConstants.LEFT);
    private final CardLayout card = new CardLayout();
    private KPanel notificationPanel;


    public NotificationGenerator(){
        notificationPanel = new KPanel(card);

        hint.setFont(KFontFactory.bodyHeaderFont());

        configureDashAlerts();
        configurePortalAlerts();
    }

    public static synchronized void trySettingNoticeDriver(){
        if (portalNoticeDriver != null) {
            return;
        }

        portalNoticeDriver = DriversPack.forgeNew(true);
    }

    public static void awareLookShift(){
        SwingUtilities.updateComponentTreeUI(dashboardPanel);
        SwingUtilities.updateComponentTreeUI(portalPanel);
        SwingUtilities.updateComponentTreeUI(clearingButton);
        SwingUtilities.updateComponentTreeUI(portalRefresher);
    }

    /**
     * <p>As alerts are passed here, they are forwarded to their respective panel.
     * <p><i>Do not call this method directly! Call Notification.create(#) instead.</i></p>
     *
     */
    public static void join(Notification newNotification){
        dashboardPanel.add(newNotification);
        if (!newNotification.isRead()) {
            effectCount(1);
        }
        ComponentAssistant.ready(dashboardPanel);
    }

    public static ActionListener deletionListener(Notification notification){
        return e -> {
            notification.getShower().dispose();
            dashboardPanel.remove(notification);
            ComponentAssistant.ready(dashboardPanel);
            Notification.NOTIFICATIONS.remove(notification);
        };
    }

    public static ActionListener clearListener(){
        return e -> {
            if (dashboardPanel.getComponentCount() > 0) {
                if (App.showOkCancelDialog("Warning","This action will remove all notifications, including unread."+(getUnreadCount() == 0 ? "" : " You have "+ Globals.checkPlurality(getUnreadCount(), "notifications")+" unread."))) {
                    dashboardPanel.removeAll();
                    effectCount(-getUnreadCount());
                    ComponentAssistant.ready(dashboardPanel);
                    Notification.NOTIFICATIONS.clear();
                }
            }
        };
    }

    /**
     * <p>For all incoming or read alerts, this should be called eventually.</p>
     * <p><i>If notification is coming(new) parse 1, else if it's being read, parse -1</i></p>
     *
     * <p>This function will also renew the ToolTipText of the Board's big-button</p>
     */
    public static void effectCount(int value){
        unreadCount += value;
        final String sendingTip = getUnreadCount() == 0 ? null : Globals.checkPlurality(getUnreadCount(), "unread notifications");
        Board.getNotificationButton().setToolTipText(sendingTip);
    }

    /**
     * <p>This gets the 'unreadCount'. How may it be needed outside? As for the big-button, it's
     * only the toolTip needed which is taken cared by renewCount(), herein</p>
     */
    private static int getUnreadCount(){
        return unreadCount;
    }

    public static void updateNotices(boolean userRequested){
        if (!userRequested && !portalRefresher.isEnabled()) {
            return;
        }
        new Thread(()-> {
            portalRefresher.setEnabled(false);
            adjustNoticeComponents(false);
            if (portalNoticeDriver == null){
                trySettingNoticeDriver();
            }
            if (portalNoticeDriver == null){
                if (userRequested) {
                    App.reportMissingDriver();
                }
                portalRefresher.setEnabled(true);
                return;
            }
            if (!InternetAvailabilityChecker.isInternetAvailable()){
                if (userRequested) {
                    App.reportNoInternet();
                }
                portalRefresher.setEnabled(true);
                return;
            }
            if (DriversPack.isIn(portalNoticeDriver)) {
                Portal.startRenewingNotices(portalNoticeDriver, userRequested);
            } else {
                final int loginTry = DriversPack.attemptLogin(portalNoticeDriver);
                if (loginTry == DriversPack.ATTEMPT_SUCCEEDED) {
                    Portal.startRenewingNotices(portalNoticeDriver,userRequested);
                } else if (loginTry == DriversPack.ATTEMPT_FAILED) {
                    if (userRequested) {
                        App.reportLoginAttemptFailed();
                    }
                    portalRefresher.setEnabled(true);
                    return;
                } else if (loginTry == DriversPack.CONNECTION_LOST) {
                    if (userRequested) {
                        App.reportConnectionLost();
                    }
                    portalRefresher.setEnabled(true);
                    return;
                }
            }
            portalRefresher.setEnabled(true);
        }).start();
    }

    private static void adjustNoticeComponents(boolean responsive){
        if (responsive) {
            registrationLabel.setText(Portal.getBufferedNotice_Registration());
            admissionLabel.setText(Portal.getBufferedNotice_Admission());
        } else {
            final String waitingText = "Contacting portal... Please wait";
            registrationLabel.setText(waitingText);
            admissionLabel.setText(waitingText);
        }
    }

    public KPanel presentedContainer(){
        final KPanel bridgePanel = new KPanel(new FlowLayout(FlowLayout.LEFT));

        final JComboBox<String> alertOptions = new JComboBox<String>(new String[] {"Dashboard", "Portal"}){
            @Override
            public JToolTip createToolTip() {
                return KLabel.preferredTip();
            }
        };
        alertOptions.setFont(KFontFactory.createPlainFont(15));
        alertOptions.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        alertOptions.addActionListener(e -> {
            if (alertOptions.getSelectedIndex() == 0) {
                bridgePanel.remove(portalRefresher);
                bridgePanel.add(clearingButton);
                ComponentAssistant.ready(bridgePanel);
                card.show(notificationPanel,"Dashboard Alerts");
                hint.setText("[Showing Local Dashboard Alerts]");
            } else if (alertOptions.getSelectedIndex() == 1) {
                bridgePanel.remove(clearingButton);
                bridgePanel.add(portalRefresher);
                ComponentAssistant.ready(bridgePanel);
                card.show(notificationPanel, "Portal Alerts");
                hint.setText("[Showing Portal Alerts]");
            }
        });
        hint.setText("[Showing Local Dashboard Alerts]");

        clearingButton = new KButton("Clear Notifications");
        clearingButton.setFont(KFontFactory.createPlainFont(15));
        clearingButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        clearingButton.setMnemonic(KeyEvent.VK_C);
        clearingButton.addActionListener(clearListener());

        bridgePanel.add(clearingButton);

        final KPanel upPanel = new KPanel(new BorderLayout());
        upPanel.add(hint, BorderLayout.WEST);
        upPanel.add(KPanel.wantDirectAddition(new FlowLayout(FlowLayout.RIGHT),null, alertOptions), BorderLayout.EAST);
        upPanel.add(bridgePanel, BorderLayout.SOUTH);

        final KPanel present = new KPanel(new BorderLayout());
        present.add(upPanel, BorderLayout.NORTH);
        present.add(new KScrollPane(notificationPanel,false), BorderLayout.CENTER);

        return present;
    }

    //Local notification functions
    private void configureDashAlerts(){
        dashboardPanel = new KPanel(){
            @Override
            public Component add(Component comp) {
                dashboardPanel.setPreferredSize(new Dimension(975,dashboardPanel.getPreferredSize().height+35));
                return super.add(comp);
            }
            @Override
            public void remove(Component comp) {
                super.remove(comp);
                dashboardPanel.setPreferredSize(new Dimension(975,dashboardPanel.getPreferredSize().height-35));
            }
        };
        dashboardPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        card.addLayoutComponent(notificationPanel.add(dashboardPanel),"Dashboard Alerts");
    }

    //For those as associated with the portal
    private void configurePortalAlerts(){
        final Dimension iDimension = new Dimension(1_000,30);

        registrationLabel = new KLabel(Portal.getBufferedNotice_Registration(),KFontFactory.createPlainFont(16));
        final KPanel alertPanel_registration = new KPanel(new BorderLayout(), iDimension);
        alertPanel_registration.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        alertPanel_registration.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SwingUtilities.invokeLater(()->{
                    new NoticeExhibition(NoticeExhibition.REGISTRATION_NOTICE).setVisible(true);
                });
            }
        });
        alertPanel_registration.add(new KLabel("REGISTRATION ALERT:",KFontFactory.createBoldFont(16),Color.BLUE),BorderLayout.WEST);
        alertPanel_registration.add(KPanel.wantDirectAddition(registrationLabel),BorderLayout.CENTER);

        admissionLabel = new KLabel(Portal.getBufferedNotice_Admission(),KFontFactory.createPlainFont(16));
        final KPanel alertPanel_admission = new KPanel(new BorderLayout(), iDimension);
        alertPanel_admission.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        alertPanel_admission.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SwingUtilities.invokeLater(()->{
                    new NoticeExhibition(NoticeExhibition.ADMISSION_NOTICE).setVisible(true);
                });
            }
        });
        alertPanel_admission.add(new KLabel("ADMISSION ALERT:",KFontFactory.createBoldFont(16),Color.BLUE),BorderLayout.WEST);
        alertPanel_admission.add(KPanel.wantDirectAddition(admissionLabel),BorderLayout.CENTER);

        portalRefresher = new KButton("Update Alerts"){
            @Override
            public void setEnabled(boolean b) {
                super.setEnabled(b);
                adjustNoticeComponents(b);
            }
        };
        portalRefresher.setFont(KFontFactory.createPlainFont(15));
        portalRefresher.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        portalRefresher.addActionListener(e -> {
            updateNotices(true);
        });

        portalPanel = new KPanel(){
            @Override
            public Component add(Component comp) {
                portalPanel.setPreferredSize(new Dimension(975,portalPanel.getPreferredSize().height+35));
                return super.add(comp);
            }
            @Override
            public void remove(Component comp) {
                super.remove(comp);
                portalPanel.setPreferredSize(new Dimension(975,portalPanel.getPreferredSize().height-35));
            }
        };
        portalPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        portalPanel.addAll(alertPanel_registration, alertPanel_admission);

        card.addLayoutComponent(notificationPanel.add(portalPanel),"Portal Alerts");
    }

    private static class NoticeExhibition extends KDialog {
        private static final String REGISTRATION_NOTICE = "Registration Notice",
                ADMISSION_NOTICE = "Admission Notice";

        private NoticeExhibition(String noticeTitle){
            super(noticeTitle);
            this.setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
            this.setResizable(true);

            final String noticeText = noticeTitle.equals(REGISTRATION_NOTICE) ? Portal.getBufferedNotice_Registration() :
                    Portal.getBufferedNotice_Admission();
            final KTextPane noticePane = KTextPane.wantHtmlFormattedPane(noticeText);
            noticePane.setBackground(Color.WHITE);
            noticePane.setPreferredSize(new Dimension(500, 125));

            final KButton disposeButton = new KButton("Ok");
            disposeButton.addActionListener(e1 -> {
                this.dispose();
            });

            final KPanel lowerPart = new KPanel();
            lowerPart.setLayout(new BoxLayout(lowerPart,BoxLayout.Y_AXIS));
            lowerPart.add(KPanel.wantDirectAddition(new KLabel("Last updated: ", KFontFactory.createBoldFont(16)),
                    new KLabel(Portal.getLastNoticeUpdate(),KFontFactory.createPlainFont(16))));
            lowerPart.add(KPanel.wantDirectAddition(new FlowLayout(FlowLayout.RIGHT),null,disposeButton));

            this.getRootPane().setDefaultButton(disposeButton);
            final KPanel contentPanel = new KPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.addAll(new KScrollPane(noticePane), lowerPart);
            this.setContentPane(contentPanel);
            this.pack();
            this.setLocationRelativeTo(Board.getRoot());
        }
    }

}
