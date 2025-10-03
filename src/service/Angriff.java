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
        entferneUeberfluessige(ring, ausgabe);
        /*
         * Zuerst wird der Grenzangriff ausgeführt, da dieser effizienter ist, als der direkte Angriff:
         * Man verliert keine Fernies und gewinnt statt einem Knoten 2 oder 3.
         */
        grenzAngriff(ring, ausgabe);
        direktAngriff(ring, ausgabe);
        verteileRest(ring, ausgabe);
        return ausgabe.getAusgabe(ring);
    }

    private void direktAngriff(Ring ring, Ausgabe ausgabe) {
        List<Knoten> seine = ring.getKnoten(Besitz.SEINS);
        while (!seine.isEmpty() && ring.getFerniesVerfuegbar() > ring.getMinKnoten(seine).getFernieAnzahl() * notizen.getPufferAgriff()) {
            Knoten knotenGegner = ring.getMinKnoten(Besitz.SEINS);
            int ferniesAngriff = (int) (knotenGegner.getFernieAnzahl() * notizen.getPufferAgriff() +1);
            try {
                ring.greifeAn(knotenGegner.getKnotenNummer(), ferniesAngriff);
                ausgabe.upsert(knotenGegner.getKnotenNummer(), ferniesAngriff);
            } catch (FernieException e) {
                ausgabe.upsert(knotenGegner.getKnotenNummer(), e.getFernies());
            } catch (MoveException e) {
                System.out.println("Knotennummer " + knotenGegner.getKnotenNummer() + ": " + e.getMessage());
                break;
            }
        }
    }
    
    private void grenzAngriff(Ring ring, Ausgabe ausgabe) {
        /*
         * Als erstes wird versucht, Dreier-Angriffe durchzuführen, da dies die effizienteste Angriffstechnik ist:
         * Man erhält die Fernies des Gegner und hat anschließend 3 Knoten mehr. 
         */
        List<Knoten> listeFrei4 = ring.getKnotenFreieNachbarn(2, 2);
        while (!listeFrei4.isEmpty() && ring.getFerniesVerfuegbar() > ring.getMinKnoten(listeFrei4).getFernieAnzahl() * notizen.getPufferAgriff()) {
            Knoten knotenGegner = ring.getMinKnoten(listeFrei4);
            int ferniesAngriff = (int) (knotenGegner.getFernieAnzahl() * notizen.getPufferAgriff() + 2);
            try {
                ring.addFernies(knotenGegner.getKnotenNummer() +1, ferniesAngriff/2);
                ring.addFernies(knotenGegner.getKnotenNummer() -1, ferniesAngriff/2);
                ausgabe.upsert(knotenGegner.getKnotenNummer() +1, ferniesAngriff/2);
                ausgabe.upsert(knotenGegner.getKnotenNummer() -1, ferniesAngriff/2);
            }  catch (MoveException e) {
                System.out.println("Knotennummer " + knotenGegner.getKnotenNummer() + ": " + e.getMessage());
                listeFrei4.remove(knotenGegner);
                continue;
            }
        }
        /*
         * Als zweites wird versucht, Grenzkämpfe durchzuführen, da dies die zweiteffizienteste Angriffstechnik ist:
         * Man erhält die Fernies des Gegners und hat anschließend aber nur 2 Knoten mehr.
         */
        List<Knoten> listeFrei2 = ring.getKnotenFreieNachbarn(2);
        while (!listeFrei2.isEmpty() && ring.getFerniesVerfuegbar() > ring.getMinKnoten(listeFrei2).getFernieAnzahl() * notizen.getPufferAgriff()) {
            Knoten knotenGegner = ring.getMinKnoten(listeFrei2);
            int ferniesAngriff = (int) (knotenGegner.getFernieAnzahl() * notizen.getPufferAgriff() + 1);
            try {
                boolean vorFrei = ring.getKnotenMitNummer(knotenGegner.getKnotenNummer() +1).getBesitz() == Besitz.UNKONTROLLIERT 
                        && ring.getKnotenMitNummer(knotenGegner.getKnotenNummer() +1).getBesitz() == Besitz.UNKONTROLLIERT ; 
                if (vorFrei) {
                    ring.addFernies(knotenGegner.getKnotenNummer() +1, ferniesAngriff);
                    ausgabe.upsert(knotenGegner.getKnotenNummer() +1, ferniesAngriff);                    
                } else {
                    ring.addFernies(knotenGegner.getKnotenNummer() -1, ferniesAngriff);
                    ausgabe.upsert(knotenGegner.getKnotenNummer() -1, ferniesAngriff);
                }
            }  catch (FernieException e) {
                ausgabe.upsert(knotenGegner.getKnotenNummer(), e.getFernies());
            } catch (MoveException e) {
                System.out.println("Knotennummer " + knotenGegner.getKnotenNummer() + ": " + e.getMessage());
                listeFrei2.remove(knotenGegner);
                continue;
            }
        }
    }
    
    @Override
    public String toString() {
        return "Angriff";
    }
}
