package ebolo.libma.commons.utils;

/**
 * Helper class with some conversion method related to time
 *
 * @author Ebolo
 * @version 11/06/2017
 * @since 11/06/2017
 */

public class Time {
    public static String milisToString(int milis) {
        switch (milis) {
            case 86400:
                return "Day(s)";
            case 604800:
                return "Week(s)";
            case 2629743:
                return "Month(s)";
            default:
                return "";
        }
    }
}
