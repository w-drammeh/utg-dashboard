package customs;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * <h1>class KTextArea</h1>
 * <p>This, and the {@link KTable} types should always be surrounded in a {@link KScrollPane}</p>
 */
public class KTextArea extends JTextArea {


    public KTextArea(){
        super();
        this.setPreferences();
    }

    /**
     * <p>Use a TextArea with limited character entry as specified by the param.</p>
     */
    public static KTextArea limitedEntry(int limit){
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
