package proto;

import main.Settings;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class KPanel extends JPanel implements Preference {
    public static final List<KPanel> ALL_PANELS = new ArrayList<KPanel>(){
        @Override
        public boolean add(KPanel panel) {
            panel.setBackground(Settings.currentBackground());
            return super.add(panel);
        }
    };


    public KPanel(){
        super();
        setPreferences();
    }

    public KPanel(int width, int height){
        this();
        setPreferredSize(new Dimension(width, height));
    }

    public KPanel(LayoutManager layout) {
        super(layout);
        setPreferences();
    }

    public KPanel(LayoutManager layout, Dimension dimension) {
        this(layout);
        setPreferredSize(dimension);
    }

    public KPanel(LayoutManager layout, Component... components) {
        this(layout);
        addAll(components);
    }

    public KPanel(Component... components){
        this();
        addAll(components);
    }

    public KPanel(Dimension dimension, Component... components) {
        this(components);
        setPreferredSize(dimension);
    }

//    layout cannot be null; you either set it or leave it
    public KPanel(LayoutManager layout, Dimension dimension, Component... components){
        this(layout, dimension);
        addAll(components);
    }

//    this is now obsolete?
    public static KPanel wantDirectAddition(LayoutManager layout, Dimension dimension, Component... components){
        final KPanel kPanel = new KPanel(layout == null ? new FlowLayout() : layout);
        kPanel.setPreferredSize(dimension);
        kPanel.addAll(components);
        return kPanel;
    }

    /**
     * Directly adds a list of components to this instance.
     * Since this function pays no heed to position, it cannot technically be used under certain
     * layouts; especially, the beloved 'Border'. However, very useful under certain other
     * beloved layouts like 'Flow', 'Box', 'Grid'.
     */
    public void addAll(Component... list) {
        for (Component c : list) {
            add(c);
        }
    }

//    does nothing if there is no component in this instance
    public void removeLast() {
        final int count = getComponentCount();
        if (count >= 1) {
            remove(count - 1);
        }
    }

    /**
     * Adds the given component, second to last, on this panel.
     * If this panel contains no children prior to this call,
     * then this call is effectively equivalent to a add(Component)
     */
    public Component addPenultimate(Component component){
        final int count = getComponentCount();
        if (count >= 1) {
            return add(component, count - 1);
        } else {
            return add(component);
        }
    }

    public void setPreferences() {
        ALL_PANELS.add(this);
    }

}
