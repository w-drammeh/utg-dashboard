package customs;

import main.App;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class KFrame extends JFrame implements Preference {
    public static final List<KFrame> ALL_FRAMES = new ArrayList<>(){
        @Override
        public boolean add(KFrame frame) {
            SwingUtilities.updateComponentTreeUI(frame);
            return super.add(frame);
        }
    };


    public KFrame(String title){
        super(title);
        this.setPreferences();
    }

    /**
     * Gets the icon used by frames and dialogs. Native systems use this as a launcher icon.
     */
    public static Image getIcon() {
        final URL iPath = App.getIconURL("dashboard.png");
        if (iPath == null) {
            App.signalFatalError(null, "Initialization Error",
                    "Internal Error Encountered: Image file could not be located or loaded.\n" +
                            "The Virtual Machine is set to quit!");
        }
        return Toolkit.getDefaultToolkit().getImage(iPath);
    }

    @Override
    public void setPreferences() {
        this.setIconImage(getIcon());
        ALL_FRAMES.add(this);
    }

}
