package Model;

public class KeyRequest extends Message {
    public Encryption encryption;

    public KeyRequest(Encryption encryptionIn, String messageIn) {
        encryption = encryptionIn;
        message = messageIn;
    }

    @Override
    public String toXML() {
        return null;
    }
}
