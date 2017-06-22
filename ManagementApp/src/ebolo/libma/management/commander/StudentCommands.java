package ebolo.libma.management.commander;

import ebolo.libma.commons.commands.CommandProcessor;
import ebolo.libma.commons.commands.CommandUtils;
import ebolo.libma.commons.commands.Commands;
import ebolo.libma.commons.net.StubCommunication;
import ebolo.libma.commons.ui.UIFactory;
import ebolo.libma.data.data.raw.user.Student;
import ebolo.libma.data.data.raw.user.utils.MetaInfo;
import ebolo.libma.data.data.ui.user.StudentUIWrapper;
import ebolo.libma.data.db.local.StudentListManager;
import javafx.scene.control.Alert;
import org.bson.Document;

import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Set of commands on student objects for librarian on client side
 *
 * @author Ebolo
 * @version 13/06/2017
 * @see Commands
 * @since 13/06/2017
 */

public class StudentCommands extends Commands {
    
    /**
     * Command adding new student
     *
     * @param student adding student
     * @return a string indicates if the result is success or a failure statement
     */
    public static Future<String> addNewStudent(final Student student) {
        return CommandProcessor.getInstance().getExecutorService().submit(() -> {
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
        });
    }
    
    /**
     * Command removing students
     *
     * @param deletingStudents list of deleting students
     * @return a string indicates if the result is success or a failure statement
     */
    @SuppressWarnings("unchecked")
    public static Future<String> removeStudent(final List<StudentUIWrapper> deletingStudents) {
        return CommandProcessor.getInstance().getExecutorService().submit(() -> {
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
        });
    }
}
