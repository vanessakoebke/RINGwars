//Abstrakte Klasse für die verschiedenen Strategien
package service;

import java.util.List;

import model.*;

/**
 * Abstract class, which declares the methods that are common to all strategies
 * and some utility methods (static).
 */
public abstract class Strategy {
    int ferniesForThisStrategy;
    Notes notes;

    /**
     * Initializes a strategy with the notes from the previous round.
     * 
     * @param notes notes
     */
    public Strategy(Notes notes) {
        this.notes = notes;
    }

    /**
     * Executes the given strategy. Returns the output to be written in the move
     * file.
     * 
     * @param ring the ring
     * @return output
     */
    public abstract Output move(Ring ring);

    /**
     * Returns the opponent's strategy based on information from previous rounds if
     * available.
     * 
     * @return opponent's strategy
     */
    public static StrategyOpponent getGegnerischeStrategie() {
        return StrategyOpponent.UNKNOWN;
    }

    /**
     * Distributes all unused fernies first on uncontrolled nodes, and, if they were
     * all full, on the agent's nodes.
     * 
     * @param ring   current status of the ring
     * @param output output so far
     */
    public void distributeUnused(Ring ring, Output output) {
        /*
         * First the unused fernies are distributed on the uncontrolled nodes, since
         * this is the most efficient (bonus fernies per occupied node).
         */
        int availableFernies;
        System.out.println("Unused fernies are being distributed.");
        output = new Expansion(notes).move(ring, output, 1);
        /*
         * If there are no more uncontrolled nodes, the functions proceeds with filling
         * up the nodes the agent owns, in order to secure them against potential
         * attacks.
         */
        while (!ring.isRingFull(Owner.MINE) && ring.getAvailableFernies() > 0) {
            // TODO hier eventuell die Knoten die am nächsten am Gegner sind zuerst besetzen
            Node node = ring.getMinNode(Owner.MINE);
            availableFernies = ring.getAvailableFernies();
            try {
                ring.addFernies(node.getNodeNumber(), availableFernies);
                output.upsert(node.getNodeNumber(), availableFernies);
            } catch (FernieException e) {
                output.upsert(node.getNodeNumber(), e.getFernies());
            } catch (MoveException e) {
                System.out.println("Node number " + node.getNodeNumber() + ": " + e.getMessage());
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

    /**
     * Leaves on every node owned by the agent only 1 fernie (to hold the node for
     * obtaining the bonus for nodes controlled) and removes the remaining fernies
     * to maximize the available fernie number.
     * 
     * @param ring   ring
     * @param output output
     */
    public void removeUnnecessary(Ring ring, Output output) {
        for (Node node : ring.getNodes(Owner.MINE)) {
            int errorCount = 0;
            if (errorCount > ring.getNodes(Owner.MINE).size() / 2) {
                System.out.println("Something has gone very wrong in the removeUnnecessary method. The method"
                        + "was unsuccessful in more than half the cases.");
            }
            try {
                if (node.getFernieCount() > 1) {
                    int temp = node.getFernieCount() - 1;
                    if (temp > 0) {
                        ring.removeFernies(node.getNodeNumber(), temp);
                        output.remove(node.getNodeNumber(), temp);
                    }
                }
            } catch (MoveException e) {
                // If removing fernies from one node was unsuccessful, the methode shall simply
                // contrinue and remove the fernies from the other nodes.
                errorCount++;
                continue;
            }
        }
        System.out.println("Available fernies this round: " + ring.getAvailableFernies());
    }

    /**
     * Removes all fernies from all nodes owned by the agent.
     * @param ring ring
     * @param output output
     */
    public void removeAll(Ring ring, Output output) {
        for (Node node : ring.getNodes(Owner.MINE)) {
            int errorCount = 0;
            if (errorCount > ring.getNodes(Owner.MINE).size() / 2) {
                System.out.println("Something has gone very wrong in the removeAll method. The method"
                        + "was unsuccessful in more than half the cases.");
            }
            try {
                int temp = node.getFernieCount();
                ring.removeFernies(node.getNodeNumber(), temp);
                output.remove(node.getNodeNumber(), temp);
                notes.addAbandoned(node.getNodeNumber());
            } catch (MoveException e) {
                // If removing fernies from one node was unsuccessful, the methode shall simply
                // contrinue and remove the fernies from the other nodes.
                errorCount++;
                continue;
            }
        }
        System.out.println("Available fernies this round: " + ring.getAvailableFernies());
    }
    
    /**
     * This method checks whether the nodes on either side of a given node are occupied by the opponent. This serves to avoid unintended edge battles.
     * @param ring ring
     * @param node node which should be checked for neighbors
     * @return true if opponent occupies neighboring nodes, else false
     */
     boolean checkForNeighbors(Ring ring, Node node) {
         int next = (node.getNodeNumber() + 1) % ring.getNodes().length;
         int prev = (node.getNodeNumber() - 1 + ring.getNodes().length) % ring.getNodes().length;
        if (ring.getNodeByNumber(next).getOwner() == Owner.THEIRS ||
                ring.getNodeByNumber(prev).getOwner() == Owner.THEIRS ) {
            return true;
        } else {
            return false;
        }
    }
}
