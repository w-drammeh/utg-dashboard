package customs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AdjustmentListener;

/**
 * The standard Dashboard Scroller. Fantastic container for swing components.
 * This class is unique in its constructors and other useful static methods embedded therein.
 */
public class KScrollPane extends JScrollPane implements Preference {


    public KScrollPane(Component insider){
        super(insider);
        this.setPreferences();
    }

    public KScrollPane(Component insider, boolean paintBorders){
        this(insider);
        this.setBorder(paintBorders ? this.getBorder() : null);
    }

    public KScrollPane(Component insider, Dimension size, boolean paintBorders){
        this(insider, paintBorders);
        this.setPreferredSize(size);
    }

    /**
     * Surround TextAreas with this.
     * If used, border modifications should be done on it, and not the textArea.
     */
    public static KScrollPane getTextAreaScroller(KTextArea textArea, Dimension dimension){
        final KScrollPane housePane = new KScrollPane(textArea, dimension,true);
        housePane.setVerticalScrollBarPolicy(KScrollPane.VERTICAL_SCROLLBAR_NEVER);
        return housePane;
    }

    /**
     * Typically, tables are surrounded with this.
     */
    public static KScrollPane getSizeMatchingScrollPane(JComponent innerComponent, int extra){
        final KScrollPane sizeTracer = new KScrollPane(innerComponent, false);
        final Dimension dimension = innerComponent.getPreferredSize();
        if (innerComponent instanceof KTable) {
            dimension.setSize(dimension.width,dimension.height+(
                    (KTable)innerComponent).getTableHeader().getPreferredSize().height + extra);
        } else {
            dimension.setSize(dimension.width,dimension.height + extra);
        }
        sizeTracer.setPreferredSize(dimension);
        return sizeTracer;
    }

    public static KScrollPane getAutoScroller(JComponent c){
        final KScrollPane scrollPane = new KScrollPane(c);
        scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> {
            final Adjustable adjustable = e.getAdjustable();
            adjustable.setValue(adjustable.getMaximum());
        });
        return scrollPane;
    }

    /**
     * Called to remove the AdjustmentListener on the ScrollPane.
     */
    public void stopAutoScrolling(){
        final JScrollBar verticalBar = this.getVerticalScrollBar();
        final AdjustmentListener[] adjustmentListeners = verticalBar.getAdjustmentListeners();
        if (adjustmentListeners.length > 0) {
            verticalBar.removeAdjustmentListener(adjustmentListeners[0]);
        }
    }

//    seems to have no effect except on sight
    public void toTop(){
        this.getVerticalScrollBar().setValue(this.getVerticalScrollBar().getMinimum());
    }

//    seems to have no effect except on sight
    public void toBottom(){
        this.getVerticalScrollBar().setValue(this.getVerticalScrollBar().getMaximum());
    }

    public void setPreferences(){
    }

}
