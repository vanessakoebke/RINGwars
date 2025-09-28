package model;

import java.util.*;
import java.util.function.Predicate;

public class Ring {
    private Knoten[] knotenListe; // ich verwende hier absichtlich ein Array und keine Liste, da die Knotenzahl
                                  // sich während der Ausführung nicht verändern soll, und ich anders als bei
                                  // einer Liste so nicht aus Versehen mehr Knoten einfügen kann als zulässig ist.
                                  // Dient als Hilfe für mich zum Troubleshooten.
    private final int maxFerniesProKnoten;
    private int ferniesVerfuegbar;
    private final int maxFerniesRunde;

    public Ring(Knoten[] knotenListe, int maxAnzahlFernies, int ferniesVerfuegbar) {
        this.knotenListe = knotenListe;
        this.maxFerniesProKnoten = maxAnzahlFernies;
        this.ferniesVerfuegbar = ferniesVerfuegbar;
        this.maxFerniesRunde = ferniesVerfuegbar + getFernies(Besitz.MEINS);
    }

    public int getAnzahlKnoten() {
        return knotenListe.length;
    }

    public Knoten[] getAlleKnoten() {
        return knotenListe;
    }

    public List<Knoten> getSichtbareKnoten() {
        List<Knoten> sichtbare = new ArrayList<>();
        for (Knoten knoten : knotenListe) {
            if (knoten.isSichtbar()) {
                sichtbare.add(knoten);
            }
        }
        return sichtbare;
    }

    public boolean isGegnerSichtbar() {
        boolean ergebnis = false;
        for (Knoten knoten : knotenListe) {
            if (knoten.getBesitz() == Besitz.SEINS) {
                ergebnis = true;
            }
        }
        return ergebnis;
    }

    public List<Knoten> getKnoten(Besitz besitz) {
        List<Knoten> ergebnis = new ArrayList<Knoten>();
        for (Knoten knoten : knotenListe) {
            if (knoten.getBesitz() == besitz) {
                ergebnis.add(knoten);
            }
        }
        return ergebnis;
    }

    public int getFernies(Besitz besitz) {
        int summe = 0;
        for (Knoten knoten : getKnoten(besitz)) {
            summe += knoten.getFernieAnzahl();
        }
        return summe;
    }

    public int getFerniesVerfuegbar() {
        return ferniesVerfuegbar;
    }
    
    public void greifeAn(int knotenNummer, int fernies) throws FernieException, MoveException {
        Knoten knoten = filter(x -> x.getKnotenNummer() == knotenNummer);
        if (fernies > maxFerniesProKnoten) {
            int ferniesNeu = maxFerniesProKnoten;
            knoten.addFernies(ferniesNeu);
            this.ferniesVerfuegbar -= ferniesNeu;
            throw new FernieException(ferniesNeu);
        } else {
            knoten.addFernies(fernies);
            this.ferniesVerfuegbar -= fernies;
        
        }
    }
    
    public Knoten getKnotenMitNummer(int knotenNummer) {
        Knoten knoten = filter(x -> x.getKnotenNummer() == knotenNummer);
        return knoten;
    }

    public void addFernies(int knotenNummer, int fernies) throws FernieException, MoveException {
        Knoten knoten = filter(x -> x.getKnotenNummer() == knotenNummer);
        /*
         * Falls die Anzahl der Fernies, die bereits auf dem Knoten sind, + die Anzahl der verfügbaren Fernies die maximal erlaubte
         * Anzahl Fernies pro Knoten überschreitet, werden dem Knoten nur die Anzahl an Fernies hinzugefügt, bis die maximale Anzahl erreicht wird.
         * Andernfalls werden alle Fernies auf den Knoten gesetzt.
         */
        if (fernies + knoten.getFernieAnzahl() > maxFerniesProKnoten) {
            int ferniesNeu = maxFerniesProKnoten - knoten.getFernieAnzahl();
            knoten.addFernies(ferniesNeu);
            this.ferniesVerfuegbar -= ferniesNeu;
            throw new FernieException(ferniesNeu);
        } else {
            knoten.addFernies(fernies);
            this.ferniesVerfuegbar -= fernies;
        }
    }

    public void removeFernies(int knotenNummer, int fernies) throws MoveException {
        Knoten knoten = filter(x -> x.getKnotenNummer() == knotenNummer);
        knoten.removeFernies(fernies);
        this.ferniesVerfuegbar += fernies;
    }

    public int getMaxFerniesProKnoten() {
        return maxFerniesProKnoten;
    }

    public float getSichtbarkeitAnteil() {
        return (float) getSichtbareKnoten().size() / getAnzahlKnoten();
    }

    public boolean isRingVoll() {
        for (Knoten knoten : getSichtbareKnoten()) {
            if (knoten.getFernieAnzahl() < maxFerniesProKnoten) {
                return false;
            }
        }
        return true;
    }

    public boolean isRingVoll(Besitz besitz) {
        for (Knoten knoten : getKnoten(besitz)) {
            if (knoten.getFernieAnzahl() < maxFerniesProKnoten) {
                return false;
            }
        }
        return true;
    }

    public Knoten filter(Predicate<Knoten> p) {
        for (Knoten k : knotenListe) {
            if (p.test(k)) {
                return k;
            }
        }
        return null;
    }

    public Knoten getMinKnoten(Besitz besitz) {
        Knoten minimum = knotenListe[0];
        for (Knoten knoten : getKnoten(besitz)) {
            if (knoten.getFernieAnzahl() < minimum.getFernieAnzahl()) {
                minimum = knoten;
            }
        }
        return minimum;
    }

    public Knoten getMaxKnoten(Besitz besitz) {
        Knoten maximum = knotenListe[0];
        for (Knoten knoten : getKnoten(besitz)) {
            if (knoten.getFernieAnzahl() < maximum.getFernieAnzahl()) {
                maximum = knoten;
            }
        }
        return maximum;
    }

    public double getDurchschnittFernieProKnoten(Besitz besitz) {
        return getFernies(besitz) / getKnoten(besitz).size();
    }
    
    public int getMaxFerniesRunde() {
        return maxFerniesRunde;
    }
    
    public List<Knoten> getLinkeHaelfte(){
        List<Knoten> liste = new ArrayList<>();
        int mitte = knotenListe.length / 2;
        for (int i = 0; i< mitte; i++) {
            liste.add(knotenListe[i]);
        }
        return liste;
    }
    
    public List<Knoten> getRechteHaelfte(){
        List<Knoten> liste = new ArrayList<>();
        int mitte = (knotenListe.length / 2) + 1;
        for (int i = mitte; i< knotenListe.length; i++) {
            liste.add(knotenListe[i]);
        }
        return liste;
    }
    
    public Knoten getGegnerLinks() {
        Knoten ergebnis = null;
        ListIterator<Knoten> iterator = getLinkeHaelfte().listIterator(getLinkeHaelfte().size());
        while(iterator.hasPrevious()) {
            Knoten aktuell = iterator.previous();
            if (aktuell.getBesitz() == Besitz.SEINS) {
                ergebnis = aktuell;
            }
        }
        return ergebnis;
    }
    
    public Knoten getGegnerRechts() {
        Knoten ergebnis = null;
        ListIterator<Knoten> iterator = getRechteHaelfte().listIterator();
        while(iterator.hasNext()) {
            Knoten aktuell = iterator.next();
            if (aktuell.getBesitz() == Besitz.SEINS) {
                ergebnis = aktuell;
            }
        }
        return ergebnis;
    }

}
