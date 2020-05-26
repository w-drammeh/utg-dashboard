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
    public static final ArrayList<Notification> NOTIFICATIONS = new ArrayList<>();
    private String heading;
    private String text;
    private String description;//dialog parses this same text, if the description is null.
    private boolean isRead;
    private Date time;
    private transient Exhibitor shower;
    private transient NotificationLayer layer;


    private Notification(String heading, String vText, String information, Date time){
        this.heading = heading;
        this.text = vText;
        this.description = information;
        this.time = time;
        this.shower = new Exhibitor(this);
        this.layer = new NotificationLayer(this);
    }

    public static void create(String heading, String vText, String information){
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
    }

    public KPanel getLayer(){
        return layer;
    }


    private static class Exhibitor extends KDialog implements Serializable {

        private  Exhibitor(Notification notification){
            super(notification.getHeading()+" - Dashboard Notification");
            this.setModalityType(KDialog.DEFAULT_MODALITY_TYPE);
            this.setResizable(true);

            final String decidedText = notification.getDescription() == null ? notification.getText() : notification.getDescription();
            final KTextPane noticePane = KTextPane.wantHtmlFormattedPane(decidedText);
            noticePane.setBackground(Color.WHITE);

            final KScrollPane textScroll = new KScrollPane(noticePane,false);
            textScroll.setPreferredSize(new Dimension(550,235));

            final KButton deleteButton = new KButton("Remove");
            deleteButton.addActionListener(NotificationGenerator.deletionListener(notification));

            final KButton disposeButton = new KButton("Close");
            disposeButton.setFocusable(true);
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
            return e-> Exhibitor.this.dispose();
        }
    }


    public static class NotificationLayer extends KPanel {
        private Notification notification;
        private KLabel innerLabel;

        private NotificationLayer(Notification notification){
            this.notification = notification;
            this.setPreferredSize(new Dimension(1_000, 30));
            this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            this.addMouseListener(forgeListener(this));
            this.setLayout(new BorderLayout());
            this.add(KPanel.wantDirectAddition(new KLabel(this.notification.heading.toUpperCase(),
                    KFontFactory.createBoldFont(16), Color.BLUE)), BorderLayout.WEST);
            this.innerLabel = new KLabel(this.notification.text, KFontFactory.createPlainFont(16),
                    this.notification.isRead ? null : Color.RED);
            this.add(KPanel.wantDirectAddition(this.innerLabel), BorderLayout.CENTER);
            this.add(KPanel.wantDirectAddition(new KLabel(MDate.formatFully(this.notification.time),
                    KFontFactory.createPlainFont(16), Color.GRAY)), BorderLayout.EAST);
        }

        private static MouseListener forgeListener(NotificationLayer layer){
            return new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    SwingUtilities.invokeLater(()-> layer.notification.shower.setVisible(true));
                    if (!layer.notification.isRead()) {
                        layer.notification.justRead();
                        layer.innerLabel.setForeground(null);
                        NotificationGenerator.effectCount(-1);
                    }
                }
            };
        }
    }

    public static void serializeAll(){
        System.out.print("Serializing notifications... ");
        MyClass.serialize(NOTIFICATIONS, "alerts.ser");
        System.out.println("Completed");
    }

    public static void deSerializeAll(){
        System.out.print("Deserializing notifications... ");
        final ArrayList<Notification> savedAlerts = (ArrayList<Notification>) MyClass.deserialize("alerts.ser");
        if (savedAlerts != null) {
            for (Notification alert : savedAlerts) {
                alert.shower = new Exhibitor(alert);
                alert.layer = new NotificationLayer(alert);
                NotificationGenerator.join(alert);
                NOTIFICATIONS.add(alert);
            }
        }
        System.out.println("Completed");
    }

}
