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
        buckets = createTable(16);
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        int keyHash = (key.hashCode() & 0x7fffffff) % cntBucket;
        return buckets[keyHash] != null;
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
        } else {
            // iterating the exist collection to testify whether need to update an old key's value
            for (Node n : buckets[keyHash]) {
                if (n.key.equals(key)) {
                    buckets[keyHash].remove(n);
                }
            }
            buckets[keyHash].add(createNode(key, value));
        }
        size += 1;
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
        return null;
    }

    @Override
    public V remove(K key, V value) {
        return null;
    }

        @Override
    public Iterator<K> iterator() {
        return new nodeIterator();
    }
//
//    class nodeIterator implements Iterator<K> {
//
//        @Override
//        public boolean hasNext() {
//            return
//        }
//
//        @Override
//        public K next() {
//            return
//        }
//    }
    
    class nodeIterator implements Iterator<K> {
        private int bucketIndex = 0;
        private Iterator<Node> nodeIterator = getNextBucketIterator();

        private Iterator<Node> getNextBucketIterator() {
            while (bucketIndex < buckets.length) {
                if (buckets[bucketIndex] != null && !buckets[bucketIndex].isEmpty()) {
                    return buckets[bucketIndex].iterator();
                }
                bucketIndex++;
            }
            return Collections.emptyIterator();
        }

        @Override
        public boolean hasNext() {
            if (nodeIterator.hasNext()) {
                return true;
            } else {
                bucketIndex++;
                nodeIterator = getNextBucketIterator();
                return nodeIterator.hasNext();
            }
        }

        @Override
        public K next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return nodeIterator.next().key;
        }
    }
}

