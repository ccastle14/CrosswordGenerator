/**
 * Handles the graphics for the puzzle,
 * creates the grid and inputs the numbers
 * for the clues into the grid.
 */
public class PuzzleBoard {
    private int WINDOW_SIZE;
    private double increment;
    private char[][] board;



    PuzzleBoard(char[][] board) {
        WINDOW_SIZE = 600;
        this.board = board;
        increment = (double) WINDOW_SIZE / board.length;


        StdDraw.setCanvasSize(WINDOW_SIZE, WINDOW_SIZE);
        StdDraw.setScale(0, WINDOW_SIZE);
        StdDraw.enableDoubleBuffering();
        StdDraw.show();
    }

    void drawAndFillGrid(){
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.005);



        //draws vertical and horizontal lines for grid
        for (int i = 0; i <= board.length; i++) {
            StdDraw.line(i * increment,0,i * increment, WINDOW_SIZE);
            StdDraw.line(0, i * increment, WINDOW_SIZE, i * increment);
        }



        StdDraw.setPenColor(StdDraw.GRAY);

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if(board[i][j] == '|')
                    StdDraw.filledSquare((j * increment) + (increment / 2),
                                        ((board[0].length - i - 1) * increment) + (increment / 2),
                                        (increment / 2) - 1);
            }
        }
    }

    void addNumbers(int[][] numberingBoard){
        StdDraw.setPenColor(StdDraw.BLACK);

        for (int i = 0; i < numberingBoard.length; i++) {
            for (int j = 0; j < numberingBoard[0].length; j++) {
                if(numberingBoard[i][j] != 0){
                    StdDraw.text((j * increment) + (increment / 2) + 10,
                            ((board[0].length - i - 1) * increment) + (increment / 2) + 10,
                            "" + numberingBoard[i][j]);
                }
            }
        }

        StdDraw.show();
    }
}
