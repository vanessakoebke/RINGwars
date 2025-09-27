package service;

import java.util.List;

import model.Notizen;
import model.Ring;

public class FallBack extends Strategy {
    public FallBack(Notizen notizen) {
        super(notizen);
    }

    @Override
    public List<String> move(Ring ring) {
        // TODO Auto-generated method stub
        return null;
    }
}
