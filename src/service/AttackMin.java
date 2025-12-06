package service;

import model.Notes;

/**
 * Attacks the nodes with the lowest fernie counts.
 */
public class AttackMin extends Attack {
    
    /**
     * Initializes the Strategy object.
     * @param notes the notes
     */
    public AttackMin(Notes notes) {
        super(notes, false);
    }
}