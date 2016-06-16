package control;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.ArrayTaskList;
import model.Task;


/**
 * Created by User on 11.06.2016.
 */
public class CheckerController {

    private static ArrayTaskList arrayTaskList;

    @FXML
    private TableView tableChecker;

    @FXML
    private TableColumn<Task, String> columChecker1;

    @FXML
    private TableColumn<Task, String> columChecker2;

    @FXML
    private void initialize(){
        showTable(arrayTaskList);
    }

    public static ArrayTaskList getArrayTaskList() {
        return arrayTaskList;
    }

    public static void setArrayTaskList(ArrayTaskList arrayTaskList) {
        CheckerController.arrayTaskList = arrayTaskList;
    }

    public void showTable(ArrayTaskList taskList){
        columChecker1.setCellValueFactory(new PropertyValueFactory<Task, String>("dateToStringHours"));
        columChecker2.setCellValueFactory(new PropertyValueFactory<Task, String>("title"));
        tableChecker.setItems(ArrayTaskToList(taskList));

    }
    public ObservableList ArrayTaskToList(ArrayTaskList taskList){
        ObservableList<Task> list = (ObservableList) FXCollections.observableArrayList();
        for (Task aTaskList : taskList) {
            list.add(aTaskList);
        }
        return list;
    }
}
