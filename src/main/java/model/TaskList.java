package model;

import java.io.Serializable;
import java.util.Iterator;

/**
 * Created by User on 23.02.2016.
 */
public abstract class TaskList implements Cloneable, Serializable, Iterable<Task>{
    abstract public void add(Task task);
    abstract public boolean remove(Task task);
    abstract public int size();
    abstract public Task getTask(int index);
    abstract public Iterator<Task> iterator();
    abstract public boolean equals(TaskList a);
    abstract public int hashCode();
    abstract public String toString();


}
