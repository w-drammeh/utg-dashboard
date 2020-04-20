package main;

import customs.KPanel;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

/**
 * <h1>class ComponentAssistant</h1>
 * <p><b>Description</b>: </p>
 */
public class ComponentAssistant implements Serializable {


    //@null-layout container components
    public static void reposition(JComponent c, int x, int y){
        c.setLocation(x,y);
    }

    static void pushUp(JComponent c, int a){
        c.setLocation(c.getX(), c.getY()-a);
    }

    static void pushDown(JComponent c, int a){
        c.setLocation(c.getX(), c.getY()+a);
    }

    public static void pushLeft(JComponent c, int a){
        c.setLocation(c.getX()-a, c.getY());
    }

    public static void pushRight(JComponent c, int a){
        c.setLocation(c.getX()+a, c.getY());
    }


    //@
    /**
     * <p>Removes everything from a component. Is that it?</p>
     */
    public static void repair(Container ... components){
        for (Container c : components) {
            c.removeAll();
        }
    }

    /**
     * <p>Re-paints and Re-validates the components hierarchy after under-going runtime modifications.</p>
     */
    public static void ready(Container ... components){
        for (Container c : components) {
            c.revalidate();
            c.repaint();
        }
    }

    /**
     * <p>Convenient way of calling Box.createRigidArea(Dimension)</p>
     */
    public static Component provideBlankSpace(int width, int height){
        return Box.createRigidArea(new Dimension(width,height));
    }

    /**
     * <p>This does not return a rigid area as do by its alternative - provideBlankSpace.
     * This returns a container(KPanel?) with the specified size and background.</p>
     */
    public static Component provideBlankSpace(Color bg, Dimension d){
        final KPanel blankComponent = new KPanel(d);
        blankComponent.setBackground(bg);

        return blankComponent;
    }

    public static Component contentBottomGap(int width){
        return provideBlankSpace(width,25);
    }

    public static Component contentBottomGap(){
        return contentBottomGap(250);
    }


}
