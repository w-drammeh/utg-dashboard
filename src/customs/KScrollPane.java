package customs;

import javax.swing.*;
import java.awt.*;

/**
 * The standard Dashboard Scroller. Fantastic container for swing components.
 * This class is unique in its constructors and other useful static methods embedded therein.
 */
public class KScrollPane extends JScrollPane implements Preference {


    public KScrollPane(Component insider){
        super(insider);
        this.setPreferences();
    }

    /**
     * This obsoletes the default, since its more useful in direct painting of the border.
     */
    public KScrollPane(Component insider, boolean paintBorders){
        this(insider);
        this.setBorder(paintBorders ? this.getBorder() : null);
    }

    public KScrollPane(Component insider, Dimension dimension, boolean paintBorders){
        super(insider);
        this.setPreferredSize(dimension);
        this.setBorder(paintBorders ? this.getBorder() : null);
        this.setPreferences();
    }

    /**
     * Surround TextAreas with this.
     * If used, border modifications should be done on it, and not the textArea.
     */
    public static KScrollPane getTextAreaScroller(KTextArea textArea, Dimension dimension){
        final KScrollPane housePane = new KScrollPane(textArea,dimension,true);
        housePane.setVerticalScrollBarPolicy(KScrollPane.VERTICAL_SCROLLBAR_NEVER);
        return housePane;
    }

    /**
     * Typically, tables are surrounded with this.
     */
    public static KScrollPane getSizeMatchingScrollPane(JComponent innerComponent, int extra){
        KScrollPane sizeTracer = new KScrollPane(innerComponent, false);
        final Dimension iSize = innerComponent.getPreferredSize();

        if (innerComponent instanceof KTable) {
            iSize.setSize(iSize.width,iSize.height+((KTable) innerComponent).getTableHeader().getPreferredSize().height+extra);
        } else {
            iSize.setSize(iSize.width,iSize.height+extra);
        }

        sizeTracer.setPreferredSize(iSize);
        return sizeTracer;
    }

    public static KScrollPane getAutoScroller(JComponent c){
        KScrollPane scrollPane = new KScrollPane(c);
        scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> {
            Adjustable adjustable = e.getAdjustable();
            adjustable.setValue(adjustable.getMaximum());
        });
        return scrollPane;
    }

    /**
     * Called to remove the AdjustmentListener on the ScrollPane.
     */
    public static void stopAutoScrollingOn(KScrollPane kScrollPane){
        final JScrollBar vScrollBar = kScrollPane.getVerticalScrollBar();
        vScrollBar.removeAdjustmentListener(vScrollBar.getAdjustmentListeners()[0]);
    }

    public void toTop(){
        this.getVerticalScrollBar().setValue(this.getVerticalScrollBar().getMinimum());
    }

    public void toBottom(){
        this.getVerticalScrollBar().setValue(this.getVerticalScrollBar().getMaximum());
    }

    public void setPreferences(){

    }

}
