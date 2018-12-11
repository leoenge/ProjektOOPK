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

        String res = "";
        if (type.toLowerCase().equals("aes")) {
            res = "<message><encrypted key=\"" +
                    DatatypeConverter.printHexBinary(rawKey) + "\" type=\"" + type + "\"></encrypted></message>";
        } else if (type.toLowerCase().equals("caesar")) {
            res = "<message><encrypted key=\"" +
                    caesarKey + "\" type=\"" + type + "\"></encrypted></message>";
        }

        return res;
    }
}
