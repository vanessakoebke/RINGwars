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
     * TODO translate In der Knotenliste werden alle Knoten sortiert nach
     * Knotennummer gespeichert. ich verwende hier absichtlich ein Array und keine
     * Liste, da die Knotenzahl sich während der Ausführung nicht verändern soll,
     * und ich anders als bei einer Liste so nicht aus Versehen mehr Knoten einfügen
     * kann als zulässig ist. Dient als Hilfe für mich zum Troubleshooten.
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
     * maximum number of fernies available per round.
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
        this.maxFerniesThisRound = availableFernies + getFernies(Ownership.MINE);
    }

    /**
     * Returns the number of nodes on the ring.
     * 
     * @return number of nodes
     */
    public int getNodeCount() {
        return nodeList.length;
    }
    // TODO prüfen ob benötigt
    /**
     * Gibt ein Array mit allen Knoten auf dem Ring zurück.
     * 
     * @return vollständige Knotenliste
     */
//    public Knoten[] getAlleKnoten() {
//        return knotenListe;
//    }

    /**
     * Returns a list of visible nodes.
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
     * Return whether the opponent is currently visible.
     * 
     * @return {@code true} if opponent is visible, {@code false} otherwise
     */
    public boolean isOpponentVisible() {
        boolean result = false;
        for (Node node : getVisibleNodes()) {
            if (node.getOwner() == Ownership.THEIRS) {
                result = true;
            }
        }
        return result;
    }

    /**
     * Returns the complete node list.

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
    public List<Node> getNodes(Ownership owner) {
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
    public int getFernies(Ownership owner) {
        int sum = 0;
        for (Node node : getNodes(owner)) {
            sum += node.getFernieCount();
        }
        return sum;
    }

    /**
     * Returns the amound of fernies that are currently free to be placed.
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
        if (node.getOwner() != Ownership.THEIRS) {
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
         * TODO translate Falls die Anzahl der Fernies, die bereits auf dem Knoten sind,
         * plus die Anzahl der zu setzenden Fernies die maximal erlaubte Anzahl Fernies
         * pro Knoten überschreitet, werden dem Knoten nur die Anzahl an Fernies
         * hinzugefügt, bis die maximale Anzahl erreicht wird.
         */
        if (node.getOwner() == Ownership.THEIRS) {
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

    //TODO prüfen ob nötig
//    private int getMaxFerniesProKnoten() {
//        return maxFerniesPerNode;
//    }

    public float getVisibilityPercentage() {
        return (float) getVisibleNodes().size() / nodeList.length;
    }

    public boolean isRingFull() {
        for (Node knoten : getVisibleNodes()) {
            if (knoten.getFernieCount() < maxFerniesPerNode) {
                return false;
            }
        }
        return true;
    }

    public boolean isRingFull(Ownership owner) {
        for (Node node : getNodes(owner)) {
            if (node.getFernieCount() < maxFerniesPerNode) {
                return false;
            }
        }
        return true;
    }

    public Node filter(Predicate<Node> p) {
        for (Node node : nodeList) {
            if (p.test(node)) {
                return node;
            }
        }
        //TODO Nullpointer exception abfangen
        return null;
    }

    public Node getMinNode(Ownership owner) {
        Node minimum = nodeList[0];
        for (Node node : getNodes(owner)) {
            if (node.getFernieCount() < minimum.getFernieCount()) {
                minimum = node;
            }
        }
        return minimum;
    }

    public Node getMinNode(List<Node> list) {
        Node minimum = list.getFirst();
        for (Node node : list) {
            if (node.getFernieCount() < minimum.getFernieCount()) {
                minimum = node;
            }
        }
        return minimum;
    }

    public Node getMaxNode(Ownership owner) {
        Node maximum = nodeList[0];
        for (Node node : getNodes(owner)) {
            if (node.getFernieCount() > maximum.getFernieCount()) {
                maximum = node;
            }
        }
        return maximum;
    }
    
    public Node getMaxNode(List<Node> list) {
        Node maximum = list.getFirst();
        for (Node node : list) {
            if (node.getFernieCount() > maximum.getFernieCount()) {
                maximum = node;
            }
        }
        return maximum;
    }

    public double getAverageFerniesPerNode(Ownership owner) {
        return getFernies(owner) / getNodes(owner).size();
    }

    public int getMaxFerniesThisRound() {
        return maxFerniesThisRound;
    }

    public List<Node> getLeftHalf() {
        List<Node> list = new ArrayList<>();
        int middle = nodeList.length / 2;
        for (int i = 0; i < middle; i++) {
            list.add(nodeList[i]);
        }
        return list;
    }

    public List<Node> getRightHalf() {
        List<Node> list = new ArrayList<>();
        int middle = (nodeList.length / 2) + 1;
        for (int i = middle; i < nodeList.length; i++) {
            list.add(nodeList[i]);
        }
        return list;
    }

    /**
     * Returns the opponent node that is closest to the agent's outermost node on the left side.
     * @return opponent node
     */
    public Node getClosestOpponentLeft() {
        Node result = null;
        ListIterator<Node> iterator = getLeftHalf().listIterator(getLeftHalf().size());
        while (iterator.hasPrevious()) {
            Node current = iterator.previous();
            if (current.getOwner() == Ownership.THEIRS) {
                result = current;
            }
        }
        //TODO nullpointer abfangen
        return result;
    }

    /**
     * Returns the opponent node that is closest to the agent's outermost node on the right side.
     * @return opponent node
     */
    public Node getClosestOpponentRight() {
        Node result = null;
        Iterator<Node> iterator = getRightHalf().iterator();
        while (iterator.hasNext()) {
            Node current = iterator.next();
            if (current.getOwner() == Ownership.THEIRS) {
                result = current;
            }
        }
        //TODO nullpointer abfangen
        return result;
    }

    public List<Node> getNodesFreeNeighbors(int forwards, int backwards) {
        List<Node> result = new ArrayList<Node>();
        for (Node node : getNodes(Ownership.THEIRS)) {
            boolean free = true;
            for (int i = 1; i <= forwards; i++) {
                if (getNodeByNumber(node.getNodeNumber() + i).getOwner() != Ownership.UNCONTROLLED) {
                    free = false;
                }
            }
            for (int j = 1; j <= backwards; j++) {
                if (getNodeByNumber(node.getNodeNumber() - j).getOwner() != Ownership.UNCONTROLLED) {
                    free = false;
                }
            }
            if (free) {
                result.add(node);
            }
        }
        return result;
    }

    public List<Node> getNodesFreeNeighbors(int neighbors) {
        List<Node> result = new ArrayList<Node>();
        for (Node node : getNodes(Ownership.THEIRS)) {
            boolean freeForwards = true;
            boolean freeBackwards = true;
            for (int i = 1; i <= neighbors; i++) {
                if (getNodeByNumber(node.getNodeNumber() + i).getOwner() != Ownership.UNCONTROLLED) {
                    freeForwards = false;
                }
            }
            for (int j = 1; j <= neighbors; j++) {
                if (getNodeByNumber(node.getNodeNumber() - j).getOwner() != Ownership.UNCONTROLLED) {
                    freeBackwards = false;
                }
            }
            if (freeForwards ^ freeBackwards) {
                result.add(node);
            }
        }
        return result;
    }
}
