package model;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by User on 09.05.2016.
 */
public class TaskIO {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss.SSS]");
    private static final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static final SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
    private static final String[] TIMES_PARTS = {" day", " hour", " minute", " second"};

    public static void write(TaskList tasks, OutputStream out) throws IOException {
        DataOutputStream outputStream = new DataOutputStream(out);
        try {
            outputStream.writeInt(tasks.size());
            Iterator<Task> iter = tasks.iterator();
            while (iter.hasNext()) {
                Task t = iter.next();
                outputStream.writeInt(t.getTitle().length());
                outputStream.writeUTF(t.getTitle());
                outputStream.writeBoolean(t.isActive());
                outputStream.writeInt(t.getRepeatInterval());
                if (t.isRepeated()) {
                    outputStream.writeLong(t.getStartTime().getTime());
                    outputStream.writeLong(t.getEndTime().getTime());
                } else {
                    outputStream.writeLong(t.getTime().getTime());
                }
            }
        } finally {
            outputStream.flush();
            outputStream.close();
        }
    }

    public static void read(TaskList tasks, InputStream in) throws IOException{
        DataInputStream inputStream = new DataInputStream(in);
        try{
            int count = inputStream.readInt();
            for(int i = 0; i < count; i++){
                Task task;
                int length = inputStream.readInt();
                String title = inputStream.readUTF();
                boolean active = inputStream.readBoolean();
                int interval = inputStream.readInt();
                Date startTime = new Date(inputStream.readLong());
                if (interval > 0) {
                    Date endTime = new Date(inputStream.readLong());
                    task = new Task(title, startTime, endTime, interval);
                } else {
                    task = new Task(title, startTime);
                }
                task.setActive(active);
                tasks.add(task);
            }
        } finally {
            inputStream.close();
        }
    }

    public static void writeBinary(TaskList tasks, File file) throws IOException {
        FileOutputStream fileOutput = new FileOutputStream(file);
        try {
            write(tasks,fileOutput);
        } finally {
            fileOutput.close();
        }
    }

    public static void readBinary(TaskList tasks, File file) throws IOException{
        FileInputStream fileInput = new FileInputStream(file);
        try {
            read(tasks, fileInput);
        } finally {
            fileInput.close();
        }
    }



    public static String write(TaskList tasks, Writer out) throws IOException {
        String name = new String();
        BufferedWriter bw = new BufferedWriter(out);
        try {
            int max = tasks.size() - 1;
            int i = 0;
            for (Task t : tasks) {
                name = makeStringFromTask(t);
                bw.append(makeStringFromTask(t));
                bw.append((i != max ? ";" : "."));
                System.out.println(makeStringFromTask(t));
                bw.newLine();
                i++;
            }
        } finally {
            bw.flush();
        }
        return name;
    }

    public static void read(TaskList tasks, Reader in) throws IOException{
        BufferedReader br = new BufferedReader(in);
        try {
            if (br.ready()) {
                String line;
                int i = 0;
                while ((line = br.readLine()) != null) {
                    tasks.add(makeTaskFromString(line));
                    i++;
                    if (line.charAt(line.length() - 1) == '.') {
                        break;
                    }
                }
            } else {
                throw new IOException("Stream не может считывать");
            }
        } finally {
            br.close();
        }
    }

    private static String makeStringFromTask(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append('"');
        String temp = new String(task.getTitle());
        temp.replaceAll("\"", "\"\"");
        sb.append(temp);
        sb.append('"');
        sb.append(" ");
        if (task.isRepeated()) {
            sb.append("from ");
        } else {
            sb.append("at ");
        }
        sb.append(sdf.format(task.getStartTime()));
        if (task.isRepeated()) {
            sb.append(" to ");
            sb.append(sdf.format(task.getEndTime()));
            sb.append(" every ");
            sb.append(makeInterval(task.getRepeatInterval()));
        }
        if (! task.isActive()) {
            sb.append(" inactive");
        }
        return sb.toString();
    }

    private static String makeInterval(int interval) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        int[] val = new int[4];
        int temp = interval;
        val[0] = temp / (24*60*60);
        temp = temp % (24*60*60);
        val[1] = temp / (60*60);
        temp = temp % (60*60);
        val[2] = temp / 60;
        temp = temp % 60;
        val[3] = temp;
        boolean first = true;
        for (int i = 0; i < val.length; i++) {
            if (val[i] != 0) {
                if (!first) {
                    sb.append(' ');
                }
                sb.append(val[i]);
                sb.append(TIMES_PARTS[i]);
                if (val[i] > 1) {
                    sb.append('s');
                }
                first = false;
            }
        }
        sb.append(']');
        return sb.toString();
    }

    public static Task makeTaskFromString(String str){
        Task task;
        String title;
        Date start;
        Date end;
        int interval;
        boolean active = false;

        String[] repeatedCheck = str.split(" from ");
        if(repeatedCheck.length == 1){
            String[] getTitleAndOther = str.split(" at ");
            title = getTitleAndOther[0].replaceFirst("\"","");//title

            String[] dateAndActive = getTitleAndOther[1].split(" ");
            start = parseToDate(dateAndActive[0].replace("[","").replace("]",""));
            if(dateAndActive[0].length() > 0 && dateAndActive[1].length() > 0)
                active = false;
            task = new Task(title,start);
            task.setActive(active);

        }else{
            title = repeatedCheck[0].replace("\"", "");
            String[] dateAndActive = repeatedCheck[1].split("[]]{1}[ ]{1}[a-z]{2,5}[ ]{1}[\\[]{1}");
            start = parseToDate(dateAndActive[0].replace("[", "").replace("]",""));
            end = parseToDate(dateAndActive[1].replace("[", "").replace("]",""));
            String[] actives = dateAndActive[2].split("\\] ");
            if(actives[0].length() > 0 && actives[1].length() > 0 )
                active = false;

            interval = parseToInterval(actives[0].replace("[", "").replace("]",""));

            task = new Task(title,start, end, interval);
            task.setActive(active);
        }

        return task;
    }

    private static Date parseToDate(String str) {
        Date d = new Date();
        int year, month, day, hour, minute, second, mill;
        String[] toPars;
        String[] toParsHM ;
        String[] toParsYMD;
        String[] toParsSM;
        int[] result = new int[5];

        toPars = str.split(" ");
        toParsYMD = toPars[0].split("-");
        toParsHM = toPars[1].split(":");
        toParsSM = toParsHM[2].split("\\.");

        year = Integer.parseInt(toParsYMD[0]);
        month = Integer.parseInt(toParsYMD[1]);
        day = Integer.parseInt(toParsYMD[2]);

        hour = Integer.parseInt(toParsHM[0]);
        minute = Integer.parseInt(toParsHM[1]);
        second = Integer.parseInt(toParsSM[0]);
        mill = Integer.parseInt(toParsSM[1]);
        result[0] = year;
        result[1] = month;
        result[2] = day;
        result[3] = hour;
        result[4] = minute;
        d.setYear(result[0]-1900);
        d.setMonth(result[1] -2);
        d.setHours((result[2])*24 + result[3] );
        d.setMinutes(result[4]);
        d.setSeconds(second + mill/1000);
        return d;
    }

    private static int parseToInterval(String str) {
        String[] string = str.split("[ ][a-z]{1,10}[ ]");
        int[] interval = new int[4];

        string[3] = string[3].split(" ")[0];
        interval[0] = Integer.parseInt(string[0]);
        interval[1] = Integer.parseInt(string[1]);
        interval[2] = Integer.parseInt(string[2]);
        interval[3] = Integer.parseInt(string[3]);
        return interval[0]*24*60*60*1000 + interval[1]*60*60*1000 + interval[2]*60*1000 + interval[3]*1000;
    }


    public static String makeDate(Task task) {
        StringBuilder sb = new StringBuilder();

        if (task.isRepeated()) {
            sb.append("\u041e\u0442");
        }
        sb.append(sdf1.format(task.getStartTime()));
        if (task.isRepeated()) {
            sb.append("\u0414\u043e");
            sb.append(sdf1.format(task.getEndTime()));
            sb.append("\u043a\u0430\u0436\u0434\u044b\u0435");
            sb.append(makeIntervalSecFormat(task.getRepeatInterval()));
        }
        return sb.toString();
    }

    private static String makeIntervalSecFormat(int interval) {
        StringBuilder sb = new StringBuilder();
        int[] val = new int[4];
        int temp = interval;
        val[0] = temp / (24*60*60*1000);
        temp = temp % (24*60*60*1000);
        val[1] = temp / (60*60*1000);
        temp = temp % (60*60*1000);
        val[2] = temp / (60*1000);
        if(val[2] < 10)
            return val[0] + " " + val[1] + ":" + "0" +val[2];

        if(val[0] == 0)
            return val[1] + ":" + val[2];
        return val[0] + " " + val[1] + ":" + val[2];

    }

    public static String makeHoursMin(Task task){
        String dateToStringHours;
        dateToStringHours = sdf2.format(task.nextTimeAfter(new Date()));
        return dateToStringHours;
    }

}
