package bstmap;

import java.util.*;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    private BSTNode root;

    private Stack<BSTNode> ancestorStack = new Stack<>();

    private class  BSTNode {
        private K key;
        private V val;
        private BSTNode left, right;
        private int size;

        public BSTNode(K key, V val, int size) {
            this.key = key;
            this.val = val;
            this.size = size;
        }
    }

    public void printInOrder() {
        printNode(root);
    }

    private void printNode(BSTNode node) {
        if (node != null) {
            printNode(node.left);
            System.out.println("Key: %s" + node.key + "Value: %d" + node.val);
            printNode(node.right);
        }
    }

    @Override
    public void clear() {
        root = clear(root);
    }

    private BSTNode clear(BSTNode node) {
        if (node == null) {
            return null;
        }
        node.left = clear(node.left);
        node.right = clear(node.right);
        node = null;
        return null;
    }

    @Override
    public boolean containsKey(K key) {
        if (key == null) {
            throw new IllegalArgumentException("calls containsKey() with a null key");
        }
        //return get(key) != null;
        return get(root, key) != null;
    }

    private V get(BSTNode x, K key) {
        if (x == null) return  null;
        int cmp = key.compareTo(x.key);
        if (cmp < 0) return get(x.left, key);
        else if (cmp > 0) return get(x.right, key);
        else return x.val;
    }

    @Override
    public V get(K key) {
        if (key == null) {
            throw new IllegalArgumentException("calls get() with a null key");
        }
        return get(root ,key);
    }

    @Override
    public int size() {
        return size(root);
    }

    private int size(BSTNode x) {
        if (x == null) return 0;
        else return x.size;
    }

    @Override
    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("calls put() with a null key");
        }
        root = put(root, key, value);
    }

    private BSTNode put(BSTNode x, K key, V val) {
        if (x == null) return new BSTNode(key, val ,1);
        int cmp = key.compareTo(x.key);
        if (cmp < 0) {
            x.left = put(x.left, key, val);
        } else if (cmp > 0) {
            x.right = put(x.right, key, val);
        } else {
            x.val = val;
        }
        x.size = 1 + size(x.left) + size(x.right);
        return x;
    }

    @Override
    public Set<K> keySet() {
        Set<K> keys = new HashSet<>();
        collectKeys(root, keys);
        return keys;
    }

    private void collectKeys(BSTNode node, Set<K> keys) {
        if (node == null)   return;
        collectKeys(node.left, keys);
        keys.add(node.key);
        collectKeys(node.right, keys);
    }

    @Override
    public V remove(K key) {
        BSTNode temp = null;
        if (key == null) throw new IllegalArgumentException("calls remove() with a null key");
        if (!containsKey(key)) return null;
        return remove(root, key, null);
    }

    @Override
    public V remove(K key, V value) {
        if (key == null) throw new IllegalArgumentException("calls remove() with a null key");
        if (!containsKey(key)) return null;
        if (get(key) != value) return null;
        return remove(root, key, null);
    }

    private V remove(BSTNode node, K key, BSTNode parent) {
        if (node == null) return null;
        ancestorStack.push(parent);
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            return remove(node.left, key, node);
        } else if (cmp > 0) {
            return remove(node.right, key, node);
        } else {    //找到目标节点
            V removedValue = node.val;

            BSTNode temp;
            while(!ancestorStack.isEmpty() && (temp = ancestorStack.pop()) != null) {
                temp.size--;
            }

            if (node.left == null && node.right == null) {  //1.没有子节点
                if (parent == null) {
                    root = null;
                } else if (parent.left == node) {
                    parent.left = null;
                } else {
                    parent.right = null;
                }
            } else if (node.left == null) {   //2.只有一个子节点（右）
                if (parent == null) {
                    root = node.right;
                } else if (parent.left == node) {
                    parent.left = node.right;
                } else {
                    parent.right = node.right;
                }
            } else if (node.right == null) {    //2.只有一个子节点（左）
                if (parent == null) {
                    root = node.left;
                } else if (parent.left == node) {
                    parent.left = node.left;
                } else {
                    parent.right = node.left;
                }
            } else {    //3.有两个子节点
                BSTNode successor = findMin(node.right);
                node.key = successor.key;
                node.val = successor.val;
                remove(node.right, successor.key, node);
            }

            return removedValue;
        }
    }

    private BSTNode findMin(BSTNode node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    @Override
    public Iterator<K> iterator() {
        return new BSTMapIterator();
    }

    private class BSTMapIterator implements Iterator<K> {
        private Stack<BSTNode> stack;

        public BSTMapIterator() {
            stack = new Stack<>();
            pushLeft(root);
        }

        private void pushLeft(BSTNode node) {
            while(node != null) {
                stack.push(node.left);
                node = node.left;
            }
        }

        public boolean hasNext() {
            return !stack.isEmpty();
        }

        public K next() {
            if (!hasNext()) {
                throw new NoSuchElementException("BSTMap iterator is empty");
            }
            BSTNode current = stack.pop();
            pushLeft(current.right);
            return current.key;
        }

    }

}
