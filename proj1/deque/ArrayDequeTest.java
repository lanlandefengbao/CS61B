package deque;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

public class ArrayDequeTest {

    @Test
    public void randomTest() {
        ArrayDeque<Integer> x = new ArrayDeque<>();
        int N = 5000;
        for (int i = 0; i < N; i++) {
            int actionIdx = StdRandom.uniform(0,4);
            if (actionIdx == 0) {
                int element = StdRandom.uniform(0,100);
                x.addFirst(element);
            }
            else if (actionIdx == 1) {
                int element = StdRandom.uniform(0,100);
                x.addLast(element);
            }
            else if (actionIdx == 2) {
                x.removeLast();
            }
            else if (actionIdx == 3) {
                x.removeFirst();
            }
            else{
                int element = StdRandom.uniform(0,100);
                x.get(element);
            }
        }

    }

    @Test
    public void testEquals() {
        ArrayDeque<Integer> x = new ArrayDeque<>();
        ArrayDeque<Integer> y = new ArrayDeque<>();
        for (int i = 0; i < 20; i++) {
            x.addFirst(i);
            y.addFirst(i);
        }
        System.out.println(x.equals(y));
        System.out.println(x);
    }

}
