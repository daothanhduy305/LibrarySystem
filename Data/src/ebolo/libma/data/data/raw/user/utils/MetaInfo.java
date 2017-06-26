package ebolo.libma.data.data.raw.user.utils;

/**
 * This is the helper class contains meta info that might be necessary for user entity
 *
 * @author Ebolo
 * @version 06/06/2017
 * @since 06/06/2017
 */

public class MetaInfo {
    public enum USER_MODE {Student, Librarian, Kernel, Alias}
    
    public enum STUDENT_COURSE {
        Computer_Science,
        Electrical_Engineering,
        Finance_Accounting,
        Mechanics,
        Business_Administration
    }
}
