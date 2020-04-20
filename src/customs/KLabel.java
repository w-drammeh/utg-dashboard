package customs;

import main.App;
import main.MyClass;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * <h1>class KLabel</h1>
 * <p>One of the custom classes. One of the most useful. KLabel extends javax.swing.JLabel
 * allowing it to be constructed in numerous ways.</p>
 * <p><i>It also overrides the toString function from the Object type, returning its text.</i></p>
 */
public class KLabel extends JLabel{


    public KLabel(){
        super();
    }

    public KLabel(String text){
        super(text);
    }

    public KLabel(String text, Font font){
        super(text);
        this.setFont(font);
    }

    public KLabel(String text, Font factory, Color fg){
        super(text);
        this.setFont(factory);
        this.setForeground(fg);
    }

    public KLabel(Icon icon){
        super(icon);
    }

    public static KLabel wantIconLabel(String iName, int iWidth, int iHeight){
        return new KLabel(MyClass.scaleForMe(new App().getIconURL(iName), iWidth, iHeight));
    }

    /**
     * <p>Used to construct a predefined label. The text passed is always loaded or
     * appended anytime setText(#) is invoked. The int determines the position (left or right)
     * of this permanent text.</p>
     */
    public static KLabel getPredefinedLabel(String permanentText, int position){
        return new KLabel(permanentText){
            @Override
            public void setText(String text) {
                if (position == SwingConstants.LEFT) {
                    super.setText(permanentText+text);
                } else if (position == SwingConstants.RIGHT) {
                    super.setText(text+permanentText);
                } else if (position == SwingConstants.CENTER) {
                    super.setText(permanentText.split(":")[0]+text+permanentText.split(":")[1]);
                }
            }

            @Override
            public void setText(int integer) {
                setText(String.valueOf(integer));
            }
        };
    }

    public static JToolTip preferredTip(){
        final JToolTip tip = new JToolTip();
        tip.setFont(KFontFactory.createPlainFont(14));
        tip.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));

        return tip;
    }

    public void setStyle(Font f, Color fg){
        this.setFont(f);
        this.setForeground(fg);
    }

    /**
     * <p>Underlines this label. The boolean param specifies whether the line is visible only on mouse-focus.</p>
     * <p><i>Notice this call sets the layout Border, and puts the Separator beneath the component.
     * Whence should not be called otherwise.</i></p>
     * <p>The line uses the Color param as its foreground. If 'null', it will assume the
     * caller's foreground instead.</p>
     */
    public void underline(Color bg, boolean onlyOnFocus){
        final KSeparator separator = new KSeparator(bg == null ? this.getForeground() : bg);
        this.setLayout(new BorderLayout());
        this.add(separator, BorderLayout.SOUTH);
        if (onlyOnFocus) {
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

}
