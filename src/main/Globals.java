package main;

public class Globals {
//    Milli-seconds equivalent to these measures
    public static final int SECOND = 1_000;
    public static final int MINUTE = 60 * SECOND;
    public static final int HOUR = 60 * MINUTE;
    public static final int DAY = 24 * HOUR;
    public static final String NONE = "None";
    public static final String UNKNOWN = "Unknown";


    public static String toFourth(double cgp){
        final String t = String.valueOf(cgp);
        return t.length() <= 6 ? t : t.substring(0,6);
    }

    /**
     * The passed string must be in plural format!
     * This is only compatible with words ending with 's' in plural form.
     */
    public static String checkPlurality(int i, String text) {
        if (i == 0) {
            return "No "+text;
        } else if (i == 1) {
            return "1 " + text.substring(0, text.length() - 1);
        } else {
            return String.join(" ", String.valueOf(i), text);
        }
    }

    /**
     * Convenient way of !Globals.isBlank(String)
     */
    public static boolean hasText(String t){
        return !isBlank(t);
    }

    public static boolean isBlank(String t){
        if (t == null) {
            return true;
        }
        for (char c : t.toCharArray()) {
            if (!Character.isWhitespace(c)) {
                return false;
            }
        }
        return true;
    }

}
