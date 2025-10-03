package model;

import java.util.*;
/**
 * Repräsentiert die Notizen, die der Agent im Laufe einer Runde macht und auf die er im nächsten Zug zugreift, um seine Strategie anzupassen.
 * Die Notizen speichern folgende Informationen:
 * <ul>
 * <li>die aktuelle Rundennummer (Stepnummer)</li>
 * <li>die Strategie des Gegners</li>
 * <li>wie oft der Gegner bisher insgesamt angegriffen hat</li> 
 * <li>wie oft er in der letzen Runde angegriffen hat</li>
 * <li>den Sichtbarkeitsradius</li>
 * <li>eine Liste der Knoten, die der Agent in der aktuellen Runde angegriffen haben (es werden sowohl direkte Angriffe als auch Grenzkampangriffe
 * gezählt</li>
 * <li>die relative Anzahl aller Angriffe, die durch den Gegner abgewehrt wurden (Anzahl meiner Angriffe / Anzahl abgewehrter Angriffe)</li>
 * <li>die relative Anzahl der Angriffe, die in der letzten Runde durch den Gegner abgewehrt wurden (Berechnung s.o.)</li>
 * <li>den Puffer an zusätzlichen Fernies, den der Agent bei seinen Angriffen verwendet (Beispiel: Puffer von 1.1 bedeutet, wenn auf dem 
 * angegriffenen Knoten 10 gegnerische Fernies liegen, greift mein Agent mit 11 Fernies an)
 * </ul>
 */
public class Notizen {
    private int aktuelleRunde;
    private StrategieGegner strategieGegner;
    private int angriffeGegnerGesamt;
    private int angriffeGegnerLetzteRunde;
    private int sichtbarkeit;
    private List<Integer> meineAngriffeDieseRunde;
    private double abgewehrteAngriffeInsgesamt;
    private double abgewehrteAngriffeLetzteRunde;
    private double pufferAgriff;
    
    public Notizen(int runde, StrategieGegner strategieGegner, int angriffeGegnerGesamt, int angriffeGegnerLetzteRunde, int sichtbarkeit, 
             double abgewehrteAngriffeInsgesamt, double abgewehrteAngriffeLetzteRunde, double pufferAngriff) {
        this.aktuelleRunde = runde;
        this.strategieGegner = strategieGegner;
        this.angriffeGegnerGesamt = angriffeGegnerGesamt;
        this.angriffeGegnerLetzteRunde = angriffeGegnerLetzteRunde;
        this.sichtbarkeit = sichtbarkeit;
        this.meineAngriffeDieseRunde = new ArrayList<>();
        this.abgewehrteAngriffeInsgesamt = abgewehrteAngriffeInsgesamt;
        this.abgewehrteAngriffeLetzteRunde = abgewehrteAngriffeLetzteRunde;
        this.pufferAgriff = pufferAngriff;
    }
    
    public Notizen(int runde, int sichtbarkeit) {
        this(runde, StrategieGegner.UNBEKANNT, 0, 0, sichtbarkeit, 0.0, 0.0, 1.0);
    }

    /**
     * Liefert die gegnerische Strategie zurück.
     * 
     * @return die gegnerische Strategie
     */
    public StrategieGegner getStrategieGegner() {
        return strategieGegner;
    }
    
    
    public int getAngriffeGesamt() {
        return angriffeGegnerGesamt;
    }

    public int getAngriffeLetzteRunde() {
        return angriffeGegnerLetzteRunde;
    }

    public int getSichtbarkeit() {
        return sichtbarkeit;
    }

    public List<Integer> getMeineAngriffeDieseRunde() {
        return meineAngriffeDieseRunde;
    }

    /**
     * Fügt der Liste der diese Runde von dem Agenten angegriffenen Knoten einen weiteren Knoten hinzu.
     * 
     * @param knotenNummer die angegriffene Knotennummer
     */
    public void addAngriff(int knotenNummer) {
        this.meineAngriffeDieseRunde.add(knotenNummer);
    }

    public double getPufferAgriff() {
        return pufferAgriff;
    }

    /**
     * Setzt einen neuen Wert für den Angriffspuffer.
     * 
     * @param pufferAgriff der neue Wert für den Angriffspuffer
     */
    public void setPufferAgriff(double pufferAgriff) {
        this.pufferAgriff = pufferAgriff;
    }

    public double getAbgewehrteAngriffeInsgesamt() {
        return abgewehrteAngriffeInsgesamt;
    }

    public double getAbgewehrteAngriffeLetzteRunde() {
        return abgewehrteAngriffeLetzteRunde;
    }

    /**
     * Gibt die Notizen als String aus, damit sie in der Datei notizen.txt gespeichert werden können.
     * 
     * @return Notizen als String
     */
    @Override
    public String toString(){
        String meineAngriffe = "";
        Iterator<Integer> iterator = meineAngriffeDieseRunde.iterator();
        //Das erste Element wurde aus der while-Schleife ausgelagert, damit der meineAngriffe-String nicht mit einem Komma endet.
        if (iterator.hasNext()) {
        meineAngriffe = String.valueOf(iterator.next());
        }
        while (iterator.hasNext()) {
            meineAngriffe += "," + String.valueOf(iterator.next());
        }
        return "Gegnerische Strategie: " + strategieGegner + System.lineSeparator() +
                "Gegnerische Angriffe insgesamt: " + angriffeGegnerGesamt + System.lineSeparator() +
                "Gegnerische Angriffe in der letzten Runde: " + angriffeGegnerLetzteRunde + System.lineSeparator() +
                "Sichtbarkeitsreichweite: " + sichtbarkeit + System.lineSeparator() +
                "Meine Angriffe in der letzten Runde: " + meineAngriffe + System.lineSeparator() +
                "Vom Gegner abgewehrte Angriffe insgesamt: " + abgewehrteAngriffeInsgesamt + System.lineSeparator() +
                "Vom Gegner abgewehrte Angriffe letzte Runde: " + abgewehrteAngriffeLetzteRunde + System.lineSeparator() +
                "Puffer für meine Angriffe: " + pufferAgriff;
    }
    
    
}
