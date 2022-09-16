package main.pathfinder.uninformed;

import java.util.*;

/**
 * Specifies the Maze Grid pathfinding problem including the actions, transitions,
 * goal test, and solution test. Can be fed as an input to a Search algorithm to
 * find and then test a solution.
 */
public class MazeProblem {

    // Fields
    // -----------------------------------------------------------------------------
    private String[] maze;
    private int rows, cols;
    private final MazeState INITIAL_STATE, GOAL_STATE;
    private static final Map<String, MazeState> TRANS_MAP = createTransitions();
    
    /**
     * Creates the transition map that maps String actions to 
     * MazeState offsets, of the format:
     * { "U": (0, -1), "D": (0, +1), "L": (-1, 0), "R": (+1, 0) }
     * 
     * @return The transition map.
     */
    private static final Map<String, MazeState> createTransitions () {
        Map<String, MazeState> result = new HashMap<>();
        result.put("U", new MazeState(0, -1));
        result.put("D", new MazeState(0,  1));
        result.put("L", new MazeState(-1, 0));
        result.put("R", new MazeState( 1, 0));
        return result;
    }
    
    
    // Constructor
    // -----------------------------------------------------------------------------
    
    /**
     * Constructs a new MazeProblem from the given maze; responsible for finding
     * the initial and goal states in the maze, and storing in the MazeProblem state.
     * 
     * @param maze An array of Strings in which characters represent the legal maze
     * entities, including:<br>
     * 'X': A wall, 'G': A goal, 'I': The initial state, '.': an open spot
     * For example, a valid maze might look like:
     * <pre>
     * String[] maze = {
     *     "XXXXXXX",
     *     "X.....X",
     *     "XIX.X.X",
     *     "XX.X..X",
     *     "XG....X",
     *     "XXXXXXX"
     * };
     * </pre>
     */
    public MazeProblem (String[] maze) {
        this.maze = maze;
        this.rows = maze.length;
        this.cols = (rows == 0) ? 0 : maze[0].length();
        MazeState foundInitial = null, foundGoal = null;
        
        // Find the initial and goal state in the given maze, and then
        // store in fields once found
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                switch (maze[row].charAt(col)) {
                case 'I':
                    foundInitial = new MazeState(col, row); break;
                case 'G':
                    foundGoal = new MazeState(col, row); break;
                case '.':
                case 'X':
                    break;
                default:
                    throw new IllegalArgumentException("Maze formatted invalidly");
                }
            }
        }
        this.INITIAL_STATE = foundInitial;
        this.GOAL_STATE = foundGoal;
    }
    
    
    // Methods
    // -----------------------------------------------------------------------------
    
    /**
     * Returns the MazeState containing the initial state (the starting position
     * of the pathfinder)
     * 
     * @return The MazeState containing the initial state.
     */
    public MazeState getInitial () {
        return this.INITIAL_STATE;
    }
    
    /**
     * Returns the MazeState containing the goal (the destination of the
     * pathfinder)
     * 
     * @return The MazeState containing the goal.
     */
    public MazeState getGoal () {
        return this.GOAL_STATE;
    }
    
    /**
     * Returns a map of the states that can be reached from the given input
     * state using any of the available actions.
     * 
     * @param state A MazeState (col, row) representing the current state
     * from which actions can be taken
     * @return Map A map of actions to the states that they lead to, of the
     * format, for current MazeState (c, r):<br>
     * { "U": (c, r-1), "D": (c, r+1), "L": (c-1, r), "R": (c+1, r) }
     */
    public Map<String, MazeState> getTransitions (MazeState state) {
        // Store transitions as a Map between actions ("U", "D", ...) and
        // the MazeStates that they result in from state
        Map<String, MazeState> result = new HashMap<>();
        
        // For each of the possible directions (stored in TRANS_MAP), test
        // to see if it is a valid transition
        for (Map.Entry<String, MazeState> action : TRANS_MAP.entrySet()) {
            MazeState actionMod = action.getValue(),
                      newState  = new MazeState(state.col, state.row);
            newState.add(actionMod);
            
            // If the given state *is* a valid transition (i.e., within
            // map bounds and no wall at the position)...
            if (newState.row >= 0 && newState.row < rows &&
                newState.col >= 0 && newState.col < cols &&
                maze[newState.row].charAt(newState.col) != 'X') {
                // ...then add it to the result!
                result.put(action.getKey(), newState);
            }
        }
        return result;
    }
    
    /**
     * Given a possibleSoln, tests to ensure that it is indeed a solution to this MazeProblem,
     * as well as returning the cost.
     * 
     * @param possibleSoln A possible solution to test, which is a list of actions of the format:
     * ["U", "D", "D", "L", ...]
     * @return A MazeTestResult object with fields: IS_SOLUTION, determining whether or not the
     * given solution solves the maze, and COST, the total cost of the solution if so, -1 otherwise.
     */
    public MazeTestResult testSolution (List<String> possibleSoln) {
        // Update the "moving state" that begins at the start and is modified by the transitions
        MazeState movingState = new MazeState(INITIAL_STATE.col, INITIAL_STATE.row);
        int cost = 0;
        
        if (possibleSoln == null) { return new MazeTestResult(false, -1); }
        
        // For each action, modify the movingState, and then check that we have landed in
        // a legal position in this maze
        for (String action : possibleSoln) {
            MazeState actionMod = TRANS_MAP.get(action);
            movingState.add(actionMod);
            if (maze[movingState.row].charAt(movingState.col) == 'X') {
                return new MazeTestResult(false, -1);
            }
            cost++;
        }
        return new MazeTestResult(this.getGoal().equals(movingState), cost);
    }
    
    /**
     * Public inner class serving as a tuple for the Maze Problem's testSolution return
     * value. Contains fields for both whether or not the provided solution actually
     * solves the maze, and the cost of the solution if so (-1 otherwise).
     */
    public static class MazeTestResult {
        
        public final boolean IS_SOLUTION;
        public final int COST;
        
        /**
         * Constructor for a MazeTestResult determining whether the result is a solution
         * or not and the associated cost of it if so.
         * @param isSoln Whether or not the provided solution solves the maze.
         * @param cost Total cost of the given solution if so, -1 otherwise.
         */
        public MazeTestResult (boolean isSoln, int cost) {
            this.IS_SOLUTION = isSoln;
            this.COST = cost;
        }
        
    }
    
}
