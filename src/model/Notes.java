package model;

import java.util.*;

/**
 * Represents the notes that the agents creates during the round and which it
 * accesses during the round to determine its behavior.
 */
public class Notes {
    private boolean initialAnalysis;
    private int currentRound;
    private StrategyOpponent[] strategyOpponent;
    private int totalAttacksByOpponent;
    private int lastRoundAttacksByOpponent;
    private int visibilityRadius;
    private List<Integer> myAttacksThisRound;
    private List<Integer> abandoned;
    private double blockedAttacksTotal;
    private double blockedAttacksLastRound;
    private double attackBuffer;
    private final double[] ratiosLastRound;
    private final double[] ratiosThisRound;


    public Notes(int round, StrategyOpponent[] strategyOpponent, int totalAttacksByOpponent,
            int lastRoundAttacksByOpponent, int visibility, List<Integer> myAttacksLastRound, List<Integer> abandoned,
            double blockedAttacksTotal, double blockedAttacksLastRound, double attackBuffer, double[] ratios, boolean analysis) {
        this.currentRound = round;
        this.strategyOpponent = strategyOpponent;
        if (strategyOpponent[0] == null) {
            this.strategyOpponent[0] = StrategyOpponent.UNKNOWN;
            this.strategyOpponent[1] = StrategyOpponent.UNKNOWN;
        }
        this.totalAttacksByOpponent = totalAttacksByOpponent;
        this.lastRoundAttacksByOpponent = lastRoundAttacksByOpponent;
        this.visibilityRadius = visibility;
        this.myAttacksThisRound = myAttacksLastRound;
        this.abandoned = abandoned;
        this.blockedAttacksTotal = blockedAttacksTotal;
        this.blockedAttacksLastRound = blockedAttacksLastRound;
        this.attackBuffer = attackBuffer;
        this.ratiosLastRound = ratios;
        this.ratiosThisRound = ratios;
        this.initialAnalysis = analysis;
    }

    public Notes(int round, int visibility) {
        this(round, new StrategyOpponent[2], 0, 0, visibility, new ArrayList<Integer>(), new ArrayList<Integer>(), 0.0, 0.0, 1.0, new double[]{0,0,0,0,0}, false);
    }

    /**
     * Returns the opponent's aggressiveness (low, middle or high).
     * @return opponent's aggressiveness
     */
    public StrategyOpponent getAggressiveness() {
        return this.strategyOpponent[0];
    }

    /**
     * Sets the opponent's aggressiveness (low, middle or high).
     * @param agr opponent's aggressiveness
     */
    public void setAggressiveness(StrategyOpponent agr) {
        this.strategyOpponent[0] = agr;
    }

    /**
     * Returns the opponent's defensiveness (low, middle or high).
     * @return opponent's defensiveness
     */
    public StrategyOpponent getDefensiveness() {
        return this.strategyOpponent[1];
    }

    /**
     * Sets the opponent's defensiveness (low, middle or high).
     * @param def opponent's defensiveness
     */
    public void setDefensiveness(StrategyOpponent def) {
        this.strategyOpponent[1] = def;
    }

    /**
     * Increments the opponent's aggressiveness or defensiveness by one level.
     * @param s aggressiveness or defensiveness
     */
    public void incrementStrategy(StrategyOpponent s) {
        switch (s) {
        case AGRESSIVE_1:
            this.strategyOpponent[0] = StrategyOpponent.AGRESSIVE_2;
        case AGRESSIVE_2:
            this.strategyOpponent[0] = StrategyOpponent.AGRESSIVE_3;
        case AGRESSIVE_3:
            this.strategyOpponent[0] = StrategyOpponent.AGRESSIVE_3;
        case DEFENSIVE_1:
            this.strategyOpponent[1] = StrategyOpponent.DEFENSIVE_2;
        case DEFENSIVE_2:
            this.strategyOpponent[1] = StrategyOpponent.DEFENSIVE_3;
        case DEFENSIVE_3:
            this.strategyOpponent[1] = StrategyOpponent.DEFENSIVE_3;
        default:
            ;
        }
    }

    /**
     * Decrements the opponent's aggressiveness or defensiveness by one level.
     * @param s aggressiveness or defensiveness
     */
    public void decrementStrategy(StrategyOpponent s) {
        switch (s) {
        case AGRESSIVE_1:
            this.strategyOpponent[0] = StrategyOpponent.AGRESSIVE_1;
        case AGRESSIVE_2:
            this.strategyOpponent[0] = StrategyOpponent.AGRESSIVE_1;
        case AGRESSIVE_3:
            this.strategyOpponent[0] = StrategyOpponent.AGRESSIVE_2;
        case DEFENSIVE_1:
            this.strategyOpponent[1] = StrategyOpponent.DEFENSIVE_1;
        case DEFENSIVE_2:
            this.strategyOpponent[1] = StrategyOpponent.DEFENSIVE_1;
        case DEFENSIVE_3:
            this.strategyOpponent[1] = StrategyOpponent.DEFENSIVE_2;
        default:
        }
    }

    /**
     * Returns the total number of attacks the opponent has carried out in this game.
     * @return total number of attacks.
     */
    public int getOpponentAttacksTotal() {
        return totalAttacksByOpponent;
    }

    /**
     * Returns the nodes that my agent has abandoned during the previous round.
     * @return abandoned nodes
     */
    public List<Integer> getAbandoned() {
        return this.abandoned;
    }
    
    /**
     * Returns the calculated visibility range.
     * @return the visibility range
     */
    public int getVisibility() {
        return visibilityRadius;
    }

    /**
     * Returns the nodes that my agent has attacked during the previous round.
     * @return
     */
    public List<Integer> getMyAttacks() {
        return myAttacksThisRound;
    }

    /**
     * Adds a node number to the list of the agent's attacks in the current round.
     * 
     * @param nodeNumber attacked node number
     */
    public void addAttack(int nodeNumber) {
        if (!myAttacksThisRound.contains(nodeNumber)) {
            this.myAttacksThisRound.add(nodeNumber);
        }
    }
    
    /**
     * Adds a node number to list of nodes that my agent has abandoned this round.
     * @param nodeNumber
     */
    public void addAbandoned(int nodeNumber) {
        this.abandoned.add(nodeNumber);
    }

    /**
     * Returns the current attack buffer.
     * @return attack buffer
     */
    public double getAttackBuffer() {
        return attackBuffer;
    }

    /**
     * Changes the attack buffer to a given values.
     * 
     * @param attackBuffer new attack buffer
     */
    public void setAttackBuffer(double attackBuffer) {
        this.attackBuffer = attackBuffer;
    }

    /**
     * Returns the relative number of attacks that were blocked by the opponent during this game.
     * @return relative number of blocked attacks
     */
    public double getBlockedAttacksTotal() {
        return blockedAttacksTotal;
    }

    /**
     * Returns the relative number of attacks that were blocked by the opponent during the previous round.
     * @return relative number of blocked attacks
     */
    public double getBlockedAttacksLastRound() {
        return blockedAttacksLastRound;
    }

    /**
     * Returns the current round number.
     * @return current round
     */
    public int getCurrentRound() {
        return currentRound;
    }

    /**
     * Returns the total number of attacks by the opponent during the current game.
     * @return total number of attacks
     */
    public int getTotalAttacksByOpponent() {
        return totalAttacksByOpponent;
    }

    /**
     * Sets the total number of attacks by the opponent during the current game.
     * @param totalAttacksByOpponent total number of attacks
     */
    public void setTotalAttacksByOpponent(int totalAttacksByOpponent) {
        this.totalAttacksByOpponent = totalAttacksByOpponent;
    }

    /**
     * Sets the number of attacks by the opponent during the previous round.
     * @param lastRoundAttacksByOpponent attacks during the previous round
     */
    public void setLastRoundAttacksByOpponent(int lastRoundAttacksByOpponent) {
        this.lastRoundAttacksByOpponent = lastRoundAttacksByOpponent;
    }

    /**
     * Sets the relative number of attacks that were blocked by the opponent during the current game. 
     * @param blockedAttacksTotal blocked attacks
     */
    public void setBlockedAttacksTotal(double blockedAttacksTotal) {
        this.blockedAttacksTotal = blockedAttacksTotal;
    }

    /**
     * Sets the relative number of attacks that were blocked by the opponent during the previous round.
     * @param blockedAttacksLastRound blocked attacks
     */
    public void setBlockedAttacksLastRound(double blockedAttacksLastRound) {
        this.blockedAttacksLastRound = blockedAttacksLastRound;
    }

    /**
     * Initializes the notes for a new round after the analysis by emptying the list of abandoned and attacked notes of the previous round.
     */
    public void initNewRound() {
        myAttacksThisRound = new ArrayList<Integer>();
        abandoned = new ArrayList<Integer>();
    }

    /**
     * Returns the notes a String, so it can be saved in the notes.txt file.
     * 
     * @return notes as String
     */
    @Override
    public String toString() {
        String myAttacksString = "";
        Iterator<Integer> iterator = myAttacksThisRound.iterator();
        // The first element has been put before the while loop, so that the String
        // doesn't end with a comma.
        if (iterator.hasNext()) {
            myAttacksString = String.valueOf(iterator.next());
        }
        while (iterator.hasNext()) {
            myAttacksString += "," + String.valueOf(iterator.next());
        }
        
        String abandonedString = "";
        Iterator<Integer> iteratorA = abandoned.iterator();
        // The first element has been put before the while loop, so that the String
        // doesn't end with a comma.
        if (iteratorA.hasNext()) {
            abandonedString = String.valueOf(iteratorA.next());
        }
        while (iteratorA.hasNext()) {
            abandonedString += "," + String.valueOf(iteratorA.next());
        }
        //@formatter:off
        return "Opponent's strategy: " + strategyOpponent[0] + "," + strategyOpponent[1] + System.lineSeparator() + 
                "Opponent's attacks total: " + totalAttacksByOpponent + System.lineSeparator() + 
                "Opponent's attacks last round: " + lastRoundAttacksByOpponent + System.lineSeparator() + 
                "Visibility radius: " + visibilityRadius  + System.lineSeparator() + 
                "My attacks last round: " + myAttacksString + System.lineSeparator() +
                "Blocked attacks total: " + blockedAttacksTotal + System.lineSeparator() +
                "Blocked attacks last round: " + blockedAttacksLastRound + System.lineSeparator() + 
                "Attack buffer: " + attackBuffer + System.lineSeparator() +
                "Used strategies (Expansion, Consolidation, Attack, Defensive): " + 
                ratiosThisRound[0] + "," + ratiosThisRound[1] + "," + ratiosThisRound[2] + "," + ratiosThisRound[3] + "," + ratiosThisRound[4] + System.lineSeparator() +
                "Abandoned nodes: " + abandonedString + System.lineSeparator() +
                "Initial analysis concluded: " + initialAnalysis;
    }
    //@formatter:on

    /**
     * Returns whether the initial analysis of the previous round has been successful.
     * @return true if the initial analysis was successful, false otherwise
     */
    public boolean isAnalysed() {
        return initialAnalysis;
    }



    /**
     * Sets the ration for the different kind of strategies.
     * 
     * @param expansion     ratio for expansion strategy
     * @param consolidation ratio for consolidation strategy
     * @param attackMax        ratio for attack strategy
     * @param defensive     ratio for defensive strategy
     */
    public void setRatiosThisRound(double expansion, double consolidation, double attackMax, double attackMin, double defensive) {
        ratiosThisRound[0] = expansion;
        ratiosThisRound[1] = consolidation;
        ratiosThisRound[2] = attackMax;
        ratiosThisRound[3] = attackMin;
        ratiosThisRound[4] = defensive;
        // TODO entfernen vor abgabe
        if (!checkRatios()) {
            System.out.println("Deine Ratios stimmen nicht (setRatios)");
        }
    }

    /**
     * Returns the mixed strategy ratios of the current round.
     * @return ratios
     */
    public double[] getRatiosThisRound() {
        return ratiosThisRound;
    }
    
    /**
     * Returns the mixed strategy ratios of the previous round.
     * @return ratios
     */
    public double[] getRatiosLastRound() {
        return ratiosLastRound;
    }

    /**
     * Checks if the calculated ratios are not superior to 1.
     * @return true if the sum of the ratios is <= 1, false otherwise
     */
    private boolean checkRatios() {
        // TODO entfernen vor Abgabe
        if (ratiosThisRound[0] + ratiosThisRound[1] + ratiosThisRound[2] + ratiosThisRound[3] + ratiosThisRound[4] < 1) {
            System.out.println("Du hast deine Ratios nicht voll ausgeschöpft.");
        }
        return ratiosThisRound[0] + ratiosThisRound[1] + ratiosThisRound[2] + ratiosThisRound[3] + ratiosThisRound[4] <= 1;
    }

    /**
     * Increases the ratio of a given strategy by a given value for the mixed strategy case.
     * @param strategy the strategy
     * @param increase the increase
     */
    public void increaseRatioBy(int strategy, double increase) {
        switch (strategy) {
        /*
         * Expansion
         */
        case 0:
            if (ratiosThisRound[4] >= increase) {
                ratiosThisRound[4] -= increase;
            } else if (ratiosThisRound[1] >= increase) {
                ratiosThisRound[1] -= increase;
            } else if (ratiosThisRound[2] >= increase) {
                ratiosThisRound[2] -= increase;
            } else if (ratiosThisRound[3] >= increase) {
                ratiosThisRound[3] -= increase;
            } else {
                return;
            }
            ratiosThisRound[0] += increase;
        //Consolidation    
        case 1:
            if (ratiosThisRound[2] >= increase) {
                ratiosThisRound[2] -= increase;
            } else if (ratiosThisRound[3] >= increase) {
                ratiosThisRound[3] -= increase;
            } else if (ratiosThisRound[0] >= increase) {
                ratiosThisRound[0] -= increase;
            } else if (ratiosThisRound[4] >= increase) {
                ratiosThisRound[4] -= increase;
            } else {
                return;
            }
            ratiosThisRound[1] += increase;
        //AttackMax
        case 2:
            if (ratiosThisRound[4] >= increase) {
                ratiosThisRound[4] -= increase;
            } else if (ratiosThisRound[1] >= increase) {
                ratiosThisRound[1] -= increase;
            } else if (ratiosThisRound[0] >= increase) {
                ratiosThisRound[0] -= increase;
            } else if (ratiosThisRound[3] >= increase) {
                ratiosThisRound[3] -= increase;
            } else {
                return;
            }
            ratiosThisRound[2] += increase;
        //AttackMin
        case 3:
            if (ratiosThisRound[4] >= increase) {
                ratiosThisRound[4] -= increase;
            } else if (ratiosThisRound[1] >= increase) {
                ratiosThisRound[1] -= increase;
            } else if (ratiosThisRound[0] >= increase) {
                ratiosThisRound[0] -= increase;
            } else if (ratiosThisRound[2] >= increase) {
                ratiosThisRound[2] -= increase;
            } else {
                return;
            }
            ratiosThisRound[2] += increase;
        //Defensive
        case 4:
            if (ratiosThisRound[2] >= increase) {
                ratiosThisRound[2] -= increase;
            } else if (ratiosThisRound[3] >= increase) {
                ratiosThisRound[3] -= increase;
            } else if (ratiosThisRound[0] >= increase) {
                ratiosThisRound[0] -= increase;
            } else if (ratiosThisRound[1] >= increase) {
                ratiosThisRound[1] -= increase;
            } else {
                return;
            }
            ratiosThisRound[4] += increase;
        }
        // TODO entfernen vor Abgabe
        if (!checkRatios()) {
            System.out.println("Deine Ratios stimmen nicht (increase).");
        }
    }
    
    //TODO prüfen ob nötig
    public void decreaseRatioBy(int strategy, double decrease) {
        switch (strategy) {
        /*
         * Expansion
         */
        case 0:
            if (ratiosThisRound[0] >= decrease) {
                ratiosThisRound[0] -= decrease;
            }
            if (ratiosThisRound[4] >= decrease) {
                ratiosThisRound[4] -= decrease;
            } else if (ratiosThisRound[1] >= decrease) {
                ratiosThisRound[1] -= decrease;
            } else if (ratiosThisRound[2] >= decrease) {
                ratiosThisRound[2] -= decrease;
            } else if (ratiosThisRound[3] >= decrease) {
                ratiosThisRound[3] -= decrease;
            } else {
                return;
            }
        //Consolidation    
        case 1:
            if (ratiosThisRound[2] >= decrease) {
                ratiosThisRound[2] -= decrease;
            } else if (ratiosThisRound[3] >= decrease) {
                ratiosThisRound[3] -= decrease;
            } else if (ratiosThisRound[0] >= decrease) {
                ratiosThisRound[0] -= decrease;
            } else if (ratiosThisRound[4] >= decrease) {
                ratiosThisRound[4] -= decrease;
            } else {
                return;
            }
            ratiosThisRound[1] += decrease;
        //AttackMax
        case 2:
            if (ratiosThisRound[4] >= decrease) {
                ratiosThisRound[4] -= decrease;
            } else if (ratiosThisRound[1] >= decrease) {
                ratiosThisRound[1] -= decrease;
            } else if (ratiosThisRound[0] >= decrease) {
                ratiosThisRound[0] -= decrease;
            } else if (ratiosThisRound[3] >= decrease) {
                ratiosThisRound[3] -= decrease;
            } else {
                return;
            }
            ratiosThisRound[2] += decrease;
        //AttackMin
        case 3:
            if (ratiosThisRound[4] >= decrease) {
                ratiosThisRound[4] -= decrease;
            } else if (ratiosThisRound[1] >= decrease) {
                ratiosThisRound[1] -= decrease;
            } else if (ratiosThisRound[0] >= decrease) {
                ratiosThisRound[0] -= decrease;
            } else if (ratiosThisRound[2] >= decrease) {
                ratiosThisRound[2] -= decrease;
            } else {
                return;
            }
            ratiosThisRound[2] += decrease;
        //Defensive
        case 4:
            if (ratiosThisRound[2] >= decrease) {
                ratiosThisRound[2] -= decrease;
            } else if (ratiosThisRound[3] >= decrease) {
                ratiosThisRound[3] -= decrease;
            } else if (ratiosThisRound[0] >= decrease) {
                ratiosThisRound[0] -= decrease;
            } else if (ratiosThisRound[1] >= decrease) {
                ratiosThisRound[1] -= decrease;
            } else {
                return;
            }
            ratiosThisRound[4] += decrease;
        }
        // TODO entfernen vor Abgabe
        if (!checkRatios()) {
            System.out.println("Deine Ratios stimmen nicht (increase).");
        }
    }
    // TODO implementieren
//    public void decreaseRatio (String strategy) {
//        switch(strategy){
//        case "E":
//            if (defensiveRatio >= 0.05) {
//                defensiveRatio -= 0.05;
//            } else if (consolidationRatio >= 0.05) {
//                consolidationRatio -= 0.05;
//            } else if (attackRatio >= 0.05) {
//                attackRatio -= 0.05;
//            } else {
//                return;
//            }
//            expansionRatio += 0.05;
//        case "C": 
//            if (attackRatio >= 0.05) {
//                attackRatio -= 0.05;
//            } else if (expansionRatio >= 0.05) {
//                expansionRatio -= 0.05;
//            } else if (defensiveRatio >= 0.05) {
//                defensiveRatio -= 0.05;
//            } else {
//                return;
//            }
//            consolidationRatio += 0.05;
//        case "A":
//            if (defensiveRatio >= 0.05) {
//                defensiveRatio -= 0.05;
//            } else if (consolidationRatio >= 0.05) {
//                consolidationRatio -= 0.05;
//            } else if (expansionRatio >= 0.05) {
//                expansionRatio -= 0.05;
//            } else {
//                return;
//            }
//            attackRatio += 0.05;
//        case "D": 
//            if (attackRatio >= 0.05) {
//                attackRatio -= 0.05;
//            } else if (expansionRatio >= 0.05) {
//                expansionRatio -= 0.05;
//            } else if (consolidationRatio >= 0.05) {
//                consolidationRatio -= 0.05;
//            } else {
//                return;
//            }
//            defensiveRatio += 0.05;
//        }
//        //TODO entfernen vor Abgabe
//        if (!checkRatios()) {
//            System.out.println("Deine Ratios stimmen nicht.");
//        }
//    }

    /**
     * Sets the value of initial analysis to true.
     */
    public void setAnalysed() {
        initialAnalysis = true;        
    }
}
