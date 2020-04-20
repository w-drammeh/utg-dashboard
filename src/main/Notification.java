package main;

import customs.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class Notification extends KPanel {
    public static final ArrayList<Notification> NOTIFICATIONS = new ArrayList<>();
    private String heading;
    private String text;
    private String description;//dialog parses this same text, if the description is null.
    private KLabel leftMostLabel, innerLabel, rightMostLabel;
    private Exhibitor shower;
    private boolean isRead;

    private Notification(String heading, String vText, String information, ActionListener buttonListener){
        this.heading = heading;
        this.text = vText;
        this.description = information;
        this.shower = new Exhibitor(this);

        this.leftMostLabel = new KLabel(heading.toUpperCase(), KFontFactory.createBoldFont(16),Color.BLUE);
        this.innerLabel = new KLabel(text, KFontFactory.createPlainFont(16), this.isRead ? null : Color.RED);
        this.rightMostLabel = new KLabel(MDate.now(),KFontFactory.createPlainFont(16), Color.GRAY);

        final KPanel easternPanel = new KPanel(new FlowLayout(FlowLayout.RIGHT));
        easternPanel.add(rightMostLabel);
        if (buttonListener != null) {
            final KButton criticalButton = KButton.getIconifiedButton("options.png", 20, 20);
            criticalButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            criticalButton.addActionListener(buttonListener);

            easternPanel.add(criticalButton);
        }

        this.setPreferredSize(new Dimension(975,30));
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.addMouseListener(forgeListener());
        this.setLayout(new BorderLayout());
        this.add(leftMostLabel, BorderLayout.WEST);
        this.add(KPanel.wantDirectAddition(innerLabel), BorderLayout.CENTER);
        this.add(easternPanel, BorderLayout.EAST);
    }

    public static void create(String heading, String vText, String information, ActionListener buttonListener){
        final Notification incoming = new Notification(heading,vText,information,buttonListener);
        NotificationGenerator.join(incoming);
        NOTIFICATIONS.add(incoming);
    }

    public static void serializeAll(){
        System.out.print("Serializing notifications... ");
        MyClass.serialize(NOTIFICATIONS, "alerts.ser");
        System.out.println("Completed");
    }

    public static void deSerializeAll(){
        System.out.print("Deserializing notifications... ");
        final ArrayList<Notification> savedAlerts = (ArrayList<Notification>) MyClass.deserialize("alerts.ser");
        for (Notification alert : savedAlerts) {
            final Notification replacementAlert = new Notification(alert.heading, alert.text, alert.description, null);
            if (alert.isRead) {
                replacementAlert.justRead();
            }
            NotificationGenerator.join(replacementAlert);
            NOTIFICATIONS.add(replacementAlert);
        }
        System.out.println("Completed");
    }

    public String getHeading(){
        return heading;
    }

    public String getText(){
        return text;
    }

    public String getDescription(){
        return description;
    }

    public KDialog getShower(){
        return shower;
    }

    //This is an enquiry
    public boolean isRead(){
        return isRead;
    }

    //This is an update
    private void justRead(){
        isRead = true;
        innerLabel.setForeground(null);
    }

    private MouseListener forgeListener(){
        return new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                SwingUtilities.invokeLater(()->{
                    Notification.this.shower.setVisible(true);
                });
                if (!Notification.this.isRead()) {
                    Notification.this.justRead();
                    NotificationGenerator.effectCount(-1);
                }
            }
        };
    }

    private static class Exhibitor extends KDialog {
        private KButton disposeButton, deleteButton;

        private  Exhibitor(Notification notification){
            super(notification.getHeading()+" - Dashboard Notification");
            this.setModalityType(KDialog.DEFAULT_MODALITY_TYPE);
            this.setResizable(true);

            final String decidedText = notification.getDescription() == null ? notification.getText() : notification.getDescription();
            final KTextPane noticePane = KTextPane.wantHtmlFormattedPane(decidedText);
            noticePane.setBackground(Color.WHITE);

            final KScrollPane textScroll = new KScrollPane(noticePane,false);
            textScroll.setPreferredSize(new Dimension(550,235));

            deleteButton = new KButton("Remove");
            deleteButton.addActionListener(NotificationGenerator.deletionListener(notification));

            disposeButton = new KButton("Close", true);
            disposeButton.addActionListener(closeListener());

            final KPanel lowerPart = new KPanel(new FlowLayout(FlowLayout.RIGHT));
            lowerPart.addAll(deleteButton, disposeButton);

            this.getRootPane().setDefaultButton(disposeButton);
            final KPanel contentPanel = new KPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.addAll(textScroll, ComponentAssistant.contentBottomGap(), lowerPart);
            this.setContentPane(contentPanel);
            this.pack();
            this.setMinimumSize(this.getPreferredSize());
            this.setLocationRelativeTo(Board.getRoot());
        }

        private ActionListener closeListener(){
            return e->{
                this.dispose();
            };
        }
    }

}
