package service;

import java.util.List;

import model.*;

public class Angriff extends Strategy {
    
    public Angriff(Notizen notizen) {
        super(notizen);
    }
    
    @Override
    public List<String> move(Ring ring) {
        Ausgabe ausgabe = new Ausgabe(ring.getMaxFerniesRunde());
        double puffer = notizen.getPufferAgriff();
        while (ring.getFerniesVerfuegbar() > ring.getMinKnoten(Besitz.SEINS).getFernieAnzahl() * puffer) {
            Knoten knotenGegner = ring.getMinKnoten(Besitz.SEINS);
            int ferniesAngriff = (int) (knotenGegner.getFernieAnzahl() * puffer);
            try {
                ring.greifeAn(knotenGegner.getKnotenNummer(), ferniesAngriff);
                ausgabe.upsert(knotenGegner.getKnotenNummer(), ferniesAngriff);
            } catch (FernieException e) {
                ausgabe.upsert(knotenGegner.getKnotenNummer(), e.getFernies());
            } catch (MoveException e) {
                System.out.println("Knotennummer " + knotenGegner.getKnotenNummer() + ": " + e.getMessage());
            }
        }
        verteileRest(ring, ausgabe);
        return ausgabe.getAusgabe(ring);
    }
}
