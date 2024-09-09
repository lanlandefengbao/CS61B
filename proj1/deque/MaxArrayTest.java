package deque;

import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.assertEquals;

public class MaxArrayTest {

    @Test
    public void StringMaxTest() {
        class comparatorS implements Comparator<String> {
            @Override
            public int compare(String o1, String o2) {
                return o1.length() - o2.length();
            }
        }
        MaxArrayDeque<String> x = new MaxArrayDeque<>(new comparatorS());
        for(int i = 0; i < 20; i++) {
            x.addLast(i+"");
        }
        // test max() with the default comparator
        assertEquals("10", x.max());

        // test max(Comparator c)
        class comparatorS2 implements Comparator<String> {
            @Override
            public int compare(String o1, String o2) {
                return Integer.parseInt(o1) - Integer.parseInt(o2);
            }
        }
        assertEquals("19", String.valueOf(x.max(new comparatorS2())));
    }
}
