package customs;

import javax.swing.*;
import java.awt.event.ActionListener;

public class KMenuItem extends JMenuItem implements Preference {


    public KMenuItem(String text){
        super(text);
        this.setPreferences();
    }

    public KMenuItem(String text, ActionListener actionListener){
        this(text);
        this.setPreferences();
        this.addActionListener(actionListener);
    }

    @Override
    public void setPreferences() {
        this.setFont(KFontFactory.createPlainFont(15));
    }

}
