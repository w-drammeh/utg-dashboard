package customs;

import java.awt.*;

public abstract class KFontFactory implements Preference {
    public static final String FONT_NAME = "Tahoma";


    public static Font createPlainFont(int size){
        return new Font(FONT_NAME, Font.PLAIN, size);
    }

    public static Font createBoldFont(int size){
        return new Font(FONT_NAME, Font.BOLD, size);
    }

    public static Font createItalicFont(int size){
        return new Font(FONT_NAME, Font.ITALIC, size);
    }

    public static Font createBoldItalic(int size){
        return new Font(FONT_NAME, Font.BOLD + Font.ITALIC, size);
    }

    /**
     * A font commonly used by all those big texts that appear at the top of the body.
     * Normally, at the top-left
     */
    public static Font bodyHeaderFont(){
        return createPlainFont(20);
    }

}
