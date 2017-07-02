package ebolo.libma.data.db.local;

import ebolo.libma.commons.commands.CommandUtils;
import ebolo.libma.commons.net.StubCommunication;
import ebolo.libma.commons.ui.ViewStatus;
import ebolo.libma.data.data.raw.user.Student;
import ebolo.libma.data.data.raw.user.utils.MetaInfo;
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
    
    @Override
    public void syncStub() {
        new Thread(() -> {
            ViewStatus.getInstance().updateStatus("Loading local db...");
            StudentListManager.getInstance().loadLocal();
            ViewStatus.getInstance().updateStatus("Ready.");
            CommandUtils.sendCommand(
                MetaInfo.USER_MODE.Kernel,
                StubCommunication.getInstance().getStub(),
                "request_update",
                StudentListManager.getInstance().getCurrentVersion(),
                "student"
            );
        }).start();
    }
}
