package customs;

import main.App;
import main.MyClass;

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


    public KLabel(String text){
        super(text);
    }

    public KLabel(){
        this("");
    }

    public KLabel(String text, Font font){
        super(text);
        this.setFont(font);
    }

    public KLabel(String text, Font font, Color fg){
        super(text);
        this.setFont(font);
        this.setForeground(fg);
    }

    public KLabel(Icon icon){
        super(icon);
    }

    public static KLabel wantIconLabel(String iName, int iWidth, int iHeight){
        return new KLabel(MyClass.scaleForMe(App.getIconURL(iName), iWidth, iHeight));
    }

    /**
     * Used to construct a predefined-label. The text passed is always loaded or
     * appended anytime setText(#) is invoked. The int-param determines the position (left or right)
     * of this permanent-text.
     * Giving any other than SwingConstants.LEFT or SwingConstants.RIGHT will provoke an IllegalArgumentException.
     */
    public static KLabel getPredefinedLabel(String permanentText, int position){
        if (!(position == SwingConstants.LEFT || position == SwingConstants.RIGHT)) {
            throw new IllegalArgumentException("Position must be one of SwingConstants.LEFT or SwingConstants.RIGHT");
        }

        return new KLabel(permanentText){
            @Override
            public void setText(String text) {
                if (position == SwingConstants.LEFT) {
                    super.setText(permanentText+text);
                } else {
                    super.setText(text+permanentText);
                }
            }
        };
    }

    /**
     * Dashboard's standard toolTip for components.
     */
    public static JToolTip preferredTip(){
        final JToolTip tip = new JToolTip();
        tip.setFont(KFontFactory.createPlainFont(14));
        tip.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
        return tip;
    }

    public void setStyle(Font f, Color fg){
        this.setFont(f);
        this.setForeground(fg);
    }

    /**
     * Underlines this label.
     * The separator is always shown beneath this label if 'alwaysVisible', otherwise only on mouseFocus.
     * Notice this call sets the layout Border, and puts the Separator beneath the component.
     * Whence should not be called otherwise.
     * The line uses the Color-param as its foreground. If 'null', it will assume the
     * caller's foreground instead.
     */
    public void underline(Color bg, boolean alwaysVisible){
        final KSeparator separator = new KSeparator(bg == null ? this.getForeground() : bg);
        this.setLayout(new BorderLayout());
        this.add(separator, BorderLayout.SOUTH);
        if (!alwaysVisible) {
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
    public JToolTip createToolTip() {
        return preferredTip();
    }

    @Override
    public String toString() {
        return this.getText();
    }

    @Override
    public void setPreferences() {

    }

}
