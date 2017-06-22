package ebolo.libma.client.commander;

import ebolo.libma.client.utils.session.SessionInfo;
import ebolo.libma.commons.commands.CommandProcessor;
import ebolo.libma.commons.commands.CommandUtils;
import ebolo.libma.commons.commands.Commands;
import ebolo.libma.commons.net.StubCommunication;
import ebolo.libma.data.data.raw.transaction.Transaction;
import ebolo.libma.data.data.raw.transaction.TransactionWrapper;
import ebolo.libma.data.data.raw.user.utils.MetaInfo;
import ebolo.libma.data.data.ui.transaction.TransactionUIWrapper;
import ebolo.libma.data.db.local.TransactionListManager;
import org.bson.Document;

import java.util.concurrent.Future;

/**
 * Set of commands for student on client side
 *
 * @author Ebolo
 * @version 13/06/2017
 * @see Commands
 * @since 13/06/2017
 */

public class StudentCommands extends Commands {
    /**
     * Command reserving a book
     *
     * @param bookObjId book object id
     * @return a string indicates if the result is success or a failure statement
     */
    public static Future<String> reserveBook(String bookObjId) {
        return CommandProcessor.getInstance().getExecutorService().submit(() -> {
            if (CommandUtils.sendCommand(
                MetaInfo.USER_MODE.Student,
                StubCommunication.getInstance().getStub(),
                "reserve_book",
                new Transaction(SessionInfo.getInstance().getStudentObjId(), bookObjId)
            )) {
                Document returnMessage = StubCommunication.getInstance().getStub().getMessageBuffer().take();
                if (returnMessage.getString("message").equals("success")) {
                    TransactionWrapper transactionWrapper = (TransactionWrapper) returnMessage.get("package");
                    TransactionListManager.getInstance().add(
                        transactionWrapper,
                        new TransactionUIWrapper(transactionWrapper, transactionWrapper.getObjectId())
                    );
                    return "success";
                }
                return returnMessage.getString("package");
            }
            return "Server is not available";
        });
    }
}
