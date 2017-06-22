package ebolo.libma.authenticate.ui.controllers;

import ebolo.libma.commons.net.Message;
import ebolo.libma.commons.net.Stub;
import ebolo.libma.data.data.raw.user.utils.MetaInfo;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.apache.commons.codec.digest.Crypt;
import org.bson.Document;

import java.io.IOException;

/**
 * Controller for user authentication dialog UI
 *
 * @author Ebolo
 * @version 08/06/2017
 * @since 08/06/2017
 */

public class AuthenticationController {
    private final Stub stub;
    @FXML
    private TextField usernameTextField;
    @FXML
    private Label infoLabel;
    @FXML
    private PasswordField passwordTextField;
    @FXML
    private Button signInButton;
    private MetaInfo.USER_MODE userMode;
    private Runnable onSuccess;
    private Thread stubMonitorThread;
    
    public AuthenticationController(
        Stub stub,
        MetaInfo.USER_MODE userMode,
        Runnable onSuccess
    ) {
        this.stub = stub;
        this.userMode = userMode;
        this.onSuccess = onSuccess;
    }
    
    @FXML
    private void signIn() throws IOException, ClassNotFoundException, InterruptedException {
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();
        
        boolean auth = false;
        
        // Send auth command
        Document userAuthenticateInfo = new Document("command_type", MetaInfo.USER_MODE.Kernel);
        userAuthenticateInfo.put("command", "auth");
        userAuthenticateInfo.put("args", new String[]{username, this.userMode.name()});
        
        if (stub.sendMessage(userAuthenticateInfo)) {
            Document resultMessage = stub.getMessageBuffer().take();
            if (!resultMessage.getString("message").equals("failed")) {
                String storedHashedPassword = resultMessage.getString("package");
                String hashedPassword = Crypt.crypt(password, storedHashedPassword);
                auth = storedHashedPassword.equals(hashedPassword);
                stub.sendMessage(Message.messageGenerate("auth", hashedPassword));
            }
        }
        
        if (auth) {
            usernameTextField.clear();
            passwordTextField.clear();
            infoLabel.getScene().getWindow().hide();
            onSuccess.run();
        } else {
            Document resultMessage = stub.getMessageBuffer().take();
            infoLabel.setText(resultMessage.getString("package"));
        }
    }
    
    public void initialize() {
        signInButton.setDisable(true);
        stubMonitorThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                if (stub.isConnected()) {
                    if (stub.isConnected())
                        Platform.runLater(() -> {
                            // disable sign in function until the app is connected to the server
                            infoLabel.setText("Server connected");
                            signInButton.setDisable(false);
                            usernameTextField.setOnAction(event -> {
                                try {
                                    signIn();
                                } catch (IOException | ClassNotFoundException | InterruptedException e) {
                                    e.printStackTrace();
                                }
                            });
                            passwordTextField.setOnAction(event -> {
                                try {
                                    signIn();
                                } catch (IOException | ClassNotFoundException | InterruptedException e) {
                                    e.printStackTrace();
                                }
                            });
                        });
                    break;
                }
            }
        });
        stubMonitorThread.start();
        
        usernameTextField.textProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (!infoLabel.getText().isEmpty())
                    infoLabel.setText("");
            }
        );
        
        passwordTextField.textProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (!infoLabel.getText().isEmpty())
                    infoLabel.setText("");
            }
        );
    }
    
    synchronized public void exit() {
        stubMonitorThread.interrupt();
    }
}
