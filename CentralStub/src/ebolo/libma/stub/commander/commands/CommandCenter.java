package ebolo.libma.stub.commander.commands;

import com.sun.istack.internal.Nullable;
import ebolo.libma.commons.net.SocketWrapper;
import ebolo.libma.data.data.raw.user.utils.MetaInfo;
import ebolo.libma.stub.commander.commands.alias.AliasCommandFactory;
import ebolo.libma.stub.commander.commands.kernel.KernelCommandFactory;
import ebolo.libma.stub.commander.commands.librarian.LibrarianCommandFactory;
import ebolo.libma.stub.commander.commands.student.StudentCommandFactory;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * Central place where external commands would be translated/decapsulated
 *
 * @author Ebolo
 * @version 07/06/2017
 * @since 06/06/2017
 */

public class CommandCenter {
    private static CommandCenter ourInstance;
    /**
     * List of commands factory that can be used to produce internal command signals
     */
    private Map<MetaInfo.USER_MODE, CommandFactory> commandFactories;
    
    private CommandCenter() {
        commandFactories = new HashMap<>();
        commandFactories.put(MetaInfo.USER_MODE.Student, StudentCommandFactory.getInstance());
        commandFactories.put(MetaInfo.USER_MODE.Librarian, LibrarianCommandFactory.getInstance());
        commandFactories.put(MetaInfo.USER_MODE.Kernel, KernelCommandFactory.getInstance());
        commandFactories.put(MetaInfo.USER_MODE.Alias, AliasCommandFactory.getInstance());
    }
    
    public static CommandCenter getInstance() {
        if (ourInstance == null)
            ourInstance = new CommandCenter();
        return ourInstance;
    }
    
    /**
     * This method would take a Bson document command, decapsulates it and distribute it properly
     *
     * @param client          source of incoming external command
     * @param commandDocument command in Bson document
     */
    public void addNewCommand(@Nullable SocketWrapper client, Document commandDocument) {
        try {
            // get command type (to separate command/permissions based on user type)
            MetaInfo.USER_MODE userMode = (MetaInfo.USER_MODE) commandDocument.get("command_type");
            String commandString = (String) commandDocument.get("command"); // command keyword
            Object[] args = (Object[]) commandDocument.get("args"); // command arguments
            
            if (userMode == null || commandString.isEmpty() || args == null || args.length == 0)
                throw new ClassCastException(); // wrong command
            else {
                CommandProcessor.getInstance().addNewCommand(
                    commandFactories.get(userMode).getCommand(client, commandString, args)
                );
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
            // TODO: Illegal document as this is a potential cause of DDOS
        }
    }
}
