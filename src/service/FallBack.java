package service;

import java.util.List;

import model.*;

/**
 * Represents a fallback strategy for when other strategies have produced invalid results.
 */
public class FallBack extends Strategy {
    public FallBack(Notes notes) {
        super(notes);
    }

    /**
     * Returns a very simple output. Uses only the {@link FallBack#distributeUnused} method.
     */
    @Override
    public Output move(Ring ring) {
        Output output = new Output(ring.getMaxFerniesThisRound());
        removeUnnecessary(ring, output);
        distributeUnused(ring, output);
        return output;
    }
    
    /**
     * Returns the name of the strategy.
     */
    @Override
    public String toString() {
        return "Fallback strategy";
    }
}
