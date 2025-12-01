package model;

import java.util.*;

/**
 * Represents the notes that the agents creates during the round and which it
 * accesses during the round to determine its behavior.
 */
public class Notes {
    private boolean initialAnalysis;
    private Ring previous = null;
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
     * Returns the opponent's strategy.
     * 
     * @return opponent's strategy
     */
    public StrategyOpponent[] getOpponentStrategy() {
        return strategyOpponent;
    }

    public StrategyOpponent getAggressiveness() {
        return this.strategyOpponent[0];
    }

    public void setAggressiveness(StrategyOpponent agr) {
        this.strategyOpponent[0] = agr;
    }

    public StrategyOpponent getDefensiveness() {
        return this.strategyOpponent[1];
    }

    public void setDefensiveness(StrategyOpponent def) {
        this.strategyOpponent[1] = def;
    }

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

    public int getOpponentAttacksTotal() {
        return totalAttacksByOpponent;
    }

    public List<Integer> getAbandoned() {
        return this.abandoned;
    }
    
    public int getVisibility() {
        return visibilityRadius;
    }

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
    
    public void addAbandoned(int nodeNumber) {
        this.abandoned.add(nodeNumber);
    }

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

    public double getBlockedAttacksTotal() {
        return blockedAttacksTotal;
    }

    public double getBlockedAttacksLastRound() {
        return blockedAttacksLastRound;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public int getTotalAttacksByOpponent() {
        return totalAttacksByOpponent;
    }

    public void setTotalAttacksByOpponent(int totalAttacksByOpponent) {
        this.totalAttacksByOpponent = totalAttacksByOpponent;
    }

    public int getLastRoundAttacksByOpponent() {
        return lastRoundAttacksByOpponent;
    }

    public void setLastRoundAttacksByOpponent(int lastRoundAttacksByOpponent) {
        this.lastRoundAttacksByOpponent = lastRoundAttacksByOpponent;
    }

    public void setBlockedAttacksTotal(double blockedAttacksTotal) {
        this.blockedAttacksTotal = blockedAttacksTotal;
    }

    public void setBlockedAttacksLastRound(double blockedAttacksLastRound) {
        this.blockedAttacksLastRound = blockedAttacksLastRound;
    }

    public void initNewRound() {
        myAttacksThisRound = new ArrayList<Integer>();
        abandoned = new ArrayList<Integer>();
    }

    public void setPrevious(Ring previous) {
        this.previous = previous;
    }

    public Ring getPrevious() {
        return previous;
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

    public double[] getRatiosThisRound() {
        return ratiosThisRound;
    }
    
    public double[] getRatiosLastRound() {
        return ratiosLastRound;
    }

    private boolean checkRatios() {
        // TODO entfernen vor Abgabe
        if (ratiosThisRound[0] + ratiosThisRound[1] + ratiosThisRound[2] + ratiosThisRound[3] + ratiosThisRound[4] < 1) {
            System.out.println("Du hast deine Ratios nicht voll ausgeschöpft.");
        }
        return ratiosThisRound[0] + ratiosThisRound[1] + ratiosThisRound[2] + ratiosThisRound[3] + ratiosThisRound[4] <= 1;
    }

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

    public void setAnalysed() {
        initialAnalysis = true;        
    }
}
