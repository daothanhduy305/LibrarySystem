package ebolo.libma.data.db.local;

import ebolo.libma.data.data.raw.user.Student;
import ebolo.libma.data.data.ui.user.StudentUIWrapper;

/**
 * This is the implementation class of the <code>ListManager</code> for managing raw data student and its ui wrapper
 *
 * @author Ebolo
 * @version 21/06/2017
 * @see ListManager
 * @since 20/06/2017
 */

public class StudentListManager extends ListManager<Student, StudentUIWrapper> {
    private static StudentListManager ourInstance;
    
    public static StudentListManager getInstance() {
        if (ourInstance == null)
            ourInstance = new StudentListManager();
        return ourInstance;
    }
}
