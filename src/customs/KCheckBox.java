package customs;

import javax.swing.*;

public class KCheckBox extends JCheckBox implements Preference {


    public KCheckBox(String text){
        super(text);
    }

    public KCheckBox(String text, boolean selected){
        super(text, selected);
    }

    @Override
    public JToolTip createToolTip(){
        return KLabel.preferredTip();
    }

    @Override
    public void setPreferences() {

    }

}
