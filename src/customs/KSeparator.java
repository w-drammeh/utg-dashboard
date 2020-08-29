package customs;

import javax.swing.*;
import java.awt.*;

public class KSeparator extends JSeparator implements Preference {


    public KSeparator(Dimension dimension) {
        super();
        this.setPreferences();
        this.setPreferredSize(dimension);
    }

    public KSeparator(Color foreground){
        super();
        this.setPreferences();
        this.setForeground(foreground);
    }

    public KSeparator(int orientation){
        super(orientation);
        this.setPreferences();
    }

    public KSeparator(Dimension size, Color foreground, int orientation){
        this(size);
        this.setForeground(foreground);
        this.setOrientation(orientation);
    }

    public KSeparator(int orientation, Color foreground){
        this(orientation);
        this.setForeground(foreground);
    }

    public KSeparator(Dimension dimension, Color foreground){
        this(dimension);
        this.setForeground(foreground);
    }

    @Override
    public void setPreferences() {
    }

}
