package service;

import java.util.ArrayList;
import java.util.List;

import model.Ring;

public class LeereMove extends Strategy {

    @Override
    public List<String> move(Ring ring, int fernies) {
        return new ArrayList<String>();
    }
}
