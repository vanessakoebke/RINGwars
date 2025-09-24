package service;

import java.util.ArrayList;
import java.util.List;

import model.Knoten;
import model.Ring;

public class Expansion extends Strategy {

    @Override
    public List<String> move(Ring ring) {
        List<String> output = new ArrayList<String>();
        List<Knoten> sichtbare = ring.getSichtbareKnoten();
        Knoten knoten;
        int anzahlSichtbare = sichtbare.size();
        if (ring.getFerniesVerfuegbar() < anzahlSichtbare) {
            for (int i = 0; i < ring.getFerniesVerfuegbar(); i++) {
                /*
                 * Falls es mehr sichtbare Knoten als verfügbare Fernies gibt, sollen die Knoten
                 * am nächsten zum Startknoten zuerst belegt werden. Dafür füge ich abwechselnd von vorne und hinten Fernies auf die Knoten hinzu.
                 */               
                if (i % 2 == 0) {
                    knoten = sichtbare.getFirst();
                    output.add(knoten.getKnotenNummer() + ",1" );
                    sichtbare.removeFirst();
                } else {
                    knoten = sichtbare.getLast();
                    output.add(knoten.getKnotenNummer() + ",1");
                    sichtbare.removeLast();
                }
            }
        } else {
            int ferniesProKnoten = ring.getFerniesVerfuegbar() / (anzahlSichtbare -1);
            int rest = ring.getFerniesVerfuegbar() % (anzahlSichtbare -1);
            output.add(sichtbare.get(1).getKnotenNummer() + "," + (ferniesProKnoten + rest));
            for (int i = 2; i < (anzahlSichtbare); i++) {
                output.add(sichtbare.get(i).getKnotenNummer() + "," + ferniesProKnoten);
            }
        }

        return output;
    }
}
