package customs;

import main.SettingsCore;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class KPanel extends JPanel implements Preference {
    public static final List<KPanel> ALL_PANELS = new ArrayList<>(){
        @Override
        public boolean add(KPanel panel) {
            panel.setBackground(SettingsCore.currentBackground());
            return super.add(panel);
        }
    };


    public KPanel(){
        super();
        this.setPreferences();
    }

    public KPanel(LayoutManager layout) {
        super(layout);
        this.setPreferences();
    }

    public KPanel(int width, int height){
        this();
        this.setPreferredSize(new Dimension(width, height));
    }

    public KPanel(LayoutManager layout, Dimension dimension) {
        this(layout);
        this.setPreferredSize(dimension);
    }

    public KPanel(Component... directComponents){
        this();
        this.addAll(directComponents);
    }

    /**
     * The layout and the dimension can be specified.
     * If the layout is null, uses the default Flow;
     * If the dimension is null, UI is asked, off course.
     */
    public static KPanel wantDirectAddition(LayoutManager layout, Dimension dimension, JComponent... directComponents){
        final KPanel kPanel = new KPanel(layout == null ? new FlowLayout() : layout);
        kPanel.setPreferredSize(dimension);
        kPanel.addAll(directComponents);
        return kPanel;
    }

    public void removeLastChild() {
        final int childrenCount = this.getComponentCount();
        if (childrenCount >= 1) {
            this.remove(childrenCount - 1);
        }
    }

    /**
     * Directly adds a list of components to this instance.
     * Since this function pays no heed to position, it cannot technically be used under certain
     * layouts; especially, the beloved 'Border'. However, very useful under certain other
     * beloved layouts like 'Flow', and 'Box'.
     */
    public void addAll(Component... list) {
        for (Component c : list) {
            this.add(c);
        }
    }

    public void setPreferences() {
        ALL_PANELS.add(this);
    }

}
