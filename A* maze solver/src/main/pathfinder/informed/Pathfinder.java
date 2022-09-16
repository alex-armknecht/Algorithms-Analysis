package main.pathfinder.informed;

import java.util.*;


/**
 * Maze Pathfinding algorithm that implements a basic, uninformed, breadth-first tree search.
 * @author Alex Armknecht
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
    public static ArrayList<String> solve (MazeProblem problem) {
        if (problem.getKeyState() == null || problem.getGoalStates().size() == 0) {
            return null; //no key return null.
        }
        boolean foundKey = false;
        MazeState currentGoal = problem.getKeyState(); //initial goal is the key.
        SearchTreeNode startNode = new SearchTreeNode(problem.getInitialState(), null, null, 0, 
                getDist(problem.getInitialState(),currentGoal), getDist(problem.getInitialState(),currentGoal));
        Set<MazeState> graveYard = new HashSet<MazeState>();
        PriorityQueue<SearchTreeNode> frontier = new PriorityQueue<SearchTreeNode>();
        frontier.add(startNode); //add initial state to beginning of frontier.
        if (aStarSearch(startNode.state, frontier, problem, currentGoal, graveYard, foundKey ) != null) {
            aStarSearch(startNode.state, frontier, problem, currentGoal, graveYard, foundKey ); // path from start to key. 
        } else {
            return null;
        }
        foundKey = true;
        graveYard.clear();
        SearchTreeNode keyNode = frontier.poll();
        currentGoal = findBestGoal(problem, problem.getKeyState(), keyNode); //find closest goal after finding key.
        frontier.clear();
        frontier.add(keyNode);
        if (aStarSearch(problem.getKeyState(), frontier, problem, currentGoal, graveYard, foundKey ) != null) {
            return aStarSearch(problem.getKeyState(), frontier, problem, currentGoal, graveYard, foundKey ); //path from key to goal.
        } else {
            return null;
        }      
    }
    
    /**
     * A private helper method that finds the historical cost of a SearchTreeNode.
     * @param problem A MazeProblem that specifies the maze, actions, transitions.
     * @param current A MazeState that the search is currently located at.
     * @param parent  A SearchTreeNode that is the parent node of the specified SearchTreeNode.
     * @return integer value of the requested SearchTreeNode's cost. 
     */
    private static int getPastCost(MazeProblem problem, MazeState current, SearchTreeNode parent) {
        return problem.getCost(current) + parent.cost;
    }
    
    /**
     * A private helper method that finds the distance between two MazeStates
     * @param current A MazeState that the search is currently located at.
     * @param goal A MazeState that the search is trying to get to. 
     * @return integer value of Manhattan distance between two coordinates. 
     */
    private static int getDist(MazeState current, MazeState goal) {
        return Math.abs((current.col - goal.col) + (current.row - goal.row));
    }
    
    /**
     * A private helper method that backtracks the path from the goal node.
     * @param goalNode the Node location of the goal state in the maze.
     * @return ArrayList of the directions took to go from the beginning to goal node. 
     */
    private static ArrayList<String> tracePath (SearchTreeNode goalNode) {
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
    
    /**
     * Finds the closest goal state from where the search is currently located.
     * @param problem A MazeProblem that specifies the maze, actions, transitions.
     * @param location A MazeState that the search is currently located at.
     * @return MazeState of the closest goal state. 
     */
    private static MazeState findBestGoal(MazeProblem problem, MazeState location, SearchTreeNode node) {
        Set<MazeState> Goals = problem.getGoalStates(); 
        MazeState idealState = new MazeState(-100, -100);
        for ( MazeState state : Goals) {
            if ((getDist(location, state) + getPastCost(problem, location, node)) < 
                    getDist(idealState, state) + getPastCost(problem, location, node)) { 
                idealState = state;
            }
        }
        return idealState;
    }
    
    /**
     * Completes an A* search from the current MazeState to the current goal state.
     * @param current A MazeState that the search is currently located at.
     * @param frontier a Priority Queue that contains all of the visited SearchTreeNodes
     * @param problem A MazeProblem that specifies the maze, actions, transitions.
     * @param currentGoal A MazeState goal that the search is currently tying to locate.
     * @param graveYard A Set of MazeStates that the search has already visited. 
     * @param foundKey A boolean value that returns true if the search has already located the key, false otherwise. 
     * @return ArrayList of the directions it took to go from the current state to the goal state.
     */
    private static ArrayList<String> aStarSearch(MazeState current, PriorityQueue<SearchTreeNode> frontier, MazeProblem problem, 
            MazeState currentGoal, Set<MazeState> graveYard, boolean foundKey) {
        while (!frontier.isEmpty()) {
            SearchTreeNode expandingNode = frontier.peek(); //the current state's node.
            if (foundKey == true) {
                currentGoal = findBestGoal(problem, expandingNode.state, expandingNode); //find best goal.
            }
            if (expandingNode.state.equals(currentGoal)) { //found goal.
                return tracePath(expandingNode);
            }
            frontier.poll();
            graveYard.add(expandingNode.state);
            Map<String, MazeState> availableMoves = new HashMap<>();
            availableMoves = problem.getTransitions(expandingNode.state);
            for (Map.Entry<String, MazeState> Entry : availableMoves.entrySet()) {
                if (!graveYard.contains(Entry.getValue())) { 
                    SearchTreeNode newChildNode =new SearchTreeNode(Entry.getValue(), Entry.getKey(), expandingNode,
                            getPastCost(problem, Entry.getValue(), expandingNode), getDist(Entry.getValue(), currentGoal), 
                            (getPastCost(problem, Entry.getValue(), expandingNode) + getDist(Entry.getValue(), currentGoal))); //create all available children
                    
                    frontier.add(newChildNode);
                }
            }
        }
        return null;
    }
}
