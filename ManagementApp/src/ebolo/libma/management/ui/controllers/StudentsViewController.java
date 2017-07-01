package ebolo.libma.management.ui.controllers;

import ebolo.libma.commons.ui.UIFactory;
import ebolo.libma.commons.ui.utils.Controller;
import ebolo.libma.commons.ui.utils.ControllerWrapper;
import ebolo.libma.data.data.ui.user.StudentUIWrapper;
import ebolo.libma.data.db.local.StudentListManager;
import ebolo.libma.management.commander.CentralCommandFactory;
import ebolo.libma.management.ui.fxml.FxmlManager;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ContextMenuEvent;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Controller of the student table view tab
 *
 * @author Ebolo
 * @version 09/06/2017
 * @since 09/06/2017
 */

public class StudentsViewController implements Controller {
    private static StudentsViewController ourInstance;
    
    @FXML
    private TableView<StudentUIWrapper> studentsTableView;
    @FXML
    private TableColumn<StudentUIWrapper, String> studentIdColumn, usernameColumn, firstNameColumn, middleNameColumn,
        lastNameColumn, courseColumn;
    @FXML
    private TableColumn<StudentUIWrapper, Integer> intakeColumn, borrowingColumn;
    
    private ContextMenu contextMenu;
    private Stage addStudentWindow;
    
    private StudentsViewController() {
    }
    
    public static StudentsViewController getInstance() {
        if (ourInstance == null)
            ourInstance = new StudentsViewController();
        return ourInstance;
    }
    
    @SuppressWarnings("Duplicates")
    @Override
    public void setUpUI() {
        // Set up general table UI
        studentsTableView.minWidthProperty().bind(studentsTableView.prefWidthProperty());
        studentsTableView.maxWidthProperty().bind(studentsTableView.prefWidthProperty());
    
        studentIdColumn.setCellValueFactory(param -> param.getValue().studentIdProperty());
        usernameColumn.setCellValueFactory(param -> param.getValue().usernameProperty());
        firstNameColumn.setCellValueFactory(param -> param.getValue().firstNameProperty());
        middleNameColumn.setCellValueFactory(param -> param.getValue().middleNameProperty());
        lastNameColumn.setCellValueFactory(param -> param.getValue().lastNameProperty());
        courseColumn.setCellValueFactory(param -> param.getValue().courseProperty());
        intakeColumn.setCellValueFactory(new PropertyValueFactory<>("intake"));
        borrowingColumn.setCellValueFactory(new PropertyValueFactory<>("borrowing"));
    
        studentsTableView.setOnContextMenuRequested(this::showContextMenu);
        studentsTableView.setItems(StudentListManager.getInstance().getUiWrapperSortedList());
        studentsTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }
    
    private void addNewStudent() throws IOException {
        if (addStudentWindow == null) {
            AddStudentController addStudentController = AddStudentController.getInstance();
            
            addStudentWindow = UIFactory.createWindow(
                "Add student",
                FxmlManager.getInstance().getFxmlTemplate("AddStudentFXML"),
                AppMainController.getInstance().getWindow(),
                0, 0,
                new ControllerWrapper(addStudentController.getClass(), addStudentController)
            );
            
            addStudentController.setUpUI();
        }
        addStudentWindow.show();
    }
    
    @SuppressWarnings("Duplicates")
    private void deleteStudents() {
        List<StudentUIWrapper> deletingStudents = studentsTableView
            .getSelectionModel()
            .getSelectedItems();
        new Thread(() -> {
            Future<String> result = CentralCommandFactory.getInstance().run(
                "student.delete_students",
                deletingStudents
            );
            try {
                String resultMessage = result.get();
                if (!resultMessage.equals("success")) {
                    UIFactory.showAnnouncement(
                        Alert.AlertType.ERROR,
                        resultMessage
                    );
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    private void showContextMenu(ContextMenuEvent event) {
        if (contextMenu == null) {
            MenuItem addNew = new MenuItem("Add new...");
            addNew.setOnAction(event1 -> {
                try {
                    addNewStudent();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            MenuItem delete = new MenuItem("Delete");
            delete.setOnAction(event1 -> deleteStudents());
            MenuItem modify = new MenuItem("Edit...");
            delete.disableProperty().bind(
                Bindings.size(StudentListManager.getInstance().getUiList())
                    .isEqualTo(0)
            );
            modify.disableProperty().bind(
                Bindings.size(StudentListManager.getInstance().getUiList())
                    .isEqualTo(0)
            );
            
            contextMenu = new ContextMenu(
                addNew,
                modify,
                delete
            );
        }
        contextMenu.show(getWindow(), event.getScreenX(), event.getScreenY());
    }
    
    @Override
    public Window getWindow() {
        return studentsTableView.getScene().getWindow();
    }
}
