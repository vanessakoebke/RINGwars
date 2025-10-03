package service;

import java.util.List;

import model.*;

public class Attack extends Strategy {
    public Attack(Notes notes) {
        super(notes);
    }

    @Override
    public List<String> move(Ring ring) {
        Output output = new Output(ring.getMaxFerniesThisRound());
        removeUnnecessary(ring, output);
        /*
         * First, the edge battle is executed, as it is more efficient than a local
         * battle: No fernies are lost, and instead of gaining only one node, 2 or 3
         * nodes are won.
         */
        edgeBattle(ring, output);
        localBattle(ring, output);
        distributeUnused(ring, output);
        return output.getOutput(ring);
    }

    private void localBattle(Ring ring, Output output) {
        List<Node> theirs = ring.getNodes(Ownership.THEIRS);
        while (!theirs.isEmpty()
                && ring.getAvailableFernies() > ring.getMinNode(theirs).getFernieCount() * notes.getAttackBuffer()) {
            Node nodeOpponent = ring.getMinNode(Ownership.THEIRS);
            int ferniesAttack = (int) (nodeOpponent.getFernieCount() * notes.getAttackBuffer() + 1);
            try {
                ring.attack(nodeOpponent.getNodeNumber(), ferniesAttack);
                output.upsert(nodeOpponent.getNodeNumber(), ferniesAttack);
            } catch (FernieException e) {
                output.upsert(nodeOpponent.getNodeNumber(), e.getFernies());
            } catch (MoveException e) {
                System.out.println("Node number  " + nodeOpponent.getNodeNumber() + ": " + e.getMessage());
                break;
            }
        }
    }

    private void edgeBattle(Ring ring, Output output) {
        /*
         * First, attempts are made to perform triple battle, as this is the most
         * efficient attack technique: The agent gains the opponent's fernies and ends
         * up with 3 additional nodes. We get a list with all nodes which have at least
         * 2 free neighbors on each side, to avoid unintended edge battles.
         */
        List<Node> listFree4 = ring.getNodesFreeNeighbors(2, 2);
        while (!listFree4.isEmpty()
                && ring.getAvailableFernies() > ring.getMinNode(listFree4).getFernieCount() * notes.getAttackBuffer()) {
            Node nodeOpponent = ring.getMinNode(listFree4);
            int ferniesAttack = (int) (nodeOpponent.getFernieCount() * notes.getAttackBuffer() + 2);
            try {
                ring.addFernies(nodeOpponent.getNodeNumber() + 1, ferniesAttack / 2);
                ring.addFernies(nodeOpponent.getNodeNumber() - 1, ferniesAttack / 2);
                output.upsert(nodeOpponent.getNodeNumber() + 1, ferniesAttack / 2);
                output.upsert(nodeOpponent.getNodeNumber() - 1, ferniesAttack / 2);
            } catch (MoveException e) {
                System.out.println("Node Number " + nodeOpponent.getNodeNumber() + ": " + e.getMessage());
            } finally {
                listFree4.remove(nodeOpponent);
            }
        }
        /*
         * Secondly, attempts are made to perform edge attacks, as this is the
         * second-most efficient attack technique: the agent gains the opponent's
         * fernies and ends up with 2 additional nodes. We get a list with the nodes
         * which have at least 2 free neighbors on one side.
         */
        List<Node> listFree2 = ring.getNodesFreeNeighbors(2);
        while (!listFree2.isEmpty()
                && ring.getAvailableFernies() > ring.getMinNode(listFree2).getFernieCount() * notes.getAttackBuffer()) {
            Node nodeOpponent = ring.getMinNode(listFree2);
            int ferniesAngriff = (int) (nodeOpponent.getFernieCount() * notes.getAttackBuffer() + 1);
            try {
                boolean freeFowards = ring.getNodeByNumber(nodeOpponent.getNodeNumber() + 1)
                        .getOwner() == Ownership.UNCONTROLLED
                        && ring.getNodeByNumber(nodeOpponent.getNodeNumber() + 1).getOwner() == Ownership.UNCONTROLLED;
                if (freeFowards) {
                    ring.addFernies(nodeOpponent.getNodeNumber() + 1, ferniesAngriff);
                    output.upsert(nodeOpponent.getNodeNumber() + 1, ferniesAngriff);
                } else {
                    ring.addFernies(nodeOpponent.getNodeNumber() - 1, ferniesAngriff);
                    output.upsert(nodeOpponent.getNodeNumber() - 1, ferniesAngriff);
                }
            } catch (FernieException e) {
                output.upsert(nodeOpponent.getNodeNumber(), e.getFernies());
            } catch (MoveException e) {
                System.out.println("Knotennummer " + nodeOpponent.getNodeNumber() + ": " + e.getMessage());
            } finally {
                listFree2.remove(nodeOpponent);        
            }
        }
    }

    @Override
    public String toString() {
        return "Attack";
    }
}
