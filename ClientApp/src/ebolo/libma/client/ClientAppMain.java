package ebolo.libma.client;

import ebolo.libma.authenticate.UserAuthenticationUI;
import ebolo.libma.client.ui.controllers.AppMainController;
import ebolo.libma.client.ui.controllers.BooksViewController;
import ebolo.libma.client.ui.controllers.StudentsViewController;
import ebolo.libma.client.ui.fxml.FxmlManager;
import ebolo.libma.client.utils.configs.AppConfigurations;
import ebolo.libma.client.utils.session.SessionInfo;
import ebolo.libma.commons.commands.CommandUtils;
import ebolo.libma.commons.commands.proc.SingleCommandProcessor;
import ebolo.libma.commons.net.StubCommunication;
import ebolo.libma.commons.ui.ScreenUtils;
import ebolo.libma.commons.ui.UIFactory;
import ebolo.libma.commons.ui.utils.ControllerWrapper;
import ebolo.libma.data.data.raw.book.Book;
import ebolo.libma.data.data.raw.user.utils.MetaInfo;
import ebolo.libma.data.data.ui.book.BookUIWrapper;
import ebolo.libma.data.db.local.BookListManager;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main class for client application
 *
 * @author Ebolo
 * @version 06/06/2017
 * @since 06/06/2017
 */

public class ClientAppMain extends Application {
    
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
        // creating window and set up UI
        primaryStage = UIFactory.createWindow(
            "Library client",
            FxmlManager.getInstance().getFxmlTemplate("AppMainFXML"),
            null, ScreenUtils.getScreenWidth() * 0.8, ScreenUtils.getScreenHeight() * 0.8,
            new ControllerWrapper(appMainController.getClass(), appMainController),
            new ControllerWrapper(booksViewController.getClass(), booksViewController),
            new ControllerWrapper(studentsViewController.getClass(), studentsViewController)
        );
        primaryStage.setMaximized(true);
        appMainController.setUpUI();
        primaryStage.setOnCloseRequest(event -> close());
        
        final Stage mainStage = primaryStage;
        // show authentication dialog
        UserAuthenticationUI.showAndAuthenticate(
            StubCommunication.getInstance().getStub(),
            MetaInfo.USER_MODE.Student,
            () -> { // on success
                mainStage.show();
                new Thread(() -> {
                    try {
                        // set up session info
                        if (CommandUtils.sendCommand(
                            MetaInfo.USER_MODE.Kernel,
                            StubCommunication.getInstance().getStub(),
                            "request_id",
                            ""
                        ))
                            SessionInfo.getInstance().setStudentObjId(
                                StubCommunication.getInstance().getStub().getMessageBuffer().take().getString("package")
                            );
                        // set up book list manager and its monitoring thread
                        BookListManager.getInstance().setInfo(
                            "Book", AppConfigurations.getWorkingDir(), Book::new, BookUIWrapper::new
                        );
                        StubCommunication.getInstance().startBookMonitorThreads();
                        StubCommunication.getInstance().startDeleteMonitorThread();
                        // request update from database (if there is any)
                        BookListManager.getInstance().syncStub();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            },
            this::close // onExit
        );
    }
    
    private void close() {
        StubCommunication.getInstance().stopStubCommunication();
        SingleCommandProcessor.getInstance().shutdown();
    }
}
