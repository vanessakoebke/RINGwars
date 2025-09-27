package service;

import java.util.*;

import model.*;

public class Konsolidierung extends Strategy {
    public Konsolidierung(Notizen notizen) {
        super(notizen);
    }

    @Override
    public List<String> move(Ring ring) {
        /*
         * Hier wird eine Liste erstellt, der Knoten, die für den Gegner sichtbar sind
         * und auf meiner Seite des Rings liegen, im Vergleich zu den äußerten Knoten
         * des Gegners. Dazu nehme ich den linken gegnerischen Knoten mit der
         * niedrigsten Knotennummer und den rechten gegnerischen Knoten mit der höchsten
         * Knotennummer und durchlaufe anschließend eine for-Schleife mit der Länge des
         * Sichtbarkeitsradiuses in Richtung zu meinem Startknoten.
         */
        List<Knoten> sichtbarFuerGegner = new ArrayList<>();
        Knoten gegnerLinks = ring.getGegnerLinks();
        Knoten gegnerRechts = ring.getGegnerRechts();
        Knoten kandidat;
        for (int i = 1; i <= notizen.getSichtbarkeit(); i++) {
            int j = i; // Java will, dass die Variable im Lambda-Ausdruck effectively final ist, daher
                       // muss ich hier eine lokale Kopie anlegen.
            if (gegnerLinks != null) {
                kandidat = ring.filter(k -> (k.getKnotenNummer() == (gegnerLinks.getKnotenNummer() - j)));
                if (kandidat.getBesitz() == Besitz.MEINS) {
                    sichtbarFuerGegner.add(kandidat);
                }
            }
            if (gegnerRechts != null) {
                kandidat = ring.filter(k -> (k.getKnotenNummer() == (gegnerRechts.getKnotenNummer() + j)));
                if (kandidat.getBesitz() == Besitz.MEINS) {
                    sichtbarFuerGegner.add(kandidat);
                }
            }
        }
        /*
         * Die Liste der Knoten, die für den Gegner sichtbar sind, ist nun erstellt.
         * Jetzt werden die verfügbaren Fernies auf diese Knoten verteilt.
         * Falls es mehr für den Gegner sichtbare Knoten als verfügbare Fernies gibt,
         * sollen zuerst die Knoten, die am nächsten am Gegner liegen, zuerst besetzt werden.
         */
        Ausgabe ausgabe = new Ausgabe(ring.getMaxFerniesRunde());
        Knoten knoten = null;
        if (ring.getFerniesVerfuegbar() < sichtbarFuerGegner.size()) {
            while (ring.getFerniesVerfuegbar() > 0 && !sichtbarFuerGegner.isEmpty()) {
                try {
                    knoten = sichtbarFuerGegner.getFirst();
                    sichtbarFuerGegner.removeFirst();
                    ring.addFernies(knoten.getKnotenNummer(), 1);
                    ausgabe.upsert(knoten.getKnotenNummer(), 1);
                } catch (FernieException e) {
                    ausgabe.upsert(knoten.getKnotenNummer(), e.getFernies());
                } catch (MoveException e) {
                    System.out.println("Knotennummer " + knoten.getKnotenNummer() + ": " + e.getMessage());
                } 
            }
        } else if (sichtbarFuerGegner.size() > 0) {
            /*
             * Falls es mehr verfügbare Fernies als für den Gegner sichtbare Knoten gibt, sollen die
             * Fernies gleichmäßig auf alle Knoten verteilt werden.
             */
            Iterator<Knoten> iterator = sichtbarFuerGegner.iterator();
            int ferniesProKnoten = ring.getFerniesVerfuegbar() / (sichtbarFuerGegner.size());
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
        verteileRest(ring, ausgabe);
        return ausgabe.getAusgabe(ring);
    }
    
    @Override
    public String toString() {
        return "Konsolidierung";
    }
}
