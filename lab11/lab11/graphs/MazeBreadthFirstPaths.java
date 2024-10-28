package lab11.graphs;

import java.util.LinkedList;
import java.util.Queue;

/**
 *  @author Josh Hug
 */
public class MazeBreadthFirstPaths extends MazeExplorer {
    /* Inherits public fields:
    public int[] distTo;
    public int[] edgeTo;
    public boolean[] marked;
    */

    private int s;
    private int t;
    private boolean found;

    public MazeBreadthFirstPaths(Maze m, int sourceX, int sourceY, int targetX, int targetY) {
        super(m);
        // Add more variables here!
        s = m.xyTo1D(sourceX, sourceY);
        t = m.xyTo1D(targetX, targetY);
        found = false;
        distTo[s] = 0;
        edgeTo[s] = s;
    }

    /** Conducts a breadth first search of the maze starting at the source. */
    private void bfs() {
        // TODO: Your code here. Don't forget to update distTo, edgeTo, and marked, as well as call announce()
        Queue<Integer> queue = new LinkedList<>();
        queue.add(s);
        marked[s] = true;
        while(!queue.isEmpty()) {
            int cur = queue.poll();
            for(int i : maze.adj(cur)) {
                if(!marked[i]) {
                    queue.add(i);
                    marked[i] = true;
                    distTo[i] = distTo[cur]+1;
                    edgeTo[i] = cur;
                    announce();
                    if(i == t) {
                        found = true;
                        return;
                    }
                }
            }
        }
    }


    @Override
    public void solve() {
         bfs();
    }
}

