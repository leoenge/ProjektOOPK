package Model;

public abstract class Message  {
    public String message;
    public String srcIP;

    public abstract String toXML();
}
