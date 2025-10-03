//Abstrakte Klasse für die verschiedenen Strategien
package service;

import java.util.List;

import model.*;

/**
 * Abstract class, which declares the methods that are common to all strategies and some utility methods (static).
 */
public abstract class Strategy {
    Notes notes;

    /**
     * Initializes a strategy with the notes from the previous round.
     * @param notes notes
     */
    public Strategy(Notes notes) {
        this.notes = notes;
    }

    /**
     * Returns the output to be written in the move file.
     * @param ring the ring
     * @return output 
     */
    public abstract List<String> move(Ring ring);

    /**
     * Static method that returns a strategy based on the state of the ring and the notes.
     * @param ring  ring
     * @param notes notes
     * @return selected strategy
     */
    public static Strategy getStrategy(Ring ring, Notes notes) {
        if (ring == null) {
            return new EmptyMove(notes);
        } else if (!ring.isOpponentVisible() || (ring.isOpponentVisible() && notes.getOpponentStrategy() != StrategyOpponent.AGRESSIVE)) {
            return new Expansion(notes);
        } else if (ring.getAverageFerniesPerNode(Ownership.THEIRS) > ring.getAverageFerniesPerNode(Ownership.MINE) * 2
                || notes.getOpponentStrategy() == StrategyOpponent.AGRESSIVE) {
            return new Consolidation(notes);
        } else if ((ring.getFernies(Ownership.MINE) > ring.getFernies(Ownership.THEIRS))) {
            return new Attack(notes);
//        } else if (ring.getFernies(Besitz.MEINS) < ring.getFernies(Besitz.SEINS)) {
//            return new Defensiv(notizen);
        } else {
            return new FallBack(notes); 
        }
    }
    
    /**
     * Returns the opponent's strategy based on information from previous rounds if available.
     * @return opponent's strategy
     */
    public static StrategyOpponent getGegnerischeStrategie() {
        return StrategyOpponent.UNKNOWN;
    }

    public void distributeUnused(Ring ring, Output ausgabe) {
        /*
         * Wenn nach Beendigung der jeweiligen Strategie noch Fernies verfügbar sind,
         * sollen diese wenn möglich alle verteilt werden. Zuerst werden Fernies auf die
         * unkontrollierten Knoten gesetzt.
         */
        List<Node> aktuelleKnoten = ring.getNodes(Ownership.UNCONTROLLED);
        int ferniesAktuell;
        while ((!ring.isRingFull(Ownership.UNCONTROLLED)) && ring.getAvailableFernies() > 0) {
            ferniesAktuell = ring.getAvailableFernies();
            try {
                ring.addFernies(aktuelleKnoten.getFirst().getNodeNumber(), ferniesAktuell);
                ausgabe.upsert(aktuelleKnoten.getFirst().getNodeNumber(), ferniesAktuell);
            } catch (FernieException e) {
                ausgabe.upsert(aktuelleKnoten.getFirst().getNodeNumber(), e.getFernies());
            } catch (MoveException e) {
                System.out
                        .println("Knotennummer " + aktuelleKnoten.getFirst().getNodeNumber() + ": " + e.getMessage());
                e.printStackTrace();
            } finally {
                aktuelleKnoten.removeFirst();
            }
        }
        /*
         * Wenn die unkontrollierten Knoten alle voll sind, sollen die restlichen
         * Fernies auf meine eigenen Knoten verteilt werden.
         */
        while (!ring.isRingFull(Ownership.MINE) && ring.getAvailableFernies() > 0) {
            Node knoten = ring.getMinNode(Ownership.MINE);
            ferniesAktuell = ring.getAvailableFernies();
            try {
                ring.addFernies(knoten.getNodeNumber(), ferniesAktuell);
                ausgabe.upsert(knoten.getNodeNumber(), ferniesAktuell);
            } catch (FernieException e) {
                ausgabe.upsert(knoten.getNodeNumber(), e.getFernies());
            } catch (MoveException e) {
                System.out.println("Knotennummer " + knoten.getNodeNumber() + ": " + e.getMessage());
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
    public void removeUnnecessary(Ring ring, Output ausgabe) {
        for (Node k: ring.getNodes(Ownership.MINE)) {
            try {
                if (ring.getNodeByNumber(k.getNodeNumber()).getFernieCount() > 1) {
                    int temp = ring.getNodeByNumber(k.getNodeNumber()).getFernieCount() - 1;
                    ring.removeFernies(k.getNodeNumber(), temp);
                    ausgabe.remove(k.getNodeNumber(), temp);
                }
            } catch (MoveException e) {
                continue;
            } 
        }
    }
}
