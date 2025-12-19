package model;

import java.util.*;

/**
 * Represents the notes that the agents creates during the game and which it
 * accesses during each round to determine its behavior. The notes contain information about the state of the game (e.g. the current round
 * or the visibility range), about my agent's and the opponent's behavior (e.g. attacks or blocks).
 */
public class Notes {
    private boolean initialAnalysis; //whether the first analysis in round 2 was carried out successfully
    private int currentRound; 
    private int visibilityRadius;
    private StrategyOpponent[] strategyOpponent; //Element 0 represents the opponent's aggressiveness and element 1 their defensiveness.
    private int totalAttacksByOpponent; //Total number of attacks that the opponent carried out against my agent during the current game.
    private int lastRoundAttacksByOpponent; //Number of attacks the opponent carried out against my agent in the previous round.
    private List<Integer> myAttacksThisRound; //Opponent nodes that my agent attacked
    private List<Integer> abandoned; //Nodes that belonged to my agent at the beginning of the round, but that were abandoned during the round. 
    //Necessary to determine whether the node was abandoned willingly or lost due to an opponent attack.
    private double blockedAttacksTotal; //Relative number of my attacks that were blocked by the opponent during the game.
    private double blockedAttacksLastRound; //Relative number of my attacks that were blocked by the opponent during the previous round.
    private double attackBuffer; //The attack buffer is a multiplier used on the number of fernies my agent uses to attack to the opponent.
    private double[] ratios; //The ratio of the different basic strategies used in the MixedStrategy. 
    //Element 0 = Expansion
    //Element 1 = Consolidation
    //Element 2 = AttackMax
    //Element 3 = AttackMin
    //Element 4 = Defensive


    /**
     * Initializes the Notes object.
     * 
     * @param round current round
     * @param strategyOpponent the aggressiveness and defensiveness of the opponent
     * @param totalAttacksByOpponent total number of attacks that the opponent carried out against my agent during the current game.
     * @param lastRoundAttacksByOpponent number of attacks the opponent carried out against my agent in the previous round.
     * @param visibility visibility range
     * @param myAttacksLastRound opponent nodes that my agent attacked
     * @param abandoned nodes my agent abandoned
     * @param blockedAttacksTotal relative number of my attacks that were blocked by the opponent during the game
     * @param blockedAttacksLastRound relative number of my attacks that were blocked by the opponent during the previous round
     * @param attackBuffer attack buffer
     * @param ratios ratio of basic strategies used for the Mixed Strategy
     * @param analysis { @code true } if the initial analysis was successful, { @code false } otherwise
     */
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
        this.ratios = ratios;
        this.initialAnalysis = analysis;
    }

    /**
     * Initializes the Notes object with default values in case the Notes object of the previous round could not be read.
     * @param round current round
     * @param visibility visibility range
     */
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
     * Returns the nodes that my agent has abandoned during the round.
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
     * Adds a node number to the list of my agent's attacks.
     * 
     * @param nodeNumber attacked node number
     */
    public void addAttack(int nodeNumber) {
        if (!myAttacksThisRound.contains(nodeNumber)) {
            this.myAttacksThisRound.add(nodeNumber);
        }
    }
    
    /**
     * Adds a node number to the list of nodes that my agent has abandoned this round.
     * @param nodeNumber number of the abandoned node
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
     * Initializes the notes for a new round after the analysis has concluded, by emptying the list of abandoned and attacked notes of the previous round.
     */
    public void initNewRound() {
        myAttacksThisRound = new ArrayList<Integer>();
        abandoned = new ArrayList<Integer>();
    }

    /**
     * Returns the notes as String, so it can be saved in the notes.txt file.
     * 
     * @return notes as String
     */
    @Override
    public String toString() {
        String myAttacksString = "";
        Iterator<Integer> iterator = myAttacksThisRound.iterator();
        // The first element has been put before the while loop, so that the String doesn't end with a comma.
        if (iterator.hasNext()) {
            myAttacksString = String.valueOf(iterator.next());
        }
        while (iterator.hasNext()) {
            myAttacksString += "," + String.valueOf(iterator.next());
        }
        
        String abandonedString = "";
        Iterator<Integer> iteratorA = abandoned.iterator();
        // The first element has been put before the while loop, so that the String doesn't end with a comma.
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
                "Visibility range: " + visibilityRadius  + System.lineSeparator() + 
                "My attacks last round: " + myAttacksString + System.lineSeparator() +
                "Blocked attacks total: " + blockedAttacksTotal + System.lineSeparator() +
                "Blocked attacks last round: " + blockedAttacksLastRound + System.lineSeparator() + 
                "Attack buffer: " + attackBuffer + System.lineSeparator() +
                "Used strategies (Expansion, Consolidation, AttackMax, AttackMin, Defensive): " + 
                ratios[0] + "," + ratios[1] + "," + ratios[2] + "," + ratios[3] + "," + ratios[4] + System.lineSeparator() +
                "Abandoned nodes: " + abandonedString + System.lineSeparator() +
                "Initial analysis concluded: " + initialAnalysis;
    }
    //@formatter:on

    /**
     * Returns whether the initial analysis of the previous round has been successful.
     * @return { @code true } if the initial analysis was successful, { @code false} otherwise
     */
    public boolean isAnalysed() {
        return initialAnalysis;
    }



    /**
     * Sets the ratio for the different kind of strategies.
     * 
     * @param expansion     ratio for expansion strategy
     * @param consolidation ratio for consolidation strategy
     * @param attackMax        ratio for attackMax strategy
     * @param attackMin     ratio for attackMin strategy
     * @param defensive     ratio for defensive strategy
     */
    public void setRatiosThisRound(double expansion, double consolidation, double attackMax, double attackMin, double defensive) {
        ratios[0] = expansion;
        ratios[1] = consolidation;
        ratios[2] = attackMax;
        ratios[3] = attackMin;
        ratios[4] = defensive;
    }

    /**
     * Returns the mixed strategy ratio.
     * @return ratio
     */
    public double[] getRatiosThisRound() {
        return ratios;
    }
    


    /**
     * Checks if the calculated ratios are not superior to 1.
     * 
     * @return { @code true } if the sum of the ratios is <= 1, { @code false } otherwise
     */
    private boolean checkRatios() {
        return ratios[0] + ratios[1] + ratios[2] + ratios[3] + ratios[4] <= 1;
    }

    /**
     * Increases the ratio of a given strategy by a given value for the mixed strategy case.
     * @param strategy the strategy
     * @param increase the increase
     */
    public void increaseRatioBy(int strategy, double increase) {
        /*
         * If the ratio of a given strategy is to be increased, consequently the ratio of another strategy needs to be lowered. For each strategy the "opposite"
         * strategy (i.e. the strategy that is in strongest contradiction witht the goal of the strategy whose ratio is to be raised) is lowered. However, the
         * ratio of a strategy can only be lowered if the resulting ratio is not inferior to zero.
         */
        switch (strategy) {
        /*
         * Expansion
         */
        case 0:
            if (ratios[4] >= increase) {
                ratios[4] -= increase;
            } else if (ratios[1] >= increase) {
                ratios[1] -= increase;
            } else if (ratios[2] >= increase) {
                ratios[2] -= increase;
            } else if (ratios[3] >= increase) {
                ratios[3] -= increase;
            } else {
                return;
            }
            ratios[0] += increase;
        //Consolidation    
        case 1:
            if (ratios[2] >= increase) {
                ratios[2] -= increase;
            } else if (ratios[3] >= increase) {
                ratios[3] -= increase;
            } else if (ratios[0] >= increase) {
                ratios[0] -= increase;
            } else if (ratios[4] >= increase) {
                ratios[4] -= increase;
            } else {
                return;
            }
            ratios[1] += increase;
        //AttackMax
        case 2:
            if (ratios[4] >= increase) {
                ratios[4] -= increase;
            } else if (ratios[1] >= increase) {
                ratios[1] -= increase;
            } else if (ratios[0] >= increase) {
                ratios[0] -= increase;
            } else if (ratios[3] >= increase) {
                ratios[3] -= increase;
            } else {
                return;
            }
            ratios[2] += increase;
        //AttackMin
        case 3:
            if (ratios[4] >= increase) {
                ratios[4] -= increase;
            } else if (ratios[1] >= increase) {
                ratios[1] -= increase;
            } else if (ratios[0] >= increase) {
                ratios[0] -= increase;
            } else if (ratios[2] >= increase) {
                ratios[2] -= increase;
            } else {
                return;
            }
            ratios[2] += increase;
        //Defensive
        case 4:
            if (ratios[2] >= increase) {
                ratios[2] -= increase;
            } else if (ratios[3] >= increase) {
                ratios[3] -= increase;
            } else if (ratios[0] >= increase) {
                ratios[0] -= increase;
            } else if (ratios[1] >= increase) {
                ratios[1] -= increase;
            } else {
                return;
            }
            ratios[4] += increase;
        }
        if (!checkRatios()) {
            System.out.println("Something went wrong when trying to increase the ratio.");
        }
    }
    
    
    /**
     * Sets the value of initial analysis to true.
     */
    public void setAnalysed() {
        initialAnalysis = true;        
    }
}
