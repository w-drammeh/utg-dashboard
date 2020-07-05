package customs;

import javax.swing.*;

public class KTextPane extends JTextPane implements Preference {
    public static final String PLAIN_TYPE = "text/plain";
    public static final String HTML_TYPE = "text/html";


    public KTextPane(String type, String text){
        super();
        this.setContentType(type);
        this.setText(text);
        this.setPreferences();
    }

    public static KTextPane wantHtmlFormattedPane(String htmlText){
        final String formattedText = "<!DOCTYPE html> <html> <head> <style> body {font-size: 12px; font-family: Tahoma;}" +
                "</style> </head> <body>" +
                htmlText +
                "</body> </html>";
        return new KTextPane("text/html", formattedText);
    }

    public void setPreferences(){
        this.setEditable(false);
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.setFont(KFontFactory.createPlainFont(15));//In case of non-HTML
    }

}
