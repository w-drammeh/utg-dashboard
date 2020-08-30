package customs;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The standard Dashboard Dialog all dialogs must inherit from.
 * By default, all dialogs are disposable, and not resizable.
 */
public class KDialog extends JDialog implements Preference {
    public static final List<KDialog> ALL_DIALOGS = new ArrayList<>(){
        @Override
        public boolean add(KDialog dialog) {
            SwingUtilities.updateComponentTreeUI(dialog);
            return super.add(dialog);
        }
    };


    public KDialog(){
        super();
        this.setPreferences();
    }

    public KDialog(String title){
        this();
        this.setTitle(title);
    }

    public void setPreferences(){
        this.setResizable(false);
        this.setIconImage(KFrame.getIcon());
        ALL_DIALOGS.add(this);
    }

}
