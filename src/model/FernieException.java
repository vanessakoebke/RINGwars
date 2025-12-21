package model;

/**
 * Thrown when the agent tries to place more fernies on a node than is allowed by the max fernie count per node. 
 * Is a subclass of {@link MoveException}.
 * <p>
 * The number of fernies that has actually been added can be retrieved by {@link #getFernies()}.
 */
public class FernieException extends MoveException {
    private int actualFernies; //number of fernies that was actually placed
    
    /**
     * Initializes the FernieException with the number of fernies that have actually been placed.
     * @param actualFernies number of actually placed fernies
     */
    public FernieException(int actualFernies) {
        super("You were trying to place more fernies on the node than is allowed. Only " + actualFernies + " fernies have been placed. The rest is available for further moves.");
        this.actualFernies = actualFernies;
    }
    
    /**
     * Returns the number of fernies that have actually been placed.
     * @return  number of actually placed fernies
     */
    public int getFernies() {
        return actualFernies;
    }
}
