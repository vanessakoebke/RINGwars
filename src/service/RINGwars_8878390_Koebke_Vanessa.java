package service;

import java.util.ArrayList;
import java.util.List;

import model.*;

public class RINGwars_8878390_Koebke_Vanessa {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Please enter valid arguments: <Step number> <Agent name>. The program terminates now.");
            return;
        }
        int round;
        try {
            round = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid step number. The program terminates now.");
            return;
        }
        String agentName = args[1];
        Ring ring = null;
        try {
            ring = Util.readStatusFile(agentName, round);
        } catch (InvalidStatusException e) {
            System.out.println(e.getMessage());
            System.out.println("An empty move file will be created.");
        }
        //TODO Disable tracker block before handing in.
        Tracker.read();
        if (round == 1) {
            Tracker.flag = false;
        }
        if (!Tracker.flag) {
            if (ring.getVisibilityPercentage() == 1 && ring.getNodes(Ownership.THEIRS).size() == 0) {
                Tracker.addWin();
            } else if (ring.getNodes(Ownership.MINE).size() == 0) {
                Tracker.addLoss();
            }
            Tracker.write();
        }
        //End tracker block
        
        Notes notes = Util.readNotes(agentName, ring, round);
        Strategy strategy = null;
        try {
            strategy = DrSmartyPants.getStrategy(ring, Util.readStatusFile(agentName, round-1), notes);
        } catch (InvalidStatusException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("Executed strategy: " + strategy.toString());
        Output output = strategy.move(ring);
        
        List<String> stringOutput;
        if(output == null) {
            stringOutput = new ArrayList<>();
        } else {
            stringOutput = output.getOutput(ring);
        }
        
        Util.writeMove(stringOutput, agentName);
        Util.writeNotes(notes.toString(), agentName);
        System.out.println("The program terminated successfully.");
    }
}
