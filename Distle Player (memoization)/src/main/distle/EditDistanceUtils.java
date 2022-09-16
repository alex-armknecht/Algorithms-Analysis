package main.distle;

import java.util.*;

public class EditDistanceUtils {
    
    /**
     * Returns the completed Edit Distance memoization structure, a 2D array
     * of ints representing the number of string manipulations required to minimally
     * turn each subproblem's string into the other.
     * 
     * @param s0 String to transform into other
     * @param s1 Target of transformation
     * @return Completed Memoization structure for editDistance(s0, s1)
     */
    public static int[][] getEditDistTable (String s0, String s1) {
        int[][] table = new int[s0.length() + 1][s1.length() + 1];
        for (int row = 0; row < s0.length() + 1; row++) {
            table[row][0] = row;
        }
        for (int col =1; col < s1.length() + 1; col++) {
            table[0][col] = col;
        }
        for (int row = 1; row < s0.length() + 1; row++) {
            for (int col = 1; col < s1.length() + 1; col++) {
                int rep = replace(row, col, table, s0, s1);
                int tran = transpose(row, col, table, s0, s1);
                int ins = insert(row, col, table);
                int del = delete(row, col, table);
                int lowestVal = rep;
                if (tran <= lowestVal) {
                    if (tran != lowestVal) {
                        lowestVal = tran;
                    } 
                }
                if (ins <= lowestVal) {
                    if (ins != lowestVal) {
                        lowestVal = ins;
                    } 
                }
                if (del <= lowestVal) {
                    if (del != lowestVal) {
                        lowestVal = del;
                    } 
                }
                table[row][col] = lowestVal;  
            }
        }
        return table; 
    }
    
    /**
     * Returns one possible sequence of transformations that turns String s0
     * into s1. The list is in top-down order (i.e., starting from the largest
     * subproblem in the memoization structure) and consists of Strings representing
     * the String manipulations of:
     * <ol>
     *   <li>"R" = Replacement</li>
     *   <li>"T" = Transposition</li>
     *   <li>"I" = Insertion</li>
     *   <li>"D" = Deletion</li>
     * </ol>
     * In case of multiple minimal edit distance sequences, returns a list with
     * ties in manipulations broken by the order listed above (i.e., replacements
     * preferred over transpositions, which in turn are preferred over insertions, etc.)
     * @param s0 String transforming into other
     * @param s1 Target of transformation
     * @param table Precomputed memoization structure for edit distance between s0, s1
     * @return List that represents a top-down sequence of manipulations required to
     * turn s0 into s1, e.g., ["R", "R", "T", "I"] would be two replacements followed
     * by a transposition, then insertion.
     */
    public static List<String> getTransformationList (String s0, String s1, int[][] table) {
        List<String> moves = new ArrayList<String>();
        moves = tracePath(s0.length(), s1.length(), s0, s1, table, moves);
        return moves;
    }
    
    /**
     * Returns the edit distance between the two given strings: an int
     * representing the number of String manipulations (Insertions, Deletions,
     * Replacements, and Transpositions) minimally required to turn one into
     * the other.
     * 
     * @param s0 String to transform into other
     * @param s1 Target of transformation
     * @return The minimal number of manipulations required to turn s0 into s1
     */
    public static int editDistance (String s0, String s1) {
        if (s0.equals(s1)) { return 0; }
        return getEditDistTable(s0, s1)[s0.length()][s1.length()];
    }
    
    /**
     * See {@link #getTransformationList(String s0, String s1, int[][] table)}.
     */
    public static List<String> getTransformationList (String s0, String s1) {
        return getTransformationList(s0, s1, getEditDistTable(s0, s1));
    }
    
    /**
     * A private helper method that performs deletion transformation that is 
     * needed to turn s0's char into s1's char
     * @param row int row on table that is being filled.
     * @param col int column on table that is being filled.
     * @param table int[][[] that contains the char's edit distances to convert s0 to s1
     * @return int edit distance of using delete to fill the space
     */
    private static int delete(int row, int col, int[][] table) {
        return table[row -1][col] + 1;
    }
    
    /**
     * A private helper method that performs insertion transformation that is 
     * needed to turn s0's char into s1's char
     * @param row int row on table that is being filled.
     * @param col int column on table that is being filled.
     * @param table int[][[] that contains the char's edit distances to convert s0 to s1
     * @return int edit distance of using insertion to fill the space
     */
    private static int insert(int row, int col, int[][] table) {
        return table[row][col -1] + 1;
    }
    
    /**
     * A private helper method that performs Replacement transformation that is 
     * needed to turn s0's char into s1's char
     * @param row int row on table that is being filled.
     * @param col int column on table that is being filled.
     * @param table int[][[] that contains the char's edit distances to convert s0 to s1
     * @param s0 String of user's word that is being compared to s1
     * @param s1 String of the secret word that the user is trying to guess.
     * @return int edit distance of using replace to fill the space
     */
    private static int replace(int row, int col, int[][] table, String s0, String s1) {
        return table[row - 1][col -1] + (s1.charAt(col - 1) == s0.charAt(row - 1) ? 0:1);
    }
    
    /**
     * A private helper method that performs Transposition transformation that is 
     * needed to turn s0's char into s1's char
     * @param row int row on table that is being filled.
     * @param col int column on table that is being filled.
     * @param table int[][[] that contains the char's edit distances to convert s0 to s1
     * @param s0 String of user's word that is being compared to s1
     * @param s1 String of the secret word that the user is trying to guess.
     * @return int edit distance of using Transposition to fill the space
     */
    private static int transpose(int row, int col, int[][] table, String s0, String s1) {
        if (row < 2 || col < 2) {
            return 100;
        }
        if (s0.charAt(row - 1) == s1.charAt(col - 2) && s1.charAt(col - 1) == s0.charAt(row - 2)) {
            return table[row - 2][col - 2] + 1;
        }
        return 99; 
    }
    
    /**
     * A recursive private helper method that traces the most efficient path of transformations 
     * in order to convert s0 to s1
     * @param row int row on table that is being filled.
     * @param col int column on table that is being filled.
     * @param s0 String of user's word that is being compared to s1
     * @param s1 String of the secret word that the user is trying to guess.
     * @param table int[][[] that contains the char's edit distances to convert s0 to s1
     * @param moves List of sequences of transformations that turns String s0 into s1
     * @return updated list of sequences of transformations that turns String s0 into s1
     */
    private static List<String> tracePath (int row, int col, String s0, String s1, int[][] table, List<String> moves) {
        if (row == 0 && col == 0) { return moves; }
        if (row == 0) { 
            moves.add("I");  
            return tracePath (row, col - 1, s0, s1, table, moves); 
        }
        if (col == 0) {  
            moves.add("D");  
            return tracePath (row - 1, col, s0, s1, table, moves); 
        }
        if (row >= 1 && col >= 1 && s0.charAt(row - 1) == (s1.charAt(col - 1))) {
            return tracePath (row - 1, col - 1, s0, s1, table, moves);
        }
        if (row >= 1 && col >= 1 && table[row][col] - 1 == table[row - 1][col - 1]) {
            moves.add("R");
            return tracePath (row - 1, col - 1, s0, s1, table, moves);
        }
        if (row >= 2 && col >= 2 && table[row][col] - 1 == table[row - 2][col - 2]
                && s0.charAt(row - 1) == s1.charAt(col - 2) && s1.charAt(col - 1) == s0.charAt(row - 2)) {
            moves.add("T");
            return tracePath (row - 2, col - 2, s0, s1, table, moves);  
        }
        if (col >= 1 && table[row][col] - 1 == table[row][col - 1]) {
            moves.add("I");
            return tracePath (row, col - 1, s0, s1, table, moves);
        }
        if (row >= 1 && table[row][col] - 1 == table[row -1][col]) {
            moves.add("D");
            return tracePath (row - 1, col, s0, s1, table, moves); 
        }
        return moves;
    }
}