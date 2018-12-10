package Model;

public class DisconnectMessage extends Message {
    public DisconnectMessage(String senderName) {
        this.senderName = senderName;
    }

    @Override
    public String toXML(boolean escapeChars) {
        return "<message sender=\""+senderName+"\"><disconnect /></message>";
    }
}
