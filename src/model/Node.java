package model;

/**
 * Represents a node on the ring.
 */
public class Node {
    private final int nodeNumber;
    private Ownership owner;
    private int fernieCount;

    /**
     * Creates a new node. After initializing the node has a final node number, an
     * owner ({@link Ownership}) and a number of fernies on the node.
     * 
     * @param nodeNumber  the node number
     * @param owner       the owner of the node
     * @param fernieCount the number of fernies on the node
     */
    public Node(int nodeNumber, String owner, int fernieCount) {
        this.nodeNumber = nodeNumber;
        this.fernieCount = fernieCount;
        switch (owner) {
        case "Y":
            this.owner = Ownership.MINE;
            break;
        case "N":
            this.owner = Ownership.UNCONTROLLED;
            break;
        case "U":
            this.owner = Ownership.UNKNOWN;
            break;
        default:
            this.owner = Ownership.THEIRS;
        }
    }

    /**
     * Returns the node number
     * 
     * @return node number
     */
    public int getNodeNumber() {
        return nodeNumber;
    }

    /**
     * Returns the owner of the node.
     * 
     * @return node owner
     */
    public Ownership getOwner() {
        return owner;
    }

    /**
     * Returns the number of fernies on the node.
     * 
     * @return number of fernies on the node
     */
    public int getFernieCount() {
        return fernieCount;
    }

    /**
     * Adds a given number of fernies to the node.
     * <p>
     * If the node belongs to the agent or is uncontrolled, the new number fernies
     * are added to the previous number fernies. 
     * If the node belongs to the opponent, it is checked whether the added fernie number is higher than the opponent's number of
     * fernies. If so, the previous fernie number is subtracted from the newly added
     * ones and this value becomes the new fernie count of the node. (Direct attack mechanic)
     * If the node is not visible, a {@link MoveException} is thrown. 
     * 
     * @param fernies fernies to be added
     * @throws MoveException thrown if the move is invalid
     */
    public void addFernies(int fernies) throws MoveException {
        if (owner == Ownership.MINE) {
            this.fernieCount += fernies;
        } else if (owner == Ownership.UNCONTROLLED && fernies > 0) {
            this.owner = Ownership.MINE;
            this.fernieCount += fernies;
        } else if (owner == Ownership.THEIRS) {
            if (this.fernieCount < fernies) {
                int temp = fernieCount;
                this.fernieCount = fernies - temp;
            } else {// TODO entfernen vor Abgabe
                throw new MoveException(
                        "Schlechter Zug: Du versuchst gerade den Gegner anzugreifen und setzt aber zu wenig Fernies ein. Knotennummer: "
                                + nodeNumber);
            }
        } else if (owner == Ownership.UNKNOWN) {
            throw new MoveException("Invalid move: You're trying to add fernies to an invisible node.");
        }
    }

    /**
     * Removes a given number of fernies from a node.
     * <p>
     * If the node belongs to the agent, and the current fernie count minus the fernies to be removed is >= 0, the given number of fernies is removed.
     * If the agents tries to remove more fernies than are currently on the node, a {@link MoveException} is thrown.
     * If the node belongs to the opponent, is uncontrolled or invisible, a {@link MoveException} is thrown.
     * @param ferniesRemove number of fernies to be removed
     * @throws MoveException thrown if the move is invalid
     */
    public void removeFernies(int ferniesRemove) throws MoveException {
        if (owner == Ownership.MINE && this.fernieCount - ferniesRemove >= 0) {
            this.fernieCount -= ferniesRemove;
            if (fernieCount == 0) {
                owner = Ownership.UNCONTROLLED;
            }
        } else if (owner == Ownership.MINE && fernieCount - ferniesRemove < 0) {
            throw new MoveException(
                    "Ungültiger Zug: Du versuchst mehr Fernies von dem Knoten zu entfernen als vorhanden sind. Knotennummer: "
                            + nodeNumber);
        } else {
            throw new MoveException(
                    "Ungültiger Zug: Du versuchst Knoten von einem Knoten zu entfernen, der nicht dir gehört. Knotennummer: "
                            + nodeNumber);
        }
    }

    /**
     * Returns whether the node is visible.
     * 
     * @return {@code true} if the node is visible, {@code false} otherwise
     */
    public boolean isVisible() {
        return (owner != Ownership.UNKNOWN);
    }
    // TODO entfernen vor Abgabe
//    @Override
//    public String toString() {
//        return knotenNummer + " - " + besitz; 
//    }
}
