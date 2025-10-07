package service;

import java.io.*;

/**
 * Helps track the success of different strategies by counting the wins and
 * losses.
 * <p>
 * To be disabled before handing in.
 */
public class Tracker {
    private static int wins;
    private static int losses;
    static boolean flag;

    public static void read() {
        try (BufferedReader br = new BufferedReader(new FileReader("statistics/stats.txt"))) {
            String[] input = new String[3];
            int i = 0;
            String line;
            while ((line = br.readLine()) != null) {
                input[i++] = line;
            }
            wins = Integer.parseInt(input[0]);
            losses = Integer.parseInt(input[1]);
            flag = Boolean.parseBoolean(input[2]);
        } catch (Exception e) {
            System.out.println("Something is wrong with the stats file.");
            wins = 0;
            losses = 0;
            flag = false;
        }
    }

    public static void addWin() {
        Tracker.wins++;
        flag = true;
    }

    public static void addLoss() {
        Tracker.losses++;
        flag = true;
    }

    public static void write() {
        File file = new File("statistics/stats.txt");
        file.getParentFile().mkdirs();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            file.createNewFile();
            bw.write(String.valueOf(wins));
            bw.newLine();
            bw.write(String.valueOf(losses));
            bw.newLine();
            bw.write(String.valueOf(flag));
        } catch (IOException e) {
            System.out.println("The stats file could not be written.");
            e.printStackTrace();
        }
    }
    
}
