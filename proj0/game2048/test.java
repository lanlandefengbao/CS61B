package game2048;

import java.util.Arrays;

public class test {
    public static void main(String[] args) {
        int[][] rawVals = new int[][] {
                {0, 0, 0, 2},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 2, 2, 4},
        };
        Board board = new Board(rawVals, 0);
//        board.setViewingPerspective(Side.WEST);
//        System.out.println(board);
//        board.setViewingPerspective(Side.EAST);
//        System.out.println(board.tile(3,1));
        //merge
//        board.move(2,2,board.tile(2,1));
//        System.out.println(board);
        //so long as the tile at tagret pos != null, board.move assume its value == current tile's
//        boolean res = board.move(3,3,board.tile(3,0));
//        System.out.println(res);
//        System.out.println(board);

        // decomposing tilt
        System.out.println(board);

        board.setViewingPerspective(Side.EAST);
        System.out.println(board);

        boolean res = board.move(3,2,board.tile(3,1));
        System.out.println(res);
        System.out.println(board);

        Model x = new Model(rawVals,0,0,false);
        x.tilt(Side.EAST);
        System.out.println(x);

        //testing findTargetPos
        int[][] rawVals1 = new int[][] {
                {2, 0, 0, 4},
                {0, 0, 0, 2},
                {0, 0, 0, 2},
                {0, 0, 0, 0},
        };
        Model x1 = new Model(rawVals,0,0,false);
        Board board1 = new Board(rawVals1, 0);
        int[] pos = x.findTargetPos(board1.tile(3,1), 2);
        System.out.println(Arrays.toString(pos));
        board1.move(pos[0],pos[1],board1.tile(3,1));
        System.out.println(board1);
    }
}

