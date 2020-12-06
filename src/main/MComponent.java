package main;

import proto.KFontFactory;

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
    public static ImageIcon scale(URL url, int width, int height) {
        ImageIcon icon = null;
        try {
            final BufferedImage buf = ImageIO.read(url);
            icon = new ImageIcon(buf.getScaledInstance(width, height, Image.SCALE_SMOOTH));
        } catch (Exception e) {
            App.silenceException(e);
        }
        return icon;
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
        return Box.createVerticalStrut(15);
    }

}
