package lab11.graphs;

import java.util.Comparator;
import java.util.PriorityQueue;

public class ModifiedPQ<E> extends PriorityQueue<E> {
    public ModifiedPQ(Comparator<? super E> comparator) {
        super(comparator);
    }

    public void changePriority(E item, double newPriority) {
        boolean removed = this.remove(item);
        if (removed) {
            if (item instanceof int[] array) {
                array[1] = (int) newPriority;
            }
            this.add(item);
        }
    }
}
