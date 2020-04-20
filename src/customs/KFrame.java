package customs;

import main.App;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <h1>class KFrame</h1>
 */
public class KFrame extends JFrame {
    public static final List<KFrame> ALL_FRAMES = new ArrayList<>();


    public KFrame(){
        super();
        this.setPreferences();
    }

    public KFrame(String title){
        super(title);
        this.setPreferences();
    }

    public KFrame(String title, int breadth, int height){
        this.setTitle(title);
        this.setSize(breadth, height);
        this.setPreferences();
    }

    public void setPreferences() {
        this.setIconImage(App.getIcon());
        ALL_FRAMES.add(this);
    }
}
