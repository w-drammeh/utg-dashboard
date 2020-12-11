package main;

import proto.KButton;
import proto.KFontFactory;
import proto.KLabel;
import proto.KPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

public class MComponent {
    public static final Cursor HAND_CURSOR = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);


    /**
     * Generates an image-icon from this URL, and scales it
     * relative to the given width and height.
     * The exception generative by this call is silenced!
     */
    public static ImageIcon scaleIcon(URL url, int width, int height) {
        ImageIcon icon = null;
        try {
            final BufferedImage buf = ImageIO.read(url);
            icon = new ImageIcon(buf.getScaledInstance(width, height, Image.SCALE_SMOOTH));
        } catch (Exception e) {
            App.silenceException(e);
        }
        return icon;
    }

    public static ImageIcon scaleIcon(String iconName, int width, int height) {
        return scaleIcon(App.getIconURL(iconName), width, height);
    }

    /**
     * Strips these containers off its children, if there's any
     */
    public static void empty(Container... components){
        for (Container c : components) {
            c.removeAll();
        }
    }

    public static void ready(Container... components){
        for (Container c : components) {
            c.repaint();
            c.revalidate();
        }
    }

    /**
     * Re-sets the state of these components, i.e on or off
     * as determined by the enable-param
     */
    public static void setState(boolean enable, Component... components){
        for (Component c : components) {
            c.setEnabled(enable);
        }
    }

    /**
     * Toggles the state of each of these components.
     * If a component is on, it will be off; and vice-versa.
     */
    public static void toggle(Component... components){
        for (Component c : components) {
            c.setEnabled(!c.isEnabled());
        }
    }

    /**
     * Dashboard's standard toolTip for components.
     */
    public static JToolTip preferredTip(){
        final JToolTip toolTip = new JToolTip();
        toolTip.setFont(KFontFactory.createPlainFont(14));
        toolTip.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
        return toolTip;
    }

    /**
     * This is particularly ideal under box-layouts.
     */
    public static Component contentBottomGap(){
        return Box.createVerticalStrut(20);
    }

    public static Component createUnavailableActivity(String activityName){
        final KLabel label1 = new KLabel(activityName, KFontFactory.createBoldFont(30));
        final KLabel label2 = new KLabel("This activity is unsupported for \"Trial Users\"",
                KFontFactory.createPlainFont(20), Color.DARK_GRAY);
        final KLabel label3 = new KLabel("If you are a student of The University of The Gambia, you may...",
                KFontFactory.createPlainFont(20), Color.GRAY);
        final KButton loginButton = new KButton("Login now");
        loginButton.setStyle(KFontFactory.createPlainFont(20), Color.BLUE);
        loginButton.setPreferredSize(new Dimension(150, 35));
        loginButton.setCursor(HAND_CURSOR);
        loginButton.undress();
        loginButton.underline(false);
        loginButton.addActionListener(Login.loginAction(loginButton));

        final KPanel innerPanel = new KPanel();
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
        innerPanel.addAll(new KPanel(label1), new KPanel(label2),
                new KPanel(new FlowLayout(FlowLayout.CENTER, 5, 25), label3, loginButton));

        final KPanel outerPanel = new KPanel(new BorderLayout());
        outerPanel.add(Box.createVerticalStrut(100), BorderLayout.NORTH);
        outerPanel.add(innerPanel, BorderLayout.CENTER);
        outerPanel.add(Box.createVerticalStrut(150), BorderLayout.SOUTH);
        return outerPanel;
    }

}
