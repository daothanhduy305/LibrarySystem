package ebolo.libma.commons.commands.command;

import java.util.concurrent.Callable;

/**
 * Command is a custom Callable with list of arguments fed in
 *
 * @author Ebolo
 * @version 28/06/2017
 * @since 28/06/2017
 */

public abstract class Command<V> implements Callable<V> {
    protected Object[] args;
    
    protected Command(Object... args) {
        this.args = args;
    }
}
