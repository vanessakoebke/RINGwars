package service;

import java.util.List;

import model.Knoten;
import model.Ring;

public class Expansion extends Strategy {

    @Override
    public List<String> move(Ring ring, int fernies) {
        List<Knoten> sichtbare = ring.getSichtbareKnoten();
        int anzahlSichtbare = sichtbare.size();
        int ferniesProKnoten = fernies / anzahlSichtbare;
        return null;
    }
}
