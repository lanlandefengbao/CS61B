package deque;

public class LinkedListDeque<type> {

    private ListNode sentinel;
    private ListNode last;
    private int size;

    private class ListNode {
        private type val;
        private ListNode next;
        private ListNode previous;
        private ListNode(type value, ListNode nxt, ListNode prev) {
            val = value;
            next = nxt;
            previous = prev;
        }
    }

    public LinkedListDeque() {
        sentinel = new ListNode((type) "sentinel", null, null);
        last = sentinel;
        size = 0;
    }

    public void addFirst(type x) {
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

    public void addLast(type x) {
        last.next = new ListNode(x, null, last);
        last = last.next;
        size ++;
    }

    public boolean isEmpty() {
        if (size == 0) {
            return true;
        }
        return false;
    }

    public int size() {
        return size;
    }

    public String printDeque() {
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
        return res;
    }

    public type removeFirst() {
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

    public type removeLast() {
        if (sentinel.next == null) {
            return null;
        }
        ListNode pre = last.previous;
        pre.next = null;
        type res = last.val;
        last = pre;
        size --;
        return res;
    }

    public ListNode get(int i) {
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
        return cur;
    }

    public void insert(type x, int i ) {
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
    }

}
