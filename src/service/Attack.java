package service;

import java.util.List;

import model.*;

public abstract class Attack extends Strategy {
    boolean attackMax;
    
    public Attack(Notes notes, boolean attackMax) {
        super(notes);
        this.attackMax = attackMax;
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
         * First, the edge battle is executed, as it is more efficient than a local
         * battle: No fernies are lost, and instead of gaining only one node, 2 or 3
         * nodes are won.
         */
        edgeBattle(ring, output);
        localBattle(ring, output);
        return output;
    }

    private void localBattle(Ring ring, Output output) {
        List<Node> theirs = ring.getNodes(Ownership.THEIRS);
        Node selected = null;
        while (!theirs.isEmpty()
                && ferniesForThisStrategy > selected.getFernieCount() * notes.getAttackBuffer()) {
            selected = selectNode(ring, theirs);
            int ferniesAttack = (int) (selected.getFernieCount() * notes.getAttackBuffer() + 1);
            try {
                ring.attack(selected.getNodeNumber(), ferniesAttack);
                ferniesForThisStrategy -= ferniesAttack;
                output.upsert(selected.getNodeNumber(), ferniesAttack);
                notes.addAttack(selected.getNodeNumber());
            } catch (FernieException e) {
                output.upsert(selected.getNodeNumber(), e.getFernies());
                notes.addAttack(selected.getNodeNumber());
            } catch (MoveException e) {
                System.out.println("Node number  " + selected.getNodeNumber() + ": " + e.getMessage());
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
        Node selected = null;
        while (!listFree4.isEmpty()
                && ferniesForThisStrategy> selected.getFernieCount() * notes.getAttackBuffer()) {
            selected = selectNode(ring, listFree4);
            int ferniesAttack = (int) (selected.getFernieCount() * notes.getAttackBuffer() + 2);
            try {
                ring.addFernies(selected.getNodeNumber() + 1, ferniesAttack / 2);
                ring.addFernies(selected.getNodeNumber() - 1, ferniesAttack / 2);
                ferniesForThisStrategy -= ferniesAttack;
                output.upsert(selected.getNodeNumber() + 1, ferniesAttack / 2);
                output.upsert(selected.getNodeNumber() - 1, ferniesAttack / 2);
                notes.addAttack(selected.getNodeNumber());
            } catch (MoveException e) {
                System.out.println("Node Number " + selected.getNodeNumber() + ": " + e.getMessage());
            } finally {
                listFree4.remove(selected);
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
                && ferniesForThisStrategy > ring.getMinNode(listFree2).getFernieCount() * notes.getAttackBuffer()) {
            selected = selectNode(ring, listFree2);
            int ferniesAngriff = (int) (selected.getFernieCount() * notes.getAttackBuffer() + 1);
            try {
                boolean freeFowards = ring.getNodeByNumber(selected.getNodeNumber() + 1)
                        .getOwner() == Ownership.UNCONTROLLED
                        && ring.getNodeByNumber(selected.getNodeNumber() + 1).getOwner() == Ownership.UNCONTROLLED;
                if (freeFowards) {
                    ring.addFernies(selected.getNodeNumber() + 1, ferniesAngriff);
                    ferniesForThisStrategy -= ferniesAngriff;
                    output.upsert(selected.getNodeNumber() + 1, ferniesAngriff);
                    notes.addAttack(selected.getNodeNumber());
                } else {
                    ring.addFernies(selected.getNodeNumber() - 1, ferniesAngriff);
                    ferniesForThisStrategy -= ferniesAngriff;
                    output.upsert(selected.getNodeNumber() - 1, ferniesAngriff);
                    notes.addAttack(selected.getNodeNumber());
                }
            } catch (FernieException e) {
                output.upsert(selected.getNodeNumber(), e.getFernies());
                ferniesForThisStrategy -= e.getFernies();
                notes.addAttack(selected.getNodeNumber());
            } catch (MoveException e) {
                System.out.println("Node number " + selected.getNodeNumber() + ": " + e.getMessage());
            } finally {
                listFree2.remove(selected);        
            }
        }
    }
    
    Node selectNode(Ring ring, List<Node> nodes) {
        if (attackMax) {
            return ring.getMaxNode(nodes);
        } else {
            return ring.getMinNode(nodes);
        }
    }

    @Override
    public String toString() {
        return "Attack";
    }
}
