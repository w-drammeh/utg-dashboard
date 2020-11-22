package customs;

import main.Globals;
import main.MComponent;
import main.MDate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * The standard dashboard text-field. One of the custom classes.
 * Note that control-fields suspend any form of pasting.
 */
public class KTextField extends JTextField implements Preference {


    public KTextField(){
        super();
        setPreferences();
    }

    public KTextField(String initialText){
	    super(initialText);
	    setPreferences();
    }

    public KTextField(Dimension preferredSize){
        this();
        setPreferredSize(preferredSize);
    }

    /**
     * Provides a field that accepts all input (like any other normal instance) until the
     * range of values are met.
     */
    public static KTextField rangeControlField(int range) {
        final KTextField rangeField = new KTextField() {
            @Override
            public void paste() {
                UIManager.getLookAndFeel().provideErrorFeedback(this);
            }
        };

        rangeField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (rangeField.getText().length() >= range) {
                    e.consume();
                }
            }
        });

        return rangeField;
    }

    /**
     * Provides a rangeControlField(int) that restricts its input to only numbers.
     */
    public static KTextField digitPlusRangeControlField(int range) {
        final KTextField digitField = new KTextField(){
            @Override
            public void paste() {
                UIManager.getLookAndFeel().provideErrorFeedback(this);
            }
        };

        digitField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (digitField.getText().length() >= range || !Character.isDigit(e.getKeyChar()) &&
                        !(e.getKeyChar() == KeyEvent.VK_DELETE || e.getKeyChar() == KeyEvent.VK_BACK_SPACE)) {
                    e.consume();
                }
            }
        });

        return digitField;
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

    public boolean hasText() {
        return Globals.hasText(getText());
    }

    public boolean isBlank() {
        return Globals.isBlank(getText());
    }

    public void setText(int n) {
        setText(Integer.toString(n));
    }

    @Override
    public JToolTip createToolTip() {
        return MComponent.preferredTip();
    }

    public void setPreferences() {
        setFont(KFontFactory.createPlainFont(15));
        setHorizontalAlignment(SwingConstants.CENTER);
        setAutoscrolls(true);
    }

}
