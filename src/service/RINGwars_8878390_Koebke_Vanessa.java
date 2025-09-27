package service;

import java.util.List;

import model.*;

public class RINGwars_8878390_Koebke_Vanessa {
    public static void main(String[] args) {
        String agentenName = "inputoutput";
        Ring ring = null;
        try {
            ring = Util.statusEinlesen(1);
        } catch (UngueltigeStatusException e) {
            System.out.println(e.getMessage());
            System.out.println("Es wird eine leere Move-Datei erzeugt.");
        }
        Notizen notizen = Util.notizenEinlesen(agentenName);
        Strategy strategie = Strategy.getStrategy(ring, notizen);
        System.out.println("Angewandte Strategie: " + strategie.toString());
        List<String> ausgabe = strategie.move(ring);
        
        Util.moveAusgeben(ausgabe, agentenName);
        Util.notizenAusgeben(notizen.toString(), agentenName);
        System.out.println("Das Programm ist erfolgreich zu Ende gelaufen.");
        System.out.println(ausgabe);
    }
}
