package model;

public class FernieException extends MoveException {
    private int tatsächlicheFernies;
    
    public FernieException(int tatsächlicheFernies) {
        super("Du versuchst zu viele Fernies auf einen Knoten zu legen. Die Ferniezahl wird gekappt. Es wurden lediglich " + tatsächlicheFernies + " Fernies hinzugefügt.");
        this.tatsächlicheFernies = tatsächlicheFernies;
    }
    
    public int getFernies() {
        return tatsächlicheFernies;
    }
}
