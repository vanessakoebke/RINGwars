package model;

import java.util.ArrayList;
import java.util.List;

public class Ausgabe {
    private final int ferniesGesamt;
    private List<Zeile> ausgabeListe;

    public Ausgabe(int ferniesGesamt) {
        this.ferniesGesamt = ferniesGesamt;
        ausgabeListe = new ArrayList<Zeile>();
    }

    public List<String> getAusgabe(Ring ring) {
        if (!check(ring)) {
            System.out.println("Ausgabe stimmt nicht. Fallback-Strategie implementieren!");
        }
        List<String> ausgabe = new ArrayList<String>();
        for (Zeile zeile : ausgabeListe) {
            ausgabe.add(zeile.knotenNummer + "," + zeile.fernieZahl);
        }
        return ausgabe;
    }

    // Hilfsmethode, um zu prüfen, ob nicht mehr Fernies verteilt wurden als ich in
    // dieser Runde verfügbar habe.
    private boolean check(Ring ring) {
        return ferniesGesamt >= ring.getFernies(Besitz.MEINS);
    }

    public void upsert(int knotenNummer, int fernieZahl) {
        for (Zeile zeile : ausgabeListe) {
            if (zeile.knotenNummer == knotenNummer&& zeile.fernieZahl>=0) {
                zeile.fernieZahl += fernieZahl;
                return;
            }
        }
        ausgabeListe.add(new Zeile(knotenNummer, fernieZahl));
    }
    
    public void remove(int knotenNummer, int fernieZahl) {
        fernieZahl = -fernieZahl;
        for (Zeile zeile : ausgabeListe) {
            if (zeile.knotenNummer == knotenNummer && zeile.fernieZahl<0) {
                zeile.fernieZahl += fernieZahl;
                return;
            }
        }
        ausgabeListe.add(new Zeile(knotenNummer, fernieZahl));
    }

    private static class Zeile {
        private int knotenNummer;
        private int fernieZahl;

        private Zeile(int knotenNummer, int fernieZahl) {
            this.knotenNummer = knotenNummer;
            this.fernieZahl = fernieZahl;
        }
    }
}
