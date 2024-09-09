package deque;

import java.util.Iterator;

public class ArrayDeque <T> implements Iterable<T>, Deque<T> {

    private T[] qequeFront;
    private T[] qequePost;
    private int sizeFront;
    private int sizePost;

    public ArrayDeque() {
        qequeFront = (T[]) new Object[4];
        qequePost = (T[]) new Object[4];
        sizeFront = 0; // cnt of actual elements
        sizePost = 0;
    }

    // arrayIdx takes 1 oe 0, 0 indicates qequeFront, i indicates qequePost.
    private void resize(int arrayIdx, int cpacity) {
        T[] newArray = (T[]) new Object[cpacity];
        if (arrayIdx == 0) {
            System.arraycopy(qequeFront, 0, newArray, 0, sizeFront);
            qequeFront = newArray;
        } else if (arrayIdx == 1) {
            System.arraycopy(qequePost, 0, newArray, 0, sizePost);
            qequePost = newArray;
        }
    }

    @Override
    public void addFirst(T x) {
        if (sizeFront < qequeFront.length) {
            qequeFront[sizeFront] = x;
            sizeFront += 1;
        } else {
            resize(0, sizeFront * 2);
            qequeFront[sizeFront] = x;
            sizeFront += 1;
        }
    }

    @Override
    public void addLast(T x) {
        if (sizePost < qequePost.length) {
            qequePost[sizePost] = x;
            sizePost += 1;
        } else {
            resize(1, sizePost * 2);
            qequePost[sizePost] = x;
            sizePost += 1;
        }
    }

    @Override
    public void printDeque() {
        String res = "";
        int i = 0;
        while (i < sizeFront + sizePost) {
            res += String.valueOf(get(i)) + " ";
            i += 1;
        }
        System.out.println(res + "\n");
    }

//    @Override
//    public boolean isEmpty() {
//        return sizeFront == 0 && sizePost == 0;
//    }

    @Override
    public int size() {
        return sizeFront + sizePost;
    }

    @Override
    public T removeFirst() {
        if (sizeFront == 0) {
            return null;
        }
        if (sizeFront <= qequeFront.length / 4) {
            resize(0, sizeFront * 2);
        }
        T res = qequeFront[sizeFront - 1];
        qequeFront[sizeFront - 1] = null;
        sizeFront -= 1;
        return res;
    }

    @Override
    public T removeLast() {
        if (sizePost == 0) {
            return null;
        }
        if (sizePost <= qequePost.length / 4) {
            resize(1, sizePost * 2);
        }
        T res = qequePost[sizePost - 1];
        qequePost[sizePost - 1] = null;
        sizePost -= 1;
        return res;
    }

    @Override
    public T get(int i) {
        if (i < sizeFront) {
            int targetPos = sizeFront - 1 - i;
            return qequeFront[targetPos];
        }
        else if (i < sizeFront + sizePost) {
            int targetPos = i - sizeFront;
            return qequePost[targetPos];
        }
        return null;
    }

    public String toString() {
        String res = "";
        int i = 0;
        while (i < sizeFront + sizePost) {
            res += String.valueOf(get(i)) + " ";
            i += 1;
        }
        return res;
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
        x.addLast(4);
        x.addFirst(5);
        x.removeFirst();
        x.removeLast();
        for (Integer i : x) {
            System.out.println(i);
        }

    }
}
