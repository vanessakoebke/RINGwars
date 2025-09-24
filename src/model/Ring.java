package model;

import java.util.ArrayList;
import java.util.List;

public class Ring {
    private Knoten[] knotenListe;       //ich verwende hier absichtlich ein Array und keine Liste, da die Knotenzahl sich während der Ausführung nicht verändern soll, und ich anders als bei einer Liste so nicht aus Versehen mehr Knoten einfügen kann als zulässig ist. Dient als Hilfe für mich zum Troubleshooten.
    private int maxAnzahlFernies;
    private int ferniesVerfuegbar;
    
    public Ring(Knoten[] knotenListe, int maxAnzahlFernies, int ferniesVerfuegbar) {
        this.knotenListe = knotenListe;
        this.maxAnzahlFernies = maxAnzahlFernies;
        this.ferniesVerfuegbar = ferniesVerfuegbar;
    }
    
    public int getAnzahlKnoten() {
        return knotenListe.length;
    }

    public Knoten[] getAlleKnoten() {
        return knotenListe;
    }
    public void setKnotenListe(Knoten[] knotenListe) {
        this.knotenListe = knotenListe;
    }
    
    public List<Knoten> getSichtbareKnoten(){
        List<Knoten> sichtbare = new ArrayList<>();
        for (Knoten knoten: knotenListe) {
            if (knoten.isSichtbar()) {
                sichtbare.add(knoten);
            }
        }
        return sichtbare;
    }
    
    public boolean isGegnerSichtbar() {
        boolean ergebnis = false;
        for(Knoten knoten: knotenListe) {
            if (knoten.getBesitz() == Besitz.SEINS) {
                ergebnis = true;
            }
        }
        return ergebnis;
    }
    
    public List<Knoten> getGegnerische(){
        List<Knoten> gegnerische = new ArrayList<Knoten>();
        for (Knoten knoten: knotenListe) {
            if (knoten.getBesitz() == Besitz.SEINS) {
                gegnerische.add(knoten);
            }
        }
        return gegnerische;
    }

    public int getFerniesVerfuegbar() {
        return ferniesVerfuegbar;
    }

    public void setFerniesVerfuegbar(int ferniesVerfuegbar) {
        this.ferniesVerfuegbar = ferniesVerfuegbar;
    }

    public int getMaxAnzahlFernies() {
        return maxAnzahlFernies;
    }
    
    public int getFernies(Besitz b) {
        int summe = 0;
        for (Knoten knoten: knotenListe) {
            if (knoten.getBesitz() == b) {
                summe += knoten.getFernieAnzahl();
            }
        }
        return summe;
    }
}
