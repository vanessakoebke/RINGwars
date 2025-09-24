//Abstrakte Klasse für die verschiedenen Strategien
package service;

import java.util.List;

import model.Besitz;
import model.Ring;

public abstract class Strategy {
    
    public abstract List<String> move(Ring ring);
    
    //Klassenmethode, die je nach aktuellem Status des Rings eine Strategie zurückliefert
    public static Strategy getStrategy(Ring ring) {
        if (ring == null) {
            return new LeereMove();
        } else if (!ring.isGegnerSichtbar()) {
            return new Expansion();
        } else  if (ring.getSichtbareKnoten().size() / ring.getAnzahlKnoten() < 0.9) {
            return new Konsolidierung();
        } else if ((ring.getSichtbareKnoten().size() / ring.getAnzahlKnoten() >= 0.9) && (ring.getFernies(Besitz.MEINS) > ring.getFernies(Besitz.SEINS))) {
            return new Angriff();
        } else if (ring.getFernies(Besitz.MEINS) < ring.getFernies(Besitz.SEINS)) {
            return new Defensiv();
        } else {
            return new LeereMove();
        }
    }
    
    //Statische Hilfsmethode, um zu prüfen, ob nicht mehr Fernies verteilt wurden als vorhanden sind
    public static boolean check(int ferniesVerfuegbar, List<String> ausgabe) {
        int summe = 0;
        for (String s : ausgabe) {
            int fernieAufKnoten = Integer.parseInt(s.split(",")[1]); //Es wird die Zahl nach dem Komma, jedes Elements der Ausgabe-List herausgenommen und aufsummiert.
            summe += fernieAufKnoten;
        }
        return ferniesVerfuegbar == summe;
    }
}
