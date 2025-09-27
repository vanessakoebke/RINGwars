//Abstrakte Klasse für die verschiedenen Strategien
package service;

import java.util.List;

import model.*;

public abstract class Strategy {
    Notizen notizen;

    public Strategy(Notizen notizen) {
        this.notizen = notizen;
    }

    public abstract List<String> move(Ring ring);

    // Klassenmethode, die je nach aktuellem Status des Rings eine Strategie
    // zurückliefert
    public static Strategy getStrategy(Ring ring, Notizen notizen) {
        if (ring == null) {
            return new LeereMove(notizen);
        } else if (!ring.isGegnerSichtbar()) {
            return new Expansion(notizen);
        } else if (ring.getDurchschnittFernieProKnoten(Besitz.SEINS) > ring.getDurchschnittFernieProKnoten(Besitz.MEINS)
                * 2) {
            return new Konsolidierung(notizen);
        } else if ((ring.getSichtbarkeitAnteil() >= 0.9)
                && (ring.getFernies(Besitz.MEINS) > ring.getFernies(Besitz.SEINS))) {
            return new Angriff(notizen);
        } else if (ring.getFernies(Besitz.MEINS) < ring.getFernies(Besitz.SEINS)) {
            return new Defensiv(notizen);
        } else {
            return new FallBack(notizen); // TODO prüfen
        }
    }

    public void verteileRest(Ring ring, Ausgabe ausgabe) {
        /*
         * Wenn nach Beendigung der jeweiligen Strategie noch Fernies verfügbar sind,
         * sollen diese wenn möglich alle verteilt werden. Zuerst werden Fernies auf die
         * unkontrollierten Knoten gesetzt.
         */
        List<Knoten> aktuelleKnoten = ring.getKnoten(Besitz.UNKONTROLLIERT);
        int ferniesAktuell;
        while ((!ring.isRingVoll(Besitz.UNKONTROLLIERT)) && ring.getFerniesVerfuegbar() > 0) {
            ferniesAktuell = ring.getFerniesVerfuegbar();
            try {
                ring.addFernies(aktuelleKnoten.getFirst().getKnotenNummer(), ferniesAktuell);
                ausgabe.upsert(aktuelleKnoten.getFirst().getKnotenNummer(), ferniesAktuell);
            } catch (FernieException e) {
                ausgabe.upsert(aktuelleKnoten.getFirst().getKnotenNummer(), e.getFernies());
            } catch (MoveException e) {
                System.out
                        .println("Knotennummer " + aktuelleKnoten.getFirst().getKnotenNummer() + ": " + e.getMessage());
                e.printStackTrace();
            } finally {
                aktuelleKnoten.removeFirst();
            }
        }
        /*
         * Wenn die unkontrollierten Knoten alle voll sind, sollen die restlichen
         * Fernies auf meine eigenen Knoten verteilt werden.
         */
        while (!ring.isRingVoll(Besitz.MEINS) && ring.getFerniesVerfuegbar() > 0) {
            Knoten knoten = ring.getMinKnoten(Besitz.MEINS);
            ferniesAktuell = ring.getFerniesVerfuegbar();
            try {
                ring.addFernies(knoten.getKnotenNummer(), ferniesAktuell);
                ausgabe.upsert(knoten.getKnotenNummer(), ferniesAktuell);
            } catch (FernieException e) {
                ausgabe.upsert(knoten.getKnotenNummer(), e.getFernies());
            } catch (MoveException e) {
                System.out.println("Knotennummer " + knoten.getKnotenNummer() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        /*
         * Wenn die unkontrollierten und meine Knoten alle voll sind, sollen die Knoten
         * des Gegners angegriffen werden.
         */
//                 TODO hier ggf. die Angriffsstrategie aufrufen
//        while (!ring.isRingVoll(Besitz.SEINS) && ring.getFerniesVerfuegbar() > 0) {
//            ferniesAktuell = ring.getFerniesVerfuegbar();
//            System.out.println(ferniesAktuell);
//            System.exit(0);
//            aktuelleKnoten = ring.getKnoten(Besitz.SEINS);
//            try {
//                ring.addFernies(aktuelleKnoten.getFirst().getKnotenNummer(), ferniesAktuell);
//                ausgabe.upsert(aktuelleKnoten.getFirst().getKnotenNummer(), ferniesAktuell);
//            } catch (FernieException e) {
//                ausgabe.upsert(aktuelleKnoten.getFirst().getKnotenNummer(), e.getFernies());
//            } catch (MoveException e) {
//                System.out
//                        .println("Knotennummer " + aktuelleKnoten.getFirst().getKnotenNummer() + ": " + e.getMessage());
//            } finally {
//                aktuelleKnoten.removeFirst();
//            }
//        }
    }
}
