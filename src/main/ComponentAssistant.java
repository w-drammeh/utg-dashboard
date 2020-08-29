package main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

public class ComponentAssistant {


    /**
     * Generates an image-icon from this URL, and scale it relative
     * to the given width and height.
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
     * This is particularly ideal under box-layouts.
     */
    public static Component contentBottomGap(){
        return Box.createVerticalStrut(25);
    }

}
