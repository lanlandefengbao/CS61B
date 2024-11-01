package lab11.graphs;

import java.util.*;

import java.util.Comparator;
import java.util.PriorityQueue;

public class ModifiedPQ<E> extends PriorityQueue<E> {
    private final HashMap<E, Integer> priorityMap;

    public ModifiedPQ(Comparator<? super E> comparator) {
        super(comparator);
        this.priorityMap = new HashMap<>();
    }

    @Override
    public boolean add(E item) {
        // Maintain priority map to track changes
        priorityMap.put(item, ((int[]) item)[1]);
        return super.add(item);
    }

    public void changePriority(E item, int newPriority) {
        if (priorityMap.containsKey(item) && priorityMap.get(item) != newPriority) {
            // Remove and re-add with updated priority only if changed
            super.remove(item);
            ((int[]) item)[1] = newPriority;
            priorityMap.put(item, newPriority);
            this.add(item);
        }
    }
}

