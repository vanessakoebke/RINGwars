package service;

import java.io.*;
import java.util.List;

import model.*;

public class Util {
    
    public static Ring einlesen(int step) throws UngueltigeStatusException{
        String dateipfad ="inputoutput/" + step + ".txt";
        String[] input = new String[5];
        try (BufferedReader br = new BufferedReader(new FileReader(dateipfad))){
            int i = 0;
            String zeile;
            while ((zeile =br.readLine()) != null) {
                input[i++] = zeile;
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //1. Prüfung: falls die Statusdatei mehr als 4 Zeilen hat, stimmt etwas nicht.
        if (input[4] != null) {
            throw new UngueltigeStatusException("Statusdatei hat mehr als 4 Zeilen!");
        }
        String[] zeile1String = input[0].split(",");
        String[] zeile2 = input[1].split(",");
        
        //2. Prüfung: falls die Anzahl der Elemente in Zeilen 1 und 2 nicht übereinstimmt, stimmt etwas nicht.
        if (zeile1String.length != zeile2.length) {
            throw new UngueltigeStatusException("Zeile 1 und Zeile 2 der Statusdatei enthalten eine ungleiche Anzahl an Knoten.");
        }
        int anzahlKnoten = zeile1String.length;
        int[] zeile1Int = new int[anzahlKnoten];
        int anzahlFerniesVerfuegbar;
        int anzahlFerniesGesamt;
        
        //3. Prüfung: falls es beim Umwandeln der Elemente der 1., 3. oder 4. Zeile eine NumberformatException gibt, stand bei einer Ferniezahlangabe keine Zahlen, d.h. die Statusdatei war fehlerhaft.
        try {
            anzahlFerniesVerfuegbar = Integer.parseInt(input[2]);
            anzahlFerniesGesamt = Integer.parseInt(input[3]);
            for (int i =0; i < anzahlKnoten; i++) {
                zeile1Int[i] = Integer.parseInt(zeile1String[i]);
            }
        } catch (NumberFormatException e){
            throw new UngueltigeStatusException("In der ersten Zeile stehen nicht nur Zahlen.");
        }
        
        //4. Prüfung: falls die Knotenanzahl 0 ist, stimmt etwas nicht.
        if (anzahlKnoten == 0) {
            throw new UngueltigeStatusException("Die Statusdatei enthält keine Knoten.");
        }
        
        Knoten[] knotenListe = new Knoten[anzahlKnoten];
        for (int i = 0; i< anzahlKnoten; i++) {
            knotenListe[i] = new Knoten(i, zeile2[i], zeile1Int[i]);
        }
        return new Ring(knotenListe, anzahlFerniesGesamt, anzahlFerniesVerfuegbar);
    }
    
    public static void ausgeben(List<String> ausgabe) {
        //TODO ausgabe schreiben
    }
    
    
}
