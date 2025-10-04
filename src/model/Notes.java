package model;

import java.util.*;

/**
 * Represents the notes that the agents creates during the round and which he
 * accesses during his round to determine his behavior.
 * <p>
 * The notes store the following information:
 * <ul>
 * <li>The current round number (step number)</li>
 * <li>The opponent's strategy</li>
 * <li>The total number of attacks by the opponent so far</li>
 * <li>The number of attacks in the previous round</li>
 * <li>The visibility radius</li>
 * <li>A list of nodes that the agent attacked in the current round (includes
 * direct and border attacks)</li>
 * <li>The relative number of attacks that were blocked by the opponent (blocked
 * attacks / my attacks)</li>
 * <li>The relative number of attacks defended in the previous round</li>
 * <li>The buffer of additional fernies used in attacks (e.g., a buffer of 1.1
 * means that if a node has 10 enemy fernies, the agent attacks with 11
 * fernies)</li>
 * </ul>
 */
public class Notes {
    private int currentRound;
    private StrategyOpponent strategyOpponent;
    private int totalAttacksByOpponent;
    private int lastRoundAttacksByOpponent;
    private int visibilityRadius;
    private List<Integer> myAttacksThisRound;
    private double blockedAttacksTotal;
    private double blockedAttacksLastRound;
    private double attackBuffer;
    double expansionRatio;
    double consolidationRatio;
    double attackRatio;
    double defensiveRatio;

    public Notes(int round, StrategyOpponent strategyOpponent, int totalAttacksByOpponent,
            int lastRoundAttacksByOpponent, int visibility, double blockedAttacksTotal, double blockedAttacksLastRound,
            double attackBuffer, double expansionRatio, double consolidationRatio, double attackRatio,
            double defensiveRatio) {
        this.currentRound = round;
        this.strategyOpponent = strategyOpponent;
        this.totalAttacksByOpponent = totalAttacksByOpponent;
        this.lastRoundAttacksByOpponent = lastRoundAttacksByOpponent;
        this.visibilityRadius = visibility;
        this.myAttacksThisRound = new ArrayList<>();
        this.blockedAttacksTotal = blockedAttacksTotal;
        this.blockedAttacksLastRound = blockedAttacksLastRound;
        this.attackBuffer = attackBuffer;
        this.expansionRatio = expansionRatio;
        this.consolidationRatio = consolidationRatio;
        this.attackRatio = attackRatio;
        this.defensiveRatio = defensiveRatio;
    }

    public Notes(int round, int visibility) {
        this(round, StrategyOpponent.UNKNOWN, 0, 0, visibility, 0.0, 0.0, 1.0, 0, 0, 0, 0);
    }

    /**
     * Returns the opponent's strategy.
     * 
     * @return opponent's strategy
     */
    public StrategyOpponent getOpponentStrategy() {
        return strategyOpponent;
    }

    public int getOpponentAttacksTotal() {
        return totalAttacksByOpponent;
    }

    public int getOpponentAttacksLastRound() {
        return lastRoundAttacksByOpponent;
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
        this.myAttacksThisRound.add(nodeNumber);
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
        //@formatter:off
        return "Opponent's strategy: " + strategyOpponent + System.lineSeparator() + 
                "Opponent's attacks total: " + totalAttacksByOpponent + System.lineSeparator() + 
                "Opponent's attacks last round: " + lastRoundAttacksByOpponent + System.lineSeparator() + 
                "Visibility radius: " + visibilityRadius  + System.lineSeparator() + 
                "My attacks last round: " + myAttacksString + System.lineSeparator() +
                "Blocked attacks total: " + blockedAttacksTotal + System.lineSeparator() +
                "Blocked attacks last roung: " + blockedAttacksLastRound + System.lineSeparator() + 
                "Attack buffer: " + attackBuffer + System.lineSeparator() +
                "Used strategies (Expansion, Consolidation, Attack, Defensive): " + 
                expansionRatio + "," + consolidationRatio + "," + attackRatio + "," + defensiveRatio;
    }
    
    /**
     * Sets the ration for the different kind of strategies.
     * @param expansion     ratio for expansion strategy
     * @param consolidation ratio for consolidation strategy
     * @param attack    ratio for attack strategy
     * @param defensive ratio for defensive strategy
     */
    public void setRatios(double expansion, double consolidation, double attack, double defensive) {
        this.expansionRatio = expansion;
        this.consolidationRatio = consolidation;
        this.attackRatio = attack;
        this.defensiveRatio = defensive;
    }
}
