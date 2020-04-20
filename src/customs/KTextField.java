package customs;

import main.Globals;
import main.MDate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * <h1>class KTextField</h1>
 * The standard dashboard text-field. One of the custom classes,
 *
 * Notice the restrictive fields are ignorant of pasting!
 */
public class KTextField extends JTextField{


	public KTextField(){
        super();
        this.setPreferences();
    }

    public KTextField(Dimension d){
        super();
        this.setPreferences();
        this.setPreferredSize(d);
    }

    public KTextField(String initial){
	    super(initial);
	    this.setPreferences();
    }

    /**
     * <p>Provides a field that accepts all input (just like any other normal instance) until the
     * range of values are met.</p>
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
     * <p>Provides a field that restricts its input to only numbers until the specified number of values are in.</p>
     */
    public static KTextField digitPlusRangeControlField(int range) {
        final KTextField smallField = new KTextField();
        smallField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (smallField.getText().length() >= range || !Character.isDigit(e.getKeyChar()) && !(e.getKeyChar() == KeyEvent.VK_DELETE || e.getKeyChar() == KeyEvent.VK_BACK_SPACE)) {
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

    public void setText(int t) {
        super.setText(String.valueOf(t));
    }

    @Override
    public JToolTip createToolTip() {
        return KLabel.preferredTip();
    }

    private void setPreferences() {
        this.setFont(KFontFactory.createPlainFont(15));
        this.setAutoscrolls(true);
        this.setHorizontalAlignment(SwingConstants.CENTER);
    }

}
