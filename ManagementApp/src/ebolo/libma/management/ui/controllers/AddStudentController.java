package ebolo.libma.management.ui.controllers;

import ebolo.libma.commons.ui.UIFactory;
import ebolo.libma.commons.ui.utils.Controller;
import ebolo.libma.data.data.raw.user.Student;
import ebolo.libma.data.data.raw.user.utils.MetaInfo;
import ebolo.libma.management.commander.StudentCommands;
import ebolo.libma.management.utils.configs.AppConfigurations;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.stage.Window;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Controller of the adding student window
 *
 * @author Ebolo
 * @version 13/06/2017
 * @since 13/06/2017
 */

public class AddStudentController implements Controller {
    private static AddStudentController ourInstance;
    
    @FXML
    private Button addButton;
    @FXML
    private TextField idTextField, fNameTextField, mNameTextField, lNameTextField, usernameTextField, emailTextField,
        intakeTextField;
    @FXML
    private ComboBox<MetaInfo.STUDENT_COURSE> courseComboBox;
    
    private List<TextField> inputs;
    
    private AddStudentController() {
    }
    
    public static AddStudentController getInstance() {
        if (ourInstance == null)
            ourInstance = new AddStudentController();
        return ourInstance;
    }
    
    @FXML
    private void addStudent() {
        // retrieve info from UI components
        String idStr = idTextField.getText(),
            fNameStr = fNameTextField.getText(),
            mNameStr = mNameTextField.getText(),
            lNameStr = lNameTextField.getText(),
            usernameStr = usernameTextField.getText(),
            emailStr = emailTextField.getText(),
            intakeStr = intakeTextField.getText(),
            courseStr = courseComboBox.getValue().name();
        new Thread(() -> {
            // check for input correctness
            int year;
            try {
                year = Integer.parseInt(intakeStr);
            } catch (NumberFormatException e) {
                UIFactory.showAnnouncement(
                    Alert.AlertType.ERROR,
                    "Wrong input format",
                    "Intake input must be integer!"
                );
                return;
            }
            String password = "defaultPassword"; // TODO use password generator here
            // start command
            Future<String> result = StudentCommands.addNewStudent(new Student(
                fNameStr, mNameStr, lNameStr, emailStr, usernameStr, password,
                MetaInfo.STUDENT_COURSE.valueOf(courseStr), year, idStr
            ));
            // show result to user
            final String resultMes;
            try {
                resultMes = result.get();
                if (!resultMes.equals("success"))
                    UIFactory.showAnnouncement(
                        Alert.AlertType.ERROR,
                        resultMes
                    );
                else if (AppConfigurations.getInstance().isCloseOnAdded())
                    Platform.runLater(this::cancel);
                else Platform.runLater(() -> inputs.forEach(TextInputControl::clear));
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    @Override
    public void setUpUI() {
        courseComboBox.setItems(FXCollections
            .observableArrayList(MetaInfo.STUDENT_COURSE.values())
            .sorted(Comparator.comparing(Enum::name))
        );
        courseComboBox.setCellFactory(param -> new ComboBoxListCell<MetaInfo.STUDENT_COURSE>() {
            @Override
            public void updateItem(MetaInfo.STUDENT_COURSE item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.name().replace('_', ' '));
            }
        });
        courseComboBox.setButtonCell(new ListCell<MetaInfo.STUDENT_COURSE>() {
            @Override
            protected void updateItem(MetaInfo.STUDENT_COURSE item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.name().replace('_', ' '));
            }
        });
        courseComboBox.getSelectionModel().selectFirst();
        
        inputs = Arrays.asList(idTextField, fNameTextField, mNameTextField, lNameTextField, usernameTextField,
            emailTextField, intakeTextField);
        
        addButton.disableProperty().bind(
            idTextField.textProperty().isEmpty().or(
                fNameTextField.textProperty().isEmpty().or(
                    lNameTextField.textProperty().isEmpty().or(
                        usernameTextField.textProperty().isEmpty().or(
                            emailTextField.textProperty().isEmpty().or(
                                intakeTextField.textProperty().isEmpty()
                            )
                        )
                    )
                )
            )
        );
    }
    
    @FXML
    private void cancel() {
        inputs.forEach(TextInputControl::clear);
        getWindow().hide();
    }
    
    @Override
    public Window getWindow() {
        return addButton.getScene().getWindow();
    }
}
