package deque;

import java.util.Deque;
import java.util.Iterator;

public class ArrayDeque<T> {
    public T[] items;
    public int size;
    //private int arraySize;
    private int firstIndex;
    private int lastIndex;

    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        //arraySize = 8;
        //firstIndex = arraySize/2 - 1;
        //lastIndex = arraySize/2;
    }

    public void addFirst(T item) {
        if (size == items.length) {
            resize(size * 2);
        }
        if (size == 0) {
            items[items.length/2 - 1] = item;
            firstIndex = items.length/2 - 1;
            lastIndex = items.length/2 - 1;
        } else if(firstIndex == 0) {
            items[size - 1] = item;
            firstIndex = size - 1;
        } else {
            items[firstIndex - 1] = item;
            firstIndex--;
        }
        size++;
    }

    public void addLast(T item) {
        if (size == items.length) {
            resize(size * 2);
        }
        if (size == 0) {
            items[items.length/2] = item;
            firstIndex = items.length/2;
            lastIndex = items.length/2;
        } else if(lastIndex == items.length - 1) {
            items[0] = item;
            lastIndex = 0;
        } else {
            items[lastIndex + 1] = item;
            lastIndex++;
        }
        size++;
    }

    public boolean isEmpty() {
        if (items[0] == null && items[items.length - 1] == null && items[items.length/2 - 1] == null && items[items.length/2] == null) {
            return true;
        }
        return false;
    }

    public int size() {
        return size;
    }

    public void printDeque() {  //图一
        if (firstIndex < lastIndex) {
            for (int i = firstIndex; i <= lastIndex; i++) {
                System.out.print(items[i] + " ");
            }
        } else {
            for (int i = firstIndex; i <= items.length - 1; i++) {
                System.out.print(items[i] + " ");
            }
            for (int i = 0; i <= lastIndex; i++) {
                System.out.print(items[i] + " ");
            }
        }
        System.out.println();
    }

    public int arraySize() {
        return items.length;
    }

    public void checkSize() {
        if (items.length > 4 * size && items.length >= 32) {
            resize(items.length / 2);
        }
    }

    public void resize(int capacity) {  //图1的扩展方法
        T[] a = (T[]) new Object[capacity];
        int tmpFirst = 0;
        int tmpLast = capacity - 1;
        if (firstIndex < lastIndex) {
            for (int i = firstIndex; i <= lastIndex; i++) {
                a[tmpFirst] = items[i];
                tmpFirst++;
            }
            firstIndex = 0;
            lastIndex = tmpFirst - 1;
        } else {
            for (int i = 0; i <= lastIndex; i++) {
                a[tmpFirst] = items[i];
                tmpFirst++;
            }
            for (int i = items.length - 1; i >= firstIndex; i--) {
                a[tmpLast] = items[i];
                tmpLast--;
            }
            firstIndex = tmpLast + 1;
            lastIndex = tmpFirst - 1;
        }
        items = a;
        //arraySize = capacity;
    }

    public T removeFirst() {
        if (size == 0) { //空
            return null;
        }
        checkSize();
        T x = items[firstIndex];
        items[firstIndex] = null;
        if (firstIndex == items.length - 1) {
            firstIndex = 0;
        } else {
            firstIndex++;
        }
        size--;
        return x;
    }

    public T removeLast() {
        if (size == 0) { //空
            return null;
        }
        checkSize();
        T x = items[lastIndex];
        items[lastIndex] = null;
        if(lastIndex == 0) {
            lastIndex = items.length - 1;
        } else {
            lastIndex--;
        }
        size--;
        return x;
    }

    public T get(int index) {
        return items[index];
    }

    public int getLastIndex() {
        return lastIndex;
    }

    public Iterator<T> iterator() {
        return new ADIterator();
    }

    private class ADIterator implements Iterator<T> {
        private int pos;
        public ADIterator() {
            pos = 0;
        }

        public boolean hasNext() {
            return pos < size;
        }

        public T next() {
            T returnItem = items[pos];
            pos++;
            return returnItem;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ArrayDeque tmp) {
            return this.size == tmp.size;
        }
        return false;
    }

}