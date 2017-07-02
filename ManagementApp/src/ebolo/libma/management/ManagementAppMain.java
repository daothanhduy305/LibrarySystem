package ebolo.libma.management;

import ebolo.libma.assistant.Alias;
import ebolo.libma.authenticate.UserAuthenticationUI;
import ebolo.libma.commons.commands.proc.SingleCommandProcessor;
import ebolo.libma.commons.net.StubCommunication;
import ebolo.libma.commons.ui.ScreenUtils;
import ebolo.libma.commons.ui.UIFactory;
import ebolo.libma.commons.ui.utils.ControllerWrapper;
import ebolo.libma.commons.ui.utils.WindowMonitor;
import ebolo.libma.data.data.raw.book.Book;
import ebolo.libma.data.data.raw.transaction.TransactionWrapper;
import ebolo.libma.data.data.raw.user.Student;
import ebolo.libma.data.data.raw.user.utils.MetaInfo;
import ebolo.libma.data.data.ui.book.BookUIWrapper;
import ebolo.libma.data.data.ui.transaction.TransactionUIWrapper;
import ebolo.libma.data.data.ui.user.StudentUIWrapper;
import ebolo.libma.data.db.local.BookListManager;
import ebolo.libma.data.db.local.StudentListManager;
import ebolo.libma.data.db.local.TransactionListManager;
import ebolo.libma.management.commander.CentralCommandFactory;
import ebolo.libma.management.ui.controllers.AppMainController;
import ebolo.libma.management.ui.controllers.BooksViewController;
import ebolo.libma.management.ui.controllers.StudentsViewController;
import ebolo.libma.management.ui.controllers.TransactionsViewController;
import ebolo.libma.management.ui.fxml.FxmlManager;
import ebolo.libma.management.utils.configs.AppConfigurations;
import javafx.application.Application;
import javafx.stage.Stage;


/**
 * Main class for management application
 *
 * @author Ebolo
 * @version 06/06/2017
 * @since 06/06/2017
 */

public class ManagementAppMain extends Application {
    
    public static void main(String[] args) {
        StubCommunication.getInstance().startStubCommunication();
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        // preparing controllers
        AppMainController appMainController = AppMainController.getInstance();
        BooksViewController booksViewController = BooksViewController.getInstance();
        StudentsViewController studentsViewController = StudentsViewController.getInstance();
        TransactionsViewController transactionsViewController = TransactionsViewController.getInstance();
        // creating window and set up UI
        primaryStage = UIFactory.createWindow(
            "Library Management",
            FxmlManager.getInstance().getFxmlTemplate("AppMainFXML"),
            null, ScreenUtils.getScreenWidth() * 0.8, ScreenUtils.getScreenHeight() * 0.8,
            new ControllerWrapper(appMainController.getClass(), appMainController),
            new ControllerWrapper(booksViewController.getClass(), booksViewController),
            new ControllerWrapper(studentsViewController.getClass(), studentsViewController),
            new ControllerWrapper(transactionsViewController.getClass(), transactionsViewController)
        );
        primaryStage.setMaximized(true);
        WindowMonitor.getInstance().setWindow(primaryStage);
        appMainController.setUpUI();
        primaryStage.setOnCloseRequest(event -> close());
        
        final Stage mainStage = primaryStage;
        // show authentication dialog
        UserAuthenticationUI.showAndAuthenticate(
            StubCommunication.getInstance().getStub(),
            MetaInfo.USER_MODE.Librarian,
            () -> { // onSuccess
                mainStage.show();
                
                // set up all list managers and their relevant monitoring threads
                BookListManager.getInstance().setInfo(
                    "Book", AppConfigurations.getWorkingDir(), Book::new, BookUIWrapper::new
                );
                StudentListManager.getInstance().setInfo(
                    "Student", AppConfigurations.getWorkingDir(), Student::new, StudentUIWrapper::new
                );
                TransactionListManager.getInstance().setInfo(
                    "Transaction", AppConfigurations.getWorkingDir(), TransactionWrapper::new, TransactionUIWrapper::new
                );
    
                StubCommunication.getInstance().startBookMonitorThreads();
                StubCommunication.getInstance().startStudentMonitorThreads();
                StubCommunication.getInstance().startTransactionMonitorThreads();
                
                // request updates from database (if there is any)
                BookListManager.getInstance().syncStub();
                StudentListManager.getInstance().syncStub();
                TransactionListManager.getInstance().syncStub();
    
                new Thread(() -> Alias.getInstance().setExtendedFactory(CentralCommandFactory.getInstance())).start();
            },
            this::close // onExit
        );
    }
    
    private void close() {
        StubCommunication.getInstance().stopStubCommunication();
        SingleCommandProcessor.getInstance().shutdown();
    }
}
