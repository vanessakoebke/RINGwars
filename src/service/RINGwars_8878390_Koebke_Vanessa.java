package service;

import java.util.List;

import model.*;

public class RINGwars_8878390_Koebke_Vanessa {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Bitte gültige Argumente angeben: <Step-Nummer> <Agentenname>. Das Programm beendet sich nun.");
            return;
        }
        int runde;
        try {
            runde = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("Bitte gültige Step-Nummer übergeben. Das Programm beendet sich nun.");
            return;
        }
        String agentenName = args[1];
        Ring ring = null;
        try {
            ring = Util.statusEinlesen(agentenName, runde);
        } catch (UngueltigeStatusException e) {
            System.out.println(e.getMessage());
            System.out.println("Es wird eine leere Move-Datei erzeugt.");
        }
        Notizen notizen = Util.notizenEinlesen(agentenName, ring, runde);
        Strategy strategie = Strategy.getStrategy(ring, notizen);
        System.out.println("Angewandte Strategie: " + strategie.toString());
        List<String> ausgabe = strategie.move(ring);
        
        Util.moveAusgeben(ausgabe, agentenName);
        Util.notizenAusgeben(notizen.toString(), agentenName);
        System.out.println("Das Programm ist erfolgreich zu Ende gelaufen.");
        System.out.println(ausgabe);
    }
}
