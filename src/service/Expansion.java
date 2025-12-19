package service;

import java.util.*;

import model.*;

/**
 * Represents an expansive strategy that tries to occupy as many nodes as possible.
 */
public class Expansion extends Strategy {
    
    /**
     * Initializes the Strategy object.
     * @param notes the notes
     */
    public Expansion(Notes notes) {
        super(notes);
    }

    /**
     * Executes the Expansion strategy with 100% of the available fernies and returns the output that will be written into the move file.
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
     * Executes the Expansion strategy with a given percentage of the available fernies and updates the output.
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

        List<Node> freeNodes = ring.getNodes(Owner.UNCONTROLLED);
        Node node = null;
        int numberFreeNodes = freeNodes.size();
        /*
         * If there are more uncontrolled nodes than available fernies, the nodes closest
         * to the starting node should be filled first. To do this, fernies are added
         * alternately to the first and last node. Afterwards, the first or last node is
         * removed from the list. By using a modulo 2, fernies are added alternately
         * to the left and right of the starting node.
         */

        if (ferniesForThisStrategy < numberFreeNodes) {
            int i = 0;
            while (ferniesForThisStrategy> 0 && !freeNodes.isEmpty()) {
                try {
                    if (i % 2 == 0) {
                        node = freeNodes.getFirst();
                        freeNodes.removeFirst();
                    } else {
                        node = freeNodes.getLast();
                        freeNodes.removeLast();
                    }
                    if (!ring.checkForNeighbors(node)) {
                        ring.addFernies(node.getNodeNumber(), 1);
                        ferniesForThisStrategy -= 1;
                        output.upsert(node.getNodeNumber(), 1);
                    }
                }  catch (FernieException e) {
                    output.upsert(node.getNodeNumber(), e.getFernies());
                    ferniesForThisStrategy -= e.getFernies();
                } catch (MoveException e) {
                    System.out.println("Node number " + node.getNodeNumber() + ": " + e.getMessage());
                } finally {
                    i++;
                }
            }
        } else if (numberFreeNodes > 0) {
            /*
             * If there are more available fernies than uncontrolled nodes, the fernies should
             * be distributed evenly among all free nodes.
             */
            Iterator<Node> iterator = freeNodes.iterator();
            int ferniesPerNode = ferniesForThisStrategy / (numberFreeNodes);
            while (ferniesForThisStrategy > 0 && iterator.hasNext()) {
                node = iterator.next();
                if (!ring.checkForNeighbors(node)) {
                    try {
                        ring.addFernies(node.getNodeNumber(), ferniesPerNode);
                        ferniesForThisStrategy -= ferniesPerNode;
                        output.upsert(node.getNodeNumber(), ferniesPerNode);
                    } catch (FernieException e) {
                        ferniesForThisStrategy -= ferniesPerNode;
                        output.upsert(node.getNodeNumber(), e.getFernies());
                    } catch (MoveException e) {
                        System.out.println("Node number " + node.getNodeNumber() + ": " + e.getMessage());
                    } 
                } 
            }
        } 
        
        return output;
    }
    

    /**
     * Returns the name of the Strategy as String.
     * @return "Expansion"
     */
    @Override
    public String toString() {
        return "Expansion";
    }
}
