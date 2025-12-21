package model;

import java.util.ArrayList;
import java.util.List;

import service.FallBack;

/**
 * Represents the output (to be written in the move file) that the agent
 * generates.
 */
public class Output {
    private final int ferniesTotal; // the total number of fernies my agent has available in the current round
    private List<Line> outputList; // list with the lines to be written in the move.txt

    /**
     * Initializes the output with the number of fernies that the agent has
     * available in the current round (new fernies + already placed fernies).
     * 
     * @param ferniesTotal total number of available fernies
     */
    public Output(int ferniesTotal) {
        this.ferniesTotal = ferniesTotal;
        outputList = new ArrayList<Line>();
    }

    /**
     * Returns the output as a String list. If the current output is invalid, the Fallback strategy is carried out.
     * 
     * @param ring the ring
     * @return output as String list
     */
    public List<String> getOutput(Ring ring) {
        List<String> outputString = new ArrayList<String>();
        if (!check()) {
            System.out.println("Something's wrong with the output. Use FallBack strategy.");
            Output fallback = new FallBack(null).move(ring); //The Fallback strategy doesn't use the notes, therefore this is just a dummy parameter.
            for (Line line : fallback.outputList) {
                outputString.add(line.nodeNumber + "," + line.fernies);
            }
            return outputString;
        }
        for (Line line : outputList) {
            outputString.add(line.nodeNumber + "," + line.fernies);
        }
        return outputString;
    }

    // Helper method to check if more fernies have been used than were available
    // this round.
    private boolean check() {
        int sum = 0;
        for (Line line : outputList) {
            sum += line.fernies;
        }
        return ferniesTotal >= sum;
    }

    /**
     * Inserts or updates a node with the number of fernies to be placed on it.
     * 
     * @param nodeNumber the node number
     * @param fernies    the number of fernies to be placed on it
     */
    public void upsert(int nodeNumber, int fernies) {
        // Avoids duplicate lines by upserting
        for (Line line : outputList) {
            if (line.nodeNumber == nodeNumber && line.fernies >= 0) {
                line.fernies += fernies;
                return;
            }
        }
        outputList.add(new Line(nodeNumber, fernies));
    }

    /**
     * Removes a given number of fernies from the node.
     * 
     * @param nodeNumber the node number
     * @param fernies    the number of fernies to be removed
     */
    public void remove(int nodeNumber, int fernies) {
        fernies = -fernies;
        // Avoids duplicate lines
        for (Line line : outputList) {
            if (line.nodeNumber == nodeNumber && line.fernies < 0) {
                line.fernies += fernies;
                return;
            }
        }
        outputList.add(new Line(nodeNumber, fernies));
    }

    /**
     * Represents one line in the output file.
     */
    private static class Line {
        private int nodeNumber;
        private int fernies;

        /**
         * Initializes the line with the node number and the fernies to be placed or
         * removed.
         * 
         * @param nodeNumber the node number
         * @param fernies    fernies to be placed or removed
         */
        private Line(int nodeNumber, int fernies) {
            this.nodeNumber = nodeNumber;
            this.fernies = fernies;
        }

        /**
         * Returns the line as String.
         */
        @Override
        public String toString() {
            return nodeNumber + " " + fernies + ",";
        }
    }
}
