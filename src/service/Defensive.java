package service;

import java.util.List;
import java.util.Random;

import model.*;

/**
 * Represents a defensive strategy that tries to elude the opponent's attacks.
 */
public class Defensive extends Strategy {
    
    /**
     * Initializes the Strategy object.
     * @param notes the notes
     */
    public Defensive(Notes notes) {
        super(notes);
    }

    /**
     * Executes the Defensive strategy with 100% of the available fernies and returns the output that will be written into the move file.
     * @param ring the ring
     * @return the output
     */
    @Override
    public Output move(Ring ring) {
        Output output = new Output(ring.getMaxFerniesThisRound());
        removeAll(ring, output);
        output = move(ring, output, 1);
        distributeUnused(ring, output);
        return output;
    }

    /**
     * Executes the Defensive strategy with a given percentage of the available fernies and updates the output.
     * @param ring the ring
     * @param output the output
     * @param ratio the percentage
     * @return the output
     */
    public Output move(Ring ring, Output output, double ratio) {
        if (ratio == 0) {
            return output;
        }
        ferniesForThisStrategy = (int) (ring.getAvailableFernies() * ratio);
        /*
         * If there are nodes that are invisible to the opponent, the agent tries to occupy all invisible nodes,
         * in order to (1) hide from the opponent and (2) still secure the highest node bonus possible.
         */
        List<Node> invisible = ring.getInvisibleForOpponent(notes.getVisibility());
        if (!invisible.isEmpty()) {
            int ferniesPerNode = ferniesForThisStrategy / invisible.size();
            for (Node node : invisible) {
                try {
                    ring.addFernies(node.getNodeNumber(), ferniesPerNode);
                    output.upsert(node.getNodeNumber(), ferniesPerNode);
                } catch (FernieException e) {
                    output.upsert(node.getNodeNumber(), e.getFernies());
                } catch (MoveException e) {
                    System.out.println("Node number " + node.getNodeNumber() + ": " + e.getMessage());
                    e.printStackTrace();
                    invisible.remove(node);
                }
            }
        } else {
            /*
             * If all nodes on the ring are visible to the opponent, the agents selects a random uncontrolled node,
             * and places all fernies on this node.
             */
            List<Node> list = ring.getNodes(Owner.UNCONTROLLED);
            Node node = list.get(new Random().nextInt(list.size()));
            try {
                ring.addFernies(node.getNodeNumber(), ferniesForThisStrategy);
                output.upsert(node.getNodeNumber(), ferniesForThisStrategy);
            } catch (FernieException e) {
                output.upsert(node.getNodeNumber(), e.getFernies());
            } catch (MoveException e) {
                System.out.println("Node number " + node.getNodeNumber() + ": " + e.getMessage());
                e.printStackTrace();
            };
        } 
        return output;
    }
    
    /**
     * Returns the name of the Strategy as String.
     * @return "Defensive"
     */
    @Override
    public String toString() {
        return "Defensive";
    }
}
