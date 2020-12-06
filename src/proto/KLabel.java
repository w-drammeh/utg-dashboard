package proto;

import main.App;
import main.MComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * One of the custom classes. One of the most useful. KLabel extends javax.swing.JLabel
 * allowing it to be constructed in numerous ways.
 * It also overrides the toString() from the Object type, returning its text.
 */
public class KLabel extends JLabel implements Preference {


    public KLabel(){
        super();
        setPreferences();
    }

    public KLabel(String text){
        super(text);
        setPreferences();
    }

    public KLabel(String text, Font font){
        this(text);
        setFont(font);
    }

    public KLabel(String text, Font font, Color fg){
        this(text);
        setStyle(font, fg);
    }

    public KLabel(Icon icon){
        super(icon);
        setPreferences();
    }

    public static KLabel wantIconLabel(String name, int width, int height){
        return new KLabel(MComponent.scale(App.getIconURL(name), width, height));
    }

    /**
     * Used to construct a predefined-label. The text passed is always loaded or
     * appended anytime setText(String) is invoked on this instance.
     * The int-param determines the position (left or right) of the permanent-text.
     */
    public static KLabel getPredefinedLabel(String permanentText, int position){
        return new KLabel() {
            @Override
            public void setText(String text) {
                super.setText(position == SwingConstants.LEFT ? permanentText + text : text + permanentText);
            }
        };
    }

    public void setStyle(Font f, Color fg){
        setFont(f);
        setForeground(fg);
    }

    /**
     * Underlines this label.
     * The separator is always shown beneath this label if 'alwaysVisible',
     * otherwise only on mouseFocus events.
     * Notice this call sets the layout Border, and puts the Separator beneath the component.
     * Whence should not be called otherwise.
     * The line uses the color-param as its foreground. If 'null', it will assume the
     * caller's foreground instead.
     */
    public void underline(Color foreground, boolean alwaysVisible){
        final KSeparator separator = new KSeparator(foreground == null ? getForeground() : foreground);
        setLayout(new BorderLayout());
        add(separator, BorderLayout.SOUTH);
        if (!alwaysVisible) {
            separator.setVisible(false);
            addMouseListener(new MouseAdapter() {
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

    public void underline(boolean alwaysVisible){
        underline(null, alwaysVisible);
    }

    public void setText(int n){
        setText(Integer.toString(n));
    }

    @Override
    public JToolTip createToolTip() {
        return MComponent.preferredTip();
    }

    @Override
    public String toString() {
        return getText();
    }

    @Override
    public void setPreferences() {
    }

}
