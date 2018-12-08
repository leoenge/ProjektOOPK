package Model;

public class DisconnectMessage extends Message {
    public DisconnectMessage(String senderName) {
        this.senderName = senderName;
    }

    public String toXML() {
        return "<message sender=\""+senderName+"\"><disconnect /></message>";
    }
}
