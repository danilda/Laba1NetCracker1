package model;


import java.io.Serializable;
import java.util.Date;

import static java.util.Objects.isNull;


public class Task implements Cloneable, Serializable  {

    private final int milliseconds = 1000;
    private String title;
    private Date time;
    private Date start;
    private Date end;
    private int interval;
    private boolean active;
    private boolean repeated;
    private String dateToString;
    private String dateToStringHours;

    public String getDateToString() {
        dateToString = TaskIO.makeDate(this);
        return dateToString;
    }

    public String getDateToStringHours() {
        dateToStringHours = TaskIO.makeHoursMin(this);
        return dateToStringHours;
    }

    public void setDateToString(String dateToString) {
        this.dateToString = dateToString;
    }

    public void setDateToStringHours(String dateToStringHours) {
        this.dateToStringHours = dateToStringHours;
    }



//    Конструктор, що конструює неактивну задачу, яка виконується у заданий час без повторення із заданою назвою.
    public Task(String title, Date time){
        if(title.isEmpty())
            throw new NullPointerException("Пустой стринг");
        this.title = title;
        this.time = time;
        start = time;
        end = time;
        active = false;
        repeated = false;
        dateToString = TaskIO.makeDate(this);

    }

//    Конструктор , що конструює
//    неактивну задачу, яка виконується у заданому проміжку часу (і початок і кінець включно) із
//    заданим інтервалом і має задану назву.
    public Task(String title, Date start, Date end, int interval){
        if(title.isEmpty())
            throw new NullPointerException("Пустой стринг");
        this.title = title;
        this.start = start;
        this.end = end;
        this.interval = interval;
        active = false;
        repeated = true;
        dateToString = TaskIO.makeDate(this);
    }
//    Методи для зчитування та встановлення назви задачі: String getTitle(),
//    void setTitle(String title).
    public String getTitle(){
        return title;
    }
    public void setTitle(String title)
    {
        if(title.isEmpty())
            throw new NullPointerException("Пустой стринг");
        this.title = title;
    }

//   Методи для зчитування та встановлення стану задачі: boolean isActive(),
//    void setActive(boolean active).
    public boolean isActive(){
        return active;
    }
    public void setActive(boolean active){
        this.active = active;
    }

//    Методи для зчитування та зміни часу виконання для задач, що не повторюються:
//    o int getTime(), у разі, якщо задача повторюється метод має повертати час початку
//    повторення;
    public Date getTime(){
        if(repeated == true){
            return start;
        }
        return time;
    }
//    o void setTime(int time), у разі, якщо задача повторювалась, вона має стати такою,
//    що не повторюється.
    public void setTime(Date time){
        this.time = time;
        start = time;
        end = time;
        interval = 0;
        dateToString = TaskIO.makeDate(this);
    }
//    Методи для зчитування та зміни часу виконання для задач, що повторюються:
//    o int getStartTime(), у разі, якщо задача не повторюється метод має повертати час
//    виконання задачі;
    public Date getStartTime(){
        if(repeated == true){
            return start;
        }

        return time;

    }
//    o int getEndTime(), у разі, якщо задача не повторюється метод має повертати час
//    виконання задачі;
    public Date getEndTime(){
        if(repeated == true){
            return end;
        }
        return time;

    }
//    o int getRepeatInterval(), у разі, якщо задача не повторюється метод має
//    повертати 0;
    public int getRepeatInterval(){
        if(repeated == true){
            return interval;
        }
        return 0;

    }
//    o void setTime(int start, int end, int interval), у разі, якщо задача не
//    повторювалася метод має стати такою, що повторюється.
    public void setTime(Date start, Date end, int interval){
        if(repeated == false){
            repeated = true;
        }
        this.start = start;
        this.interval = interval;
        this.end = end;
        dateToString = TaskIO.makeDate(this);
    }
//    Метод для перевірки повторюваності задачі boolean isRepeated().
    public boolean isRepeated(){
        return repeated;
    }

    public Date nextTimeAfter(Date current) {
        Date result = null;
        if (isActive()) {
            if (isRepeated()) {
                if (current.compareTo(start) < 0) {
                    result = start;
                } else {
                    if (current.before(end)) {
                        Date point = start;
                        long repeatNumber = ((end.getTime() - start.getTime())) / interval * milliseconds;
                        for (long i = 0; i <= repeatNumber; i++) {
                            Date next = new Date(point.getTime()  + interval * milliseconds);
                            if (current.compareTo(point) >= 0
                                    && current.compareTo(next) < 0
                                    && next.compareTo(end) <= 0) {
                                result = next;
                                break;
                            }
                            point = next;
                        }
                    }
                }
            } else {
                if (current.compareTo(time) < 0) {
                    result = time;
                }
            }
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (isNull(obj)) {
            return false;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        if (this.hashCode() != obj.hashCode()) {
            return false;
        }
        return true;
    }
    @Override
    public int hashCode() {
        return this.getTitle().hashCode()*11+ this.getTime().hashCode();
    }

    @Override
    public String toString() {
        if(!this.repeated) return "Задание : " + this.getTitle() + "\n Время : " + getTime() + "\n" +
                "Активно " + isActive() + "\n";
        return "Задание : " + this.getTitle() + "\n Время начала : "
                + getStartTime() + "\n время конца : " + getEndTime() + "\n"
                + "интервал повторения "+ getRepeatInterval() + "\n" +
                "Активно " + isActive() + "\n" ;
    }

    @Override
    public Task clone() throws CloneNotSupportedException {
        Task a = (Task)super.clone();
        a.title = this.title;
        a.time = this.time;
        a.start = this.start;
        a.end = this.end;
        a.interval = this.interval;
        a.active = this.active;
        a.repeated = this.repeated;
        return a;
    }

}
