package service;

import java.io.*;
import java.util.List;

import model.*;

/**
 * Utility class with static methods for reading and writing files.
 */
public class Util {
    /**
     * Reads the current step file.
     * 
     * @param agentName name of the agent (directory)
     * @param step      step number
     * @return ring
     * @throws InvalidStatusException if the step file is invalid
     */
    public static Ring readStatusFile(String agentName, int step) throws InvalidStatusException {
        String path = agentName + "/" + step + ".txt";
        String[] input = new String[5];
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            int i = 0;
            String line;
            while ((line = br.readLine()) != null) {
                input[i++] = line;
            }
        } catch (FileNotFoundException e) {
            System.out.println("The step file was not found. An empty move file will be created.");
        } catch (IOException e) {
            System.out.println("The step file could not be read. An empty move file will be created.");
        }
        /*
         * 1st check: if the step file has more than 4 lines, a message is displayed,
         * however, if the first 4 lines are correct, the program proceeds (see Moodle
         * answer by Andrea Frank).
         */
        if (input[4] != null) {
            System.out.println(
                    "Step file has more than 4 lines. The programm continues, and tries to parse the first 4 lines.");
        }
        String[] line1String = input[0].split(",");
        String[] line2 = input[1].split(",");
        /*
         * 2nd check: if the number of elements in the first and the second line are
         * different, something is wrong.
         */
        if (line1String.length != line2.length) {
            throw new InvalidStatusException("Line 1 and 2 have a different number of elements.");
        }
        int numberNodes = line1String.length;
        int[] line1Int = new int[numberNodes];
        int availableFernies;
        int maxFerniesPerNode;
        /*
         * 3rd check: if parsing the elements in line 1, 3 or 4 causes a
         * NumberFormatException something is wrong.
         */
        try {
            availableFernies = Integer.parseInt(input[2]);
            maxFerniesPerNode = Integer.parseInt(input[3]);
            for (int i = 0; i < numberNodes; i++) {
                line1Int[i] = Integer.parseInt(line1String[i]);
            }
        } catch (NumberFormatException e) {
            throw new InvalidStatusException("Number parsing for amount of fernies in line 1, 3 or 4 failed.");
        }
        // 4th check: if the node count is 0, something is wrong.
        if (numberNodes == 0) {
            throw new InvalidStatusException("There are no nodes in the step file.");
        }
        Node[] nodeList = new Node[numberNodes];
        for (int i = 0; i < numberNodes; i++) {
            /*
             * 5th check: If the visibility status in line 1 and 2 don't match, something is
             * wrong.
             */
            if ((line1Int[i] == -1 && !line2[i].equals("U")) || (line1Int[i] != -1 && line2[i].equals("U"))) {
                throw new InvalidStatusException("For node " + i
                        + " the visibility status in line 1 and 2 don't match. (Line 1 has -1 and line 2 doesn't have U or the other way around.)");
            }
            // 6th check: if a node status is uncontrolled and the fernie count is != 0,
            // something is wrong.
            if ((line1Int[i] == 0 && !line2[i].equals("N")) || (line1Int[i] != 0 && line2[i].equals("N"))) {
                throw new InvalidStatusException("For node " + i
                        + " the uncontrolled status of line 1 and 2 don't match. (Line 1 has 0 and line doesn't have N, or the other way around.)");
            }
            nodeList[i] = new Node(i, line2[i], line1Int[i]);
        }
        return new Ring(nodeList, maxFerniesPerNode, availableFernies);
    }

    /**
     * Writes the move file into the agent's directory.
     * 
     * @param output    output String list
     * @param agentName agent name (name of directory)
     */
    public static void writeMove(List<String> output, String agentName) {
        File move = new File(agentName, "move.txt");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(move))) {
            move.createNewFile();
            /*
             * in order for the move file not to end with an empty line, I have extracted
             * the first line from the for loop. The loop then starts with a line break.
             */
            if (output != null && !output.isEmpty()) {
                bw.write(output.get(0));
                if (output.size() > 1) {
                    for (int i = 1; i < output.size(); i++) {
                        bw.newLine();
                        bw.write(output.get(i));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("The move file could not be created (see StackTrace). The program terminates now.");
            e.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * Writes the notes created in the current round by the agent into the notes
     * file in the agent's directory.
     * 
     * @param notes     notes
     * @param agentName agent name (name of the directory)
     */
    public static void writeNotes(String notes, String agentName) {
        File file = new File(agentName, "notes.txt");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            file.createNewFile();
            if (notes != null) {
                bw.write(notes);
            }
        } catch (IOException e) {
            System.out.println("The move file could not be created (see StackTrace). The program terminates now.");
            e.printStackTrace();
        }
    }

    /**
     * Reads the notes from the previous round stored in the notes file.
     * 
     * @param agentName agent name (name of the directory)
     * @param ring      ring
     * @param round     current round
     * @return Notes object
     */
    public static Notes readNotes(String agentName, Ring ring, int round) {
        /*
         * If the notes file cannot be found, read or parsed, I will calculate the
         * visibility based on the current step file, by subtracting the node number of
         * my outermost node before an invisible node from the first invisible name.
         */
        int visibilityCalculated = 0;
        if (ring != null) {
            try {
                Node lastNode = null;
                for (Node node : ring.getNodes()) {
                    if (node.getOwner() == Ownership.MINE) {
                        lastNode = node;
                    }
                    if (node.getOwner() == Ownership.UNKNOWN) {
                        visibilityCalculated = node.getNodeNumber() - lastNode.getNodeNumber();
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.println("Hilfe, beim ermitteln der Sichtbarkeit ist was schief gelaufen.");
                e.printStackTrace();
            }
        }
        String path = agentName + "/notes.txt";
        String[] input = new String[20];
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            int i = 0;
            String line;
            while ((line = br.readLine()) != null) {
                input[i++] = line;
            }
        } catch (FileNotFoundException e) {
            System.out.println("There is no notes file. It will be created this round.");
            return new Notes(round, visibilityCalculated);
        } catch (IOException e) {
            System.out.println("The notes file could not be read. It will be created this round.");
            return new Notes(round, visibilityCalculated);
        }
        /*
         * If the notes file can be read, I will try to parse the visibility range saved in the notes file, since it might be more
         * accurate than the calculated one (i.e. if the whole ring is visible, the calculation above won't work properly).
         */
        Notes notes;
        int visibility = 0;
        /*
         * To parse my notes file I always use the code input[i].split(": ")[1]. This splits the line at the end of the descriptor (": "),
         * and I will only need the part after the descriptor ([1]).
         */
        try {
            String line1 = input[0].split(": ")[1];
            //TODO Strategie des Gegners besser verarbeiten
            StrategyOpponent strategie;
            switch (line1) {
            case "AGRESSIVE":
                strategie = StrategyOpponent.AGRESSIVE;
            case "DEFENSIVE":
                strategie = StrategyOpponent.DEFENSIVE;
            default:
                strategie = StrategyOpponent.UNKNOWN;
            }
            //TODO besser parsen
            int attacksByOpponentTotal = Integer.parseInt(input[1].split(": ")[1]);
            int attacksByOpponentLastRound = Integer.parseInt(input[2].split(": ")[1]);
            //The visibility range can be read from line 4.
            visibility = Integer.parseInt(input[3].split(": ")[1]);
            
            /*
             * The attacks the agent has carried out last round can be read from line 5.
             * After parsing them into an int array, I will check whether an attacked node has become mine.
             * If not, it increments the counter of blocked attacks. This will be important for determining a 
             * possible defensive strategy by the opponent and the attack buffer for the current round.
             */
            String[] line5String = input[4].split(": ")[1].split(",");
            int[] myAttacksLastRound = new int[line5String.length];
            for (int i = 0; i < line5String.length; i++) {
                myAttacksLastRound[i] = Integer.parseInt(line5String[i]);
            }
            int sumBlockedAttacksLastRound = 0;
            for (int nodeNumber : myAttacksLastRound) {
                if (ring.getNodeByNumber(nodeNumber).getOwner() != Ownership.MINE) {
                    sumBlockedAttacksLastRound++;
                }
            }
            double blockedAttacksLastRound = (double) (sumBlockedAttacksLastRound / myAttacksLastRound.length);
            double blockedAttacksTotal = ((Double.parseDouble(input[6].split(": ")[1]) * round -1) + (blockedAttacksLastRound)) /round ;
            
            /*
             * Reads last rounds attack buffer from the file. If more than 30% of last rounds attacks were blocked,
             * the attack buffer will be incremented by 10% points.
             */
            double puffer = Double.parseDouble(input[8].split(": ")[1]);
            if (blockedAttacksLastRound > 0.3) {
                puffer += 0.1;
            }
            
            /*
             * Reads which strategies (and in which ratio) were used last round.
             */
            String[] line10 = input[9].split(": ")[1].split(",");
            double expansionRatio = Double.parseDouble(line10[0]);
            double consolidationRatio = Double.parseDouble(line10[1]);;
            double attackRatio = Double.parseDouble(line10[2]);;
            double defensiveRatio = Double.parseDouble(line10[3]);;
            
            //If parsing has concluded successfully, the information will be stored in the notes object.
            notes = new Notes(round, strategie, attacksByOpponentTotal, attacksByOpponentLastRound, visibility,
                    blockedAttacksTotal, blockedAttacksLastRound, puffer, expansionRatio, consolidationRatio, attackRatio, defensiveRatio);
        } catch (Exception e) {
            System.out.println("The notes file is empty or invalid.");
            e.printStackTrace();
            /*
             * If parsing the visibility range from the notes file failed, I will use the calculated visibility from above.
             */
            if (visibility == 0) {
                visibility = visibilityCalculated;
            }
            notes = new Notes(round, visibility);
        }
        return notes;
    }
}
