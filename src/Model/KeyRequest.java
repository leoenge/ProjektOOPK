package Model;

public class KeyRequest extends Message {
    public String type;
    public KeyRequest(String type, String message) {
        this.type = type;
        this.message = message;
    }

    @Override
    public String toXML(boolean escapeChars) {
        if (escapeChars) {
            this.escapeChars();
        }
        String res = "<message><keyrequest type=\"" + type + "\">" + message + "</keyrequest></message>";
        return res;
    }
}
