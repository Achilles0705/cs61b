package deque;


import java.util.Deque;

public class LinkedListDeque<T> {
    private class IntNode {
        public T item;
        public IntNode prev;
        public IntNode next;

        public IntNode(T i,IntNode p, IntNode n){
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

    public void addFirst(T item) {
        IntNode p = new IntNode(item, sentinelBegin, sentinelBegin.next);
        sentinelBegin.next.prev = p;
        sentinelBegin.next = p;
        size++;
    }

    public void addLast(T item) {
        IntNode p = new IntNode(item, sentinelEnd.prev, sentinelEnd);
        sentinelEnd.prev.next = p;
        sentinelEnd.prev = p;
        size++;
    }

    public boolean isEmpty() {
        return (sentinelBegin.next == sentinelEnd);
    }

    public int size() { return size; }

    public void printDeque() {
        IntNode p = sentinelBegin;
        while(p != null) {
            System.out.print(p.item + " ");
            p = p.next;
        }
        System.out.println();
    }

    public T removeFirst() {
        if(size == 0) { //空
            return null;
        }
        size--;
        IntNode p = sentinelBegin.next;
        T tmp = p.item;
        if(size == 0) {    //一个元素
            sentinelBegin.next = sentinelEnd;
            sentinelEnd.prev = sentinelBegin;
            return tmp;
        }
        sentinelBegin.next = p.next;
        p.next.prev = sentinelBegin;
        return tmp;
    }

    public T removeLast() {
        if(size == 0) { //空
            return null;
        }
        size--;
        IntNode p = sentinelEnd.prev;
        T tmp = p.item;
        if(size == 0) {    //一个元素
            sentinelBegin.next = sentinelEnd;
            sentinelEnd.prev = sentinelBegin;
            return tmp;
        }
        sentinelEnd.prev = p.prev;
        p.prev.next = sentinelEnd;
        return tmp;
    }

    public T get(int index) {
        IntNode p = sentinelBegin;
        while(index != 0){
            p = p.next;
            index--;
        }
        return p.item;
    }

    public T getRecursive(IntNode p, int index)  {
        if(index == 0) {
            return p.item;
        }
        return getRecursive(p.next, index-1);
    }

    /*public Iterator<T> iterator() {

    }*/

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

}