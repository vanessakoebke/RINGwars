package service;

import java.util.List;

import model.*;

/**
 * Abstract class that represents an aggressive strategy that attacks opponent nodes.
 */
public abstract class Attack extends Strategy {
    boolean attackMax; //true if the strongest nodes will be attacked, false otherwise 

    /**
     * Initializes the attack strategy with the notes and the information whether the strongest or weakest nodes will be attacked.
     * @param notes the notes
     * @param attackMax true if the strongest nodes will be attacked, false otherwise
     */
    public Attack(Notes notes, boolean attackMax) {
        super(notes);
        this.attackMax = attackMax;
    }

    /**
     * Executes the Attack strategy with 100% of the available fernies and returns the output that will be written into the move file.
     * @param ring the ring
     * @return the output
     */
    @Override
    public Output move(Ring ring) {
        Output output = new Output(ring.getMaxFerniesThisRound());
        removeUnnecessary(ring, output);
        output = move(ring, output, 1);
        distributeUnused(ring, output);
        return output;
    }

    /**
     * Executes the Attack strategy with a given percentage of the available fernies and updates the output.
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
         * First, the edge battle is executed, as it is more efficient than a local
         * battle: No fernies are lost, and instead of gaining only one node, 2 or 3
         * nodes are won.
         */
        edgeBattle(ring, output);
        localBattle(ring, output);
        return output;
    }

    /**
     * Attempts local battles, i.e. the agent attacks an opponent node directly.
     * @param ring the ring
     * @param output the output
     */
    private void localBattle(Ring ring, Output output) {
        List<Node> theirs = ring.getNodes(Owner.THEIRS);
        Node selected = selectNode(ring, theirs);
        // The agent attacks with one fernie more than is required by the number of fernies currently on the node multiplied by the attack buffer.
        while (!theirs.isEmpty() && selected != null
                && ferniesForThisStrategy > selected.getFernieCount() * notes.getAttackBuffer() +1) {
            selected = selectNode(ring, theirs);
            int ferniesAttack = (int) (selected.getFernieCount() * notes.getAttackBuffer() +1);
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
                e.printStackTrace();
                theirs.remove(selected); //To avoid infinity loops
            }
        }
    }

    /**
     * Attempts edge battles, i.e. the agent occupies one or both of the attacked node's neighbor nodes.
     * @param ring the ring
     * @param output the output
     */
    private void edgeBattle(Ring ring, Output output) {
        /*
         * First, attempts are made to perform triple battle, as this is the most
         * efficient attack technique: The agent gains the opponent's fernies and ends
         * up with 3 additional nodes. We get a list with all nodes which have at least
         * 2 free neighbors on each side, to avoid unintended edge battles.
         */
        List<Node> listFree4 = ring.getNodesFreeNeighbors(2, 2);
        Node selected = selectNode(ring, listFree4);
        while (!listFree4.isEmpty() && selected != null
                && ferniesForThisStrategy > selected.getFernieCount() * notes.getAttackBuffer() *1.1) {
            selected = selectNode(ring, listFree4);
            int ferniesAttack = (int) (selected.getFernieCount() * notes.getAttackBuffer() *1.1);
            try {
                ring.addFernies((selected.getNodeNumber() + 1) % ring.getNodeCount(), ferniesAttack / 2);
                ring.addFernies((selected.getNodeNumber() - 1 + ring.getNodeCount()) % ring.getNodeCount(), ferniesAttack / 2);
                ferniesForThisStrategy -= ferniesAttack;
                output.upsert((selected.getNodeNumber() + 1) % ring.getNodeCount(), ferniesAttack / 2);
                output.upsert((selected.getNodeNumber() - 1 + ring.getNodeCount()) % ring.getNodeCount(), ferniesAttack / 2);
                notes.addAttack(selected.getNodeNumber());
            } catch (MoveException e) {
                System.out.println("Node Number " + selected.getNodeNumber() + ": " + e.getMessage());
                e.printStackTrace();
            } finally {
                listFree4.remove(selected); //To avoid infinity loops
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
                && ferniesForThisStrategy > ring.getMinNode(listFree2).getFernieCount() * notes.getAttackBuffer() *1.1) { //Additional 1.1 is explained in PDF file.
            selected = selectNode(ring, listFree2);
            int ferniesAngriff = (int) (selected.getFernieCount() * notes.getAttackBuffer() *1.1);
            try {
                boolean freeFowards = ring.getNodeByNumber((selected.getNodeNumber() + 1) % ring.getNodeCount())
                        .getOwner() == Owner.UNCONTROLLED
                        && ring.getNodeByNumber((selected.getNodeNumber() + 1) % ring.getNodeCount()).getOwner() == Owner.UNCONTROLLED;
                if (freeFowards) {
                    ring.addFernies((selected.getNodeNumber() + 1) % ring.getNodeCount(), ferniesAngriff);
                    ferniesForThisStrategy -= ferniesAngriff;
                    output.upsert((selected.getNodeNumber() + 1) % ring.getNodeCount(), ferniesAngriff);
                    notes.addAttack(selected.getNodeNumber());
                } else {
                    ring.addFernies((selected.getNodeNumber() - 1 + ring.getNodeCount()) % ring.getNodeCount(), ferniesAngriff);
                    ferniesForThisStrategy -= ferniesAngriff;
                    output.upsert((selected.getNodeNumber() - 1 + ring.getNodeCount()) % ring.getNodeCount(), ferniesAngriff);
                    notes.addAttack(selected.getNodeNumber());
                }
            } catch (FernieException e) {
                output.upsert(selected.getNodeNumber(), e.getFernies());
                ferniesForThisStrategy -= e.getFernies();
                notes.addAttack(selected.getNodeNumber());
            } catch (MoveException e) {
                System.out.println("Node number " + selected.getNodeNumber() + ": " + e.getMessage());
                e.printStackTrace();
            } finally {
                listFree2.remove(selected); //To avoid infinity loops
            }
        }
    }

    /**
     * Returns either the strongest or weakest node from a list of nodes, depending on the Strategy object is an instance of AttackMax or AttackMin.
     * @param ring the ring
     * @param nodes the node list from which to select a node
     * @return the selected node
     */
    private Node selectNode(Ring ring, List<Node> nodes) {
        if (attackMax) {
            return ring.getMaxNode(nodes);
        } else {
            return ring.getMinNode(nodes);
        }
    }

    /**
     * Returns the name of the Strategy as String.
     * @return "Attack"
     */
    @Override
    public String toString() {
        return "Attack";
    }
}
