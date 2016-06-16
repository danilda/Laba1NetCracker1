package control;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.ArrayTaskList;
import model.Task;
import model.TaskIO;
import model.Tasks;
import view.Main;

import java.util.Date;

public class Controller {

    private ArrayTaskList arrayTaskList = Main.getArrayTaskList();

    @FXML
    private TableView table;

    @FXML
    private TextField aroundTextField;

    @FXML
    private TableColumn<Task, String> colum1;

    @FXML
    private TableColumn<Task, String> colum2;

    @FXML
    private void initialize() throws Exception {
        showTable(arrayTaskList);
    }


    public void addTask(ActionEvent actionEvent) throws Exception {
        try{
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/view/edit.fxml"));
            stage.setTitle("Hello World");
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Node)actionEvent.getSource()).getScene().getWindow());
            stage.show();

            EditController.STAGE = stage;

            EditController.setNowTask(null);
        }
        catch (Exception e){
            throw new Exception("Ошибка в addTask");
        }
    }


    public void showTable(ArrayTaskList taskList){
        System.out.println("Дошло");
        colum1.setCellValueFactory(new PropertyValueFactory<Task, String>("dateToString"));
        colum2.setCellValueFactory(new PropertyValueFactory<Task, String>("title"));
        table.setItems(ArrayTaskToList(taskList));

    }

    public ObservableList ArrayTaskToList(ArrayTaskList taskList){
        ObservableList<Task> list = (ObservableList) FXCollections.observableArrayList();
        for (Task aTaskList : taskList) {
            list.add(aTaskList);
        }
        return list;
    }


    public void showAllTasks(ActionEvent actionEvent){
        showTable(arrayTaskList);
    }

    public void onClickTable(ActionEvent actionEvent) throws Exception{
        Task task = (Task)table.getSelectionModel().getSelectedItem();
        EditController.setNowTask(task);
        System.out.println(task.toString());
        try{
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/view/edit.fxml"));
            stage.setTitle("Hello World");
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Node)actionEvent.getSource()).getScene().getWindow());
            stage.show();

            EditController.STAGE = stage;
            stage.show();
            if(!stage.isShowing()){
                showTable(arrayTaskList);
            }
            EditController.setNowTask(null);
        }
        catch (Exception e){
            EditController.setNowTask(null);
            throw new Exception("Ошибка в onClickTable");
        }
    }
    public void onClickDelete(ActionEvent actionEvent) throws Exception{
        Task task = (Task)table.getSelectionModel().getSelectedItem();
        Main.getArrayTaskList().remove(task);
        showTable(arrayTaskList);
        TaskIO.writeBinary(Main.getArrayTaskList(), Main.getFile());
    }

    public void showAroundTasks(ActionEvent actionEvent) {
        String all = aroundTextField.getText();
        String[] all2;
        all2 = all.split(" ");
        int day;
        int hours;
        int min;
        if(all2[0].length() != 0  && all2[1].length() !=  0){
            day = Integer.parseInt(all2[0]);

            String[] all3 = all2[1].split(":");

            hours = Integer.parseInt(all3[0]);
            min = Integer.parseInt(all3[1]);
            ArrayTaskList newArrayTaskList = (ArrayTaskList) Tasks.incoming(
                    Main.getArrayTaskList() , new Date(), new Date(new Date().getTime() +
                            day*24*60*60*1000 + hours*60*60*1000 + min*60*1000 ) );
            showTable( newArrayTaskList);
        } else if(all2[0].length() != 0){
            String[] all3 = all2[0].split(":");
            if(!all3[1].isEmpty()){
                hours = Integer.parseInt(all3[0]);
                min = Integer.parseInt(all3[1]);
                ArrayTaskList newArrayTaskList = (ArrayTaskList) Tasks.incoming(
                        Main.getArrayTaskList() , new Date(), new Date(new Date().getTime()
                                + hours*60*60*1000 + min*60*1000 ) );
                showTable(newArrayTaskList);
            } else {
                day = Integer.parseInt(all3[0]);
                ArrayTaskList newArrayTaskList = (ArrayTaskList) Tasks.incoming(
                        Main.getArrayTaskList() , new Date(), new Date(new Date().getTime()
                                + day*60*60*1000*24) );
                showTable(newArrayTaskList);
            }
        }

    }


}
