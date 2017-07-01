package ebolo.libma.management.commander.book.ui;

import ebolo.libma.assistant.ui.BotInterface;
import ebolo.libma.commons.commands.command.Command;
import ebolo.libma.management.ui.controllers.BooksViewController;
import javafx.application.Platform;

import java.io.IOException;

/**
 * Client's side command for adding new book to the system
 * This command requires UI triggering
 *
 * @author Ebolo
 * @version 28/06/2017
 * @see Command
 * @see ebolo.libma.management.commander.book.AddBookCommand
 * @since 28/06/2017
 */

public class UIAddBookCommand extends Command<String> {
    @Override
    public String call() throws Exception {
        BotInterface.getInstance().setAliasStatus("");
        BotInterface.getInstance().addText("Alias", "Opening book adding window...");
        Platform.runLater(() -> {
            try {
                BooksViewController.getInstance().addNewBook();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return "";
    }
}
