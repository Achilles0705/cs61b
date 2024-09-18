package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author Achilles
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!
    private HashSet<K> allKeys = new HashSet<>();

    private static final int initialSize = 16;

    private static final double initialFactor = 0.75;

    private int n;

    private static double loadFactor;

    //private int[] table;

    /** Constructors */
    public MyHashMap() {
        this(initialSize);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, initialFactor);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        loadFactor = maxLoad;
        //this.size = initialSize;
        //table = new int[initialSize];
        buckets = createTable(initialSize);
    }

    private int hash(K key) {
        int h = key.hashCode();
        return Math.abs(h) % buckets.length;    //Object.java中的hashcode()返回的是
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
        //Node cur = new Node(key, value);
        //put(key, value);
        //return cur;
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new ArrayList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        buckets = new Collection[tableSize];
        for (int i = 0; i < tableSize; i++) {
            buckets[i] = createBucket();
        }
        return buckets;
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!

    public void clear() {
        for (int i = 0; i < buckets.length; i++) {
            buckets[i].clear();
        }
        n = 0;
    }

    public boolean containsKey(K key) {
        int i = hash(key);
        //return buckets[i] != null;
        for (Node node : buckets[i]) {
            if (node.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    public V get(K key) {
        int i = hash(key);
        for (Node node : buckets[i]) {
            if (node.key.equals(key)) { // 找到 key 相同的节点
                return node.value; // 返回对应的 value
            }
        }
        return null;
    }

    public int size() {
        return this.n;
    }

    private void resize() {

    }

    public void put(K key, V value) {
        int i = hash(key);
        for (Node node : buckets[i]) {
            if (node.key.equals(key)) { // 找到 key 相同的节点
                node.value = value;
                return;
            }
        }
        Node current = new Node(key, value);
        this.n++;
        buckets[i].add(current);
        allKeys.add(key);
    }

    public Set<K> keySet() {
        return this.allKeys;
    }

    public V remove(K key) {
        if (!containsKey(key)) {
            return null;
        }
        int i = hash(key);
        for (Node node : buckets[i]) {
            if (node.key.equals(key)) {
                buckets[i].remove(node);
                allKeys.remove(key);
                this.n--;
                return node.value;
            }
        }
        return null;
    }

    public V remove(K key, V value) {
        if (!containsKey(key)) {
            return null;
        }
        int i = hash(key);
        for (Node node : buckets[i]) {
            if (node.key.equals(key)) {
                if (node.value != value) {
                    return null;
                }
                buckets[i].remove(node);
                allKeys.remove(key);
                this.n--;
                return node.value;
            }
        }
        return null;
    }

    public Iterator<K> iterator() {
        return allKeys.iterator();
    }

    /*private class KeyIterator implements Iterator<K> {

        private int bucketIndex = 0;
        private Node currentNode = (Node) buckets[bucketIndex];

        public boolean hasNext() {
            for (int i = bucketIndex; i < buckets.length; i++) {
                while (buckets[i] != null)
            }
        }

    }*/

}
