package service;

import model.*;

public class MixedStrategy extends Strategy {
    double[] ratio;

    public MixedStrategy(Notes notes) {
        super(notes);
        this.ratio = notes.getRatiosThisRound();
    }

    @Override
    // TODO rations einpassen
    public Output move(Ring ring) {
        Output output = new Output(ring.getMaxFerniesThisRound());
        removeUnnecessary(ring, output);
        new Consolidation(notes).move(ring, output, ratio[1]);
        new AttackMax(notes).move(ring, output, ratio[2]);
        new AttackMin(notes).move(ring, output, ratio[3]);
        new Defensive(notes).move(ring, output, ratio[4]);
        new Defensive(notes).move(ring, output, ratio[0]);
        distributeUnused(ring, output);
        return output;
    }
}
