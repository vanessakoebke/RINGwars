package service;

import java.util.*;

import model.*;

public class Expansion extends Strategy {
    public Expansion(Notizen notizen) {
        super(notizen);
    }

    @Override
    public List<String> move(Ring ring) {
        
        Ausgabe ausgabe = new Ausgabe(ring.getMaxFerniesRunde());
        entferneUeberfluessige(ring, ausgabe);
        List<Knoten> freie = ring.getKnoten(Besitz.UNKONTROLLIERT);
        Knoten knoten = null;
        int anzahlFreie = freie.size();
        /*
         * Falls es mehr unkontrollierte Knoten als verfügbare Fernies gibt, sollen die Knoten
         * am nächsten zum Startknoten zuerst belegt werden. Dafür füge ich abwechselnd
         * auf den ersten und den letzten Knoten Fernies hinzu. Anschließend entferne
         * ich jeweils den ersten oder letzten Knoten aus der Liste. Durch die Abfrage
         * mit Modulo 2, füge ich abwechselnd links und rechts vom Startknoten Fernies
         * hinzu.
         */
        if (ring.getFerniesVerfuegbar() < anzahlFreie) {
            int i = 0;
            while (ring.getFerniesVerfuegbar() > 0 && !freie.isEmpty()) {
                try {
                    if (i % 2 == 0) {
                        knoten = freie.getFirst();
                        freie.removeFirst();
                        ring.addFernies(knoten.getKnotenNummer(), 1);
                        ausgabe.upsert(knoten.getKnotenNummer(), 1);
                    } else {
                        knoten = freie.getLast();
                        freie.removeLast();
                        ring.addFernies(knoten.getKnotenNummer(), 1);
                        ausgabe.upsert(knoten.getKnotenNummer(), 1);
                    }
                }  catch (FernieException e) {
                    ausgabe.upsert(knoten.getKnotenNummer(), e.getFernies());
                } catch (MoveException e) {
                    System.out.println("Knotennummer " + knoten.getKnotenNummer() + ": " + e.getMessage());
                } finally {
                    i++;
                }
            }
        } else if (anzahlFreie > 0) {
            /*
             * Falls es mehr verfügbare Fernies als unkontrollierte Knoten gibt, sollen die
             * Fernies gleichmäßig auf alle freien Knoten verteilt werden.
             */
            Iterator<Knoten> iterator = freie.iterator();
            int ferniesProKnoten = ring.getFerniesVerfuegbar() / (anzahlFreie);
            while (ring.getFerniesVerfuegbar() > 0 && iterator.hasNext()) {
                knoten = iterator.next();
                try {
                    ring.addFernies(knoten.getKnotenNummer(), ferniesProKnoten);
                    ausgabe.upsert(knoten.getKnotenNummer(), ferniesProKnoten);
                }  catch (FernieException e) {
                    ausgabe.upsert(knoten.getKnotenNummer(), e.getFernies());
                } catch (MoveException e) {
                    System.out.println("Knotennummer " + knoten.getKnotenNummer() + ": " + e.getMessage());
                } 
            }
        } 
        verteileRest(ring, ausgabe); //Falls bei der Division ferniesProKnoten ein Rest übrig geblieben ist, wird dieser nun verteilt.
        
        return ausgabe.getAusgabe(ring);
    }
    
    @Override
    public String toString() {
        return "Expansion";
    }
}
