package customs;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The standard dashboard dialog. All dialogs must inherit this class!
 * By default, all dialogs are disposable, but not resizable.
 */
public class KDialog extends JDialog implements Preference {
    public static final List<KDialog> ALL_DIALOGS = new ArrayList<>();


    public KDialog(){
        super();
        this.setPreferences();
    }

    public KDialog(String title){
        this.setTitle(title);
        this.setPreferences();
    }

    public void setPreferences(){
        this.setResizable(false);
        this.setIconImage(KFrame.getIcon());
        ALL_DIALOGS.add(this);
    }

}
