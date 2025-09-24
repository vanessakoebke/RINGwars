package model;

public class UngueltigeStatusException extends Exception{
    public UngueltigeStatusException(String message) {
        super("Ungültige Statusdatei: "+ message);
    }
}
