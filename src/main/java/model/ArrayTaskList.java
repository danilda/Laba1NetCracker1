package model;

import org.apache.log4j.Logger;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by User on 07.02.2016.
 */
public class ArrayTaskList extends TaskList {
    private static final Logger log = Logger.getLogger(ArrayTaskList.class);
    private class ArrayIterator implements Iterator<Task>{
        private int cursor;       // следующий элемент
        private int last = -1; // последний элемент

        public boolean hasNext() {
            return cursor < size();
        }

        public Task next(){
            if (!hasNext()) {
                log.error("Нет следующего елемента");
                return null;
            } else {
                last = cursor;
                cursor++;
                return getTask(last);
            }
        }

        public void remove() {
            if (last < 0)
                log.error("Нет элемента на удаление");
            try {
                ArrayTaskList.this.remove(getTask(last));
                cursor = last;
                last = -1;
            } catch (IndexOutOfBoundsException e) {
                log.error("Ошибка в методе remove : " + e.getMessage());
            }
        }
    }

    private Task[] array = new Task[10];
    private int lastIndex = 0;

    public void add(Task task){
        if(task == null)
            log.error("Добавление таска со значением null");
        if(lastIndex == array.length){
            Task[] arrayTmp = new Task[array.length+10];
            for(int i = 0; i < array.length; i++)
                arrayTmp[i] = array[i];
            arrayTmp[lastIndex] = task;
            lastIndex++;
            array = arrayTmp;
        }
        array[lastIndex] = task;
        lastIndex++;
    }
    public boolean remove(Task task){
        if(task == null)
            log.error("Удаление таска со значением null");
        boolean point = false;
        for(int i = 0; i < lastIndex; i++){
            if(task.equals(array[i]) == true && point == false){
                point = true;
                array[i] = null;
            } else if(point == true && array[i]!= null){
                array[i - 1] = array[i];
            } else if( point == true && array[i]== null ) {
                array[i-1] = null;
            }
        }
        if(point == true){
            lastIndex--;
            return true;
        }
        if(point == false){
            return false;
        }
        return false;
    }
    public int size(){
        return lastIndex;
    }

    public Task getTask(int index){
        if(index > lastIndex-1)
            log.error("Ошибка в методе getTask : возврат несуществующего элемента ");
        return array[index];
    }



    @Override
    public Iterator<Task> iterator() {
        return new ArrayIterator();
    }

    @Override
    public boolean equals(TaskList a) {
        Iterator thisIter = this.iterator();
        Iterator Iter = a.iterator();
        while (thisIter.hasNext() && Iter.hasNext()){
            if(!thisIter.next().equals(Iter.next()))
                return false;
        }
        if(thisIter.hasNext()||Iter.hasNext())
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = 1;
        Iterator iter = this.iterator();
        while(iter.hasNext()){
            result *= iter.next().hashCode()/7;
        }
        return result;
    }

    @Override
    public String toString() {
        if (this.size()<1){
            return "";
        }
        String string = new String();
        Iterator iter = this.iterator();
        while (iter.hasNext()){
            string+= iter.next().toString();
        }
        return string;
    }
    public ArrayTaskList clone() throws CloneNotSupportedException {
        ArrayTaskList a = (ArrayTaskList) super.clone();
        a.array = this.array;
        a.lastIndex = this.lastIndex;
        return a;
    }

}
