package customs;

import main.App;
import main.MyClass;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * <h1>class KButton</h1>
 */
public class KButton extends JButton {
    private String initialTip;


    public KButton(){
        super();
        this.setPreferences();
    }

    public KButton(String text){
        super(text);
        this.setPreferences();
    }

    public KButton(String text, boolean f){
        super(text);
        this.setPreferences();
        this.setFocusable(f);
    }

    public KButton(String text, int width, int height){
        super(text);
        this.setPreferences();
        this.setPreferredSize(new Dimension(width, height));
    }

    /**
     * Construct dressed iconified buttons
     */
    public KButton(Icon icon) {
        super(icon);
        this.setPreferences();
    }

    public static KButton getIconifiedButton(String  iconName, int iWidth, int iHeight){
        KButton iconButton = new KButton(MyClass.scaleForMe(App.getIconURL(iconName), iWidth, iHeight));
        iconButton.undress();

        return iconButton;
    }

    public void setStyle(Font f, Color fg){
        this.setFont(f);
        this.setForeground(fg);
    }

    /**
     * Invoked to force undressing on buttons not constructed with getIconifiedButton(#)
     */
    public void undress(){
        this.setBorderPainted(false);
        this.setContentAreaFilled(false);
    }

    public void dress(){
        this.setBorderPainted(true);
        this.setContentAreaFilled(true);
    }

    public void underline(Color bg, boolean visibleOnlyOnFocus){
        final KSeparator separator = new KSeparator(bg == null ? this.getForeground() : bg);
        this.setLayout(new BorderLayout());
        this.add(separator, BorderLayout.SOUTH);
        if (visibleOnlyOnFocus) {
            separator.setVisible(false);
            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    separator.setVisible(true);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    separator.setVisible(false);
                }
            });
        }
    }

    public void setText(int n){
        this.setText(Integer.toString(n));
    }

    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        if (b) {
            this.setToolTipText(initialTip);
        } else {
            this.setToolTipText(null);
        }
    }

    @Override
    public void setToolTipText(String text) {
        super.setToolTipText(text);
        if (text != null) {
            this.initialTip = text;
        }
    }

    public void setToolTipText(int n) {
        this.setToolTipText(Integer.toString(n));
    }

    @Override
    public JToolTip createToolTip(){
        return KLabel.preferredTip();
    }

    private void setPreferences(){
        this.setFocusable(false);
    }

}
