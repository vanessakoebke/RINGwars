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
        // TODO Disable tracker block before handing in.
        Tracker.read();
        if (round == 1) {
            Tracker.flag = false;
        }
        if (!Tracker.flag) {
            if (ring.getVisibilityPercentage() == 1 && ring.getNodes(Owner.THEIRS).size() == 0) {
                Tracker.addWin();
            } else if (ring.getNodes(Owner.MINE).size() == 0) {
                Tracker.addLoss();
            }
            Tracker.write();
        }
        // End tracker block
        Notes notes = Util.readNotes(agentName, ring, round);
        Strategy strategy = null;
        strategy = DrSmartyPants.getStrategy(ring, notes);
        String stars = "****";
        System.out.println(stars + System.lineSeparator() + stars + System.lineSeparator() + stars);
        System.out.println(round);
        System.out.println("Executed strategy: "+ strategy.toString() + System.lineSeparator() +
                "Expansion (" + notes.getRatiosThisRound()[0] +"), Consolidation (" + notes.getRatiosThisRound()[1] +
                "), AttackMax (" + notes.getRatiosThisRound()[2] + "), AttackMin ("+ notes.getRatiosThisRound()[3] + "), Defensive (" + notes.getRatiosThisRound()[4] + ")");
        Output output = strategy.move(ring);
        List<String> stringOutput;
        if (output == null) {
            stringOutput = new ArrayList<>();
        } else {
            stringOutput = output.getOutput(ring);
        }
        Util.writeMove(stringOutput, agentName);
        Util.writeNotes(notes.toString(), agentName);
        System.out.println(stringOutput.toString());
        System.out.println("The program terminated successfully.");
        System.out.println(stars + System.lineSeparator() + stars + System.lineSeparator() + stars);
    }
}
