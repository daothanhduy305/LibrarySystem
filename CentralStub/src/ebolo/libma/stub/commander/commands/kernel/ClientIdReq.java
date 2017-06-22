package ebolo.libma.stub.commander.commands.kernel;

import ebolo.libma.commons.net.Message;
import ebolo.libma.commons.net.SocketWrapper;
import ebolo.libma.stub.commander.commands.Command;

/**
 * Command for a client requiring his/her info
 *
 * @author Ebolo
 * @version 20/06/2017
 * @see Command
 * @since 20/06/2017
 */

public class ClientIdReq extends Command {
    
    ClientIdReq(SocketWrapper client, Object[] args) {
        super(client, args);
    }
    
    @Override
    protected void notifyCommandResult() {
    
    }
    
    @Override
    protected boolean checkCorrectness() {
        return true;
    }
    
    @Override
    protected boolean legalAction() throws Exception {
        return client.sendMessage(Message.messageGenerate("message", client.getClientId()));
        // TODO: send info in need
    }
}
