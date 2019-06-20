import java.util.*;
import java.io.*;

/**
 * @author Colin Cassell
 *
 * Takes user input from a file to get words and clues,
 * generatres and displays the resulting crossword puzzle.
 */
public class Main {
    //name of input file for words and clues
    private static String fileName = "example3";

    //data structure to store each word and its corresponding clue
    private static Map<String, String> wordsAndClues = new HashMap<>();

    //stores number of words
    private static int numWords;

    //crossword puzzle permanent, completed board state
    private static char[][] permBoard;

    //flag for whether puzzle is done being created
    private static boolean done = false;

    //set of all words that have been added to the board
    private static Set<Word> words = new HashSet<>();


    //starts the program
    public static void main(String[] args) throws Exception{
        //determines which file to open for the puzzle input
        readInput(fileName);

        //creates the board
        instantiateBoard();


        //tries each word as the starting word for the puzzle, stops when it successfully creates a puzzle
        for(String str : wordsAndClues.keySet()) {
            fillBoard(str);
            if(done)
                break;
        }

        if(!done)
            System.out.println("Sorry, no puzzle can be created with the given words.");



        int[][] numberingBoard = makeNumberingBoard();

        //draws and displays the actual puzzle
        PuzzleBoard pb = new PuzzleBoard(permBoard);
        pb.drawAndFillGrid();
        pb.addNumbers(numberingBoard);

        System.out.println("Clues:");
        printClues(numberingBoard);

        System.out.println("\n\n\nCorrect, completed board:");
        printBoard(permBoard);
    }


    /**
     * reads input file and fills the wordsAndClues map with data
     * input file will be comma separated list of word,clue,word,clue,etc.
     *
     * @param fileName
     * @throws Exception
     */
    public static void readInput(String fileName) throws Exception{
        File f = new File("./inputs/" + fileName);
        Scanner s = new Scanner(f);
        String str = s.nextLine();
        String[] arr = str.split(",");

        numWords = arr.length / 2;
        for (int i = 0; i < arr.length; i+=2) {
            wordsAndClues.put(arr[i],arr[i+1]);
        }

        s.close();
    }

    //makes the board 3 spaces larger than the longest word in the puzzle, fills it with '|'
    public static void instantiateBoard(){
        int max = 0;
        for(String str : wordsAndClues.keySet()){
            if(str.length() > max)
                max = str.length();
        }
        permBoard = new char[max + 3][max + 3];

        for (int i = 0; i < permBoard.length; i++) {
            for (int j = 0; j < permBoard[0].length; j++) {
                permBoard[i][j] = '|';
            }
        }
    }


    //adds starting word to board, makes recursive call
    public static void fillBoard(String firstWord){
        int startingRow = permBoard.length/2;
        int startingCol = (permBoard.length/2) - (firstWord.length()/2);
        Word startingWord = new Word(firstWord, startingRow, startingCol, true);
        char[][] thisBoard = new char[permBoard.length][permBoard[0].length];
        for (int i = 0; i < thisBoard.length; i++) {
            for (int j = 0; j < thisBoard[0].length; j++) {
                thisBoard[i][j] = '|';
            }
        }
        insertWordHorizontal(startingWord, thisBoard);

        //will keep track of which words have been used
        Set<Word> usedWords = new HashSet<>();
        usedWords.add(startingWord);

        //set of words to use in puzzle, removing the first word
        Set<String> wordsToUse = new HashSet<>(wordsAndClues.keySet());



        //recursive call to create every possible board until it creates a valid board
        makePuzzle(wordsToUse, usedWords, thisBoard);
    }


    /**
     * recursive function that will attempt to place words in
     * every possible combination until finding a successful combination
     *
     * @param wordsToUse
     * @param usedWords
     * @param tempBoard
     */
    public static void makePuzzle(Set<String> wordsToUse, Set<Word> usedWords, char[][] tempBoard){
        //base case, if all words used, finish
        if(numWords == usedWords.size()) {
            permBoard = tempBoard;
            done = true;
            words = usedWords;
        }
        else{

            //loop through all words
            for(String word : wordsToUse){

                //makes set of used strings
                Set<String> allUsedWords = new HashSet<>();
                for(Word w : usedWords){
                    allUsedWords.add(w.getText());
                }

                //if this word not already used
                if(!allUsedWords.contains(word)){

                    //loop through all used words to find intersections between word and all used words
                    for(Word used : usedWords) {

                        //stores all intersections between used word and new word for insertion
                        ArrayList<Word> intersectionWords = new ArrayList<>();

                        //checks each letter in word for intersection(s) in used word
                        for (int i = 0; i < word.length(); i++) {
                            int num = 0;

                            //while this char still exists
                            while (used.getText().indexOf(word.charAt(i), num) != -1) {
                                int index = used.getText().indexOf(word.charAt(i), num);
                                num = index + 1;

                                int wordRow = 0;
                                int wordCol = 0;
                                if (used.isHorizontal()) {
                                    wordRow = used.getRow() - i;
                                    wordCol = used.getCol() + index;
                                } else {
                                    wordRow = used.getRow() + index;
                                    wordCol = used.getCol() - i;
                                }

                                Word addWord = new Word(word, wordRow, wordCol, !used.isHorizontal());

                                intersectionWords.add(addWord);
                            }

                        }

                        //for each intersection, try adding the word and recursing more down that path
                        for(Word inter : intersectionWords){
                            //makes new board to use as parameter for recursive call
                            char[][] newTempBoard = new char[tempBoard.length][tempBoard[0].length];
                            for (int i = 0; i < newTempBoard.length; i++) {
                                for (int j = 0; j < newTempBoard[0].length; j++) {
                                    newTempBoard[i][j] = tempBoard[i][j];
                                }
                            }

                            //flag to keep track of whether word has been added successfully
                            boolean added = false;
                            if (inter.isHorizontal())
                                added = insertWordHorizontal(inter, newTempBoard);
                            else
                                added = insertWordVertical(inter, newTempBoard);

                            //if added, recurse
                            if (added) {
                                Set<Word> newUsedWords = new HashSet<>(usedWords);
                                newUsedWords.add(inter);
                                makePuzzle(wordsToUse, newUsedWords, newTempBoard);
                                break;
                            }
                            if(done)
                                break;
                        }
                        if(done)
                            break;
                    }
                }
                if(done)
                    break;
            }
        }
    }


    //these two methods attempt to place a given word on the board, returning true if successful, false otherwise
    private static boolean insertWordHorizontal(Word word, char[][] thisBoard){
        String text = word.getText();
        int r = word.getRow();
        int c = word.getCol();

        if(r < 0 || c < 0)
            return false;

        //return false if outside length
        if(c + text.length() > thisBoard.length)
            return false;


        //return false if overlaps an existing word on the board
        //checks for whether it's adjacent to another word, in which case it will also return false
        for (int i = 0; i < text.length(); i++) {
            //if this spot on the board doesn't equal this character
            if(thisBoard[r][c + i] != text.charAt(i)){
                //if spot not empty, don't insert word
                if(thisBoard[r][c + i] != '|')
                    return false;

                //if it would touch other words, don't insert
                if(r + 1 < thisBoard.length && thisBoard[r + 1][c + i] != '|')
                    return false;
                if(r - 1 >= 0 && thisBoard[r - 1][c + i] != '|')
                    return false;
            }


            //if start or end, make sure not adjacent horizontally
            if(i == 0 && c - 1 >= 0 && thisBoard[r][c - 1] != '|')
                return false;
            if(i == text.length() - 1 && c + i + 1 < thisBoard[0].length && thisBoard[r][c + i + 1] != '|')
                return false;
        }

        //actually adds word to board once it's confirmed there's space for it
        for (int i = 0; i < text.length(); i++) {
            thisBoard[r][c + i] = text.charAt(i);
        }

        return true;
    }

    private static boolean insertWordVertical(Word word, char[][] thisBoard){
        String text = word.getText();
        int r = word.getRow();
        int c = word.getCol();

        if(r < 0 || c < 0)
            return false;

        //return false if outside length
        if(r + text.length() > thisBoard.length)
            return false;

        //return false if overlaps an existing word on the board
        //checks for whether it's adjacent to another word, in which case it will also return false
        for (int i = 0; i < text.length(); i++) {
            //if this spot on the board doesn't equal this character
            if(thisBoard[r + i][c] != text.charAt(i)){
                //if spot not empty, don't insert word
                if(thisBoard[r + i][c] != '|')
                    return false;

                //if it would touch other words, don't insert
                if(c + 1 < thisBoard[0].length && thisBoard[r + i][c + 1] != '|')
                    return false;
                if(c - 1 >= 0 && thisBoard[r + i][c - 1] != '|')
                    return false;
            }

            //if start or end, make sure not adjacent vertically
            if(i == 0 && r - 1 >= 0 && thisBoard[r - 1][c] != '|') {
                return false;
            }
            if(i == text.length() - 1 && r + i + 1 < thisBoard.length && thisBoard[r + i + 1][c] != '|') {
                return false;
            }
        }

        //actually adds word to board once it's confirmed there's space for it
        for (int i = 0; i < text.length(); i++) {
            thisBoard[r + i][c] = text.charAt(i);
        }

        return true;
    }


    public static void printBoard(char[][] thisBoard){
        for (int i = 0; i < thisBoard.length; i++) {
            for (int j = 0; j < thisBoard[0].length; j++) {
                System.out.print(thisBoard[i][j]);
            }
            System.out.println();
        }
    }

    //makes board to keep track of numbering
    private static int[][] makeNumberingBoard(){
        int[][] out = new int[permBoard.length][permBoard[0].length];
        for(Word word : words){
            int r = word.getRow();
            int c = word.getCol();
            out[r][c] = 1;
        }
        int count = 1;
        for (int i = 0; i < out.length; i++) {
            for (int j = 0; j < out[0].length; j++) {
                if(out[i][j] == 1){
                    out[i][j] = count;
                    count++;
                }
            }
        }
        return out;
    }

    //finds a word - or words - corresponding to a given row and column
    private static Word[] findWord(int row, int col){
        Word[] out = new Word[2];
        int count = 0;
        for(Word word : words){
            if(word.getRow() == row && word.getCol() == col){
                out[count] = word;
                count++;
            }
        }
        return out;
    }

    //prints all clues
    private static void printClues(int[][] numberingBoard){
        for (int i = 0; i < numberingBoard.length; i++) {
            for (int j = 0; j < numberingBoard[0].length; j++) {
                if(numberingBoard[i][j] != 0){
                    Word[] currentWords = findWord(i,j);
                    String orientation0 = "";
                    String orientation1 = "";
                    if(currentWords[0].isHorizontal())
                        orientation0 = "Across";
                    else
                        orientation0 = "Down";
                    if(currentWords[1] != null) {
                        if (currentWords[1].isHorizontal())
                            orientation1 = "Across";
                        else
                            orientation1 = "Down";
                    }
                    System.out.println(numberingBoard[i][j] + " " + orientation0 + ": " + wordsAndClues.get(currentWords[0].getText()));
                    if(!orientation1.equals(""))
                        System.out.println(numberingBoard[i][j] + " " + orientation1 + ": " + wordsAndClues.get(currentWords[1].getText()));
                }
            }
        }
    }
}
