package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author B Li
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
    private int cntBucket = 16;
    private double upperLoadFactor = .75;
    private int size = 0;

    /**
     * Constructors
     */
    public MyHashMap() {
        buckets = new Collection[cntBucket];
    }

    public MyHashMap(int initialSize) {
        cntBucket = initialSize;
        buckets = new Collection[cntBucket];
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad     maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        cntBucket = initialSize;
        upperLoadFactor = maxLoad;
        buckets = new Collection[cntBucket];
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     * <p>
     * The only requirements of a hash table bucket are that we can:
     * 1. Insert items (`add` method)
     * 2. Remove items (`remove` method)
     * 3. Iterate through items (`iterator` method)
     * <p>
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     * <p>
     * Override this method to use different data structures as
     * the underlying bucket type
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<Node>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] newBuckets = new Collection[tableSize];
        return newBuckets;
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!

    @Override
    public void clear() {
        size = 0;
        cntBucket = 16;
        buckets = createTable(cntBucket);
    }

    @Override
    public boolean containsKey(K key) {
        int keyHash = (key.hashCode() & 0x7fffffff) % cntBucket;
        if(buckets[keyHash] == null) {
            return false;
        }
        for(Node n : buckets[keyHash]) {
            if(n.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public V get(K key) {
        int keyHash = (key.hashCode() & Integer.MAX_VALUE) % cntBucket;
        if (buckets[keyHash] == null) {
            return null;
        }
        for (Node n : buckets[keyHash]) {
            if (n.key.equals(key)) {
                return n.value;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        // resizing
        if ((double) size / cntBucket > upperLoadFactor) {
            //prepare for new buckets
            if (cntBucket < Math.pow(2, 30)) {
                cntBucket = cntBucket * 2;
            } else {
                cntBucket = Integer.MAX_VALUE;
            }
            Collection<Node>[] newBuckets = createTable(cntBucket);
            size = 0;
            //iterate over each Key-value mapping of each collection
            for (Collection<Node> c : buckets) {
                if (c == null) {
                    continue;
                }
                for (Node n : c) {
                    int keyHash = (n.key.hashCode() & Integer.MAX_VALUE) % cntBucket;
                    if (newBuckets[keyHash] == null) {
                        newBuckets[keyHash] = createBucket();
                        newBuckets[keyHash].add(n);
                    } else {
                        newBuckets[keyHash].add(n);
                    }
                    size += 1;
                }
            }
            buckets = newBuckets;
        }
        // put
        int keyHash = (key.hashCode() & 0x7fffffff) % cntBucket;
        if (buckets[keyHash] == null) {
            buckets[keyHash] = createBucket();
            buckets[keyHash].add(createNode(key, value));
            size += 1;
        } else {
            // iterating the exist collection to testify whether need to update an old key's value
            // all relevant Iterators(for int[] and for LinkedList) has been implemented by default
            boolean replaced = false;
            for (Node n : buckets[keyHash]) {
                if (n.key.equals(key)) {
                    buckets[keyHash].remove(n);
                    replaced = true;
                    break;
                }
            }
            buckets[keyHash].add(createNode(key, value));
            if(!replaced){
                size += 1;
            }
        }
    }

    @Override
    public Set<K> keySet() {
        Set<K> s = new HashSet<>();
        for (Collection<Node> c : buckets) {
            if (c == null) {
                continue;
            }
            for (Node n : c) {
                s.add(n.key);
            }
        }
        return s;
    }

    @Override
    public V remove(K key) {
        if(!containsKey(key)) {
            throw new UnsupportedOperationException("no such key found in current HashMap");
        }
        int keyHash = (key.hashCode() & Integer.MAX_VALUE) % cntBucket;
        for(Node n : buckets[keyHash]) {
            if(n.key == key) {
                buckets[keyHash].remove(n);
                size -= 1;
                return n.value;
            }
        }
        return null;
    }

    @Override
    public V remove(K key, V value) {
        return remove(key);
    }

    // allowing iterating through keys with enhanced for statement
    @Override
    public Iterator<K> iterator() {
        return new keyIterator();
    }

    class keyIterator implements Iterator<K> {

        int bucketIdx = 0;
        private Iterator<Node> getNodeIterator() {
            while(bucketIdx < buckets.length) {
                if(buckets[bucketIdx] != null) {
                    Iterator<Node> It = buckets[bucketIdx].iterator();
                    bucketIdx += 1;
                    return It;
                }
                bucketIdx += 1;
            }
            return null;
        }

        Iterator<Node> nodeIterator = getNodeIterator();

        @Override
        public boolean hasNext() {
            if(nodeIterator == null) {
                return false;
            }
            return nodeIterator.hasNext();
        }

        @Override
        public K next() {
            K res =  nodeIterator.next().key;
            if(!nodeIterator.hasNext()) {
                nodeIterator = getNodeIterator();
            }
            return res;
        }
    }

}

