package service;

import model.Notes;
import model.Ring;

/**
 * Implements the logic to deduce the optimal strategy for the given game history.
 */
public class DrSmartyPants {
    private Ring thisRound;
    private Ring lastRound;
    private Notes notes;
    
    public Strategy run(Ring thisRound, Ring lastRound, Notes notes) {
        double successConsolidation;
        double successAttack;
        double successDefensive;
        return new Expansion(notes);
    }
}
