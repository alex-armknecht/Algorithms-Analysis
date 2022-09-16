package main.pathfinder.uninformed;

/**
 * SearchTreeNode class that is used in the Search algorithm to 
 * construct the Search tree.
 */
class SearchTreeNode {
    
    // [!] Fields public for exercise purposes only
    public MazeState state;
    public String action;
    public SearchTreeNode parent;
    
    /**
     * Constructs a new SearchTreeNode to be used in the Search Tree.
     * 
     * @param state The MazeState (col, row) that this node represents.
     * @param action The action that *led to* this state / node.
     * @param parent Reference to parent SearchTreeNode in the Search Tree.
     */
    public SearchTreeNode (MazeState state, String action, SearchTreeNode parent) {
        this.state = state;
        this.action = action;
        this.parent = parent;
    }
    
}