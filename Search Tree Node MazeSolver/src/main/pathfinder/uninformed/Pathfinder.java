package main.pathfinder.uninformed;

import java.util.*;

/**
 * Maze Pathfinding algorithm that implements a basic, uninformed, breadth-first tree search
 * given a MazeProblem with specified initial, goal, and wall states.
 * @author Alex Armknecht, Anna Garren, Sophia Wagner
 */
public class Pathfinder {
    
    /**
     * Given a MazeProblem, which specifies the actions and transitions available in the
     * search, returns a solution to the problem as a sequence of actions that leads from
     * the initial to a goal state.
     * 
     * @param problem A MazeProblem that specifies the maze, actions, transitions.
     * @return An ArrayList of Strings representing actions that lead from the initial to
     * the goal state, of the format: ["R", "R", "L", ...]
     */
    public static List<String> solve (MazeProblem problem) {
        SearchTreeNode initState = new SearchTreeNode(problem.getInitial(), null, null);
        Queue<SearchTreeNode> frontier = new LinkedList<SearchTreeNode>();
        frontier.add(initState); //add initial state to beginning of frontier
        while (!frontier.isEmpty()) {
            SearchTreeNode expandingNode = frontier.poll();
            //generate children
            Map<String, MazeState> availableMoves = new HashMap<>();
            availableMoves = problem.getTransitions(expandingNode.state);
            for (Map.Entry<String, MazeState> Entry : availableMoves.entrySet()) {
                SearchTreeNode newChildNode =new SearchTreeNode(Entry.getValue(), Entry.getKey(), expandingNode); //create all available children
                if (newChildNode.state.equals(problem.getGoal())) { //goal reached, now backtrack.
                    return tracePath(newChildNode);
                } else {
                    frontier.add(newChildNode); //if not goal then add child to frontier
                } 
            }  
        }
        return null;
    }
    
    /**
     * Is a private helper method that backtracks the path from the goal node.
     * @param goalNode  the Node location of the goal state in the maze.
     * @return ArrayList  of the directions took to go from the beginning to goal node. 
     */
    private static List<String> tracePath (SearchTreeNode goalNode) {
        //start at goal node and create list going up
        List<String> winningActions = new ArrayList<String>();
        SearchTreeNode current = goalNode;
        while (current.action != null) {
            winningActions.add(current.action);
            current = current.parent;
        }
        ArrayList<String> correctOrder = new ArrayList<String>();
        for (int i = winningActions.size() - 1; i >= 0; i--) {  //reverse the arrayList
            correctOrder.add(winningActions.get(i));
        }
        return correctOrder; 
    }
}
