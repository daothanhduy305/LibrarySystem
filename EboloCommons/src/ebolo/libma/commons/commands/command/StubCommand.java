package ebolo.libma.commons.commands.command;

import com.sun.istack.internal.Nullable;
import ebolo.libma.commons.net.Message;
import ebolo.libma.commons.net.SocketWrapper;

/**
 * Internal stub command consists of a client, command arguments. It would be fed with a checking function
 * to check the correctness of the command, an onSuccess action and a illegalAction fallback
 *
 * @author Ebolo
 * @version 28/06/2017
 * @since 06/06/2017
 */

public abstract class StubCommand extends Command<String> {
    protected final SocketWrapper client;
    protected String failedReason = "Internal command error!";
    
    protected StubCommand(@Nullable SocketWrapper client, Object... args) {
        super(args);
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
    public String call() {
        synchronized (client) {
            try {
                if (checkCorrectness()) {
                    if (!legalAction())
                        throw new Exception();
                } else
                    throw new Exception();
                return "success";
            } catch (Exception e) {
                // TODO: wrong command -> fallback
                try {
                    illegalAction();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                return "failed";
            }
        }
    }
}
