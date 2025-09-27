package model;

public class Notizen {
    private StrategieGegner strategieGegner;
    private int angriffeGesamt;
    private int angriffeLetzteRunde;
    private int sichtbarkeit;
    
    public Notizen(StrategieGegner strategieGegner, int angriffeGesamt, int angriffeLetzteRunde, int sichtbarkeit) {
        this.strategieGegner = strategieGegner;
        this.angriffeGesamt = angriffeGesamt;
        this.angriffeLetzteRunde = angriffeLetzteRunde;
        this.sichtbarkeit = sichtbarkeit;
    }
    
    public Notizen(int sichtbarkeit) {
        this(StrategieGegner.UNBEKANNT, 0, 0, sichtbarkeit);
    }

    public StrategieGegner getStrategieGegner() {
        return strategieGegner;
    }
    
    
    public int getAngriffeGesamt() {
        return angriffeGesamt;
    }

    public int getAngriffeLetzteRunde() {
        return angriffeLetzteRunde;
    }

    public int getSichtbarkeit() {
        return sichtbarkeit;
    }

    @Override
    public String toString(){
        return "Gegnerische Strategie: " + strategieGegner + System.lineSeparator() +
                "Angriffe insgesamt: " + angriffeGesamt + System.lineSeparator() +
                "Angriffe in der letzten Runde: " + angriffeLetzteRunde + System.lineSeparator() +
                "Sichtbarkeitsreichweite: " + sichtbarkeit;
    }
    
    
}
