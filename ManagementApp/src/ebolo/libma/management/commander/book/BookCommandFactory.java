package ebolo.libma.management.commander.book;

import ebolo.libma.commons.commands.command.Command;
import ebolo.libma.commons.commands.factory.CommandFactory;
import ebolo.libma.management.commander.book.ui.UIAddBookCommand;

/**
 * Set of commands on book objects for librarian on client side
 *
 * @author Ebolo
 * @version 28/06/2017
 * @see CommandFactory
 * @since 10/06/2017
 */

public class BookCommandFactory implements CommandFactory<String> {
    private static BookCommandFactory ourInstance;
    
    private BookCommandFactory() {
    }
    
    public static BookCommandFactory getInstance() {
        if (ourInstance == null)
            ourInstance = new BookCommandFactory();
        return ourInstance;
    }
    
    @Override
    public Command<String> getCommand(String command, Object... args) {
        switch (command) {
            case "ui_add_book":
                return new UIAddBookCommand();
            case "add_book":
                return new AddBookCommand(args);
            case "delete_books":
                return new DeleteBooksCommand(args);
            case "modify_book":
                return new ModifyBookCommand(args);
        }
        return null;
    }
}
