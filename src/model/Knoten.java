package model;

public class Knoten {
    private final int knotenNummer;
    private Besitz besitz;
    private int fernieAnzahl;
    private boolean sichtbar;

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
        return sichtbar;
    }

    public void setSichtbar(boolean sichtbar) {
        this.sichtbar = sichtbar;
    }
    
    @Override
    public String toString() {
        return knotenNummer + " - " + besitz; 
    }
}
