package ebolo.libma.management.commander.student;

import ebolo.libma.commons.commands.CommandUtils;
import ebolo.libma.commons.commands.command.Command;
import ebolo.libma.commons.net.StubCommunication;
import ebolo.libma.data.data.raw.user.Student;
import ebolo.libma.data.data.raw.user.utils.MetaInfo;
import ebolo.libma.data.data.ui.user.StudentUIWrapper;
import ebolo.libma.data.db.local.StudentListManager;
import org.bson.Document;

/**
 * Client's side command for adding new student to the system
 *
 * @author Ebolo
 * @version 28/06/2017
 * @see Command
 * @since 28/06/2017
 */

public class AddStudentCommand extends Command<String> {
    private Student student;
    
    AddStudentCommand(Object[] args) {
        super(args);
        student = (Student) args[0];
    }
    
    @Override
    public String call() throws Exception {
        if (CommandUtils.sendCommand(
            MetaInfo.USER_MODE.Librarian,
            StubCommunication.getInstance().getStub(),
            "add_student", student)) {
            Document response = StubCommunication
                .getInstance()
                .getStub()
                .getMessageBuffer()
                .take();
            if (!response.getString("message").equals("failed")) {
                StudentListManager.getInstance().add(
                    student,
                    new StudentUIWrapper(student, response.getString("package"))
                );
                return "success";
            } else
                return response.getString("package");
        }
        return "Server is not available";
    }
}
