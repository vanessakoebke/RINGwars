package service;

import java.util.List;

import model.*;

public class MixedStrategy extends Strategy {
    double expansionRatio;
    double consolidationRatio;
    double attackRatio;
    double defensiveRatio;
    
    private MixedStrategy(Notes notes, double expansionRatio, double consolidationRatio, double attackRatio,
            double defensiveRatio) {
        super(notes);
        this.expansionRatio = expansionRatio;
        this.consolidationRatio = consolidationRatio;
        this.attackRatio = attackRatio;
        this.defensiveRatio = defensiveRatio;
    }

    @Override
    public Output move(Ring ring) {
        Output output = new Output(ring.getMaxFerniesThisRound());
        removeUnnecessary(ring, output);
        new Consolidation(notes).move(ring, output, consolidationRatio);
        new Attack(notes).move(ring, output, attackRatio);
        new Defensive(notes).move(ring, output, defensiveRatio);
        new Defensive(notes).move(ring, output, expansionRatio);
        distributeUnused(ring, output);
        return output;
    }


}
