package deque;

import java.util.Iterator;

import static java.lang.System.arraycopy;

public class ArrayDeque <T> implements Iterable<T>, Deque<T> {

    private int size;
    private T[] items;
    private int frontNext;
    private int postNext;

    public ArrayDeque() {
        size = 0;
        items = (T[]) new Object[8];
        frontNext = 8-1;
        postNext = 0;
    }

    private void resize(int capacity) {
        T[] newArray = (T[]) new Object[capacity];
        // if no addFirst before
        if(frontNext < size-1){
            arraycopy(items,frontNext+1, newArray, 0, (size-1)-frontNext);
            arraycopy(items, 0, newArray, (size-1)-frontNext, frontNext+1);
        }
        // if addFirst before
        else if(frontNext == size-1){
            arraycopy(items, 0, newArray, 0,size);
        }
        items = newArray;
        frontNext = capacity-1;
        postNext = size;
    }

    @Override
    public void addFirst(T x) {
        if(size == items.length){
            resize(size*2);
        }
        items[frontNext] = x;
        frontNext -= 1;
        if(frontNext < 0){
            frontNext = items.length-1;
        }
        size += 1;
    }

    @Override
    public void addLast(T x) {
        if(size == items.length){
            resize(size*2);
        }
        items[postNext] = x;
        postNext += 1;
        if(postNext == items.length){
            postNext = 0;
        }
        size += 1;
    }

    public String toString() {
        String res = "";
        if(frontNext == items.length-1){
            for(int i = 0; i < postNext; i++){
                res += items[i]+" ";
            }
        } else if(frontNext < items.length-1){
            for(int i = frontNext+1; i < items.length; i++){
                res += items[i]+" ";
            }
            for(int i = 0; i < postNext; i++){
                res += items[i]+" ";
            }
        }
        return res;
    }

    @Override
    public void printDeque() {
        System.out.println(this);
    }

//    @Override
//    public boolean isEmpty() {
//        return size == 0;
//    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public T removeFirst() {
        if(size == 0){
            return null;
        }
        T res;
        if(frontNext == items.length-1){
            res = items[0];
            items[0] = null;
            frontNext = 0;
        }
        else{
            res = items[frontNext+1];
            items[frontNext+1] = null;
            frontNext += 1;
        }
        size -= 1;
        //resize the size down if it's too wasteful
        if(size <= items.length/4 && size >= 1){
            resize(items.length/2);
        }
        return res;
    }

    @Override
    public T removeLast() {
        if(size == 0){
            return null;
        }
        T res;
        if(postNext != 0){
            res = items[postNext-1];
            items[postNext-1] = null;
            postNext -= 1;
        }
        else{
            postNext = items.length-1;
            res = items[postNext];
            items[postNext] = null;
        }
        size -= 1;
        //resize the size down if it's too wasteful
        if(size <= items.length/4 && size >= 1){
            resize(items.length/2);
        }
        return res;
    }

//    @Override
//    public T get(int i) {
//        if(i >= size){
//            return null;
//        }
//        int targetIdx;
//        if(frontNext == items.length-1){
//            if(size != items.length) {
//                targetIdx = i;
//            }
//            else{
//                targetIdx = (items.length-1)-i;
//            }
////            targetIdx = i;
//        }
//        else{
//            int dev = (items.length-1)-frontNext;
//            if(i <= dev){
//                targetIdx = frontNext+i;
//            }
//            else{
//                targetIdx = i-dev;
//            }
//        }
//        return items[targetIdx];
//    }

    @Override
    public T get(int i) {
        if(i >= size){
            return null;
        }
        int targetIdx = (frontNext + 1 + i) % items.length;
        return items[targetIdx];
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int currentIndex = 0;

        @Override
        public boolean hasNext() {
            return currentIndex < size();
        }

        @Override
        public T next() {
            T item = get(currentIndex);
            currentIndex += 1;
            return item;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof ArrayDeque Deque) {
            for (int i = 0; i < this.size(); i++) {
                if (this.get(i) == Deque.get(i)) {
                    continue;
                }
                else {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static void main(String[] arg) {
        ArrayDeque<Integer> x = new ArrayDeque<>();
        x.addFirst(1);
        x.addFirst(2);
        x.addFirst(5);
        x.addFirst(6);
        x.addFirst(7);
        x.addFirst(8);
        x.addFirst(9);
        x.addFirst(10);
        x.addFirst(11);
        x.addLast(11);
        x.removeLast();
        x.removeLast();
        x.removeFirst();
        x.printDeque();
    }
}
