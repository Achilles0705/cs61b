package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T> {
    private class IntNode {
        private T item;
        private IntNode prev;
        private IntNode next;

        IntNode(T i, IntNode p, IntNode n) {
            item = i;
            prev = p;
            next = n;
        }
    }

    private IntNode sentinelBegin;
    private IntNode sentinelEnd;
    private int size;

    public LinkedListDeque() {
        sentinelBegin = new IntNode(null, null, null);
        sentinelEnd = new IntNode(null, sentinelBegin, null);
        sentinelBegin.next = sentinelEnd;
        size = 0;
    }

    public LinkedListDeque(T x) {
        sentinelBegin = new IntNode(null, null, null);
        sentinelEnd = new IntNode(null, sentinelBegin, null);
        sentinelBegin.next = sentinelEnd;
        IntNode tmp = new IntNode(x, sentinelBegin, sentinelEnd);
        sentinelBegin.next = tmp;
        sentinelEnd.prev = tmp;
        size = 1;
    }

    @Override
    public void addFirst(T item) {
        IntNode p = new IntNode(item, sentinelBegin, sentinelBegin.next);
        sentinelBegin.next.prev = p;
        sentinelBegin.next = p;
        size++;
    }

    @Override
    public void addLast(T item) {
        IntNode p = new IntNode(item, sentinelEnd.prev, sentinelEnd);
        sentinelEnd.prev.next = p;
        sentinelEnd.prev = p;
        size++;
    }

    /*public boolean isEmpty() {
        return (sentinelBegin.next == sentinelEnd);
    }*/

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        IntNode p = sentinelBegin.next;
        while (p.next != null) {
            System.out.print(p.item + " ");
            p = p.next;
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (size == 0) { //空
            return null;
        }
        size--;
        IntNode p = sentinelBegin.next;
        T tmp = p.item;
        if (size == 0) {    //一个元素
            sentinelBegin.next = sentinelEnd;
            sentinelEnd.prev = sentinelBegin;
            return tmp;
        }
        sentinelBegin.next = p.next;
        p.next.prev = sentinelBegin;
        return tmp;
    }

    @Override
    public T removeLast() {
        if (size == 0) { //空
            return null;
        }
        size--;
        IntNode p = sentinelEnd.prev;
        T tmp = p.item;
        if (size == 0) {    //一个元素
            sentinelBegin.next = sentinelEnd;
            sentinelEnd.prev = sentinelBegin;
            return tmp;
        }
        sentinelEnd.prev = p.prev;
        p.prev.next = sentinelEnd;
        return tmp;
    }

    @Override
    public T get(int index) {
        IntNode p = sentinelBegin.next;
        while (index != 0) {
            p = p.next;
            index--;
        }
        return p.item;
    }

    public T getRecursive(IntNode p, int index) {
        if (index == 0) {
            return p.item;
        }
        return getRecursive(p.next, index - 1);
    }

    public Iterator<T> iterator() {
        return new LLIterator();
    }

    private class LLIterator implements Iterator<T> {
        //private int pos;
        IntNode p;
        LLIterator() {
            this.p = sentinelBegin.next;
        }

        public boolean hasNext() {
            return p.next != null;
        }

        public T next() {
            T returnItem = p.next.item;
            p = p.next;
            return returnItem;
        }

    }

    /*public boolean equals(Object o) {
        if(!(o instanceof Deque)){
            return false;
        }
        Deque tmp = ((Deque) o).reversed();
        if(tmp.size() != size){
            return false;
        }
        IntNode p = sentinelBegin;
        while(p != null){
            if(o.equals())
        }
    }*/

    @Override
    /*public boolean equals(Object o) {
        if (o instanceof LinkedListDeque tmp) {
            return this.size == tmp.size;
        }
        return false;
    }*/
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof Deque)) {
            return false;
        }
        Deque<T> oc = (Deque<T>) o;
        if (oc.size() != this.size) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (!(this.get(i).equals(oc.get(i)))) {
                return false;
            }
        }
        return true;
    }

}
