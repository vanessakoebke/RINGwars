package service;

import java.util.ArrayList;
import java.util.List;

import model.Ring;
import model.UngueltigeStatusException;

public class RINGwars_8878390_Koebke_Vanessa {
    public static void main(String[] args) {
        String agentenName = "inputoutput";
        Ring ring = null;
        try {
            ring = Util.einlesen(1);
        } catch (UngueltigeStatusException e) {
            System.out.println(e.getMessage());
            System.out.println("Es wird eine leere Move-Datei erzeugt.");
        }
        Strategy strategie = Strategy.getStrategy(ring);
        List<String> ausgabe = strategie.move(ring);
        
        //Finale Prüfung, ob nicht mehr Fernies gesetzt wurden, als verfügbar sind.
        if (!Strategy.check(ring.getFerniesVerfuegbar(), ausgabe)) {
            System.out.println("Hilfe, du hast die falsche Ferniezahl veteilt.");
            ausgabe = new LeereMove().move(ring);
        }
        
        Util.ausgeben(ausgabe, agentenName);
    }
}
