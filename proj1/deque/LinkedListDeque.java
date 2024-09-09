package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Iterable<T>, Deque<T>{

    private ListNode sentinel;
    private ListNode last;
    private int size;

    @Override
    public Iterator<T> iterator() {
        return new DequeIterator();
    }

    private class DequeIterator implements Iterator<T> {

        private ListNode cur = sentinel.next;

        @Override
        public boolean hasNext() {
            return cur != null;
        }

        @Override
        public T next() {
            T res = cur.val;
            cur = cur.next;
            return res;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof LinkedListDeque Deque) {
            if (Deque.size != size) {
                return false;
            }
            ListNode cur1 = sentinel;
            ListNode cur2 = Deque.sentinel;
            while (cur1 != null) {
                if (cur1.val == cur2.val) {
                    cur1 = cur1.next;
                    cur2 = cur2.next;
                }
                else {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private class ListNode {
        private T val;
        private ListNode next;
        private ListNode previous;
        private ListNode(T value, ListNode nxt, ListNode prev) {
            val = value;
            next = nxt;
            previous = prev;
        }
    }

    public LinkedListDeque() {
        sentinel = new ListNode((T) "sentinel", null, null);
        last = sentinel;
        size = 0;
    }

    @Override
    public void addFirst(T x) {
        ListNode first = new ListNode(x, sentinel.next, sentinel);
        if (size < 1) {
            last = first;
        }
        else {
            sentinel.next.previous = first;
        }
        sentinel.next = first;
        size ++;
    }

    @Override
    public void addLast(T x) {
        last.next = new ListNode(x, null, last);
        last = last.next;
        size ++;
    }

//    @Override
//    public boolean isEmpty() {
//        if (size == 0) {
//            return true;
//        }
//        return false;
//    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        String res = "";
        ListNode cur = sentinel.next;
        while (cur != null) {
            if (cur.next != null) {
                res += String.valueOf(cur.val) + " ";
            }
            else {
                res +=  String.valueOf(cur.val) + "\n";
            }
            cur = cur.next;
        }
        System.out.println(res);
    }

    @Override
    public T removeFirst() {
        if (sentinel.next == null) {
            return null;
        }
        else {
            ListNode first = sentinel.next;
            sentinel.next = sentinel.next.next;
            size --;
            return first.val;
        }
    }

    @Override
    public T removeLast() {
        if (sentinel.next == null) {
            return null;
        }
        ListNode pre = last.previous;
        pre.next = null;
        T res = last.val;
        last = pre;
        size --;
        return res;
    }

    @Override
    public T get(int i) {
        ListNode cur = sentinel.next;
        while (i > 0) {
            if (cur != null) {
                cur = cur.next;
                i --;
            }
            else {
                return null;
            }
        }
        return cur.val;
    }

    public T getRecursive(int index) {
        if (sentinel == null) {
            return null;
        }
        if (index == 0) {
            return last.val;
        }
//        return getRecursive(index -= 1);
        return getRecursive(--index);
    }


    public void insert(T x, int i ) {
        if (i > size) {
            return;
        }
        ListNode cur = sentinel.next;
        while (i > 1) {
            cur = cur.next;
            i -= 1;
        }
        ListNode newNode = new ListNode(x, cur, cur.previous);
        cur.previous.next = newNode;
        cur.previous = newNode;
    }

    public String toString() {
        String res = String.valueOf(sentinel.val);
        ListNode cur = sentinel.next;
        while (cur != null) {
            res += " <-> " + cur.val;
            cur = cur.next;
        }
        return res;
    }

    public static void main(String[] arg) {
        LinkedListDeque x = new LinkedListDeque();
        x.addFirst(1);
        x.addFirst(2);
        x.addFirst(3);
        System.out.println(x);
        x.insert(7,2);
        x.insert(10,10);
        System.out.println(x);
        System.out.println(x.get(3));
        System.out.println(x.getRecursive(3));
    }

}
