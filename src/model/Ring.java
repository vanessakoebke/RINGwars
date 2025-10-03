package model;

import java.util.*;
import java.util.function.Predicate;

/**
 * Diese Klasse bildet den Zustand des Spielfeldes (Ring) und die dazugehörigen
 * Informationen ab.
 */
public class Ring {
    /*
     * In der Knotenliste werden alle Knoten sortiert nach Knotennummer gespeichert.
     */
    private Knoten[] knotenListe; // ich verwende hier absichtlich ein Array und keine Liste, da die Knotenzahl
                                  // sich während der Ausführung nicht verändern soll, und ich anders als bei
                                  // einer Liste so nicht aus Versehen mehr Knoten einfügen kann als zulässig ist.
                                  // Dient als Hilfe für mich zum Troubleshooten.
    /*
     * Hier wird die maximal erlaubte Anzahl von Fernies pro Knoten gespeichert.
     * Dieses Feld ist final, da es nach Erstellung des Rings, nicht mehr verändert
     * werden darf.
     */
    private final int maxFerniesProKnoten;
    /*
     * Hier wird aktuell verfügbare Fernieanzahl gespeichert. Sie wird mit den neuen
     * Fernies aus der Step-Datei initialisiert und beim Setzen oder Entfernen von
     * Fernies auf einen Knoten inkrementiert bzw. dekrementiert.
     */
    private int ferniesVerfuegbar;
    /*
     * Hier wird gespeichert wie viele Fernies der Agenten maximal zur Verfügung
     * stehen (Anzahl neuer Fernies + Anzahl aller Fernies des Agenten zum
     * Rundenbeginn). Diese Zahl ist final, da im Laufe eines Zuges keine weiteren
     * Fernies hinzukommen können. Dieses Attribut dient ausschließlich dazu, der
     * Ausgabe (@see Ausgabe) übergeben zu werden, damit diese überprüfen kann, ob
     * die Summe der Fernies in der Ausgabe nicht die maximal verfügbaren Fernies
     * pro Runde überschreitet.
     */
    private final int maxFerniesRunde;

    /**
     * Erstellt einen neuen {@code Ring}.
     * <p>
     * Nach der Initialisierung enthält der Ring ein {@link Knoten}-Array, in dem
     * die Knoten sortiert nach Knotennummer eingetragen sind, die Zahl neuer
     * Fernies in dieser Runde, die maximale Ferniezahl pro Knoten und die maximal
     * verfügbare Ferniezahl für die aktuelle Runde (Anzahl neuer Fernies + Anzahl
     * bisheriger Fernies im Besitz des Agenten).
     * 
     * @param knotenListe         die Knoten des Rings, sortiert nach Knotennummer
     * @param maxFerniesProKnoten die maximal erlaubte Anzahl an Fernies auf einem
     *                            Knoten
     * @param ferniesVerfuegbar   die neu erhaltenen Fernies in der aktuellen Runde
     */
    public Ring(Knoten[] knotenListe, int maxFerniesProKnoten, int ferniesVerfuegbar) {
        this.knotenListe = knotenListe;
        this.maxFerniesProKnoten = maxFerniesProKnoten;
        this.ferniesVerfuegbar = ferniesVerfuegbar;
        this.maxFerniesRunde = ferniesVerfuegbar + getFernies(Besitz.MEINS);
    }

    /**
     * Gibt die Anzahl der Knoten in dem Ring zurück.
     * 
     * @return Knotenzahl des Rings
     */
    public int getAnzahlKnoten() {
        return knotenListe.length;
    }
    // TODO prüfen ob benötigt
    /**
     * Gibt ein Array mit allen Knoten auf dem Ring zurück.
     * 
     * @return vollständige Knotenliste
     */
//    public Knoten[] getAlleKnoten() {
//        return knotenListe;
//    }

    /**
     * Gibt eine Liste der sichtbaren Knoten zurück.
     * 
     * @return sichtbare Knoten
     */
    public List<Knoten> getSichtbareKnoten() {
        List<Knoten> sichtbare = new ArrayList<>();
        for (Knoten knoten : knotenListe) {
            if (knoten.isSichtbar()) {
                sichtbare.add(knoten);
            }
        }
        return sichtbare;
    }

    /**
     * Gibt zurück, ob der Gegner aktuell sichtbar ist.
     * 
     * @return gibt {@code true}, falls es sichtbare Knoten des Gegners gibt; gibt
     *         {@code false} zurück, falls der Gegner nicht sichtbar ist
     */
    public boolean isGegnerSichtbar() {
        boolean ergebnis = false;
        for (Knoten knoten : knotenListe) {
            if (knoten.getBesitz() == Besitz.SEINS) {
                ergebnis = true;
            }
        }
        return ergebnis;
    }

    /**
     * Gibt eine Liste mit den Knoten eines Besitzers zurück.
     * 
     * @param besitz der Besitzstatus, für den eine Knotenliste ausgegeben werden
     *               soll
     * @return Knotenliste eines Besitzers
     */
    public List<Knoten> getKnoten(Besitz besitz) {
        List<Knoten> ergebnis = new ArrayList<Knoten>();
        for (Knoten knoten : knotenListe) {
            if (knoten.getBesitz() == besitz) {
                ergebnis.add(knoten);
            }
        }
        return ergebnis;
    }

    /**
     * Gibt die Gesamtferniezahl eines Besitzers zurück.
     * 
     * @param besitz der Besitzstatus, für den die Gesamtferniezahl ausgegeben
     *               werden soll
     * @return die Gesamtferniezahl eines Besitzers
     */
    public int getFernies(Besitz besitz) {
        int summe = 0;
        for (Knoten knoten : getKnoten(besitz)) {
            summe += knoten.getFernieAnzahl();
        }
        return summe;
    }

    /**
     * Gibt zurück, wie viele Fernies aktuell frei sind und gesetzt werden können.
     * 
     * @return freie Ferniezahl
     */
    public int getFerniesVerfuegbar() {
        return ferniesVerfuegbar;
    }

    /**
     * Gibt den Knoten mit einer bestimmten Knotennummer zurück.
     * 
     * @param knotenNummer die gesuchte Knotennummer
     * @return der Knoten mit der gesuchten Knotennummer
     */
    public Knoten getKnotenMitNummer(int knotenNummer) {
        Knoten knoten = filter(x -> x.getKnotenNummer() == knotenNummer);
        return knoten;
    }

    /**
     * Greift einen gegnerischen Knoten mit einer bestimmten Fernieanzahl an. Die Anzahl der verfügbaren Fernies wird dekrementiert.
     * <p>
     * Falls der angegriffene Knoten nicht dem Gegner gehört oder ein anderer ungültiger Spielzug ausgeführt wird, wird eine {@link MoveException}
     * geworfen.
     * <p>
     * Falls die Fernieanzahl, die auf den Knoten gesetzt werden soll, minus die gegnerischen Fernies höher ist als die maximal erlaubte
     * Ferniezahl pro Knoten, wird nur mit der maximal möglichen Ferniezahl angegeriffen.
     * @param knotenNummer  die Knotennummer, die angegriffen werden soll
     * @param fernies               die Anzahl Fernies, die auf den angegriffenen Knoten gelegt werden soll
     * @throws MoveException    falls der Zug auf diesem Knoten ungültigen ist
     */
    public void greifeAn(int knotenNummer, int fernies) throws  MoveException {
        Knoten knoten = filter(x -> x.getKnotenNummer() == knotenNummer);
        if (knoten.getBesitz() != Besitz.SEINS) {
            throw new MoveException("Du versuchst einen Knoten anzugreifen, der nicht dem Gegner gehört. Verwende die addFernies-Funktion.");
            /*
             * Falls der Knoten dem Gegner gehört und die Anzahl der Fernies, die Anzahl der
             * zu setzenden Fernies minus die Anzahl der gegnerischen Fernies auf dem Knoten
             * die maximal erlaubte Anzahl Fernies pro Knoten überschreitet, werden dem
             * Knoten nur die Anzahl an Fernies hinzugefügt, bis die maximale Anzahl
             * erreicht wird.
             */
        } else if (fernies - knoten.getFernieAnzahl() > maxFerniesProKnoten) {
            int ferniesNeu = maxFerniesProKnoten + knoten.getFernieAnzahl();
            knoten.addFernies(ferniesNeu);
            this.ferniesVerfuegbar -= ferniesNeu;
            throw new FernieException(ferniesNeu);
        } else {
            knoten.addFernies(fernies);
            this.ferniesVerfuegbar -= fernies;
        }
    }

    /**
     * Legt Fernies auf einen Knoten. Die Anzahl der verfügbaren Fernies wird dekrementiert.
     * <p>
     * Falls ein ungültiger Spielzug ausgeführt wird, wird eine {@link MoveException} geworfen.
     * <p>
     * Falls die Fernieanzahl, die auf den Knoten gelegt werden soll, minus die bisher auf dem Knoten befindlichen Fernies höher ist als die maximal erlaubte
     * Ferniezahl pro Knoten, wird nur die maximal mögliche Ferniezahl auf den Knoten gelegt.
     * @param knotenNummer  die Nummer des Knoten, auf den Fernies gelegt werden sollen
     * @param fernies               die Anzahl Fernies, die auf den Knoten gelegt werden soll
     * @throws MoveException    falls der Zug auf diesem Knoten ungültigen ist
     */
    public void addFernies(int knotenNummer, int fernies) throws MoveException {
        Knoten knoten = filter(x -> x.getKnotenNummer() == knotenNummer);
        /*
         * Falls die Anzahl der Fernies, die bereits auf dem Knoten sind, plus die Anzahl der zu setzenden Fernies die
         * maximal erlaubte Anzahl Fernies pro Knoten überschreitet, werden dem Knoten
         * nur die Anzahl an Fernies hinzugefügt, bis die maximale Anzahl erreicht wird.
         */
        if (knoten.getBesitz() == Besitz.SEINS) {
            throw new MoveException("Du versuchst Fernies auf einen gegnerischen Knoten zu legen. Verwende die greifeAn-Funktion.");
        } else if (fernies + knoten.getFernieAnzahl() > maxFerniesProKnoten) {
            int ferniesNeu = maxFerniesProKnoten - knoten.getFernieAnzahl();
            knoten.addFernies(ferniesNeu);
            this.ferniesVerfuegbar -= ferniesNeu;
            throw new FernieException(ferniesNeu);
        } else {
            knoten.addFernies(fernies);
            this.ferniesVerfuegbar -= fernies;
        }
    }

    /**
     * Entfernt Fernies von einem Knoten im Besitz des Agenten. Die Anzahl der verfügbaren Fernies wird inkrementiert.
     * <p>
     * Wird versucht Fernies von einem Knoten zu entfernen, der nicht im Besitz des Agenten ist, oder wird versucht, mehr Fernies von dem Knoten
     * zu entfernen als vorhanden sind, wird eine {@link MoveException} geworfen.
     * @param knotenNummer  die Nummer des Knoten, von dem Fernies entfernt werden sollen
     * @param fernies               die Anzahl Fernies, die entfernt werden soll
     * @throws MoveException    falls ein ungültiger Zug durchgeführt wird
     */
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
    
    public Knoten getMinKnoten(List<Knoten> liste) {
        Knoten minimum = liste.getFirst();
        for (Knoten knoten : liste) {
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

    public List<Knoten> getLinkeHaelfte() {
        List<Knoten> liste = new ArrayList<>();
        int mitte = knotenListe.length / 2;
        for (int i = 0; i < mitte; i++) {
            liste.add(knotenListe[i]);
        }
        return liste;
    }

    public List<Knoten> getRechteHaelfte() {
        List<Knoten> liste = new ArrayList<>();
        int mitte = (knotenListe.length / 2) + 1;
        for (int i = mitte; i < knotenListe.length; i++) {
            liste.add(knotenListe[i]);
        }
        return liste;
    }

    public Knoten getGegnerLinks() {
        Knoten ergebnis = null;
        ListIterator<Knoten> iterator = getLinkeHaelfte().listIterator(getLinkeHaelfte().size());
        while (iterator.hasPrevious()) {
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
        while (iterator.hasNext()) {
            Knoten aktuell = iterator.next();
            if (aktuell.getBesitz() == Besitz.SEINS) {
                ergebnis = aktuell;
            }
        }
        return ergebnis;
    }
    
    public List<Knoten> getKnotenFreieNachbarn(int vor, int zurueck){
        List<Knoten> ergebnis = new ArrayList<Knoten>();
        for (Knoten k: getKnoten(Besitz.SEINS)) {
            boolean frei = true;
            for (int i = 1; i <= vor; i++) {
                if (getKnotenMitNummer(k.getKnotenNummer() + i).getBesitz() != Besitz.UNKONTROLLIERT) {
                    frei = false;
                }
            }
            for (int j = 1; j <= zurueck; j++) {
                if (getKnotenMitNummer(k.getKnotenNummer() - j).getBesitz() != Besitz.UNKONTROLLIERT) {
                    frei = false;
                }
            }
            if (frei) {
                ergebnis.add(k);
            }
        }
        return ergebnis;
    }
    
    public List<Knoten> getKnotenFreieNachbarn(int nachbarn){
        List<Knoten> ergebnis = new ArrayList<Knoten>();
        for (Knoten k: getKnoten(Besitz.SEINS)) {
            boolean freiVor = true;
            boolean freiZurueck = true;
            for (int i = 1; i <= nachbarn; i++) {
                if (getKnotenMitNummer(k.getKnotenNummer() + i).getBesitz() != Besitz.UNKONTROLLIERT) {
                    freiVor = false;
                }
            }
            for (int j = 1; j <= nachbarn; j++) {
                if (getKnotenMitNummer(k.getKnotenNummer() - j).getBesitz() != Besitz.UNKONTROLLIERT) {
                    freiZurueck = false;
                }
            }
            if (freiVor ^ freiZurueck) {
                ergebnis.add(k);
            }
        }

        return ergebnis;
    }
    

}
