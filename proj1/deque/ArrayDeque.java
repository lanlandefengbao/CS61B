package deque;

public class ArrayDeque <type> {

    private type[] qequeFront;
    private type[] qequePost;
    private int sizeFront;
    private int sizePost;

    public ArrayDeque() {
        qequeFront = (type[]) new Object[4];
        qequePost = (type[]) new Object[4];
        sizeFront = 0; // cnt of actual elements
        sizePost = 0;
    }

    // arrayIdx takes 1 oe 0, 0 indicates qequeFront, i indicates qequePost.
    private void resize(int arrayIdx, int cpacity) {
        type[] newArray = (type[]) new Object[cpacity];
        if (arrayIdx == 0) {
            System.arraycopy(qequeFront, 0, newArray, 0, sizeFront);
            qequeFront = newArray;
        } else if (arrayIdx == 1) {
            System.arraycopy(qequePost, 0, newArray, 0, sizePost);
            qequePost = newArray;
        }
    }

    public void addFirst(type x) {
        if (sizeFront < qequeFront.length) {
            qequeFront[sizeFront] = x;
            sizeFront += 1;
        } else {
            resize(0, sizeFront * 2);
            qequeFront[sizeFront] = x;
            sizeFront += 1;
        }
    }

    public void addLast(type x) {
        if (sizePost < qequePost.length) {
            qequePost[sizePost] = x;
            sizePost += 1;
        } else {
            resize(1, sizePost * 2);
            qequePost[sizePost] = x;
            sizePost += 1;
        }
    }

    public void printDeque() {
        String res = "";
        int i = 0;
        while (i < sizeFront + sizePost) {
            res += String.valueOf(get(i)) + " ";
            i += 1;
        }
        System.out.println(res + "\n");
    }

    public boolean isEmpty() {
        return sizeFront == 0 && sizePost == 0;
    }

    public int size() {
        return sizeFront + sizePost;
    }

    public type removeFirst() {
        if (sizeFront == 0) {
            return null;
        }
        if (sizeFront <= qequeFront.length / 4) {
            resize(0, sizeFront * 2);
        }
        type res = qequeFront[sizeFront - 1];
        qequeFront[sizeFront - 1] = null;
        sizeFront -= 1;
        return res;
    }

    public type removeLast() {
        if (sizePost == 0) {
            return null;
        }
        if (sizePost <= qequePost.length / 4) {
            resize(1, sizePost * 2);
        }
        type res = qequePost[sizePost - 1];
        qequePost[sizePost - 1] = null;
        sizePost -= 1;
        return res;
    }

    public type get(int i) {
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

    public static void main(String[] arg) {
        ArrayDeque x = new ArrayDeque<>();
        x.addFirst(1);
        x.addFirst(2);
        x.addFirst("a");
        x.addLast(new int[]{1,2,3});
        x.addLast(4);
        x.addFirst(5);
        x.removeFirst();
        x.removeLast();
        x.printDeque();
        System.out.println(x);
    }
}

