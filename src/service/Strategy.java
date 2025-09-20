//Abstrakte Klasse für die verschiedenen Strategien
package service;

import java.util.List;

import model.Ring;

public abstract class Strategy {
    public abstract List<String> move(Ring ring, int fernies);
    
    //Klassenmethode, die je nach aktuellem Status des Rings eine Strategie zurückliefert
    public static Strategy getStrategy(Ring ring) {
        if (!ring.isGegnerSichtbar()) {
            return new Expansion();
        } else {
            return new LeereMove();
        }
    }
}
