package model;

/**
 * Exception thrown when the agent tries to place for fernies on a node than is allowed by the max fernie count per node in the ring. 
 * Is a subclass of {@link MoveException}.
 * <p>
 * The number of fernies that has actually been added can be retrieved by {@link #getFernies()}
 */
public class FernieException extends MoveException {
    private int actualFernies;
    
    /**
     * Initializes the FernieException with the numer of fernies that have actually been placed.
     * @param actualFernies number of actually placed fernies
     */
    public FernieException(int actualFernies) {
        super("Du versuchst zu viele Fernies auf einen Knoten zu legen. Die Ferniezahl wird gekappt. Es wurden lediglich " + actualFernies + " Fernies hinzugefügt.");
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
