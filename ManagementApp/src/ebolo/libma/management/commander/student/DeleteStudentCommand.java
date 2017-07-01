package ebolo.libma.management.commander.student;

import ebolo.libma.commons.commands.CommandUtils;
import ebolo.libma.commons.commands.command.Command;
import ebolo.libma.commons.net.StubCommunication;
import ebolo.libma.commons.ui.UIFactory;
import ebolo.libma.data.data.raw.user.utils.MetaInfo;
import ebolo.libma.data.data.ui.user.StudentUIWrapper;
import ebolo.libma.data.db.local.StudentListManager;
import javafx.scene.control.Alert;
import org.bson.Document;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Client's side command for deleting student(s) from the system
 *
 * @author Ebolo
 * @version 28/06/2017
 * @see Command
 * @since 28/06/2017
 */

public class DeleteStudentCommand extends Command<String> {
    private List<StudentUIWrapper> deletingStudents;
    
    @SuppressWarnings("unchecked")
    DeleteStudentCommand(Object[] args) {
        super(args);
        deletingStudents = (List<StudentUIWrapper>) args[0];
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public String call() throws Exception {
        if (CommandUtils.sendCommand(
            MetaInfo.USER_MODE.Librarian,
            StubCommunication.getInstance().getStub(),
            "remove_students",
            deletingStudents.parallelStream().map(StudentUIWrapper::getObjectId).collect(Collectors.toList())
        )) {
            Document response = StubCommunication
                .getInstance()
                .getStub()
                .getMessageBuffer()
                .take();
            if (!response.getString("message").equals("failed")) {
                List<String> deletedStudents = (List<String>) response.get("package");
                
                UIFactory.showAnnouncement(
                    Alert.AlertType.INFORMATION,
                    "Deleted successfully: " + deletedStudents.size() + '/' + deletingStudents.size()
                );
                
                StudentListManager.getInstance().delete(
                    deletingStudents
                        .parallelStream()
                        .filter(bookUIWrapper -> deletedStudents
                            .contains(bookUIWrapper.getObjectId())
                        ).collect(Collectors.toList())
                );
                return "success";
            } else
                return response.getString("package");
        }
        return "Server is not available";
    }
}
