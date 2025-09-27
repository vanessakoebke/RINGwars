package service;

import java.util.List;

import model.*;

public class LeereMove extends Strategy {

    public LeereMove(Notizen notizen) {
        super(notizen);
    }

    @Override
    public List<String> move(Ring ring) {
        return null;
    }
}
