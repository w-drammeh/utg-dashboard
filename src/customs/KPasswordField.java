package customs;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class KPasswordField extends JPasswordField {
    public static final List<KPasswordField> ALL_PSSWD_FIELDS = new ArrayList<>();


    public KPasswordField(){
        this.setPreferences();
    }

    public KPasswordField(String initial){
        super(initial);
        this.setPreferences();
    }

    private void setPreferences(){

    }

}
