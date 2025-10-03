package service;

import java.util.List;

import model.*;

public class Defensive extends Strategy {

    public Defensive(Notes notes) {
        super(notes);
    }

    @Override
    public List<String> move(Ring ring) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String toString() {
        return "Defensive";
    }
}
