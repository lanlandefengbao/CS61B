package deque;

import java.util.Comparator;

public class MaxArrayDeque <T> extends ArrayDeque<T>{

    private Comparator<T> comparator;

    public MaxArrayDeque(Comparator<T> c) {
//        super();
        comparator = c;
    }

    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }
        T res = get(0);
        for(int i = 0; i < size()-1; i++) {
            if(c.compare(get(i), get(i+1)) < 0) {
                res = get(i+1);
            }
        }
        return res;

    }

    public T max() {
        return max(comparator);
    }
}
