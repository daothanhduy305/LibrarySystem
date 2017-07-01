package ebolo.libma.client.commander.commands;

import ebolo.libma.client.utils.session.SessionInfo;
import ebolo.libma.commons.commands.CommandUtils;
import ebolo.libma.commons.commands.command.Command;
import ebolo.libma.commons.net.StubCommunication;
import ebolo.libma.data.data.raw.transaction.Transaction;
import ebolo.libma.data.data.raw.transaction.TransactionWrapper;
import ebolo.libma.data.data.raw.user.utils.MetaInfo;
import ebolo.libma.data.data.ui.transaction.TransactionUIWrapper;
import ebolo.libma.data.db.local.TransactionListManager;
import org.bson.Document;

/**
 * StubCommand reserving a book
 *
 * @author Ebolo
 * @version 28/06/2017
 * @see Command
 * @since 28/06/2017
 */

public class ReserveBookCommand extends Command<String> {
    private String bookObjId;
    
    public ReserveBookCommand(Object[] args) {
        super(args);
        bookObjId = (String) args[0];
    }
    
    @Override
    public String call() throws Exception {
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
    }
}
