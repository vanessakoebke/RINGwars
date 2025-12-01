package model;

import java.util.*;
import java.util.function.Predicate;

/**
 * Represents the current status of the playing field and associated
 * informations.
 * <p>
 * <ul>
 * <li>the node list</li>
 * <li>the maximum number of fernies per node (final)</li>
 * <li>the numbers of fernies that are currently free and ready to be
 * placed</li>
 * <li>the maximum number of fernies that the agent has available this round
 * (new fernies + all fernies on nodes owned by the agent)</li>
 * </ul>
 */
public class Ring {
    /*
     * In the node list all nodes are stored ordered by node number. I intentionally use an array and not a list, because the the number of nodes
     * shall be fixed at the beginning, and no nodes may be added afterwards.
     */
    private Node[] nodeList;
    /*
     * Maximum amount of fernies per node. Variable is final because this number
     * doesn't change over the course of the game.
     */
    private final int maxFerniesPerNode;
    /*
     * Stores the currently available number of fernies. It is initialized with the
     * new fernies from the step file and is incremented or decremented whenever
     * fernies are placed on or removed from a node.
     */
    private int availableFernies;
    /*
     * Stores the maximum number of fernies available to the agent (number of new
     * fernies + number of all fernies the agent had at the beginning of the round).
     * This value is final, since no additional fernies can be gained during a turn.
     * This attribute is used exclusively to be passed to the output (@see Ausgabe)
     * so it can verify that the sum of fernies in the output does not exceed the
     * maximum number of fernies available in a given round.
     */
    private final int maxFerniesThisRound;

    /**
     * Creates a new {@code Ring}.
     * <p>
     * After initialization, the ring contains a {@link Node} array in which the
     * nodes are stored in ascending order by their node number, the number of new
     * fernies for the current round, the maximum number of fernies allowed per
     * node, and the maximum number of fernies available for the current round
     * (number of new fernies + number of fernies already owned by the agent).
     *
     * @param nodeList          the nodes of the ring, sorted by node number
     * @param maxFerniesPerNode the maximum number of fernies allowed on a node
     * @param availableFernies  the newly received fernies in the current round
     */
    public Ring(Node[] nodeList, int maxFerniesPerNode, int availableFernies) {
        this.nodeList = nodeList;
        this.maxFerniesPerNode = maxFerniesPerNode;
        this.availableFernies = availableFernies;
        this.maxFerniesThisRound = availableFernies + getFernies(Owner.MINE);
    }

    /**
     * Returns the number of nodes on the ring.
     * 
     * @return number of nodes
     */
    public int getNodeCount() {
        return nodeList.length;
    }


    /**
     * Returns the list of nodes that are visible to the agent.
     * 
     * @return visible nodes
     */
    public List<Node> getVisibleNodes() {
        List<Node> visible = new ArrayList<>();
        for (Node node : nodeList) {
            if (node.isVisible()) {
                visible.add(node);
            }
        }
        return visible;
    }

    /**
     * Returns the list of nodes that invisible to the opponent. The visibility used is the one calculated at the beginning of each
     * round by {@link Util}.
     * @param visibility the visibility radius
     * @return nodes that are invisible to the opponent
     */
    public List<Node> getInvisibleForOpponent(int visibility) {
        List<Node> result = new ArrayList<>();
        for (Node node : nodeList) {
            if (!isVisibleForOpponent(node, visibility) && node.isVisible() && node.getOwner() != Owner.THEIRS) {
                result.add(node);
            }
        }
        return result;
    }

    /**
     * Returns the list of nodes that visible to the opponent. The visibility used is the one calculated at the beginning of each
     * round by {@link Util}.
     * @param visibility the visibility radius
     * @return nodes that are visible to the opponent
     */
    public List<Node> getVisibleForOpponent(int visibility) {
        List<Node> result = new ArrayList<>();
        for (Node node : nodeList) {
            if (isVisibleForOpponent(node, visibility)) {
                result.add(node);
            }
        }
        return result;
    }

    /**
     * Returns whether a given node is visible for the opponent. The visibility used is the one calculated at the beginning of each
     * round by {@link Util}.
     * @param node the node
     * @param visibility the visibility radius
     * @return true if the node is visible for the opponent, false otherwise
     */
    private boolean isVisibleForOpponent(Node node, int visibility) {
        boolean visible = false;
        for (int i = 0; i <= visibility; i++) {
            int nextIndex = (node.getNodeNumber() + i) % nodeList.length;
            int prevIndex = (node.getNodeNumber() - i + nodeList.length) % nodeList.length;
            if (nodeList[nextIndex].getOwner() == Owner.THEIRS || nodeList[prevIndex].getOwner() == Owner.THEIRS) {
                visible = true;
            }
        }
        return visible;
    }

    /**
     * Returns whether the opponent is currently visible.
     * 
     * @return {@code true} if opponent is visible, {@code false} otherwise
     */
    public boolean isOpponentVisible() {
        boolean result = false;
        for (Node node : getVisibleNodes()) {
            if (node.getOwner() == Owner.THEIRS) {
                result = true;
            }
        }
        return result;
    }

    /**
     * Returns the complete node list.
     * 
     * @return node list
     */
    public Node[] getNodes() {
        return nodeList;
    }

    /**
     * Returns a list of nodes by a given owner.
     * 
     * @param owner the owner for which the node list should be created
     * @return nodes by a given owner
     */
    public List<Node> getNodes(Owner owner) {
        List<Node> result = new ArrayList<Node>();
        for (Node node : nodeList) {
            if (node.getOwner() == owner) {
                result.add(node);
            }
        }
        return result;
    }

    /**
     * Returns the total amount of fernies owned by a given owner.
     * 
     * @param owner owner for which the total fernie amount should be returned
     * @return total amount of fernies owned by a given owner
     */
    public int getFernies(Owner owner) {
        int sum = 0;
        for (Node node : getNodes(owner)) {
            sum += node.getFernieCount();
        }
        return sum;
    }

    /**
     * Returns the amount of fernies that are currently free to be placed.
     * 
     * @return available fernies
     */
    public int getAvailableFernies() {
        return availableFernies;
    }

    /**
     * Returns the node with a given node number.
     * 
     * @param nodeNumber node number
     * @return the node with the given node number
     */
    public Node getNodeByNumber(int nodeNumber) {
        Node node = filter(x -> x.getNodeNumber() == nodeNumber);
        return node;
    }

    /**
     * Attacks an opponent's node with a specified number of fernies. The number of
     * available fernies is decremented accordingly.
     * <p>
     * If the targeted node does not belong to the opponent or another invalid move
     * is attempted, a {@link MoveException} is thrown.
     * <p>
     * If the number of fernies placed on the node minus the opponent's fernies
     * exceeds the maximum number of fernies allowed per node, the attack is carried
     * out only with the maximum possible number of fernies.
     *
     * @param nodeNumber the number of the node to be attacked
     * @param fernies    the number of fernies to place on the targeted node
     * @throws MoveException if the move on this node is invalid
     */
    public void attack(int nodeNumber, int fernies) throws MoveException {
        Node node = filter(x -> x.getNodeNumber() == nodeNumber);
        if (node.getOwner() != Owner.THEIRS) {
            // TODO entfernen vor Abgabe
            throw new MoveException(
                    "Du versuchst einen Knoten anzugreifen, der nicht dem Gegner gehört. Verwende die addFernies-Funktion.");
            /*
             * If the node belongs to the opponent and the number of fernies to be placed
             * minus the number of the opponent's fernies on the node exceeds the maximum
             * allowed number of fernies per node, only the allowed amount of fernies is
             * added to the node.
             */
        } else if (fernies - node.getFernieCount() > maxFerniesPerNode) {
            int ferniesNew = maxFerniesPerNode + node.getFernieCount();
            node.addFernies(ferniesNew);
            this.availableFernies -= ferniesNew;
            throw new FernieException(ferniesNew);
        } else {
            node.addFernies(fernies);
            this.availableFernies -= fernies;
        }
    }

    /**
     * Places fernies on a node. The number of available fernies is decremented
     * accordingly.
     * <p>
     * If an invalid move is attempted, a {@link MoveException} is thrown.
     * <p>
     * If the number of fernies to be placed on the node plus the fernies already
     * present on the node exceeds the maximum number of fernies allowed per node,
     * only the maximum possible number of fernies is placed on the node.
     *
     * @param nodeNumber the number of the node on which fernies should be placed
     * @param fernies    the number of fernies to place on the node
     * @throws MoveException if the move on this node is invalid
     */
    public void addFernies(int nodeNumber, int fernies) throws MoveException {
        Node node = filter(x -> x.getNodeNumber() == nodeNumber);
        /*
         * If the number of fernies on the node + the amount of fernies to be placed is more than the maximum allowed number
         * of fernies per node, there will be added only enough fernies to reach the maximum allowed number. This number of
         * actually placed fernies is returned through a FernieException.
         */
        if (node.getOwner() == Owner.THEIRS) {
            // TODO entferne vor Abgabe
            throw new MoveException(
                    "Du versuchst Fernies auf einen gegnerischen Knoten zu legen. Verwende die greifeAn-Funktion.");
        } else if (fernies + node.getFernieCount() > maxFerniesPerNode) {
            int ferniesNew = maxFerniesPerNode - node.getFernieCount();
            node.addFernies(ferniesNew);
            this.availableFernies -= ferniesNew;
            throw new FernieException(ferniesNew);
        } else {
            node.addFernies(fernies);
            this.availableFernies -= fernies;
        }
    }

    /**
     * Removes fernies from a node owned by the agent. The number of available
     * fernies is incremented accordingly.
     * <p>
     * If an attempt is made to remove fernies from a node not owned by the agent,
     * or to remove more fernies than are present on the node, a
     * {@link MoveException} is thrown.
     *
     * @param nodeNumber the number of the node from which fernies should be removed
     * @param fernies    the number of fernies to remove
     * @throws MoveException if an invalid move is performed
     */
    public void removeFernies(int nodeNumber, int fernies) throws MoveException {
        Node node = filter(x -> x.getNodeNumber() == nodeNumber);
        node.removeFernies(fernies);
        this.availableFernies += fernies;
    }

    /**
     * Returns how much of the ring is visible.
     * @return visibility ratio of the ring
     */
    public float getVisibilityPercentage() {
        return (float) getVisibleNodes().size() / nodeList.length;
    }

    /**
     * Returns whether all visible nodes on the ring are occupied to maximum fernie capacity.
     * @return true if all visible nodes are fully occupied, false otherwise
     */
    public boolean isRingFull() {
        for (Node knoten : getVisibleNodes()) {
            if (knoten.getFernieCount() < maxFerniesPerNode) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns whether all nodes owned by a given owner are occupied to maximum fernie capacity.
     * @param owner the owner
     * @return true if all nodes by the owner are fully occupied, false otherwise
     */
    public boolean isRingFull(Owner owner) {
        for (Node node : getNodes(owner)) {
            if (node.getFernieCount() < maxFerniesPerNode) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the node that matches a given filter predicate.
     * @param p the filter predicate
     * @return the node that matches the criterion
     */
    private Node filter(Predicate<Node> p) {
        for (Node node : nodeList) {
            if (p.test(node)) {
                return node;
            }
        }
        // TODO Nullpointer exception abfangen
        return null;
    }

    /**
     * Returns the node with lowest fernie count by a given owner. If there are several nodes with the same lowest
     * fernie count, a random node is selected.
     * @param owner the owner
     * @return the/a node with the lowest fernie count by the owner
     */
    public Node getMinNode(Owner owner) {
        Node minimum = getNodes(owner).getFirst();
        for (Node node : getNodes(owner)) {
            if (node.getFernieCount() < minimum.getFernieCount()) {
                minimum = node;
            }
        }
        List<Node> minList = new ArrayList<>();
        for (Node node : getNodes(owner)) {
            if (node.getFernieCount() == minimum.getFernieCount()) {
                minList.add(node);
            }
        }
        minimum = minList.get(new Random().nextInt(minList.size()));
        return minimum;
    }

    /**
     * Returns the node with lowest fernie count from a given node list.
     * @param list the node list
     * @return the node with the lowest fernie count
     */
    public Node getMinNode(List<Node> list) {
        if (!list.isEmpty()) {
            Node minimum = list.getFirst();
            for (Node node : list) {
                if (node.getFernieCount() < minimum.getFernieCount()) {
                    minimum = node;
                }
            }
            return minimum;
        }
        //TODO nullpointer
        return null;
    }

    /**
     * Returns the node with highest fernie count by a given owner. If there are several nodes with the same highest
     * fernie count, a random node is selected.
     * @param owner the owner
     * @return the/a node with the highest fernie count by the owner
     */
    public Node getMaxNode(Owner owner) {
        Node maximum = getNodes(owner).getFirst();
        for (Node node : getNodes(owner)) {
            if (node.getFernieCount() > maximum.getFernieCount()) {
                maximum = node;
            }
        }
        List<Node> maxList = new ArrayList<>();
        for (Node node : getNodes(owner)) {
            if (node.getFernieCount() == maximum.getFernieCount()) {
                maxList.add(node);
            }
        }
        maximum = maxList.get(new Random().nextInt(maxList.size()));
        return maximum;
    }

    /**
     * Returns the node with highest fernie count from a given node list.
     * @param list the node list
     * @return the node with the highest fernie count
     */
    public Node getMaxNode(List<Node> list) {
        if (!list.isEmpty()) {
            Node maximum = list.getFirst();
            for (Node node : list) {
                if (node.getFernieCount() > maximum.getFernieCount()) {
                    maximum = node;
                }
            }
            return maximum;
        }
        return null;
        //TODO nullpointer
    }

    /**
     * Returns the average number of fernies per node by a given owner.
     * @param owner the owner
     * @return average number of fernies per node
     */
    public double getAverageFerniesPerNode(Owner owner) {
        return getFernies(owner) / getNodes(owner).size();
    }

    /**
     * Returns the maximum number of fernies that the agent has available this round, i.e. the new fernies and the sum of fernies on nodes
     * owned by the agent.
     * @return maximum number of available fernies
     */
    public int getMaxFerniesThisRound() {
        return maxFerniesThisRound;
    }


    /**
     * Returns a list of nodes owned by the opponent which have a given numbers of free neighbors.
     * @param forwards required free nodes forwards
     * @param backwards required free nodes backwards
     * @return node list
     */
    public List<Node> getNodesFreeNeighbors(int forwards, int backwards) {
        List<Node> result = new ArrayList<Node>();
        List<Node> theirs = getNodes(Owner.THEIRS);
        for (Node node : theirs) {
            boolean free = true;
            for (int i = 1; i <= forwards; i++) {
                int next = (node.getNodeNumber() + i) % nodeList.length;
                if (getNodeByNumber(next).getOwner() != Owner.UNCONTROLLED) {
                    free = false;
                }
            }
            for (int j = 1; j <= backwards; j++) {
                int prev = (node.getNodeNumber() - j + nodeList.length) % nodeList.length;
                if (getNodeByNumber(prev).getOwner() != Owner.UNCONTROLLED) {
                    free = false;
                }
            }
            if (free) {
                result.add(node);
            }
        }
        return result;
    }

    /**
     * Returns a list of nodes owned by the opponent which have a given number of free neighbors in one direction.
     * @param neighbors required free neighbors in one direction
     * @return node list
     */
    public List<Node> getNodesFreeNeighbors(int neighbors) {
        List<Node> result = new ArrayList<Node>();
        if (!getNodes(Owner.THEIRS).isEmpty()) {
            for (Node node : getNodes(Owner.THEIRS)) {
                boolean freeForwards = true;
                boolean freeBackwards = true;
                for (int i = 1; i <= neighbors; i++) {
                    int next = (node.getNodeNumber() + i) % nodeList.length;
                    if (getNodeByNumber(next).getOwner() != Owner.UNCONTROLLED) {
                        freeForwards = false;
                    }
                }
                for (int j = 1; j <= neighbors; j++) {
                    int prev = (node.getNodeNumber() - j + nodeList.length) % nodeList.length;
                    if (getNodeByNumber(prev).getOwner() != Owner.UNCONTROLLED) {
                        freeBackwards = false;
                    }
                }
                //Here I use ^ for xor because I do not want to repeat the nodes that are already covered by the method with the same name but two parameters.
                if (freeForwards ^ freeBackwards) {
                    result.add(node);
                }
            }
        }
        return result;
    }

    /**
     * Returns the number of fernies that are available if all but one fernie is removed from nodes owned by the agent.
     * @return available fernies
     */
    public int calcUnnecessary() {
        int result = 0;
        for (Node node : getNodes(Owner.MINE)) {
            if (node.getFernieCount() > 1) {
                result += node.getFernieCount() - 1;
            }
        }
        return result;
    }

    //TODO check if necessary
//    @Override
//    public String toString() {
//        String string = "";
//        for (Node node : nodeList) {
//            string += node.getNodeNumber() + ";";
//        }
//        return string;
//    }
}
