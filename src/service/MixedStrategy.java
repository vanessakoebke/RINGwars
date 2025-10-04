package service;

import java.util.List;

import model.*;

public class MixedStrategy extends Strategy {
    private double expansionRatio;
    private double consolidationRatio;
    private double attackRatio;
    private double defensiveRatio;
    private boolean attackMax;
    
    private MixedStrategy(Notes notes, double expansionRatio, double consolidationRatio, double attackRatio,
            double defensiveRatio, boolean attackMax) {
        super(notes);
        this.expansionRatio = expansionRatio;
        this.consolidationRatio = consolidationRatio;
        this.attackRatio = attackRatio;
        this.defensiveRatio = defensiveRatio;
        this.attackMax = attackMax;
    }

    @Override
    public Output move(Ring ring) {
        Output output = new Output(ring.getMaxFerniesThisRound());
        removeUnnecessary(ring, output);
        new Consolidation(notes).move(ring, output, consolidationRatio);
        if (attackMax) {
            new AttackMax(notes).move(ring, output, attackRatio);
        } else {
            new AttackMin(notes).move(ring, output, attackRatio);
        }
        new Defensive(notes).move(ring, output, defensiveRatio);
        new Defensive(notes).move(ring, output, expansionRatio);
        distributeUnused(ring, output);
        return output;
    }


}
