package view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import control.Checker;
import model.ArrayTaskList;
import model.TaskIO;
import org.apache.log4j.Logger;
import java.io.File;
import java.util.Date;


public class Main extends Application {
    private static final Logger log = Logger.getLogger(Main.class);

    private static ArrayTaskList arrayTaskList = new ArrayTaskList();
    private static File file = new File("src/main/resources/data/file.txt");

    public static File getFile() {
        return file;
    }

    public static void setFile(File file) {
        Main.file = file;
    }

    public static ArrayTaskList getArrayTaskList() {
        return arrayTaskList;
    }

    public static void setArrayTaskList(ArrayTaskList nowTask) {
        Main.arrayTaskList = nowTask;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        final Stage finalPrimaryStageCopy = primaryStage;
        final Thread currentThread = Thread.currentThread();
        System.out.println(file.toString());
        log.info("Начало работы");
        Date app = new Date();
        Date app1 = new Date();
        Date app2 = new Date();
        Date app3 = new Date();
        Date app4 = new Date();
        app1.setTime(app.getTime()+ 1000000);
        app2.setTime(app.getTime()+ 4000000);
        app3.setTime(app.getTime()+ 8000000);
        app4.setTime(app.getTime()+ 16000000);
        if(file.length() != 0 ) {
            TaskIO.readBinary(arrayTaskList, file);
            log.info(arrayTaskList.toString());
        }

        Parent root = FXMLLoader.load(getClass().getResource("/view/sample.fxml"));
        primaryStage.setTitle("Task Manager");
        Scene scene = new Scene(root, 600, 275);

        Checker.setNode(primaryStage.getOwner());

        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(610);
        primaryStage.setScene(scene);
        primaryStage.show();
        Platform.setImplicitExit(false);

        final Thread checker = new Thread(new Checker(finalPrimaryStageCopy));


        final Thread thread = new Thread(new Runnable() {
            public void run() {
                for(;;){

                    Platform.runLater(checker);
                    try {
                        Thread.sleep(3600000);
                    } catch (InterruptedException e) {
                        e.getStackTrace();
                    }
                    if(currentThread.isInterrupted()){
                        checker.interrupt();
                    }
                }

            }
        });
        thread.start();
    }

    public static void main(String[] args) {
        launch(args);
    }


}
