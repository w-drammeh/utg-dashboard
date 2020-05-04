package customs;

import javax.swing.*;
import java.awt.*;

/**
 * Not particularly so used in this implementation.
 */
public class KPasswordField extends JPasswordField implements Preference {


    public KPasswordField(){
        this.setPreferences();
    }

    public KPasswordField(Dimension d){
        this();
        this.setPreferredSize(d);
    }

    public KPasswordField(String initial){
        super(initial);
        this.setPreferences();
    }

    public void setPreferences(){
        this.setFont(KFontFactory.createPlainFont(15));
        this.setAutoscrolls(true);
        this.setHorizontalAlignment(SwingConstants.CENTER);
    }

}
