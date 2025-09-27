package model;

public class Knoten {
    private final int knotenNummer;
    private Besitz besitz;
    private int fernieAnzahl;

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

    public int getKnotenNummer() {
        return knotenNummer;
    }

    public Besitz getBesitz() {
        return besitz;
    }
    



    public int getFernieAnzahl() {
        return fernieAnzahl;
    }
    
    public void addFernies(int fernies) throws MoveException{
        if (besitz == Besitz.MEINS) {
            this.fernieAnzahl += fernies;
        } else  if (besitz == Besitz.UNKONTROLLIERT && fernies >0) {
            besitz = Besitz.MEINS;
            this.fernieAnzahl += fernies;
        } else if (besitz == Besitz.SEINS) {
            if (this.fernieAnzahl < fernies) {
                this.fernieAnzahl = fernies;
            } else {
                throw new MoveException("Schlechter Zug: Du versuchst gerade den Gegner anzugreifen und setzt aber zu wenig Fernies ein. Knotennummer: " + knotenNummer);
            }
        } else if (besitz == Besitz.UNBEKANNT){
            throw new MoveException("Ungültiger Zug: Du versuchst einen nicht-sichtbaren Knoten zu besetzen.");
        }
    }
    
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


    public boolean isSichtbar() {
        return (besitz != Besitz.UNBEKANNT);
    }
    
    @Override
    public String toString() {
        return knotenNummer + " - " + besitz; 
    }
}
