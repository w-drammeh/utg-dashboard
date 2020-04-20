package customs;

import java.awt.*;

/**
 * <h1>class KFontFactory</h1>
 * <p><b>Description</b>: One the custom classes of ease, the KFontFactory type inherits java.awt.Font, thus
 * making it easier, through its static methods, to bring a Font where needed without
 * instantiating the Font class directly.</p>
 */
public class KFontFactory {
    public static final String FONT_NAME = "Tahoma";


    public static Font createBoldFont(int size){
        return new Font(FONT_NAME, Font.BOLD, size);
    }

    public static Font createPlainFont(int size){
        return new Font(FONT_NAME, Font.PLAIN, size);
    }

    public static Font createItalicFont(int size){
        return new Font(FONT_NAME, Font.ITALIC, size);
    }

    public static Font createBoldItalic(int size){
        return new Font(FONT_NAME,Font.BOLD + Font.ITALIC,size);
    }


    /**
     * <p>A font commonly used by all those big texts that appear at the top the the body</p>
     */
    public static Font bodyHeaderFont(){
        return createBoldFont(20);
    }

}
