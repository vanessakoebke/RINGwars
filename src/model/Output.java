package model;

import java.util.ArrayList;
import java.util.List;

public class Output {
    private final int ferniesTotal;
    private List<Line> outputList;

    public Output(int ferniesTotal) {
        this.ferniesTotal = ferniesTotal;
        outputList = new ArrayList<Line>();
    }

    public List<String> getOutput(Ring ring)  {
        if (!check(ring)) {
            System.out.println(outputList.toString());
            System.out.println("Something's wrong with the output. Use fallback strategy instead.");
            //TODO fallback
            
        }
        List<String> output = new ArrayList<String>();
        for (Line line : outputList) {
            output.add(line.nodeNumber + "," + line.fernies);
        }
        return output;
    }

    // Helper method to check if more fernies have been used than were available this round.
    private boolean check(Ring ring) {
        return ferniesTotal <= ring.getFernies(Owner.MINE);
    }

    public void upsert(int nodeNumber, int fernies) {
        for (Line line : outputList) {
            if (line.nodeNumber == nodeNumber&& line.fernies>=0) {
                line.fernies += fernies;
                return;
            }
        }
        outputList.add(new Line(nodeNumber, fernies));
    }
    
    public void remove(int nodeNumber, int fernies) {
        fernies = -fernies;
        for (Line line : outputList) {
            if (line.nodeNumber == nodeNumber && line.fernies<0) {
                line.fernies += fernies;
                return;
            }
        }
        outputList.add(new Line(nodeNumber, fernies));
    }

    private static class Line {
        private int nodeNumber;
        private int fernies;

        private Line(int nodeNumber, int fernies) {
            this.nodeNumber = nodeNumber;
            this.fernies = fernies;
        }
        
        @Override
        public String toString() {
            return nodeNumber + " " + fernies + ",";
        }
    }
}
