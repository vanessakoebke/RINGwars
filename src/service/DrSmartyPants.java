package service;

import model.*;

/**
 * Implements the logic to deduce the optimal strategy for the given game
 * history.
 */
public class DrSmartyPants {
    /**
     * Returns a strategy based on the state of the ring this round, and last round,
     * and the notes.
     * 
     * @return selected strategy
     */
    public static Strategy getStrategy(Ring thisRound, Notes notes) {
        if (thisRound == null) {
            return new EmptyMove(notes);
        }
        if (!thisRound.isOpponentVisible()) {
            notes.setRatiosThisRound(1, 0, 0, 0, 0);
            return new Expansion(notes);
        }
        int available = thisRound.getAvailableFernies() + thisRound.calcUnnecessary();
        if (thisRound.getNodes(Ownership.THEIRS).size() < 5 && available > thisRound.getMaxNode(Ownership.THEIRS).getFernieCount() *1.5 ) {
            notes.setRatiosThisRound(0, 0, 1, 0, 0);
            return new AttackMax(notes);
        }
        if (notes.isAnalysisSuccessful()) {
            if (available > thisRound.getMaxNode(Ownership.THEIRS).getFernieCount()) {
                notes.setRatiosThisRound(0, 0, 1, 0, 0);
                return new AttackMax(notes);
            } else if (available > thisRound.getMinNode(Ownership.THEIRS).getFernieCount() * 2) {
                notes.setRatiosThisRound(0, 0, 0, 1, 0);
                return new AttackMin(notes);
            } else if (notes.getOpponentStrategy()[0] == StrategyOpponent.AGRESSIVE_2) {
                notes.setRatiosThisRound(0.5, 0.5, 0, 0, 0);
            } else if (notes.getOpponentStrategy()[1] == StrategyOpponent.AGRESSIVE_3) {
                notes.setRatiosThisRound(0, 0.5, 0, 0, 0.5);
            }
            return new FallBack(notes);
        } else {
            //TODO implement
            
            }
            /*
             * If I have more fernies than 75% of the opponents fernies, I will attack.
             */
            if (available >= thisRound.getFernies(Ownership.THEIRS) *0.75) { 
                
            }
            return new MixedStrategy(notes);
        }

    

    /**
     * Returns the ring from a given round.
     * 
     * @param agentName agent name
     * @param round     round for which the ring will be retrieved
     * @return
     */
    private static Ring getPreviousRound(int round) {
        Ring ringRound;
        try {
            ringRound = Util.readStatusFile(round);
        } catch (InvalidStatusException e) {
            e.printStackTrace();
            return null;
        }
        return ringRound;
    }

    /**
     * Analyzes the notes and writes updated information back.
     * 
     * @param notes notes
     */
    public static void analyze(Ring thisRound, Notes notes) {
        Ring previousRound =getPreviousRound(notes.getCurrentRound() - 1);
        notes.setPrevious(previousRound);
        if (previousRound == null) {
            return;
        }
        // Opponent's attacks
        int lastRoundAttacksByOpponent = 0;
        for (Node nodeP : previousRound.getNodes(Ownership.MINE)) {
            Node nodeT = thisRound.getNodeByNumber(nodeP.getNodeNumber());
            if (nodeT.getOwner() != Ownership.MINE) {
                lastRoundAttacksByOpponent++;
            }
        }
        notes.setLastRoundAttacksByOpponent(lastRoundAttacksByOpponent);
        int totalAttacksOpponent = lastRoundAttacksByOpponent + notes.getTotalAttacksByOpponent();
        notes.setTotalAttacksByOpponent(totalAttacksOpponent);
        double attacksOpponentAver = totalAttacksOpponent / (notes.getCurrentRound() - 1);
        // My attacks
        int blockedAttacksAbs = 0;
        for (Integer nodeNumber : notes.getMyAttacks()) {
            if (thisRound.getNodeByNumber(nodeNumber.intValue()).getOwner() != Ownership.MINE) {
                blockedAttacksAbs++;
            }
        }
        int blockedAttacksLastRoundRel = blockedAttacksAbs / notes.getMyAttacks().size();
        notes.setBlockedAttacksLastRound(blockedAttacksLastRoundRel);
        notes.setBlockedAttacksTotal(
                (blockedAttacksLastRoundRel + notes.getBlockedAttacksTotal() * notes.getCurrentRound() - 1)
                        / notes.getCurrentRound());
        notes.initMyAttacksThisRound();
        // Attack buffer
        // Strategy opponent
        // Tolerance of 5% above or below the average value
        // Agressiveness
        if (lastRoundAttacksByOpponent > attacksOpponentAver * 1.05) {
            if (notes.getAggressiveness() == StrategyOpponent.UNKNOWN) {
                notes.setAggressiveness(StrategyOpponent.AGRESSIVE_1);
            } else {
                notes.increment(notes.getAggressiveness());
                notes.increaseRatioBy(4, 0.1);
            }
        } else if (lastRoundAttacksByOpponent < attacksOpponentAver * 0.95) {
            if (notes.getAggressiveness() == StrategyOpponent.UNKNOWN) {
                notes.setAggressiveness(StrategyOpponent.AGRESSIVE_1);
            } else {
                notes.decrement(notes.getAggressiveness());
                notes.increaseRatioBy(2, 0.1);
                notes.increaseRatioBy(3, 0.1);
            }
        }
        // Defensiveness
        if (blockedAttacksLastRoundRel > notes.getBlockedAttacksTotal() * 1.05) {
            if (notes.getDefensiveness() == StrategyOpponent.UNKNOWN) {
                notes.setDefensiveness(StrategyOpponent.DEFENSIVE_1);
            } else {
                notes.increment(notes.getDefensiveness());
                notes.setAttackBuffer(notes.getAttackBuffer() + 0.1);
            }
        } else if (blockedAttacksLastRoundRel < notes.getBlockedAttacksTotal() * 0.95) {
            if (notes.getDefensiveness() == StrategyOpponent.UNKNOWN) {
                notes.setDefensiveness(StrategyOpponent.DEFENSIVE_1);
            } else {
                notes.decrement(notes.getDefensiveness());
                notes.setAttackBuffer(notes.getAttackBuffer() - 0.05);
                // it's intentionally 0.05 because a decrease in defensiveness in one round
                // might not mean that this behavior will continue => safety buffer
            }
        }
        notes.setAnalysisSuccessful(true);
    }
}
