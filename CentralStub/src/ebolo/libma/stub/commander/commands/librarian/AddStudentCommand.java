package ebolo.libma.stub.commander.commands.librarian;

import ebolo.libma.commons.commands.command.StubCommand;
import ebolo.libma.commons.net.Message;
import ebolo.libma.commons.net.SocketWrapper;
import ebolo.libma.data.data.raw.user.Student;
import ebolo.libma.stub.db.DbPortal;
import ebolo.libma.stub.db.updates.UpdateFactory;
import ebolo.libma.stub.net.managers.ActiveUserManager;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Collections;

/**
 * StubCommand for librarian add new student
 *
 * @author Ebolo
 * @version 13/06/2017
 * @see StubCommand
 * @since 13/06/2017
 */

public class AddStudentCommand extends StubCommand {
    private Student student;
    
    AddStudentCommand(SocketWrapper client, Object[] args) {
        super(client, args);
    }
    
    @Override
    protected void notifyCommandResult() {
    
    }
    
    @Override
    protected boolean checkCorrectness() {
        try {
            if (args.length == 1) {
                student = (Student) args[0];
                return true;
            }
            throw new Exception();
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    protected boolean legalAction() throws Exception {
        // convert student into Bson document
        Document studentDocument = student.toMongoDocument();
        Document query = new Document("auth.username", student.getUsername());
        // check if username is used
        if (DbPortal.getInstance().getUserDb().find(query).first() == null) {
            studentDocument.put("last_modified", System.currentTimeMillis());
            DbPortal.getInstance().getUserDb().insertOne(studentDocument);
            ObjectId studentObjectId = studentDocument.getObjectId("_id");
            // if everything is ok then send back success message to client
            if (studentObjectId != null) {
                client.sendMessage(Message.messageGenerate("success", studentObjectId.toString()));
                ActiveUserManager.getInstance().sendMessageToAllLibrarians(client.getClientId(), UpdateFactory.createUpdate(
                    Collections.singletonList(studentDocument), "student"
                ));
                return true;
            } else
                failedReason = "Something went wrong. Cannot insert new student into database!";
        } else
            failedReason = "A user with username " + '\"' + student.getUsername() + '\"' + " exists in database!";
        return false;
    }
}
