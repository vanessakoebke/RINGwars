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

    public void setBesitz(Besitz besitz) {
        this.besitz = besitz;
    }

    public int getFernieAnzahl() {
        return fernieAnzahl;
    }

    public void setFernieAnzahl(int fernieAnzahl) {
        this.fernieAnzahl = fernieAnzahl;
    }

    public boolean isSichtbar() {
        return (besitz != Besitz.UNBEKANNT);
    }
    
    @Override
    public String toString() {
        return knotenNummer + " - " + besitz; 
    }
}
