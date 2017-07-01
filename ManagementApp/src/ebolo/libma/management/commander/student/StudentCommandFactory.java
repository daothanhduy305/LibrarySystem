package ebolo.libma.management.commander.student;

import ebolo.libma.commons.commands.command.Command;
import ebolo.libma.commons.commands.factory.CommandFactory;

/**
 * Set of commands on student objects for librarian on client side
 *
 * @author Ebolo
 * @version 28/06/2017
 * @see CommandFactory
 * @since 13/06/2017
 */

public class StudentCommandFactory implements CommandFactory<String> {
    private static StudentCommandFactory ourInstance;
    
    private StudentCommandFactory() {
    }
    
    public static StudentCommandFactory getInstance() {
        if (ourInstance == null)
            ourInstance = new StudentCommandFactory();
        return ourInstance;
    }
    
    @Override
    public Command<String> getCommand(String command, Object... args) {
        switch (command) {
            case "add_student":
                return new AddStudentCommand(args);
            case "delete_students":
                return new DeleteStudentCommand(args);
        }
        return null;
    }
}
