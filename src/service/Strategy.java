package service;

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
        output = new Expansion(notes).move(ring, output, 1);
        /*
         * If there are no more uncontrolled nodes, the functions proceeds with filling
         * up the nodes the agent owns, in order to secure them against potential
         * attacks.
         */
        while (!ring.isRingFull(Owner.MINE) && ring.getAvailableFernies() > 0) {
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
    }

    /**
     * Leaves on every node owned by the agent only 1 fernie (to hold the node for
     * obtaining the bonus for nodes controlled) and removes the remaining fernies
     * to maximize the available fernie number for the current round.
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
                // continue and remove the fernies from the other nodes.
                errorCount++;
                continue;
            }
        }
    }
}
