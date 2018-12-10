package Model;

import javax.xml.bind.DatatypeConverter;

public class KeyResponse extends Message {
    byte[] rawKey;
    int caesarKey;
    String type;

    KeyResponse(byte[] rawKey, String type) {
        this.rawKey = rawKey;
        this.type = type;
    }

    KeyResponse(int caesarKey, String type) {
        this.caesarKey = caesarKey;
        this.type = type;
    }

    @Override
    public String toXML(boolean escapeChars) {
        if (escapeChars) {
            this.escapeChars();
        }
        String res = "<message><encryption key=\"" +
                DatatypeConverter.printHexBinary(rawKey) + "\" type=\"" + type + "\"></encrpytion></message>";

        return res;
    }
}
