package model;

/**
 * Repräsentiert einen Knoten des Rings.
 */
public class Knoten {
    private final int knotenNummer;
    private Besitz besitz;
    private int fernieAnzahl;

    /**
     * Erstellt einen neuen Knoten. Nach der Initialisierung hat der Knoten eine finale Knotennummer, einen Besitzstatus ({@link Besitz}) und eine Fernieanzahl, die auf 
     * ihm liegt.
     * @param knotenNummer  die Knotennummer
     * @param besitz                der Besitzstatus des Knoten
     * @param fernieAnzahl      die Fernieanzahl
     */
    public Knoten(int knotenNummer, String besitz, int fernieAnzahl) {
        this.knotenNummer = knotenNummer;
        this.fernieAnzahl = fernieAnzahl;
        switch (besitz) {
        case "Y":
            this.besitz = Besitz.MEINS;
            break;
        case "N":
            this.besitz =Besitz.UNKONTROLLIERT;
            break;
        case "U":
            this.besitz = Besitz.UNBEKANNT;
            break;
        default:
            this.besitz = Besitz.SEINS;
        }
    }

    /**
     * Gibt die Knotennummer zurück.
     * @return  Knotennummer
     */
    public int getKnotenNummer() {
        return knotenNummer;
    }

    /**
     * Gibt den Besitzstatus des Knotens zurück.
     * @return  der Besitzstatus
     */
    public Besitz getBesitz() {
        return besitz;
    }
    


    /**
     * Gibt die Fernieanzahl, die auf dem Knoten liegt, zurück.
     * @return  die Fernieanzahl
     */
    public int getFernieAnzahl() {
        return fernieAnzahl;
    }
    
    /**
     * Fügt eine Anzahl Fernies dem Knoten hinzu. 
     * <p>
     * Falls der Knoten dem Agenten gehört oder unkontrolliert ist, wird die zugefügte Ferniezahl zur bisherigen Ferniezahl addiert.
     * Falls der Knoten dem Gegner gehört, wird geprüft, ob der die zugefügten Ferniezahl höher ist als die bisherigen gegnerischen Fernies.
     * Falls ja, wird die bisherige Fernieanzahl von den neu hinzugefügten abgezogen und dieser Wert die neue Ferniezahl des Knoten.
     * Falls der Knoten nicht sichtbar ist, wird eine {@link MoveException} geworfen.
     * @param fernies               hinzuzufügende Ferniezahl
     * @throws MoveException    Exception, die geworfen wird, falls der Zug ungültig ist
     */
    public void addFernies(int fernies) throws MoveException{
        if (besitz == Besitz.MEINS) {
            this.fernieAnzahl += fernies;
        } else  if (besitz == Besitz.UNKONTROLLIERT && fernies >0) {
            besitz = Besitz.MEINS;
            this.fernieAnzahl += fernies;
        } else if (besitz == Besitz.SEINS) {
            if (this.fernieAnzahl < fernies) {
                int temp = fernieAnzahl;
                this.fernieAnzahl = fernies - temp;
            } else {//TODO entfernen vor Abgabe
                throw new MoveException("Schlechter Zug: Du versuchst gerade den Gegner anzugreifen und setzt aber zu wenig Fernies ein. Knotennummer: " + knotenNummer);
            }
        } else if (besitz == Besitz.UNBEKANNT){
            throw new MoveException("Ungültiger Zug: Du versuchst einen nicht-sichtbaren Knoten zu besetzen.");
        }
    }
    
    /**
     * Entfernt eine Anzahl Fernies vom Knoten.
     * <p>
     * Falls der Knoten dem Agenten gehört und aktuelle Ferniezahl - die zu entfernende Ferniezahl >= 0 ist, wird die entsprechende Ferniezahl entfernt.
     * Falls der Knoten nicht dem Agenten gehört oder er versucht mehr Fernies zu entfernen als aktuell auf dem Knoten liegen, wird eine 
     * {@link MoveException} geworfen.
     * @param fernies               zu entfernende Ferniezahl
     * @throws MoveException    Exception, die geworfen wird, falls der Zug ungültig ist
     */
    public void removeFernies(int fernies) throws MoveException {
        if (besitz == Besitz.MEINS && fernieAnzahl - fernies >= 0) {
            this.fernieAnzahl -= fernies;
            if (fernieAnzahl == 0) {
                besitz = Besitz.UNKONTROLLIERT;
            }
        } else  if (besitz == Besitz.MEINS && fernieAnzahl - fernies < 0){
            throw new MoveException("Ungültiger Zug: Du versuchst mehr Fernies von dem Knoten zu entfernen als vorhanden sind. Knotennummer: "+ knotenNummer);
        } else {
            throw new MoveException("Ungültiger Zug: Du versuchst Knoten von einem Knoten zu entfernen, der nicht dir gehört. Knotennummer: " + knotenNummer);
        }
    }

    /**
     * Gibt den Sichtbarkeitsstatus des Knoten als Wahrheitswert zurück.
     * @return  gibt {@code true} zurück, falls der Knoten sichtbar ist; gibt {@code false} zurück, falls der Knoten nicht sichtbar ist
     */
    public boolean isSichtbar() {
        return (besitz != Besitz.UNBEKANNT);
    }
    
    //TODO entfernen vor Abgabe
//    @Override
//    public String toString() {
//        return knotenNummer + " - " + besitz; 
//    }
}
