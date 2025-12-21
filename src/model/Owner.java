package model;

/**
 * Represents the ownership status of a node.
 */
public enum Owner {

    /**
     * Node belongs to my agent.
     */
    MINE, 
    /**
     * Node is uncontrolled.
     */
    UNCONTROLLED, 
    /**
     * Node is invisible.
     */
    UNKNOWN, 
    /**
     * Node belongs to the opponent.
     */
    THEIRS;
}
