package customs;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * This, and the KTable types should always be surrounded in a KScrollPane?
 */
public class KTextArea extends JTextArea implements Preference {


    public KTextArea(){
        super();
        this.setPreferences();
    }

    /**
     * Use a TextArea with limited character entry as specified by the limit-param.
     */
    public static KTextArea getLimitedEntryArea(int limit){
        final KTextArea controlArea = new KTextArea();
        controlArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (controlArea.getText().length() >= limit) {
                    e.consume();
                }
            }
        });
        return controlArea;
    }

    public void setPreferences() {
        this.setLineWrap(true);
        this.setWrapStyleWord(true);
        this.setAutoscrolls(true);
        this.setFont(KFontFactory.createPlainFont(15));
    }

}
