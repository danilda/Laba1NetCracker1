package control;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.ArrayTaskList;
import model.Tasks;
import org.apache.log4j.Logger;
import view.Main;

import java.util.Date;

/**
 * Created by User on 11.06.2016.
 */
public class Checker implements Runnable {
    private static final Logger log = Logger.getLogger(Checker.class);
    private static Window node;
    private Stage stages;


    public Checker(Stage stages) {
        this.stages = stages;
    }

    public static void setNode(Window node) {
        Checker.node = node;
    }

    public static Window getNode() {
        return node;
    }


    public void run() {
        if (stages.isShowing()) {
            ArrayTaskList list = (ArrayTaskList) Tasks.incoming(Main.getArrayTaskList(), new Date(),
                    new Date(new Date().getTime() + 24 * 60 * 60 * 1000));
            if (list != null) {
                log.info(list.toString());
                CheckerController.setArrayTaskList(list);

                try {
                    showChecker();
                } catch (Exception e) {
                    log.error("Ошибка в добавление нового элемента" + e.getMessage());
                }
            }
        }

    }

    public void showChecker() throws Exception{
        try {
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/view/check.fxml"));
            stage.setTitle("Hello World");
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(Checker.getNode());
            if(!stage.isShowing()){
                stage.show();
            }


        } catch (Exception e) {
            log.error("Ошибка в отображения диалогового окна :" + e.getMessage());
        }
    }


}
