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
        setPreferences();
    }

    public KScrollPane(Component insider, Dimension size){
        this(insider);
        setPreferredSize(size);
    }

    public static KScrollPane getAutoScroller(JComponent c){
        final KScrollPane scrollPane = new KScrollPane(c);
        scrollPane.getVerticalScrollBar().addAdjustmentListener(e-> {
            final Adjustable adjustable = e.getAdjustable();
            adjustable.setValue(adjustable.getMaximum());
        });
        return scrollPane;
    }

    /**
     * Called to remove the AdjustmentListener on the ScrollPane.
     */
    public void stopAutoScrolling(){
        final JScrollBar verticalBar = getVerticalScrollBar();
        final AdjustmentListener[] adjustmentListeners = verticalBar.getAdjustmentListeners();
        if (adjustmentListeners.length > 0) {
            verticalBar.removeAdjustmentListener(adjustmentListeners[0]);
        }
    }

//    seems to have no effect except on sight
    public void toTop(){
        getVerticalScrollBar().setValue(0);
    }

//    seems to have no effect except on sight
    public void toBottom(){
        getVerticalScrollBar().setValue(getVerticalScrollBar().getMaximum());
    }

    public void setPreferences(){
    }

}
