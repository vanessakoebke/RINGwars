package service;

import java.util.*;

import model.*;

public class Expansion extends Strategy {
    public Expansion(Notes notes) {
        super(notes);
    }

    @Override
    public List<String> move(Ring ring) {
        
        Output output = new Output(ring.getMaxFerniesThisRound());
        removeUnnecessary(ring, output);
        List<Node> freeNodes = ring.getNodes(Ownership.UNCONTROLLED);
        Node node = null;
        int numberFreeNodes = freeNodes.size();
        /*
         * If there are more uncontrolled nodes than available fernies, the nodes closest
         * to the starting node should be filled first. To do this, fernies are added
         * alternately to the first and last node. Afterwards, the first or last node is
         * removed from the list. By using a modulo 2, fernies are added alternately
         * to the left and right of the starting node.
         */

        if (ring.getAvailableFernies() < numberFreeNodes) {
            int i = 0;
            while (ring.getAvailableFernies() > 0 && !freeNodes.isEmpty()) {
                try {
                    if (i % 2 == 0) {
                        node = freeNodes.getFirst();
                        freeNodes.removeFirst();
                        ring.addFernies(node.getNodeNumber(), 1);
                        output.upsert(node.getNodeNumber(), 1);
                    } else {
                        node = freeNodes.getLast();
                        freeNodes.removeLast();
                        ring.addFernies(node.getNodeNumber(), 1);
                        output.upsert(node.getNodeNumber(), 1);
                    }
                }  catch (FernieException e) {
                    output.upsert(node.getNodeNumber(), e.getFernies());
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
            int ferniesPerNode = ring.getAvailableFernies() / (numberFreeNodes);
            while (ring.getAvailableFernies() > 0 && iterator.hasNext()) {
                node = iterator.next();
                try {
                    ring.addFernies(node.getNodeNumber(), ferniesPerNode);
                    output.upsert(node.getNodeNumber(), ferniesPerNode);
                }  catch (FernieException e) {
                    output.upsert(node.getNodeNumber(), e.getFernies());
                } catch (MoveException e) {
                    System.out.println("Node number " + node.getNodeNumber() + ": " + e.getMessage());
                } 
            }
        } 
        distributeUnused(ring, output); 
        
        return output.getOutput(ring);
    }
    
    @Override
    public String toString() {
        return "Expansion";
    }
}
