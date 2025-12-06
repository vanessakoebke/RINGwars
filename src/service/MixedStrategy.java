package service;

import model.*;

/**
 * Represents a mixed strategy that combines elements of different basic strategies.
 */
public class MixedStrategy extends Strategy {
    double[] ratio;

    public MixedStrategy(Notes notes) {
        super(notes);
        this.ratio = notes.getRatiosThisRound();
    }

    /**
     * Executes the Mixed Strategy using the ratios stored in the { @link Notes } object and returns the output that will be written into the move file.
     * @param ring the ring
     * @return the output
     */
    @Override
    public Output move(Ring ring) {
        Output output = new Output(ring.getMaxFerniesThisRound());
        removeUnnecessary(ring, output);
        new AttackMax(notes).move(ring, output, ratio[2]);
        new AttackMin(notes).move(ring, output, ratio[3]);
        new Expansion(notes).move(ring, output, ratio[0]);
        new Consolidation(notes).move(ring, output, ratio[1]);
        new Defensive(notes).move(ring, output, ratio[4]);
        distributeUnused(ring, output);
        return output;
    }
    
    /**
     * Returns the name of the Strategy as String.
     * @return "Mixed Strategy"
     */
    @Override
    public String toString() {
        return "Mixed Strategy";
    }
}
