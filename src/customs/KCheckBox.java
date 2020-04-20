package customs;

import javax.swing.*;

/**
 * <h1>class KCheckBox</h1>
 */
public class KCheckBox extends JCheckBox {


    public KCheckBox(){
        super();
    }

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

}
