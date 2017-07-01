package ebolo.libma.stub.commander.commands.kernel;

import ebolo.libma.commons.commands.command.StubCommand;
import ebolo.libma.commons.net.Message;
import ebolo.libma.commons.net.SocketWrapper;

/**
 * StubCommand for a client requiring his/her info
 *
 * @author Ebolo
 * @version 20/06/2017
 * @see StubCommand
 * @since 20/06/2017
 */

public class ClientIdReq extends StubCommand {
    
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
