package main.pathfinder.informed;

/**
 * SearchTreeNode that is used in the Search algorithm to construct the Search
 * tree.
 * @author Alex Armknecht
 */
class SearchTreeNode implements Comparable<SearchTreeNode>{
    
    MazeState state;
    String action;
    SearchTreeNode parent;
    int cost;
    int dist;
    int eval;
    
    /**
     * Constructs a new SearchTreeNode to be used in the Search Tree.
     * 
     * @param state The MazeState (row, col) that this node represents.
     * @param action The action that *led to* this state / node.
     * @param parent Reference to parent SearchTreeNode in the Search Tree.
     * @param cost The cost of moving to the SearchTreeNode
     * @param dist The Manhattan distance to the goal SearchTreeNode
     * @param eval The evaluation function of the SearchTreeNode (cost + dist);
     */
    public SearchTreeNode (MazeState state, String action, SearchTreeNode parent, int cost, int dist, int eval) {
        this.state = state;
        this.action = action;
        this.parent = parent;
        this.cost = cost;
        this.dist = dist;
        this.eval = eval;
    }
    
    /**
     * Compares two SearchTreeNodes by their evaluation function value.
     * @param other A SearchTreeNode that will be compared.
     * @return integer value. If int < 0 then the other node has higher priority and vice versa. 
     * If int = 0 then both SearchTreeNodes have the same priority. 
     */
    public int compareTo (SearchTreeNode other) {
        return this.eval - other.eval;
    }
    
}