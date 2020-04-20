package customs;

import javax.swing.*;
import java.awt.*;

/**
 * <h1>class KSeparator</h1>
 */
public class KSeparator extends JSeparator {


    public KSeparator(Color bg){
        super();
        this.setBackground(bg);
    }

    public KSeparator(int orientation){
        super(orientation);
    }

    public KSeparator(int orientation, Color bg){
        this(orientation);
        this.setBackground(bg);
    }

    public KSeparator(Color bg, Dimension dimension){
        super();
        this.setBackground(bg);
        this.setPreferredSize(dimension);
    }

    public KSeparator(Dimension dimension) {
        super();
        this.setPreferredSize(dimension);
    }

}
