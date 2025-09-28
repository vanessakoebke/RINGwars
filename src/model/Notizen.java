package model;

import java.util.*;

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
        this(runde, StrategieGegner.UNBEKANNT, 0, 0, sichtbarkeit, 0.0, 0.0, 1.1);
    }

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

    public void setMeineAngriffeDieseRunde(List<Integer> meineAngriffeDieseRunde) {
        this.meineAngriffeDieseRunde = meineAngriffeDieseRunde;
    }

    public double getPufferAgriff() {
        return pufferAgriff;
    }

    public void setPufferAgriff(double pufferAgriff) {
        this.pufferAgriff = pufferAgriff;
    }

    public double getAbgewehrteAngriffeInsgesamt() {
        return abgewehrteAngriffeInsgesamt;
    }

    public double getAbgewehrteAngriffeLetzteRunde() {
        return abgewehrteAngriffeLetzteRunde;
    }

    @Override
    public String toString(){
        String meineAngriffe = "";
        Iterator<Integer> iterator = meineAngriffeDieseRunde.iterator();
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
