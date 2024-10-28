package lab11.graphs;

import java.util.Comparator;
import java.util.PriorityQueue;

public class MazeDijkstra extends MazeExplorer {
    // inherit
//    protected int[] distTo;
//    protected int[] edgeTo;
//    protected boolean[] marked;
//    protected Maze maze;

    private int s;
    private int t;
    private boolean found = false;
    private ModifiedPQ<int[]> distPQ;

    // define the way of ordering in PQ
    private static class ArrayComparator implements Comparator<int[]> {
        @Override
        public int compare(int[] o1, int[] o2) {
            return Integer.compare(o1[1], o2[1]);
        }
    }

    public MazeDijkstra(Maze m, int sourceX, int sourceY, int targetX, int targetY) {
        super(m);
        s = m.xyTo1D(sourceX, sourceY);
        t = m.xyTo1D(targetX, targetY);

        distPQ = new ModifiedPQ<int[]>(new ArrayComparator());

        for(int i = 0; i < m.V(); i++) {
            if(i != s) {
                distTo[i] = Integer.MAX_VALUE;
                distPQ.add(new int[]{i, Integer.MAX_VALUE});
            }
            else {
                distTo[i] = 0;
                distPQ.add(new int[]{i, 0});
            }
        }
        edgeTo[s] = s;
    }

    private void Dijkstra() {
        while(distPQ.peek() != null) {
            int cur = distPQ.poll()[0];
            marked[cur] = true;
            for(int[] i : maze.adjWithWeights(cur)) {
                int distNew;
                if(!marked[i[0]]) {
                    if(distTo[i[0]] == Integer.MAX_VALUE) {
                        distNew = i[1];
                    }
                    else {
                        distNew = Math.min(i[1]+distTo[cur], distTo[i[0]]);
                    }
                    distTo[i[0]] = distNew;
                    edgeTo[i[0]] = cur;
                    distPQ.changePriority(new int[]{i[0], distTo[i[0]]}, distNew);
                }

            }
            announce();
        }
    }

    @Override
    public void solve() {
        Dijkstra();
    }

}
