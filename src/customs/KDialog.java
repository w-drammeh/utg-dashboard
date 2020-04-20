package customs;

import main.App;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <h1>class KDialog</h1>
 * <p><b>Description</b>: The standard dashboard dialog. All dialogs must extend this class.</p>
 * <p><i>By default, all dialogs are disposable, and resizable 'false'.</i></p>
 */
public class KDialog extends JDialog{
    public static final List<KDialog> ALL_DIALOGS = new ArrayList<>();


    public KDialog(){
        super();
        this.setPreferences();
    }

    public KDialog(String title){
        this.setTitle(title);
        this.setPreferences();
    }

    public KDialog(String title, int width, int length){
        this.setTitle(title);
        this.setSize(width, length);
        this.setPreferences();
    }

    private void setPreferences(){
        this.setResizable(false);
        this.setIconImage(App.getIcon());
        ALL_DIALOGS.add(this);
    }
}
