package model;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static java.util.Objects.isNull;

/**
 * Created by User on 23.02.2016.
 */
public class LinkedTaskList extends TaskList {
    private class LinkedIterator implements Iterator<Task>{
        private Node lastNode = root;
        private int cursor;       // следующий элемент
        private int last = -1; // последний элемент


        public boolean hasNext() {
            return cursor < size();
        }

        public Task next(){
            if (!hasNext()) {
                throw new NoSuchElementException("Нет следующего елемента");
            } else {
                Node a = lastNode;
                last = cursor;
                lastNode = lastNode.next;
                cursor++;
                return a.data;
            }
        }

        public void remove() {
            if (last < 0)
                throw new IllegalStateException();
            try {
                LinkedTaskList.this.remove(getTask(last));
                cursor = last;
                last = -1;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }
    }
    private class Node {
        public Task data;
        public Node prev;
        public Node next;
    }
    private Node root;
    private Node last;
    private int amount;
    @Override
    public void add(Task task) {
        if(task == null)
            throw new NullPointerException("Добавление элемента null в список");
        if(isNull(root)){
            root = new Node();
            root.data = task;
            last = root;
        }
        else{
            Node a = new Node();
            a.data = task;
            last.next = a;
            a.prev = last;
            last = a;
        }
        amount++;
    }

    @Override
    public boolean remove(Task task) {
        Node tmp = root;
        if(task == null)
            throw new NullPointerException();
        while (tmp != null)
        {
            if(tmp.data.equals(task)){
                amount--;
                tmp.prev.next = tmp.next;
                return true;
            }
            tmp = tmp.next;
        }

        return false;
    }

    @Override
    public int size() {
        return amount;
    }

    @Override
    public Task getTask(int index) {
        Node tmp;
        if(index < amount/2.){
            tmp = root;
            for(int i = 0; i < index; i++){
                tmp = tmp.next;
            }
            return tmp.data;
        } else {
            tmp = last;
            for(int i = amount; i > index; i--){
                tmp = tmp.prev;
            }
            return tmp.data;
        }

    }


    public Iterator<Task> iterator() {
        return new LinkedIterator();
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
            result *= iter.next().hashCode()/11;
        }
        return result;
    }

    @Override
    public String toString() {
        if (this.size()<=1){
            return "";
        }
        String string = new String();
        Iterator iter = this.iterator();
        while (iter.hasNext()){
            string+= iter.next().toString();
        }
        return string;
    }
    public LinkedTaskList clone() throws CloneNotSupportedException {
        LinkedTaskList a = (LinkedTaskList) super.clone();
        a.root = this.root;
        a.last = this.last;
        a.amount = this.amount;
        return a;
    }

}
