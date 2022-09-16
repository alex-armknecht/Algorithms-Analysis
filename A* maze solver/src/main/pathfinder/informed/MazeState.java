package main.pathfinder.informed;

import java.util.Objects;

/**
 * Maze Pathfinding representation of a given state, i.e., an occupiable position
 * in the given maze.
 */
public class MazeState {
    
    public int col, row;
    
    /**
     * Constructs a new MazeState, which tracks the given row and column that it
     * represents in the Maze.<br>
     * <b>NOTE: Row 0, Column 0 is located at the upper-left-hand corner of the maze!</b>
     * @param col Integer column number of this state (X coord in a Cartesian plane)
     * @param row Integer row number of this state (Y coord in a Cartesian plane)
     */
    public MazeState (int col, int row) {
        this.col = col;
        this.row = row;
    }
    
    /**
     * [Mutator] Adds the coordinates of the given other MazeState to this one's; useful
     * for computing offsets given in MazeProblem transitions.
     * 
     * @param other The other MazeState to add to this one.
     */
    public void add (MazeState other) {
        this.col += other.col;
        this.row += other.row;
    }
    
    @Override
    public boolean equals (Object other) {
        if (this == other) { return true; }
        if (other == null || this.getClass() != other.getClass()) { return false; }
        return this.row == ((MazeState) other).row && this.col == ((MazeState) other).col;
    }
    
    @Override
    public int hashCode () {
        return Objects.hash(this.col, this.row);
    }
    
    @Override
    public String toString () {
        return "(" + this.col + ", " + this.row + ")";
    }
    
    @Override
    public MazeState clone () {
        return new MazeState(this.col, this.row);
    }
    
}
