package proto;

import java.awt.*;

public abstract class KFontFactory implements Preference {
    public static final String FONT_NAME = "Tahoma";
    public static final Font BODY_HEAD_FONT = createPlainFont(20);

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

}
