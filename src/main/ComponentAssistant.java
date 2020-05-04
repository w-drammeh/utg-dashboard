package main;

import javax.swing.*;
import java.awt.*;

public class ComponentAssistant {


    /**
     * Removes everything from a component. Is that it?
     */
    public static void repair(Container ... components){
        for (Container c : components) {
            c.removeAll();
        }
    }

    /**
     * Re-paints and Re-validates the components' hierarchy after undergoing runtime modifications.
     */
    public static void ready(Container ... components){
        for (Container c : components) {
            c.repaint();
            c.revalidate();
        }
    }

    // For null-layout containers
    public static void reposition(Component c, int x, int y){
        c.setLocation(x, y);
    }

    static void pushUp(JComponent c, int a){
        c.setLocation(c.getX(), c.getY() - a);
    }

    static void pushDown(JComponent c, int a){
        c.setLocation(c.getX(), c.getY() + a);
    }

    public static void pushLeft(JComponent c, int a){
        c.setLocation(c.getX() - a, c.getY());
    }

    public static void pushRight(JComponent c, int a){
        c.setLocation(c.getX() + a, c.getY());
    }

    public static Component contentBottomGap(){
        return Box.createVerticalStrut(25);
    }


}
