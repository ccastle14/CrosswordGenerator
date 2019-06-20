/**
 * Represents a word in terms of the puzzle -
 * stores the text, as well as the row and column
 * of the first letter, and whether the word is
 * horizontal or vertical.
 */

public class Word {
    private String text;
    private int row;
    private int col;
    private boolean horizontal;


    public Word(String text, int row, int col, boolean horizontal) {
        this.text = text;
        this.row = row;
        this.col = col;
        this.horizontal = horizontal;
    }

    public String getText() {
        return text;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean isHorizontal() {
        return horizontal;
    }

}
