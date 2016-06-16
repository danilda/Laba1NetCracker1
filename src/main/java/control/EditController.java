package control;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Task;
import model.TaskIO;
import org.apache.log4j.Logger;
import view.Main;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by User on 24.05.2016.
 */
public class EditController {
    @FXML
    public static Stage STAGE;

    @FXML
    Label firstLable;

    @FXML
    Label secondLable;

    @FXML
    Label thertLable;

    @FXML
    TextArea titleField;

    @FXML
    TextField startTime;

    @FXML
    TextField endTime;

    @FXML
    TextField intervalTime;

    @FXML
    CheckBox activeCheck;

    @FXML
    CheckBox repitedCheck;

    @FXML
    Button addButton;

    private static Task nowTask;
    public Task cTask;
    private boolean active = true;
    private boolean repited;
    private String title;
    private Date start = new Date();
    private Date end = new Date();
    private int interval;
    private static final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static final SimpleDateFormat sdf2 = new SimpleDateFormat("dd HH:mm");
    private static final SimpleDateFormat sdf3 = new SimpleDateFormat("dd");
    private static final Logger log = Logger.getLogger(EditController.class);


    public static Task getNowTask() {
        return nowTask;
    }

    public static void setNowTask(Task nowTask) {
        EditController.nowTask = nowTask;
    }

    @FXML
    private void initialize(){
        if(nowTask == null){

        } else {
            cTask = nowTask;
            addButton.setText("Изменить");
            titleField.setText(cTask.getTitle());
            if(nowTask.isRepeated()){

                startTime.setText(sdf1.format(cTask.getStartTime()));
                endTime.setText(sdf1.format(cTask.getEndTime()));
                intervalTime.setText(sdf2.format(new Date(cTask.getRepeatInterval())));
            } else {
                startTime.setText(sdf1.format(cTask.getStartTime()));
            }

            repitedCheck.setSelected(cTask.isRepeated());
            activeCheck.setSelected(cTask.isActive());
            cTask.toString();

        }

    }

    public void parseToDate (TextField t, Date d){
        int year, month, day, hour, minute;

        String[] toPars = new String[2];
        String[] toParsHM = new String[2];
        String[] toParsYMD = new String[3];
        int[] result = new int[5];

        toPars = t.getText().split(" ");
        toParsYMD = toPars[0].split("-");
        toParsHM = toPars[1].split(":");

        year = Integer.parseInt(toParsYMD[0]);
        month = Integer.parseInt(toParsYMD[1]);
        day = Integer.parseInt(toParsYMD[2]);

        hour = Integer.parseInt(toParsHM[0]);
        minute = Integer.parseInt(toParsHM[1]);
        result[0] = year;
        result[1] = month;
        result[2] = day;
        result[3] = hour;
        result[4] = minute;
        d.setYear(result[0]-1900);
        d.setMonth(result[1] - 1);
        d.setHours(result[2]*24 + result[3] - (Integer.parseInt(sdf3.format(new Date())))*24);
        d.setMinutes(result[4]);


    }

    public void addNewTask(ActionEvent actionEvent){
        if(cTask != null)
            Main.getArrayTaskList().remove(cTask);
        title = titleField.getText();

        if(repited){
            parseToDate(startTime, start);
            parseToDate(endTime, end);

            int day, hour, minute;
            String[] toPars;
            String[] toParsHM;
            toPars = intervalTime.getText().split(" ");
            toParsHM = toPars[1].split(":");
            day = Integer.parseInt(toPars[0]);
            hour = Integer.parseInt(toParsHM[0]);
            minute = Integer.parseInt(toParsHM[1]);
            interval = day*24*60*60*1000 + hour*60*60*1000 + minute*60*1000;
            cTask = new Task(title, start, end, interval);
            Main.getArrayTaskList().add(cTask);

        } else {
            parseToDate(startTime, start);

            cTask = new Task(title, start);
            cTask.setActive(active);
            Main.getArrayTaskList().add(cTask);
        }

        try {
            TaskIO.writeBinary(Main.getArrayTaskList(), Main.getFile());
        } catch (IOException e) {
            log.info(e.getMessage());
        }

        STAGE.close();

    }

    public void isActive(ActionEvent actionEvent){
        if(cTask != null)
            active = cTask.isActive();
        if(active){
            active = false;
        } else {
            active = true;
        }
        if(cTask != null){
            cTask.setActive(active);
            cTask.toString();
        }
;
    }

    public void isRepited(ActionEvent actionEvent){
        if(repited){
            repited = false;
            endTime.setVisible(false);
            intervalTime.setVisible(false);
            firstLable.setText("Время");
            secondLable.setVisible(false);
            thertLable.setVisible(false);
        } else {
            repited = true;
            endTime.setVisible(true);
            intervalTime.setVisible(true);
            firstLable.setText("Время начала");
            secondLable.setVisible(true);
            thertLable.setVisible(true);
        }
    }

}
