package ebolo.libma.stub.commander.commands;

import com.sun.istack.internal.Nullable;
import ebolo.libma.commons.net.Message;
import ebolo.libma.commons.net.SocketWrapper;

/**
 * Internal command. An internal command consists of a client, command arguments. It would be fed with a checking function
 * to check the correctness of the command, an onSuccess action and a illegalAction fallback
 *
 * @author Ebolo
 * @version 06/06/2017
 * @since 06/06/2017
 */

public abstract class Command implements Runnable {
    protected final SocketWrapper client;
    protected Object[] args;
    protected String failedReason = "Internal command error!";
    
    protected Command(@Nullable SocketWrapper client, Object[] args) {
        this.args = args;
        this.client = client;
    }
    
    protected abstract void notifyCommandResult();
    
    protected abstract boolean checkCorrectness();
    
    protected abstract boolean legalAction() throws Exception;
    
    protected void illegalAction() throws Exception {
        // By default, it would send out a failed message with failing reason
        client.sendMessage(Message.messageGenerate("failed", failedReason));
    }
    
    @Override
    public void run() {
        synchronized (client) {
            try {
                if (checkCorrectness()) {
                    if (!legalAction())
                        throw new Exception();
                } else
                    throw new Exception();
            } catch (Exception e) {
                // TODO: wrong command -> fallback
                try {
                    illegalAction();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
