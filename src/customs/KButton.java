package customs;

import main.App;
import main.ComponentAssistant;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * The standard Dashboard Button.
 * It is a convention that buttons modify their toolTips based on their states
 * (on or off) as appropriate.
 */
public class KButton extends JButton implements Preference {
    private String initialTip;


    public KButton(){
        super();
        this.setPreferences();
    }

    public KButton(String text){
        super(text);
        this.setPreferences();
    }

    /**
     * Constructs an iconified button; which, by default or under most UIs, is "dressed".
     */
    public KButton(Icon icon){
        super(icon);
        this.setPreferences();
    }

    public static KButton getIconifiedButton(String iconName, int iWidth, int iHeight){
        final KButton iconButton = new KButton(ComponentAssistant.scale(App.getIconURL(iconName), iWidth, iHeight));
        iconButton.undress();
        return iconButton;
    }

    public void setStyle(Font font, Color foreground){
        this.setFont(font);
        this.setForeground(foreground);
    }

    /**
     * Invoked to force "undressing" on buttons not constructed with getIconifiedButton(#).
     */
    public void undress(){
        this.setBorderPainted(false);
        this.setContentAreaFilled(false);
    }

    public void redress(){
        this.setBorderPainted(true);
        this.setContentAreaFilled(true);
    }

    /**
     * See KLabel.underline(Color, boolean)
     */
    public void underline(Color fg, boolean alwaysVisible){
        final KSeparator separator = new KSeparator(fg == null ? this.getForeground() : fg);
        this.setLayout(new BorderLayout());
        this.add(separator, BorderLayout.SOUTH);
        if (!alwaysVisible) {
            separator.setVisible(false);
            this.addMouseListener(new MouseAdapter(){
                @Override
                public void mouseEntered(MouseEvent e){
                    separator.setVisible(true);
                }
                @Override
                public void mouseExited(MouseEvent e){
                    separator.setVisible(false);
                }
            });
        }
    }

    public void underline(boolean alwaysVisible){
        this.underline(null, alwaysVisible);
    }

    public void setText(int n){
        this.setText(Integer.toString(n));
    }

    public void setToolTipText(int n) {
        this.setToolTipText(Integer.toString(n));
    }

    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        this.setToolTipText(b ? initialTip : null);
    }

    @Override
    public void setToolTipText(String text) {
        super.setToolTipText(text);
        if (text != null) {
            this.initialTip = text;
        }
    }

    @Override
    public JToolTip createToolTip(){
        return KLabel.preferredTip();
    }

    @Override
    public void setPreferences(){
        this.setFont(KFontFactory.createPlainFont(15));
        this.setFocusable(false);
    }

}
