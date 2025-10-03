package model;

/**
 * Thrown if a given move is invalid.
 */
public class MoveException extends Exception{
    
    /**
     * Initializes the exception with a message.
     * @param message exception message
     */
    public MoveException(String message) {
        super(message);
    }
}
