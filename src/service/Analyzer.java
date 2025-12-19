package service;

import model.*;

/**
 * Implements the logic to deduce the optimal strategy for the given game
 * history and analyzes and saves relevant information from the previous round.
 */
public class Analyzer {
    
    /**
     * Returns a strategy based on the state of the ring this round, and last round,
     * and the notes.
     * 
     * @return selected strategy
     */
    public static Strategy getStrategy(Ring thisRound, Notes notes) {
        // Analyze the results of the previous round.
        if (notes.getCurrentRound() != 1) {
            analyze(thisRound, notes);
        }
        /*
         * Simple reflex agent
         * 
         */
        if (thisRound == null || thisRound.getAvailableFernies() == 0) {
            return new EmptyMove(notes);
        }
        if (!thisRound.isOpponentVisible()) {
            return new Expansion(notes);
        }
        int available = thisRound.getAvailableFernies() + thisRound.calcUnnecessary();
        if (thisRound.getNodes(Owner.THEIRS).size() < 5 && thisRound.getNodes(Owner.THEIRS).size() > 0
                && available > thisRound.getMaxNode(Owner.THEIRS).getFernieCount() * notes.getAttackBuffer() * 1.1) {
            return new AttackMax(notes);
        }
        if (thisRound.getFernies(Owner.THEIRS) > thisRound.getFernies(Owner.MINE) * 1.5) {
            return new Defensive(notes);
        }
        /*
         * Learning agent
         */
        if (notes.isAnalysed()) {
            return new MixedStrategy(notes);
        } else {
            return new FallBack(notes);
        }
    }

    /**
     * Returns the ring from the previous round in the state it was left by my agent.
     * @return the ring prediction from the previous round
     */
    private static Ring getPreviousRound() {
        Ring ringPrediction;
        try {
            ringPrediction = Util.readStatusFile("prediction");
        } catch (InvalidStatusException e) {
            e.printStackTrace();
            return null;
        }
        return ringPrediction;
    }

    /**
     * Analyzes the notes and writes back updated information.
     * 
     * @param notes notes
     */
    private static void analyze(Ring thisRound, Notes notes) {
        Ring previousRound = getPreviousRound();
        if (previousRound == null) {
            return;
        }
        /*
         * Initialize ratios for round 1
         */
        if (!notes.isAnalysed()) {
            notes.setRatiosThisRound(0, 0, 0, 1, 0); 
            notes.setAnalysed();
        }
        // Opponent's attacks
        int lastRoundAttacksByOpponent = 0;
        for (Node nodeP : previousRound.getNodes(Owner.MINE)) {
            Node nodeT = thisRound.getNodeByNumber(nodeP.getNodeNumber());
            if (nodeT.getOwner() != Owner.MINE && !notes.getAbandoned().contains(nodeT.getNodeNumber())) {
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
        //To differentiate between "I have not attacked" and "No attacks were blocked" I use -1 for the first case and 0 for the second.
        int blockedAttacksLastRoundRel = -1;
        if (!notes.getMyAttacks().isEmpty() ) {
            blockedAttacksLastRoundRel = blockedAttacksAbs / notes.getMyAttacks().size();
        }
        notes.setBlockedAttacksLastRound(blockedAttacksLastRoundRel);

        if (notes.getBlockedAttacksTotal() != -1 && blockedAttacksLastRoundRel != -1) {
            notes.setBlockedAttacksTotal(
                    (blockedAttacksLastRoundRel + notes.getBlockedAttacksTotal() * (notes.getCurrentRound() - 1))
                    / notes.getCurrentRound());            
        }
        notes.initNewRound();
        
        /*If more than 30% of last
         * rounds attacks were blocked, the attack buffer will be incremented by 10%
         * points.
         */
        if (blockedAttacksLastRoundRel > 0.3) {
            notes.setAttackBuffer(notes.getAttackBuffer() + 0.1);
        }
        
        /*
         *  Opponent's aggressiveness
         *  If the opponent attacks less than 1/8 of my nodes, their aggressiveness is rated the lowest level. If they attack between 1/8 and 1/3
         *  they are rated on the middle level. If they attack more than 1/3, they are rated the highest aggressiveness level.
         */
        if (lastRoundAttacksByOpponent > 0 && lastRoundAttacksByOpponent <=previousRound.getNodes(Owner.MINE).size()  /8) {
            switch (notes.getAggressiveness()) {
            case UNKNOWN:  notes.increaseRatioBy(1, 0.05); break;
            default: ;
            }
                notes.setAggressiveness(StrategyOpponent.AGRESSIVE_1);
            
        } else if (lastRoundAttacksByOpponent > previousRound.getNodes(Owner.MINE).size() /8  
                && lastRoundAttacksByOpponent < previousRound.getNodes(Owner.MINE).size() /3) {
            switch (notes.getAggressiveness()) {
            case UNKNOWN:  notes.increaseRatioBy(1, 0.1); break;
            case AGRESSIVE_1: notes.increaseRatioBy(1, 0.05); break;
            default: ;
            }
                notes.setAggressiveness(StrategyOpponent.AGRESSIVE_2);
        } else {
            switch (notes.getAggressiveness()) {
            case UNKNOWN:  notes.increaseRatioBy(1, 0.15); break;
            case AGRESSIVE_1: notes.increaseRatioBy(1, 0.1); break;
            case AGRESSIVE_2: notes.increaseRatioBy(1, 0.05);
            default: ;
            }
            notes.setAggressiveness(StrategyOpponent.AGRESSIVE_3);
        }
    /*
     * Opponent's defensiveness 
     * If the opponent blocks > 5% more attacks than on
     * average, the opponent's defensiveness is incremented. If the opponent blocks
     * >5% less attacks than on average, the opponent's defensiveness is decreased.
     */
    if(blockedAttacksLastRoundRel>notes.getBlockedAttacksTotal()*1.05)

    {
        if (notes.getDefensiveness() == StrategyOpponent.UNKNOWN) {
            notes.setDefensiveness(StrategyOpponent.DEFENSIVE_1);
        } else {
            notes.incrementStrategy(notes.getDefensiveness());
            notes.setAttackBuffer(notes.getAttackBuffer() + 0.1);
        }
    }else if(blockedAttacksLastRoundRel<notes.getBlockedAttacksTotal()*0.95)
    {
        if (notes.getDefensiveness() == StrategyOpponent.UNKNOWN) {
            notes.setDefensiveness(StrategyOpponent.DEFENSIVE_1);
        } else {
            notes.decrementStrategy(notes.getDefensiveness());
            notes.setAttackBuffer(notes.getAttackBuffer() - 0.05);
            // it's intentionally 0.05 because a decrease in defensiveness in one round
            // might not mean that this behavior will continue => safety buffer
        }
    }
}}
