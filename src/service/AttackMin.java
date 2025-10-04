package service;

import model.Notes;

/**
 * Attacks the nodes with the lowest fernie count.
 */
public class AttackMin extends Attack {
    
    AttackMin(Notes notes) {
        super(notes, false);
    }
}