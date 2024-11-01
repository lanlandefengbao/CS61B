package lab11.graphs;

import java.util.Arrays;

public class DijkstraDemo {
    public static void main(String[] args) {
        Maze maze = new Maze("lab11/graphs/maze.txt");

        int startX = 1;
        int startY = 1;
        int targetX = maze.N();
        int targetY = maze.N();

        MazeExplorer md = new MazeDijkstra(maze, startX, startY, targetX, targetY);
        md.solve();
        System.out.println(Arrays.toString(md.edgeTo));
    }
}
