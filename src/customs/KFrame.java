package customs;

import main.App;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class KFrame extends JFrame implements Preference {
    public static final List<KFrame> ALL_FRAMES = new ArrayList<>();


    public KFrame(String title){
        super(title);
        this.setPreferences();
    }

    public KFrame(){
        this("");
    }

    /**
     * Gets the icon used by frames and dialogs. The native system also use this as a launcher icon.
     */
    public static Image getIcon() {
        final URL iPath = App.getIconURL("dashboard.png");
        if (iPath == null) {
            App.signalFatalError(null, "Initialization Failure","Internal Error Encountered: Image file could not be located or loaded.\nThe Virtual Machine is set to quit!");
        }
        return Toolkit.getDefaultToolkit().getImage(iPath);
    }

    @Override
    public void setPreferences() {
        this.setIconImage(getIcon());
        ALL_FRAMES.add(this);
    }

}
