package service;

import model.Notes;

/**
 * Attacks the nodes with the highest fernie count.
 */
public class AttackMax extends Attack {
    
    AttackMax(Notes notes) {
        super(notes, true);
    }
}
