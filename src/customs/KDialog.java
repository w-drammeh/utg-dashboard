package customs;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The standard Dashboard Dialog all dialogs must inherit from.
 * By default, all dialogs are disposable, not resizable,
 */
public class KDialog extends JDialog implements Preference {
    public static final List<KDialog> ALL_DIALOGS = new ArrayList<>();


    public KDialog(){
        super();
        setPreferences();
    }

    public KDialog(String title){
        this();
        setTitle(title);
    }

    public void setPreferences(){
        setResizable(false);
        setIconImage(KFrame.getIcon());
        ALL_DIALOGS.add(this);
    }

}
