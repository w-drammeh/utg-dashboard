package main;

/**
 * <h1>class SpecialClass</h1>
 */
public class Globals {
    //Milli-seconds equivalent to these measures
    public static final int SECOND_IN_MILLI = 1_000;
    public static final int MINUTE_IN_MILLI = 60 * SECOND_IN_MILLI;
    public static final int HOUR_IN_MILLI = 60 * MINUTE_IN_MILLI;
    public static final int DAY_IN_MILLI = 24 * HOUR_IN_MILLI;
    public static final int PORTAL_WAIT_TIME = 50;


    /**
     * This is intended for CGPs in the analysis only! Dashboard does not compute the real CGP, remember?
     */
    public static String toFourth(double cgp){
        final String t = String.valueOf(cgp);
        return t.length() <= 6 ? t : t.substring(0,6);
    }

    /**
     * The passed string must be in plural! Use this for only Whole Numbers.
     * This is only compatible with words ending with 's' as their plural form
     */
    public static String checkPlurality(int i, String pluralText) {
        if (i == 0) {
            return "No "+pluralText;
        } else if (i == 1) {
            return "1 " + pluralText.substring(0, pluralText.length() - 1);
        } else {
            return i+" "+pluralText;
        }
    }

    /**
     * <p><b>consequences:</b></p>
     * <p>Split 'string' regex=" "</p>
     * <p>All startings shall capitalize</p>
     * <p>All others shall not (if)</p>
     */
    public static String toTitleCase(String string){
        final StringBuilder builder = new StringBuilder();
        final String[] a = string.split(" ");
        for (String temp : a) {
            builder.append(temp.charAt(0)).append(temp.substring(1));
        }

        return builder.toString();
    }

    public static boolean hasText(String t){
        return !(t == null || isBlank(t));
    }

    /**
     * Unless otherwise necessary, it's preferred to call hasText(£) first.
     * This method should be generally obsolete as it is inherently dangerous.
     */
    public static boolean isBlank(String t){
        if (t == null) {
            throw new IllegalArgumentException();
        }

        for (char c : t.toCharArray()) {
            if (!Character.isWhitespace(c)) {
                return false;
            }
        }
        return true;
    }

}
