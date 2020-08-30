package customs;

import javax.swing.*;

public class KCheckBox extends JCheckBox implements Preference {


    public KCheckBox(String text){
        super(text);
        this.setPreferences();
    }

    public KCheckBox(String text, boolean selected){
        super(text, selected);
        this.setPreferences();
    }

    @Override
    public JToolTip createToolTip(){
        return KLabel.preferredTip();
    }

    @Override
    public void setPreferences() {
    }

}
