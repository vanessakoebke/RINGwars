package service;

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
        Notes notes = Util.readNotes(agentName, ring, round);
        Strategy strategy = DrSmartyPants.getStrategy();
        System.out.println("Executed strategy: " + strategy.toString());
        Output output = strategy.move(ring);
        
        List<String> stringOutput = output.getOutput(ring);
        
        Util.writeMove(stringOutput, agentName);
        Util.writeNotes(notes.toString(), agentName);
        System.out.println("The program terminated successfully.");
    }
}
