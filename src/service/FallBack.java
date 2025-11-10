package service;

import java.util.Iterator;
import java.util.List;

import model.*;

/**
 * Represents a fallback strategy for when other strategies have produced invalid results.
 */
public class FallBack extends Strategy {
    public FallBack(Notes notes) {
        super(notes);
    }

    /**
     * Returns a very simple output. Uses only the {@link FallBack#distributeUnused} method.
     */
    @Override
    public Output move(Ring ring) {
        Output output = new Output(ring.getMaxFerniesThisRound());
        try {
            ring = Util.readStatusFile(notes.getCurrentRound());
            List<Node> mine = ring.getNodes(Owner.MINE);
            Node node = null;
            if (ring.getAvailableFernies() < mine.size()) {
                while (ring.getAvailableFernies() > 0 && !mine.isEmpty()) {
                    try {
                        node = mine.getFirst();
                        mine.removeFirst();
                        ring.addFernies(node.getNodeNumber(), 1);
                        output.upsert(node.getNodeNumber(), 1);
                    } catch (FernieException e) {
                        output.upsert(node.getNodeNumber(), e.getFernies());
                    } catch (MoveException e) {
                        System.out.println("Node number " + node.getNodeNumber() + ": " + e.getMessage());
                    } 
                }
            } else  if (mine.size() > 0) {
                int ferniesPerNode = ring.getAvailableFernies() / mine.size();
                for (Node n: mine) {
                    try {
                        ring.addFernies(n.getNodeNumber(), ferniesPerNode);
                        output.upsert(n.getNodeNumber(), ferniesPerNode);
                    }  catch (FernieException e) {
                        ferniesForThisStrategy -= e.getFernies();
                        output.upsert(n.getNodeNumber(), e.getFernies());
                    } catch (MoveException e) {
                        System.out.println("Node number " + n.getNodeNumber() + ": " + e.getMessage());
                    } 
                }
            }
        } catch (InvalidStatusException e) {
            output = new EmptyMove(notes).move(ring);
        }
        return output;
    }
    
    /**
     * Returns the name of the strategy.
     */
    @Override
    public String toString() {
        return "Fallback strategy";
    }
}
