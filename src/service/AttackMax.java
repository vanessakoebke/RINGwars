package service;

import model.Notes;

/**
 * Represents a strategy that attacks the nodes with the highest fernie counts.
 */
public class AttackMax extends Attack {
    
    /**
     * Initializes the Strategy object.
     * @param notes the notes
     */
    public AttackMax(Notes notes) {
        super(notes, true);
    }
}
