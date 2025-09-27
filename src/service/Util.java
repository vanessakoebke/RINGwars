package service;

import java.io.*;
import java.util.List;

import model.*;

public class Util {
    public static Ring statusEinlesen(int step) throws UngueltigeStatusException {
        String dateipfad = "inputoutput/" + step + ".txt";
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
            throw new UngueltigeStatusException(
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
            throw new UngueltigeStatusException(
                    "An einer Stelle wo eine Fernieanzahl erwartet wurde, stand keine Zahl.");
        }
        // 4. Prüfung: falls die Knotenanzahl 0 ist, stimmt etwas nicht.
        if (anzahlKnoten == 0) {
            throw new UngueltigeStatusException("Die Statusdatei enthält keine Knoten.");
        }
        Knoten[] knotenListe = new Knoten[anzahlKnoten];
        for (int i = 0; i < anzahlKnoten; i++) {
            // 5. Prüfung: Falls der Sichtbarkeitsstatus in Zeile 1 und 2 nicht
            // übereinstimmen, stimmt etwas nicht.
            if ((zeile1Int[i] == -1 && !zeile2[i].equals("U")) || (zeile1Int[i] != -1 && zeile2[i].equals("U"))) {
                throw new UngueltigeStatusException("Bei Knoten " + i
                        + " stimmt der Sichtbarkeitsstatus aus Zeile 1 und 2 nicht überein. (Zeile 1 hat -1 und Zeile 2 hat nicht U oder umgekehrt.)");
            }
            //6. Prüfung: falls der Status nicht-kontrolliert ist und die Ferniezahl != 0 ist, stimmt etwas nicht.
            if ((zeile1Int[i] == 0 && !zeile2[i].equals("N")) || (zeile1Int[i] != 0 && zeile2[i].equals("N"))) {
                throw new UngueltigeStatusException("Bei Knoten " + i
                        + " stimmt der Unkontrolliertstatus aus Zeile 1 und 2 nicht überein. (Zeile 1 hat 0 und Zeile 2 hat nicht N oder umgekehrt.)");
            }
            knotenListe[i] = new Knoten(i, zeile2[i], zeile1Int[i]);
        }
        return new Ring(knotenListe, anzahlFerniesGesamt, anzahlFerniesVerfuegbar);
    }

    public static void moveAusgeben(List<String> ausgabe, String agentenName) {
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

    public static void notizenAusgeben(String notizen, String agentenName) {
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

    public static Notizen notizenEinlesen(String agentenName) {
        String dateipfad = agentenName  + "/notizen.txt";
        String[] input = new String[5];
        try (BufferedReader br = new BufferedReader(new FileReader(dateipfad))) {
            int i = 0;
            String zeile;
            while ((zeile = br.readLine()) != null) {
                input[i++] = zeile;
            }
        } catch (Exception e) {
            System.out.println("Die Datei notizen.txt konnte nicht eingelesen werden. Es wird eine neue Datei erzeugt.");
            return new Notizen(2); //TODO korrigieren
        }        
        Notizen notizen;
        try {
            String zeile1 = input[0].split(": ")[1];
            StrategieGegner strategie;
            switch(zeile1) {
            case "AGGRESIV": strategie = StrategieGegner.AGRESSIV;
            case "DEFENSIV": strategie = StrategieGegner.DEFENSIV;
            default: strategie = StrategieGegner.UNBEKANNT;
            }
            int zeile2 =  Integer.parseInt(input[1].split(": ")[1]);
            int zeile3 =  Integer.parseInt(input[2].split(": ")[1]);
            int zeile4 =  Integer.parseInt(input[3].split(": ")[1]);
            notizen = new Notizen(strategie, zeile2, zeile3, zeile4);
        } catch (Exception e) {
            System.out.println("Eingelesene notizen.txt ist leer.");
            notizen = new Notizen(2);
        }
        
        return notizen;
    }
}
