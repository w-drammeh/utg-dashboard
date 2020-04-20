package customs;

import main.SettingsCore;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <h1>class KPanel</h1>
 * <p><b>Description</b>: Panels as we know are basically the most fundamental of
 * swing gui builders.</p>
 * <p><i>The KPanel type has many useful constructors.</i></p>
 */
public class KPanel extends JPanel {
    public static final List<KPanel> ALL_PANELS = new ArrayList<>();


    public KPanel(){
        super();
        this.setPreferences();
    }

    public KPanel(LayoutManager layout) {
        super(layout);
        this.setPreferences();
    }

    public KPanel(Dimension d) {
        super();
        this.setPreferredSize(d);
        this.setPreferences();
    }

    public KPanel(int pWidth, int pHeight){
        this(new Dimension(pWidth, pHeight));
    }

    public KPanel(LayoutManager layout, Dimension dimension) {
        super(layout);
        this.setPreferredSize(dimension);
        this.setPreferences();
    }

    /**
     * <p>A convenient way of calling wantDirectAddition(null, null, directComponent)</p>
     * <p>Still respecting the default layout, this will add all the directComponents
     * to the returned instance.</p>
     * <p>Notice that the ability of this call to accept multiple param-components
     * will minimize declaration of panels for the sake of holding multiple components
     * instead of direct addition.</p>
     */
    public static KPanel wantDirectAddition(Component... directComponents){
        final KPanel panel = new KPanel();
        for (Component comp : directComponents) {
            panel.add(comp);
        }

        return panel;
    }

    /**
     * <p>Returns a panel which, by the way, contains only one component - i.e this param.</p>
     * <p><i>The layout and the dimension can be specified. If the layout is null, it uses the default Flow;
     * If the dimension is null, UI is asked, off course.</i></p>
     *
     * <p><i>If you have the intention of using both params null, then use the alternative over-loaded call.</i></p>
     * <p><i>Accepts one component.</i></p>
     */
    public static KPanel wantDirectAddition(LayoutManager layout, Dimension dimension, JComponent... directComponents){
        final KPanel kPanel = new KPanel(layout == null ? new FlowLayout(FlowLayout.CENTER) : layout);
        kPanel.setPreferredSize(dimension);
        for (JComponent directComponent : directComponents) {
            kPanel.add(directComponent);
        }

        return kPanel;
    }

    public void removeLastChild(){
        final int childrenCount = this.getComponentCount();
        if (childrenCount >= 0) {
            this.remove(childrenCount - 1);
        }
    }

    /**
     * <p>Directly adds a list of components to this instance.</p>
     * <p><i>Since this function pays no heed to position, it cannot be used under certain
     * layouts, especially, the beloved 'border'. However, very useful under certain other
     * beloved layouts like 'flow', and 'box'.</i></p>
     */
    public void addAll(Component ... list){
        for(Component c : list){
            this.add(c);
        }

    }

    private void setPreferences(){
        this.setBackground(SettingsCore.currentBackground());//Some are created on runtime, off course
        ALL_PANELS.add(this);
    }

}
