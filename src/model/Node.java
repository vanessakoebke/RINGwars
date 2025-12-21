package model;

/**
 * Represents a node on the ring. The Node object includes the node number, its owner and the fernies currently placed on it.
 */
public class Node {
    private final int nodeNumber;
    private Owner owner;
    private int fernieCount;

    /**
     * Creates a new node. After initializing, the node has a final node number, an
     * owner ({@link Owner}) and a number of fernies on the node.
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
            this.owner = Owner.MINE;
            break;
        case "N":
            this.owner = Owner.UNCONTROLLED;
            break;
        case "U":
            this.owner = Owner.UNKNOWN;
            break;
        default:
            this.owner = Owner.THEIRS;
        }
    }

    /**
     * Returns the node number.
     * 
     * @return node number
     */
    public int getNodeNumber() {
        return nodeNumber;
    }

    /**
     * Returns the node's owner.
     * 
     * @return node owner
     */
    public Owner getOwner() {
        return owner;
    }
    
    //There is no setter for the owner attribute, because it should only be changed through the add or remove fernie methods.

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
     * If the node belongs to the agent or is uncontrolled, the new number of fernies
     * are added to the previous number fernies. 
     * <p>
     * If the node belongs to the opponent, it is checked whether the added fernie number is higher than the opponent's number of
     * fernies. If so, the previous fernie number is subtracted from the newly added
     * ones and this value becomes the new fernie count of the node (direct attack mechanic). Otherwise a {@link MoveException} is thrown.
     * <p>
     * If the node is invisible, a {@link MoveException} is thrown. 
     * 
     * @param fernies fernies to be added
     * @throws MoveException thrown if the move is invalid or discouraged
     */
    public void addFernies(int fernies) throws MoveException {
        if (owner == Owner.MINE) {
            this.fernieCount += fernies;
        } else if (owner == Owner.UNCONTROLLED && fernies > 0) {
            this.owner = Owner.MINE; //By placing fernies on an uncontrolled node, the node becomes mine.
            this.fernieCount += fernies;
        } else if (owner == Owner.THEIRS) {
            if (this.fernieCount < fernies) {
                int temp = fernieCount;
                this.fernieCount = fernies - temp;
            } else {
                throw new MoveException(
                        "Bad move: You're trying to attack the opponent but are using too few fernies. Node number: "
                                + nodeNumber);
            }
        } else if (owner == Owner.UNKNOWN) {
            throw new MoveException("Invalid move: You're trying to add fernies to an invisible node.");
        }
    }

    /**
     * Removes a given number of fernies from the node.
     * <p>
     * If the node belongs to the agent, and the current fernie count minus the fernies to be removed is >= 0, the move is carried out.
     * <p>
     * If the agents tries to remove more fernies than are currently on the node, a {@link MoveException} is thrown.
     * <p>
     * If the node belongs to the opponent, is uncontrolled or invisible, a {@link MoveException} is thrown.
     * 
     * @param ferniesRemove number of fernies to be removed
     * @throws MoveException thrown if the move is invalid
     */
    public void removeFernies(int ferniesRemove) throws MoveException {
        if (owner == Owner.MINE && this.fernieCount - ferniesRemove >= 0) {
            this.fernieCount -= ferniesRemove;
            if (fernieCount == 0) {
                owner = Owner.UNCONTROLLED; //If I remove all fernies from the node, the node becomes uncontrolled.
            }
        } else if (owner == Owner.MINE && fernieCount - ferniesRemove < 0) {
            throw new MoveException(
                    "Invalid move: You are trying to remove more fernies from the node than are currently placed on it. Node number: "
                            + nodeNumber);
        } else if (owner != Owner.MINE){
            throw new MoveException(
                    "Invalid move: You are trying to remove fernies from a node that isn't yours. Node number: "
                            + nodeNumber);
        }
    }

    /**
     * Returns whether the node is visible.
     * 
     * @return {@code true} if the node is visible, {@code false} otherwise
     */
    public boolean isVisible() {
        return (owner != Owner.UNKNOWN);
    }

}
