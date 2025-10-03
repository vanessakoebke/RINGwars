package service;

import java.io.*;
import java.util.List;

import model.*;

public class Util {
    public static Ring readStatusFile(String agentenName, int step) throws InvalidStatusException {
        String dateipfad = agentenName +"/" + step + ".txt";
        String[] input = new String[5];
        try (BufferedReader br = new BufferedReader(new FileReader(dateipfad))) {
            int i = 0;
            String zeile;
            while ((zeile = br.readLine()) != null) {
                input[i++] = zeile;
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // 1. Prüfung: falls die Statusdatei mehr als 4 Zeilen hat, stimmt etwas nicht,
        // aber Programm setzt sich fort (siehe Moodle-Antwort von Frau Frank).
        if (input[4] != null) {
            System.out.println(
                    "Statusdatei hat mehr als 4 Zeilen! Das Programm setzt sich fort, falls die ersten 4 Zeilen gültig sind.");
        }
        String[] zeile1String = input[0].split(",");
        String[] zeile2 = input[1].split(",");
        // 2. Prüfung: falls die Anzahl der Elemente in Zeilen 1 und 2 nicht
        // übereinstimmt, stimmt etwas nicht.
        if (zeile1String.length != zeile2.length) {
            throw new InvalidStatusException(
                    "Zeile 1 und Zeile 2 der Statusdatei enthalten eine ungleiche Anzahl an Knoten.");
        }
        int anzahlKnoten = zeile1String.length;
        int[] zeile1Int = new int[anzahlKnoten];
        int anzahlFerniesVerfuegbar;
        int anzahlFerniesGesamt;
        // 3. Prüfung: falls es beim Umwandeln der Elemente der 1., 3. oder 4. Zeile
        // eine NumberformatException gibt, stand bei einer Ferniezahlangabe keine
        // Zahlen, d.h. die Statusdatei war fehlerhaft.
        try {
            anzahlFerniesVerfuegbar = Integer.parseInt(input[2]);
            anzahlFerniesGesamt = Integer.parseInt(input[3]);
            for (int i = 0; i < anzahlKnoten; i++) {
                zeile1Int[i] = Integer.parseInt(zeile1String[i]);
            }
        } catch (NumberFormatException e) {
            throw new InvalidStatusException(
                    "An einer Stelle wo eine Fernieanzahl erwartet wurde, stand keine Zahl.");
        }
        // 4. Prüfung: falls die Knotenanzahl 0 ist, stimmt etwas nicht.
        if (anzahlKnoten == 0) {
            throw new InvalidStatusException("Die Statusdatei enthält keine Knoten.");
        }
        Node[] knotenListe = new Node[anzahlKnoten];
        for (int i = 0; i < anzahlKnoten; i++) {
            // 5. Prüfung: Falls der Sichtbarkeitsstatus in Zeile 1 und 2 nicht
            // übereinstimmen, stimmt etwas nicht.
            if ((zeile1Int[i] == -1 && !zeile2[i].equals("U")) || (zeile1Int[i] != -1 && zeile2[i].equals("U"))) {
                throw new InvalidStatusException("Bei Knoten " + i
                        + " stimmt der Sichtbarkeitsstatus aus Zeile 1 und 2 nicht überein. (Zeile 1 hat -1 und Zeile 2 hat nicht U oder umgekehrt.)");
            }
            //6. Prüfung: falls der Status nicht-kontrolliert ist und die Ferniezahl != 0 ist, stimmt etwas nicht.
            if ((zeile1Int[i] == 0 && !zeile2[i].equals("N")) || (zeile1Int[i] != 0 && zeile2[i].equals("N"))) {
                throw new InvalidStatusException("Bei Knoten " + i
                        + " stimmt der Unkontrolliertstatus aus Zeile 1 und 2 nicht überein. (Zeile 1 hat 0 und Zeile 2 hat nicht N oder umgekehrt.)");
            }
            knotenListe[i] = new Node(i, zeile2[i], zeile1Int[i]);
        }
        return new Ring(knotenListe, anzahlFerniesGesamt, anzahlFerniesVerfuegbar);
    }

    public static void writeMove(List<String> ausgabe, String agentenName) {
        File move = new File(agentenName, "move.txt");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(move))) {
            move.createNewFile();
            if (ausgabe != null && !ausgabe.isEmpty()) {
                bw.write(ausgabe.get(0)); // damit die Move-Datei nicht mit einer leeren Zeile endet, habe ich die erste
                                          // Zeile aus der for-Schleife rausgezogen und beginne die for-Schleife mit
                                          // einer Leerzeile.
                if (ausgabe.size() > 1) {
                    for (int i = 1; i < ausgabe.size(); i++) {
                        bw.newLine();
                        bw.write(ausgabe.get(i));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(
                    "Es konnte keine move.txt erstellt werden (siehe StackTrace). Das Programm beendet sich nun.");
            e.printStackTrace();
        }
    }

    public static void writeNotes(String notizen, String agentenName) {
        File datei = new File(agentenName, "notizen.txt");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(datei))) {
            datei.createNewFile();
            if (notizen != null) {
                bw.write(notizen);
            }
        } catch (IOException e) {
            System.out.println(
                    "Es konnte keine notizen.txt erstellt werden (siehe StackTrace). Das Programm beendet sich nun.");
            e.printStackTrace();
        }
    }

    public static Notes readNotes(String agentenName, Ring ring, int runde) {
        String dateipfad = agentenName  + "/notizen.txt";
        String[] input = new String[8];
        try (BufferedReader br = new BufferedReader(new FileReader(dateipfad))) {
            int i = 0;
            String zeile;
            while ((zeile = br.readLine()) != null) {
                input[i++] = zeile;
            }
        }  catch (FileNotFoundException e) {
            System.out.println("Es gibt noch keine Notiz-Datei. Sie wird diese Runde neu erzeugt");
            return new Notes(runde, 2);
        }  catch (IOException e) {
            System.out.println("Die Datei notizen.txt konnte nicht eingelesen werden. Es wird eine neue Datei erzeugt.");
            return new Notes(runde, 2); 
        }     
        Notes notizen;
        try {
            String zeile1 = input[0].split(": ")[1];
            StrategyOpponent strategie;
            switch(zeile1) {
            case "AGRESSIV": strategie = StrategyOpponent.AGRESSIVE;
            case "DEFENSIV": strategie = StrategyOpponent.DEFENSIVE;
            default: strategie = StrategyOpponent.UNKNOWN;
            }
            int angriffeGegnerGesamt =  Integer.parseInt(input[1].split(": ")[1]);
            int angriffeGegnerLetzteRunde =  Integer.parseInt(input[2].split(": ")[1]);
            int sichtbarkeit =  Integer.parseInt(input[3].split(": ")[1]);
            String[] zeile5String = input[4].split(": ")[1].split(",");
            int[] meineAngriffeLetzteRunde = new int[zeile5String.length];
            for (int i = 0; i< zeile5String.length; i++) {
                meineAngriffeLetzteRunde[i] = Integer.parseInt(zeile5String[i]);
            }
            double abgewehrteAngriffeGesamt = Double.parseDouble(input[6].split(": ")[1]);
            int summeAbgewehrt = 0;
            for (int knotenNummer : meineAngriffeLetzteRunde) {
                if (ring.getNodeByNumber(knotenNummer).getOwner() != Ownership.MINE) {
                    summeAbgewehrt++;
                }
            }
            double abgewehrteAngriffeLetzteRunde = (double) (summeAbgewehrt / meineAngriffeLetzteRunde.length);
            abgewehrteAngriffeGesamt = (abgewehrteAngriffeGesamt * (runde -1) + abgewehrteAngriffeLetzteRunde ) / runde;
            double puffer = Double.parseDouble(input[8].split(": ")[1]);
            notizen = new Notes(runde, strategie, angriffeGegnerGesamt, angriffeGegnerLetzteRunde, sichtbarkeit, 
                    abgewehrteAngriffeGesamt, abgewehrteAngriffeLetzteRunde, puffer);
        } catch (Exception e) {
            System.out.println("Eingelesene notizen.txt ist leer oder fehlerhaft.");
            notizen = new Notes(runde, 2);
        }
        
        return notizen;
    }
}
