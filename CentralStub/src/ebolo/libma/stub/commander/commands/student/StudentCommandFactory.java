package ebolo.libma.stub.commander.commands.student;

import com.sun.istack.internal.Nullable;
import ebolo.libma.commons.net.SocketWrapper;
import ebolo.libma.stub.commander.commands.Command;
import ebolo.libma.stub.commander.commands.CommandFactory;

/**
 * Student command factory manages available set of internal commands for student users only
 *
 * @author Ebolo
 * @version 07/06/2017
 * @see CommandFactory
 * @since 06/06/2017
 */

public class StudentCommandFactory implements CommandFactory {
    private static StudentCommandFactory ourInstance;
    
    private StudentCommandFactory() {
    }
    
    public static StudentCommandFactory getInstance() {
        if (ourInstance == null)
            ourInstance = new StudentCommandFactory();
        return ourInstance;
    }
    
    @Override
    public Command getCommand(@Nullable SocketWrapper client, String command, Object[] args) {
        switch (command) {
            case "reserve_book":
                return new ReserveCommand(client, args);
        }
        return null;
    }
}
