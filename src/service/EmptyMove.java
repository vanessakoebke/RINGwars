package service;

import model.*;

/**
 * Is used when the step file is invalid and an empty move file should be created.
 */
public class EmptyMove extends Strategy {

    /**
     * Initializes the Strategy object.
     * @param notes
     */
    public EmptyMove(Notes notes) {
        super(notes);
    }

    /**
     * Returns null due to invalid step file.
     * @param the ring
     * @return null
     */
    @Override
    public Output move(Ring ring) {
        return null;
    }
    
    /**
     * Returns the name of the strategy and an explanation.
     * @return strategy name and message
     */
    @Override
    public String toString() {
        return "Empty move file is created (step file was invalid).";
    }
}
