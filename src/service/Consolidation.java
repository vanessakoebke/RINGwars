package service;

import java.util.*;

import model.*;

public class Consolidation extends Strategy {
    public Consolidation(Notes notes) {
        super(notes);
    }

    @Override
    public Output move(Ring ring){
        Output output = new Output(ring.getMaxFerniesThisRound());
        removeUnnecessary(ring, output);
       output = move(ring, output, 1);
       distributeUnused(ring, output);
       return output;
    }
    
    public Output move(Ring ring, Output output, double ratio) {
        if (ratio == 0) {
            return output;
        }
        ferniesForThisStrategy = (int) (ring.getAvailableFernies() * ratio);
        /*
         * Here a list is created of nodes that are visible to the opponent and lie on my 
         * side of the ring, compared to the opponent's outermost nodes.  
         * To do this, I take the left opponent node with the lowest node number and the 
         * right opponent node with the highest node number, and then iterate through a 
         * for-loop with the length of the visibility radius towards my starting node.
         */
        List<Node> visibleForOpponent = new ArrayList<>();
        Node leftmostOpponent = ring.getClosestOpponentLeft();
        Node rightmostOpponent = ring.getClosestOpponentRight();
        Node candidate;
        for (int i = 1; i <= notes.getVisibility(); i++) {
            int j = i; //Java wants the variable in a lambda expression to be effectively final, therefore I have to create a copy.
            if (leftmostOpponent != null) {
                candidate = ring.filter(k -> (k.getNodeNumber() == (leftmostOpponent.getNodeNumber() - j)));
                if (candidate.getOwner() == Ownership.MINE) {
                    visibleForOpponent.add(candidate);
                }
            }
            if (rightmostOpponent != null) {
                candidate = ring.filter(k -> (k.getNodeNumber() == (rightmostOpponent.getNodeNumber() + j)));
                if (candidate.getOwner() == Ownership.MINE) {
                    visibleForOpponent.add(candidate);
                }
            }
        }
        /*
         * The list of nodes visible to the opponent has now been created.
         * First I remove all unnecessary fernies from invisible nodes.
         * Next, the available fernies are distributed among these nodes.
         * If there are more opponent-visible nodes than available fernies, 
         * the nodes closest to the opponent should be filled first.
         */
        Node node = null;
        if (ferniesForThisStrategy < visibleForOpponent.size()) {
            while (ferniesForThisStrategy > 0 && !visibleForOpponent.isEmpty()) {
                try {
                    node = visibleForOpponent.getFirst();
                    visibleForOpponent.removeFirst();
                    ring.addFernies(node.getNodeNumber(), 1);
                    ferniesForThisStrategy -= 1;
                    output.upsert(node.getNodeNumber(), 1);
                } catch (FernieException e) {
                    ferniesForThisStrategy -= 1;
                    output.upsert(node.getNodeNumber(), e.getFernies());
                } catch (MoveException e) {
                    System.out.println("Node number " + node.getNodeNumber() + ": " + e.getMessage());
                } 
            }
        } else if (visibleForOpponent.size() > 0) {
            /*
             * If there are more available fernies than opponent-visible nodes, the fernies should
             * be distributed evenly among all nodes.
             */
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
    
    @Override
    public String toString() {
        return "Consolidation";
    }
}
