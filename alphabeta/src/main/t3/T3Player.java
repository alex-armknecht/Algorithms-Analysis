package main.t3;

import java.util.*;

/**
 * Artificial Intelligence responsible for playing the game of T3!
 * Implements the alpha-beta-pruning mini-max search algorithm
 */
public class T3Player {
    
    /**
     * Workhorse of an AI T3Player's choice mechanics that, given a game state,
     * makes the optimal choice from that state as defined by the mechanics of
     * the game of Tic-Tac-Total.
     * Note: In the event that multiple moves have equivalently maximal minimax
     * scores, ties are broken by move col, then row, then move number in ascending
     * order (see spec and unit tests for more info). The agent will also always
     * take an immediately winning move over a delayed one (e.g., 2 moves in the future).
     * @param state The state from which the T3Player is making a move decision.
     * @return The T3Player's optimal action.
     */
    public T3Action choose (T3State state) { 
        for (Map.Entry<T3Action,T3State> entry : state.getTransitions().entrySet()) {
            if (entry.getValue().isWin()) {
                return entry.getKey();
            }
        }
        return alphabeta(state, Integer.MIN_VALUE, Integer.MAX_VALUE, true).savedAction;
    }
    
    /**
     * Private helper method that uses the Alpha–beta pruning search algorithm in order
     * to find the best action and save computation by not checking worse quality actions.
     * @param state a T3State from which the T3Player is making a move decision.
     * @param alph a integer that keeps track of the alpha (min) value for comparable needs.
     * @param bet a integer that keeps track of the beta (max) value for comparable needs.
     * @param maxNode a boolean value that is true if search is currently on a max node; false otherwise.
     * @return an actAndVal that contains the T3Player's action and value.
     */
    private actAndVal alphabeta(T3State state, int alph, int bet, boolean maxNode) {
        if (state.isWin() && !maxNode) { return new actAndVal(1, null); } 
        if (state.isWin() && maxNode) { return new actAndVal(-1, null); } 
        if (state.isTie()) { return new actAndVal(0, null); } 
        if (maxNode) { 
            actAndVal bestMove = new actAndVal(0, null);
            int eval = Integer.MIN_VALUE;
            int bestEval = Integer.MIN_VALUE;
            for (Map.Entry<T3Action,T3State> entry : state.getTransitions().entrySet()) {
                eval = Math.max(eval, alphabeta(entry.getValue(), alph, bet, false).value); 
                alph = Math.max(alph, eval);
                if (bet <= alph) {
                    break;
                }
                if (eval > bestEval) {
                    bestEval = eval;
                    bestMove.savedAction = entry.getKey();
                    bestMove.value = eval;
                }
            }
            return bestMove;
        } else { 
            actAndVal bestMove = new actAndVal(0, null);
            int eval = Integer.MIN_VALUE;
            int bestEval = Integer.MIN_VALUE;
            for (Map.Entry<T3Action,T3State> entry : state.getTransitions().entrySet()) {
                eval = Math.min(eval, alphabeta(entry.getValue(), alph, bet, true).value);
                bet = Math.min(bet, eval);
                if (bet <= alph) {
                    break;
                }
                if (eval > bestEval) {
                    bestEval = eval;
                    bestMove.savedAction = entry.getKey();
                    bestMove.value = eval;
                }
            } 
            return bestMove;
        }
    }
    
    /**
     * actAndVal is a class that holds the value of the T3Player's action and value. 
     * Important for the alphabeta function because stores the int value for recursive 
     * purposes but also saves the action for the choose function. 
     */
    public class actAndVal { 
        private int value;
        private T3Action savedAction;
        
        /**
         * Constructs a new actAndVal with the given value and action.
         * @param value  an integer value with 3 possibilities. 1=win. -1=lose. 0=tie.
         * @param savedAction a T3Action that is the T3Player's current action. 
         */
        private actAndVal (int value, T3Action savedAction) {
            this.value = value;
            this.savedAction = savedAction;
        } 
    }
}

