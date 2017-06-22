package ebolo.libma.data.data.raw.book.utils;

/**
 * A helper class contains language enum type and conversion methods
 *
 * @author Ebolo
 * @version 11/06/2017
 * @since 11/06/2017
 */

public class BookLanguage {
    public static String getFullLangTerm(String shortLangTerm) {
        LANG lang = LANG.valueOf(shortLangTerm);
        switch (lang) {
            case en:
                return "English";
            case de:
                return "German";
            case es:
                return "Spanish";
            case fr:
                return "French";
            case it:
                return "Italian";
            case vi:
                return "Vietnamese";
            default:
                return "";
        }
    }
    
    public static LANG getLang(String term) {
        switch (term) {
            case "English":
                return LANG.en;
            case "German":
                return LANG.de;
            case "Spanish":
                return LANG.es;
            case "French":
                return LANG.fr;
            case "Italian":
                return LANG.it;
            case "Vietnamese":
                return LANG.vi;
            default:
                return LANG.en;
        }
    }
    
    public enum LANG {en, vi, de, fr, es, it}
}
