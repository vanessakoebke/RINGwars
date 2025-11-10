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
        /*
         * Simple reflex agent
         * 
         */
        if (thisRound == null || thisRound.getAvailableFernies() == 0) {
            return new EmptyMove(notes);
        }
        int available = thisRound.getAvailableFernies() + thisRound.calcUnnecessary();
        if (!thisRound.isOpponentVisible()) {
            notes.setRatiosThisRound(1, 0, 0, 0, 0);
            return new Expansion(notes);
        }
        if (thisRound.getNodes(Owner.THEIRS).size() < 5
                && available > thisRound.getMaxNode(Owner.THEIRS).getFernieCount() * notes.getAttackBuffer() * 1.1) {
            notes.setRatiosThisRound(0, 0, 1, 0, 0);
            return new AttackMax(notes);
        }
        if (thisRound.getFernies(Owner.THEIRS) > thisRound.getFernies(Owner.MINE) * 2) {
            notes.setRatiosThisRound(0, 0, 0, 0, 1);
            return new Defensive(notes);
        }
        /*
         * Learning agent
         */
        if (notes.getCurrentRound() != 1) {
            analyze(thisRound, notes);
        }
        if (notes.isAnalysed()) {
            return new MixedStrategy(notes);      
        } else {
            return new FallBack(notes);
        }

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
        Ring previousRound = getPreviousRound(notes.getCurrentRound() - 1);
        notes.setPrevious(previousRound);
        if (previousRound == null) {
            return;
        }
        /*
         * Initialize ratios for round
         */
        int available = thisRound.getAvailableFernies() + thisRound.calcUnnecessary();
        if (!notes.isAnalysed()) {
            if (available > thisRound.getFernies(Owner.THEIRS) * 1.1) {
                notes.setRatiosThisRound(0, 0, 0, 1, 0);
            } else if (notes.getOpponentStrategy()[0] == StrategyOpponent.AGRESSIVE_2) {
                notes.setRatiosThisRound(0, 0.5, 0, 0.5, 0);
            } else if (notes.getOpponentStrategy()[1] == StrategyOpponent.AGRESSIVE_3) {
                notes.setRatiosThisRound(0, 0.5, 0, 0, 0.5);
            } else {
                notes.setRatiosThisRound(0, 0, 0, 1, 0);
            }
            notes.setAnalysed();
        }
        // Opponent's attacks
        int lastRoundAttacksByOpponent = 0;
        for (Node nodeP : previousRound.getNodes(Owner.MINE)) {
            Node nodeT = thisRound.getNodeByNumber(nodeP.getNodeNumber());
            if (nodeT.getOwner() != Owner.MINE) {
                lastRoundAttacksByOpponent++;
            }
        }
        notes.setLastRoundAttacksByOpponent(lastRoundAttacksByOpponent);
        int totalAttacksOpponent = lastRoundAttacksByOpponent + notes.getTotalAttacksByOpponent();
        notes.setTotalAttacksByOpponent(totalAttacksOpponent);
        /*
         * My attacks 
         * I will check whether an attacked node has become mine. If not, it
         * increments the counter of blocked attacks. This will be important for
         * determining a possible defensive strategy by the opponent and the attack
         * buffer for the current round.
         */
        int blockedAttacksAbs = 0;
        for (Integer nodeNumber : notes.getMyAttacks()) {
            if (thisRound.getNodeByNumber(nodeNumber.intValue()).getOwner() != Owner.MINE) {
                blockedAttacksAbs++;
            }
        }
        int blockedAttacksLastRoundRel = -1;
        if (!notes.getMyAttacks().isEmpty()) {
            blockedAttacksLastRoundRel = blockedAttacksAbs / notes.getMyAttacks().size();
        }
        /*If more than 30% of last
        * rounds attacks were blocked, the attack buffer will be incremented by 10%
        * points.
        */
        if (blockedAttacksLastRoundRel > 0.3) {
            notes.setAttackBuffer(notes.getAttackBuffer() + 0.1);
        }
        notes.setBlockedAttacksLastRound(blockedAttacksLastRoundRel);
        notes.setBlockedAttacksTotal(
                (blockedAttacksLastRoundRel + notes.getBlockedAttacksTotal() * notes.getCurrentRound() - 1)
                        / notes.getCurrentRound());
        notes.initNewRound();
        
        
        /*
         *  Opponent's aggressiveness
         *  If the opponent attacks > 5% more nodes than on average, the opponent's aggressiveness is incremented.
         *  If the opponent attacks >5% less nodes than on average, the opponent's aggressiveness is decreased.
         */
        double attacksOpponentAver = totalAttacksOpponent / (notes.getCurrentRound() - 1);
        if (lastRoundAttacksByOpponent > attacksOpponentAver * 1.05) {
            if (notes.getAggressiveness() == StrategyOpponent.UNKNOWN) {
                notes.setAggressiveness(StrategyOpponent.AGRESSIVE_1);
            } else {
                notes.incrementStrategy(notes.getAggressiveness());
                notes.increaseRatioBy(2, 0.1);
            }
        } else if (lastRoundAttacksByOpponent < attacksOpponentAver * 0.95) {
            if (notes.getAggressiveness() == StrategyOpponent.UNKNOWN) {
                notes.setAggressiveness(StrategyOpponent.AGRESSIVE_1);
            } else {
                notes.decrementStrategy(notes.getAggressiveness());
                notes.increaseRatioBy(2, 0.1);
                notes.increaseRatioBy(3, 0.1);
            }
        }
        /*
         *  Opponent's defensiveness
         *  If the opponent blocks > 5% more attacks than on average, the opponent's defensiveness is incremented.
         *  If the opponent blocks >5% less attacks than on average, the opponent's defensiveness is decreased.
         */
        if (blockedAttacksLastRoundRel > notes.getBlockedAttacksTotal() * 1.05) {
            if (notes.getDefensiveness() == StrategyOpponent.UNKNOWN) {
                notes.setDefensiveness(StrategyOpponent.DEFENSIVE_1);
            } else {
                notes.incrementStrategy(notes.getDefensiveness());
                notes.setAttackBuffer(notes.getAttackBuffer() + 0.1);
            }
        } else if (blockedAttacksLastRoundRel < notes.getBlockedAttacksTotal() * 0.95) {
            if (notes.getDefensiveness() == StrategyOpponent.UNKNOWN) {
                notes.setDefensiveness(StrategyOpponent.DEFENSIVE_1);
            } else {
                notes.decrementStrategy(notes.getDefensiveness());
                notes.setAttackBuffer(notes.getAttackBuffer() - 0.05);
                // it's intentionally 0.05 because a decrease in defensiveness in one round
                // might not mean that this behavior will continue => safety buffer
            }
        }
    }
}
