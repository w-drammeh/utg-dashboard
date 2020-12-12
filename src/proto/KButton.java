package proto;

import main.App;
import main.MComponent;

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
        setPreferences();
    }

    public KButton(String text){
        super(text);
        setPreferences();
    }

    /**
     * Constructs an iconified button; which, by default or under most UIs, is "dressed".
     */
    public KButton(Icon icon){
        super(icon);
        setPreferences();
    }

    public static KButton getIconifiedButton(String name, int width, int height){
        final KButton iconButton = new KButton(MComponent.scaleIcon(App.getIconURL(name), width, height));
        iconButton.undress();
        return iconButton;
    }

    public void setStyle(Font font, Color foreground){
        setFont(font);
        setForeground(foreground);
    }

    /**
     * Invoked to force "undressing" on buttons not constructed with getIconifiedButton(#).
     */
    public void undress(){
        setBorderPainted(false);
        setContentAreaFilled(false);
    }

    public void redress(){
        setBorderPainted(true);
        setContentAreaFilled(true);
    }

    /**
     * See KLabel.underline(Color, boolean)
     */
    public void underline(Color foreground, boolean alwaysVisible){
        final KSeparator separator = new KSeparator(foreground == null ? getForeground() : foreground);
        setLayout(new BorderLayout());
        add(separator, BorderLayout.SOUTH);
        if (!alwaysVisible) {
            separator.setVisible(false);
            addMouseListener(new MouseAdapter(){
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
        underline(null, alwaysVisible);
    }

    public void setText(int n){
        setText(Integer.toString(n));
    }

    public void setToolTipText(int n){
        setToolTipText(Integer.toString(n));
    }

    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        setToolTipText(b ? initialTip : null);
    }

    @Override
    public void setToolTipText(String text) {
        super.setToolTipText(text);
        if (text != null) {
            initialTip = text;
        }
    }

    @Override
    public JToolTip createToolTip(){
        return MComponent.preferredTip();
    }

    @Override
    public void setPreferences(){
        this.setFocusable(false);
    }

}
