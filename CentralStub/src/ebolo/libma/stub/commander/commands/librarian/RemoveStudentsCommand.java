package ebolo.libma.stub.commander.commands.librarian;

import ebolo.libma.commons.net.Message;
import ebolo.libma.commons.net.SocketWrapper;
import ebolo.libma.stub.commander.commands.Command;
import ebolo.libma.stub.db.DbPortal;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

/**
 * Command for librarian removing a list of students
 *
 * @author Ebolo
 * @version 13/06/2017
 * @see Command
 * @since 13/06/2017
 */

public class RemoveStudentsCommand extends Command {
    private List<String> studentsObjIds;
    
    RemoveStudentsCommand(SocketWrapper client, Object[] args) {
        super(client, args);
    }
    
    @Override
    protected void notifyCommandResult() {
    
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected boolean checkCorrectness() {
        try {
            if (args.length == 1) {
                studentsObjIds = (List<String>) args[0];
                return true;
            }
            throw new Exception();
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    protected boolean legalAction() throws Exception {
        List<String> resultList = new ArrayList<>(studentsObjIds.size());
        
        studentsObjIds.forEach(s -> {
            if (DbPortal.getInstance().getUserDb()
                .deleteOne(new Document("_id", new ObjectId(s)))
                .getDeletedCount() > 0) {
                resultList.add(s);
            }
        });
        
        if (resultList.size() > 0) {
            client.sendMessage(Message.messageGenerate("success", resultList));
            return true;
        } else {
            failedReason = "Cannot delete student(s) from database!";
            return false;
        }
    }
}
