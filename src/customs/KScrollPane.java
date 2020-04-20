package customs;

import javax.swing.*;
import java.awt.*;

/**
 * <h1>class KScrollPane</h1>
 * <p>The standard Scroller. Fantastic container for swing components</p>
 * <p><i>This class is unique in its constructors and other useful static methods embedded therein.</i></p>
 */
public class KScrollPane extends JScrollPane {
//    public static List<KScrollPane> ALL_SCROLLERS = new ArrayList<>();


    public KScrollPane(Component insider){
        super(insider);
        this.setPreferences();
    }

    /**
     * <p>This obsoletes the default, since its more useful in direct painting of the border.</p>
     * <p>Do not give any param if you wish to paint, for borders are, by default, painted.</p>
     */
    public KScrollPane(Component insider, boolean paintBorders){
        super(insider);
        this.setPreferences();
        if (!paintBorders) {
            this.setBorder(null);
        }
    }

    public KScrollPane(Component insider, Dimension prfSize, boolean paintBorders){
        super(insider);
        this.setPreferredSize(prfSize);
        if (!paintBorders) {
            this.setBorder(null);
        }
        this.setPreferences();
    }

    /**
     * Surround TextAreas with this.
     * If used, border modifications should be done on it, not the textArea.
     */
    public static KScrollPane getTextAreaScroller(KTextArea textArea, Dimension dimension){
        final KScrollPane housePane = new KScrollPane(textArea,dimension,true);
        housePane.setVerticalScrollBarPolicy(KScrollPane.VERTICAL_SCROLLBAR_NEVER);

        return housePane;
    }

    /**
     * Typically, tables are surrounded with this
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
     * <p>Called to remove the AdjustmentListener on the ScrollPane</p>
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

    private void setPreferences(){
//        this.setBackground(SettingsCore.currentBackground());
//        ALL_SCROLLERS.add(this);
    }

}
