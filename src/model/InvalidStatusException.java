package model;

/**
 * Thrown when the status file of the current step is not valid.
 */
public class InvalidStatusException extends Exception{
    
    /**
     * Initializes the exception with a message.
     * @param message the exception message
     */
    public InvalidStatusException(String message) {
        super("Invalid status file: "+ message);
    }
}
