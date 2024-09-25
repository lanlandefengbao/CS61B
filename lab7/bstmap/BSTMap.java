package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K,V>{

    private BSTtreeNode root;
    private int size;

    public BSTMap() {
//        root = null;
//        size = 0;
    }

    private class BSTtreeNode {

        private K Key;
        private V Value;
        private BSTtreeNode left;
        private BSTtreeNode right;

        private BSTtreeNode(K key, V value) {
            Key = key;
            Value = value;
            left = null;
            right = null;
        }

    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        return findK(key, root) != null;
    }

    private K findK(K key, BSTtreeNode cur) {
        if(cur == null){
            return null;
        }
        int gap = key.compareTo(cur.Key);
        if(gap == 0){
            return cur.Key;
        }
        if(gap > 0){
            return findK(key, cur.right);
        }
        else{
            return findK(key, cur.left);
        }
    }

    @Override
    public V get(K key) {
        return findV(key, root);
    }

    private V findV(K key, BSTtreeNode cur) {
        if(cur == null){
            return null;
        }
        int gap = key.compareTo(cur.Key);
        if(gap == 0){
            return cur.Value;
        }
        if(gap > 0){
            return findV(key, cur.right);
        }
        else{
            return findV(key, cur.left);
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        root = putRecursion(key, value, root);
    }

    private BSTtreeNode putRecursion(K key, V value, BSTtreeNode cur) {
        if(cur == null){
            cur = new BSTtreeNode(key, value);
            size += 1;
        }
        else if(key.compareTo(cur.Key) == 0){
            // nothing changed
        }
        else{
            if(key.compareTo(cur.Key) < 0){
                cur.left = putRecursion(key, value, cur.left);
            }
            else{
                cur.right = putRecursion(key, value, cur.right);
            }
        }
        return cur;
    }

    /**
     * prints out your BSTMap in order of increasing Key
     */
    // we need in-order traverse
    private void printInorder(){
        System.out.println(traverse(root));
    }

    private String traverse(BSTtreeNode cur){
        if(cur == null){
            return "";
        }
        String left = traverse(cur.left);
        String mid = String.valueOf(cur.Key)+": "+String.valueOf(cur.Value);
        String right = traverse(cur.right);
        if(left.isEmpty() && right.isEmpty()){
            return mid;
        }
        else if(left.isEmpty()){
            return mid+", "+right;
        }
        else if(right.isEmpty()){
            return left+", "+mid;
        }
        else{
            return left+", "+mid+", "+right;
        }
    }

    @Override
    public Set keySet() {
        throw new UnsupportedOperationException();
    }

    /** when removing a node:
     * 1.if it has two substrees, replace it with right subtree's MINnode;
     * 2.if it has one subtree, replace it with the subtree's root node (it must be a leaf node)
     * 3.if it is the leaf node, just drop it */

    @Override
    public V remove(K key) {
        if(root == null){
            return null;
        }
        V value = get(key);
        substitute(findNode(key, root));
        size -= 1;
        return value;
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    private BSTtreeNode findNode(K key, BSTtreeNode cur) {
        if(cur == null){
            return null;
        }
        int gap = key.compareTo(cur.Key);
        if(gap == 0){
            return cur;
        }
        if(gap > 0){
            return findNode(key, cur.right);
        }
        else{
            return findNode(key, cur.left);
        }
    }

    private BSTtreeNode findMinNode(BSTtreeNode cur){
        if(cur.left == null){
            return cur;
        }
        return findMinNode(cur.left);
    }

    private void gbc(K key, BSTtreeNode cur){
        if(key.compareTo(cur.Key) == 0){
            clear();
        }
        else{
            if(key.compareTo(cur.left.Key) == 0){
                cur.left = null;
            }
            else if(key.compareTo(cur.right.Key) == 0){
                cur.right = null;
            }
            else{
                if(key.compareTo(cur.Key) > 0){
                    gbc(key, cur.right);
                }
                else if(key.compareTo(cur.Key) < 0){
                    gbc(key, cur.left);
                }
            }
        }
    }

    private void substitute(BSTtreeNode node) {
        if(node.left == null && node.right == null){
            gbc(node.Key, root);
        }
        else if(node.left == null){
            node.Key = node.right.Key;
            node.Value = node.right.Value;
            node.right = null;
        }
        else if(node.right == null){
            node.Key = node.left.Key;
            node.Value = node.left.Value;
            node.left = null;
        }
        else{
            BSTtreeNode replacement = findMinNode(node.right);
            node.Key = replacement.Key;
            node.Value = replacement.Value;
            if(replacement.right == null){
                gbc(replacement.Key, root);
            }
            else{
                substitute(replacement);
            }
        }
    }

    @Override
    public Iterator<K> iterator() {
        return new BSTMapIterator();
    }

    private class BSTMapIterator implements Iterator<K> {

        private BSTtreeNode cur;

        public BSTMapIterator(){
            cur = root;
        }

        @Override
        public boolean hasNext() {
            return cur != null;
        }

        @Override
        public K next() {
            K key = findMinK(root);
            BSTMap.this.remove(key); //restructure current BSTtree
            return key;
        }
    }

    private K findMinK(BSTtreeNode cur){
        if(cur.left == null){
            K key = cur.Key;
            return key;
        }
        return findMinK(cur.left);
    }

    public static void main(String[] args){
        BSTMap<String, Integer> x = new BSTMap<>();
        x.put("a", 1);
        x.put("b", 2);
        x.put("c", 3);
        x.put("d", 4);
        x.put("e", 5);
        x.remove("d");
//        x.put("d",5);
        x.printInorder();
        System.out.println(x.size());
    }
}
