package customs;

import main.Globals;
import main.MDate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * The standard dashboard text-field. One of the custom classes.
 */
public class KTextField extends JTextField implements Preference {


    public KTextField(String initial){
	    super(initial);
	    this.setPreferences();
    }

    public KTextField(){
        this("");
    }

    public KTextField(Dimension d){
        this();
        this.setPreferredSize(d);
    }

    /**
     * Provides a field that accepts all input (just like any other normal instance) until the
     * range of values are met.
     */
    public static KTextField rangeControlField(int range) {
        final KTextField smallField = new KTextField();
        smallField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (smallField.getText().length() >= range) {
                    e.consume();
                }
            }
        });
        return smallField;
    }

    /**
     * Provides a field that restricts its input to only numbers until the specified number of values are in.
     */
    public static KTextField digitPlusRangeControlField(int range) {
        final KTextField smallField = new KTextField();
        smallField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (smallField.getText().length() >= range || !Character.isDigit(e.getKeyChar()) &&
                        !(e.getKeyChar() == KeyEvent.VK_DELETE || e.getKeyChar() == KeyEvent.VK_BACK_SPACE)) {
                    e.consume();
                }
            }
        });
        return smallField;
    }

    public static KTextField newDayField(){
        final KTextField dayField = digitPlusRangeControlField(2);
        dayField.setPreferredSize(new Dimension(50,30));
        return dayField;
    }

    public static KTextField newMonthField(){
        return newDayField();
    }

    public static KTextField newYearField(){
        final KTextField yearField = digitPlusRangeControlField(4);
        yearField.setPreferredSize(new Dimension(75,30));
        yearField.setText(String.valueOf(MDate.thisYear()));
        return yearField;
    }

    public boolean hasText(){
        return Globals.hasText(this.getText());
    }

    public boolean hasNoText(){
        return !this.hasText();
    }

    public void setText(int n) {
        super.setText(String.valueOf(n));
    }

    @Override
    public JToolTip createToolTip() {
        return KLabel.preferredTip();
    }

    public void setPreferences() {
        this.setFont(KFontFactory.createPlainFont(15));
        this.setAutoscrolls(true);
        this.setHorizontalAlignment(SwingConstants.CENTER);
    }

}
