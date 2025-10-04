package service;

import java.util.List;

import model.*;

public class Defensive extends Strategy {

    public Defensive(Notes notes) {
        super(notes);
    }

    @Override
    public Output move(Ring ring) {
        Output output = new Output(ring.getMaxFerniesThisRound());
        removeUnnecessary(ring, output);
       output = move(ring, output, 1);
       distributeUnused(ring, output);
       return output;
    }
    
    @Override
    public String toString() {
        return "Defensive";
    }

    public Output move(Ring ring, Output output, double ratio) {
        if (ratio == 0) {
            return output;
        }
        ferniesForThisStrategy = (int) (ring.getAvailableFernies() * ratio);
        return output;
        
    }
}
