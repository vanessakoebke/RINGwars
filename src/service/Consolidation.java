package service;

import java.util.*;

import model.*;

/**
 * Represents a strategy that fortifies the nodes closest to the opponent's nodes.
 */
public class Consolidation extends Strategy {
    
    /**
     * Initializes the Strategy object.
     * @param notes the notes
     */
    public Consolidation(Notes notes) {
        super(notes);
    }

    /**
     * Executes the Consolidation strategy with 100% of the available fernies and returns the output to be written in the move file.
     * @param ring the ring
     * @return the output
     */
    @Override
    public Output move(Ring ring){
        Output output = new Output(ring.getMaxFerniesThisRound());
        removeUnnecessary(ring, output);
       output = move(ring, output, 1);
       distributeUnused(ring, output);
       return output;
    }
    
    /**
     * Executes the Consolidation strategy with a given percentage of the available fernies and updates the output.
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
        List<Node> visibleForOpponent = ring.getVisibleForOpponent(notes.getVisibility());
        /*
         * The agent places all available fernies evenly on the nodes that are visible to the opponent.
         */
        Node node = null;
        if (visibleForOpponent.size() > 0) {
            Iterator<Node> iterator = visibleForOpponent.iterator();
            int ferniesPerNode = ferniesForThisStrategy / (visibleForOpponent.size());
            while (ferniesForThisStrategy > 0 && iterator.hasNext()) {
                node = iterator.next();
                try {
                    ring.addFernies(node.getNodeNumber(), ferniesPerNode);
                    ferniesForThisStrategy -= ferniesPerNode;
                    output.upsert(node.getNodeNumber(), ferniesPerNode);
                }  catch (FernieException e) {
                    ferniesForThisStrategy -= e.getFernies();
                    output.upsert(node.getNodeNumber(), e.getFernies());
                } catch (MoveException e) {
                    System.out.println("Node number " + node.getNodeNumber() + ": " + e.getMessage());
                } 
            }
        }
        return output;
    }
    
    /**
     * Returns the name of the Strategy as String.
     * @return "Consolidation"
     */
    @Override
    public String toString() {
        return "Consolidation";
    }
}
