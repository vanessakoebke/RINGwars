package service;

import java.util.List;

import model.*;

/**
 * Is used when the step file is invalid and an empty move file should be created.
 */
public class EmptyMove extends Strategy {

    public EmptyMove(Notes notes) {
        super(notes);
    }

    /**
     * Returns null.
     * @return null
     */
    @Override
    public List<String> move(Ring ring) {
        return null;
    }
    
    /**
     * Returns the name of the strategy and an explanation.
     * @return strategy name and message
     */
    @Override
    public String toString() {
        return "Empty move file is created (step file was invalid";
    }
}
