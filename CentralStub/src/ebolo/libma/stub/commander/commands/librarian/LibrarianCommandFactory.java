package ebolo.libma.stub.commander.commands.librarian;

import ebolo.libma.commons.net.SocketWrapper;
import ebolo.libma.stub.commander.commands.Command;
import ebolo.libma.stub.commander.commands.CommandFactory;

/**
 * Librarian command factory manages available set of internal commands for librarian users only
 *
 * @author Ebolo
 * @version 07/06/2017
 * @see CommandFactory
 * @since 06/06/2017
 */

public class LibrarianCommandFactory implements CommandFactory {
    private static LibrarianCommandFactory ourInstance;
    
    private LibrarianCommandFactory() {
    }
    
    public static LibrarianCommandFactory getInstance() {
        if (ourInstance == null)
            ourInstance = new LibrarianCommandFactory();
        return ourInstance;
    }
    
    @Override
    public Command getCommand(SocketWrapper client, String command, Object[] args) {
        switch (command) {
            case "add_book":
                return new AddBookCommand(client, args);
            case "remove_books":
                return new RemoveBooksCommand(client, args);
            case "add_student":
                return new AddStudentCommand(client, args);
            case "remove_students":
                return new RemoveStudentsCommand(client, args);
            case "modify_book":
                return new ModifyCommand(client, args);
        }
        return null;
    }
}
