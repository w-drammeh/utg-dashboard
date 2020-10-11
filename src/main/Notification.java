package main;

import customs.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Or NotificationSelf
 */
public class Notification implements Serializable {
    private String heading;
    private String text;
    private String information;//dialog parses this same text, if the description is null.
    private boolean isRead;
    private Date time;
    private transient Exhibitor shower;
    private transient NotificationLayer layer;
    public static final ArrayList<Notification> NOTIFICATIONS = new ArrayList<>();


    private Notification(String heading, String vText, String information, Date time) {
        this.heading = heading;
        this.text = vText;
        this.information = information;
        this.time = time;
        this.shower = new Exhibitor(this);
        this.layer = new NotificationLayer(this);
    }

    public static void create(String heading, String vText, String information) {
        final Notification incoming = new Notification(heading, vText, information, new Date());
        NotificationGenerator.join(incoming);
        NOTIFICATIONS.add(incoming);
    }

    public String getHeading(){
        return heading;
    }

    public String getText(){
        return text;
    }

    public String getInformation(){
        return information;
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
        layer.innerLabel.setForeground(null);
    }

    public KPanel getLayer(){
        return layer;
    }


    private static class Exhibitor extends KDialog {

        private  Exhibitor(Notification notification){
            super(notification.getHeading()+" - Dashboard Notification");
            setModalityType(KDialog.DEFAULT_MODALITY_TYPE);
            setResizable(true);

            final String decidedText = notification.getInformation() == null ? notification.getText() : notification.getInformation();
            final KTextPane noticePane = KTextPane.wantHtmlFormattedPane(decidedText);

            final KScrollPane textScroll = new KScrollPane(noticePane);
            textScroll.setPreferredSize(new Dimension(550,235));

            final KButton deleteButton = new KButton("Remove");
            deleteButton.addActionListener(NotificationGenerator.deleteAction(notification));

            final KButton disposeButton = new KButton("Close");
            disposeButton.setFocusable(true);
            disposeButton.addActionListener(closeListener());

            final KPanel lowerPart = new KPanel(new FlowLayout(FlowLayout.RIGHT));
            lowerPart.addAll(deleteButton, disposeButton);

            final KPanel contentPanel = new KPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.addAll(textScroll, MComponent.contentBottomGap(), lowerPart);
            setContentPane(contentPanel);
            getRootPane().setDefaultButton(disposeButton);
            pack();
            setLocationRelativeTo(Board.getRoot());
        }

        private ActionListener closeListener(){
            return e-> dispose();
        }

    }


    private static class NotificationLayer extends KPanel {
        private Notification notification;
        private KLabel innerLabel;

        private NotificationLayer(Notification alert) {
            this.notification = alert;
            setPreferredSize(new Dimension(1_000, 35));
            setCursor(MComponent.HAND_CURSOR);
            addMouseListener(forgeListener(this));
            setLayout(new BorderLayout());
            add(new KPanel(new KLabel(alert.heading.toUpperCase(), KFontFactory.createBoldFont(16),
                    Color.BLUE)), BorderLayout.WEST);
            innerLabel = new KLabel(alert.text, KFontFactory.createPlainFont(16), alert.isRead ? null : Color.RED);
            add(new KPanel(innerLabel), BorderLayout.CENTER);
            add(new KPanel(new KLabel(MDate.formatFully(alert.time),
                    KFontFactory.createPlainFont(16), Color.GRAY)), BorderLayout.EAST);
        }

        private static MouseListener forgeListener(NotificationLayer layer){
            return new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    layer.innerLabel.setFont(KFontFactory.createBoldFont(16));
                }

                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    SwingUtilities.invokeLater(()-> layer.notification.shower.setVisible(true));
                    if (!layer.notification.isRead()) {
                        layer.notification.justRead();
                        NotificationGenerator.effectCount(-1);
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    layer.innerLabel.setFont(KFontFactory.createPlainFont(16));
                }
            };
        }
    }


    public static void serializeAll() {
        System.out.print("Serializing Notifications... ");
        Serializer.toDisk(NOTIFICATIONS, "alerts.ser");
        System.out.println("Completed.");
    }

    public static void deSerializeAll(){
        System.out.print("Deserializing Notifications... ");
        final ArrayList<Notification> savedAlerts = (ArrayList<Notification>) Serializer.fromDisk("alerts.ser");
        if (savedAlerts == null) {
            System.err.println("Unsuccessful.");
        } else {
            for (Notification alert : savedAlerts) {
                alert.shower = new Exhibitor(alert);
                alert.layer = new NotificationLayer(alert);
                NotificationGenerator.join(alert);
                NOTIFICATIONS.add(alert);
            }
            System.out.println("Completed successfully.");
        }
    }

}
