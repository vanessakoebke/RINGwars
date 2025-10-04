package service;

import model.*;

/**
 * Implements the logic to deduce the optimal strategy for the given game history.
 */
public class DrSmartyPants {
    private static Ring thisRound;
    private static Ring lastRound;
    private static Notes notes;
    
    public Strategy run() {
        double successConsolidation;
        double successAttack;
        double successDefensive;
        return new Expansion(notes);
    }
    
    /**
     * Static method that returns a strategy based on the state of the ring this round, and last roung, and the notes.
     */
    public static Strategy getStrategy() {
        if (thisRound == null) {
            return new EmptyMove(notes);
        } else if (!thisRound.isOpponentVisible() || (thisRound.isOpponentVisible() && notes.getOpponentStrategy() != StrategyOpponent.AGRESSIVE)) {
            notes.setRatios(1, 0, 0, 0);
            return new Expansion(notes);
        } else if (thisRound.getAverageFerniesPerNode(Ownership.THEIRS) > thisRound.getAverageFerniesPerNode(Ownership.MINE) * 2
                || notes.getOpponentStrategy() == StrategyOpponent.AGRESSIVE) {
            notes.setRatios(0, 1, 0, 0);
            return new Consolidation(notes);
        } else if ((thisRound.getFernies(Ownership.MINE) > thisRound.getFernies(Ownership.THEIRS))) {
            notes.setRatios(0, 0, 1, 0);
            return new AttackMax(notes);
//        } else if (ring.getFernies(Besitz.MEINS) < ring.getFernies(Besitz.SEINS)) {
//            return new Defensiv(notizen);
        } else {
            notes.setRatios(0, 0, 0, 0);
            return new FallBack(notes); 
        }
    }
}
