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
//        System.out.println(board);
//
//        board.setViewingPerspective(Side.EAST);
//        System.out.println(board);
//
//        boolean res = board.move(3,2,board.tile(3,1));
//        System.out.println(res);
//        System.out.println(board);

        Model x = new Model(rawVals,0,0,false);
        x.tilt(Side.WEST);
        System.out.println(x);
    }
}

