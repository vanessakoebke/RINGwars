package service;

import model.*;

public class RINGwars_8878390_Koebke_Vanessa {
    public static void main(String[] args) {
        
        Ring ring = null;
        try {
            ring = Util.einlesen(1);
        } catch (UngueltigeStatusException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        for (Knoten k: ring.getAlleKnoten()) {
            System.out.println(k);
        }
        //Util.ausgeben(new ArrayList<String>());
    }
}
