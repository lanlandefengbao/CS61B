package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void test1() {
        int[] nums = new int[]{4,5,6};
        AListNoResizing<Integer> l1 = new AListNoResizing<>();
        BuggyAList<Integer> l2 = new BuggyAList<>();
        for (int i = 0; i < nums.length; i++) {
            l1.addLast(nums[i]);
            l2.addLast(nums[i]);
        }
        assertEquals(l1.size(), l2.size());

        for (int i = 0; i < nums.length; i++) {
            assertEquals(l1.removeLast(), l2.removeLast());
        }
    }

    @Test
    public void test2() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> L2 = new BuggyAList<>();
        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                L2.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                int size2 = L2.size();
            } else if (operationNumber == 2) {
                if (L.size() > 0) {
                    int x = L.removeLast();
                    int x2 = L2.removeLast();
                }
            } else {
                if (L.size() > 0) {
                    int x = L.getLast();
                    int x2 = L2.getLast();
                }
            }
        }
    }
}
