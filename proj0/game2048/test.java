package game2048;

public class test {
    public static void main(String[] args) {
        int[][] rawVals = new int[][] {
                {2, 4, 2, 0},
                {4, 2, 4, 0},
                {2, 4, 4, 0},
                {4, 2, 4, 0},
        };
        Board board = new Board(rawVals, 0);
//        board.setViewingPerspective(Side.WEST);
//        System.out.println(board);
//        board.setViewingPerspective(Side.NORTH);
//        System.out.println(board.tile(3,1));
        //merge
//        board.move(2,2,board.tile(2,1));
//        System.out.println(board);
        //so long as the tile at tagret pos != null, board.move assume its value == current tile's
        boolean res = board.move(2,3,board.tile(2,0));
        System.out.println(board);
        System.out.println(res);
    }
}

