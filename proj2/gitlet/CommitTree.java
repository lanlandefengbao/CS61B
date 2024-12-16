package gitlet;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Queue;

/** Represent the commitTree (a Directed-Acyclic-Graph) with HashMap<String, Commit[]> */
public class CommitTree {

    private HashMap<String, Commit[]> VerticesTable = new HashMap<>();
    private HashMap<String, Integer> DistanceTable = new HashMap<>();

    /** Traverse the graph in the BFS manner to transfer it into a HashMap, starting from cur
     * Meanwhile record each vertex's distance from starting point */
    public CommitTree(Commit cur) {
        Queue<Commit> fringe = new ArrayDeque<>() {};
        fringe.add(cur);
        DistanceTable.put(cur.hash(), 0);
        bfs(fringe);
    }

    private void bfs(Queue<Commit> fringe) {
        while (!fringe.isEmpty()) {
           Commit c = fringe.poll();
           Commit[] parents = c.getParents();
           String SHA1 = c.hash();
           for(Commit parent : parents) {
               fringe.add(parent);
               String parentSHA1 = parent.hash();
               if(!DistanceTable.containsKey(parentSHA1)) {
                   // If a vertex has been visited, the shortest path to it has been found. We shouldn't update it in this case.
                     DistanceTable.put(parentSHA1, DistanceTable.get(SHA1) + 1);
               }
           }
           VerticesTable.put(SHA1, parents);
        }
    }

    public HashMap<String, Integer> getDistanceTable() {
        return DistanceTable;
    }

}
