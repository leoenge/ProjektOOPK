package Model;

public abstract class Message  {
    public String message;
    public String senderName;
    public String srcIP;

    public abstract String toXML(boolean escapeChars);

    public String toString() { return message; }

    //Adds the encryption tags required around the message.
    //!!DOES NOT DO THE ACTUAL ENCRYPTION!!
    public void addEncryptionTags(String key, String type) {
        message = "<encrypted type=\"" + type + "\" key=\"" + key + "\">" + message + "</encrypted>";
    }

    /**
     * Escapes xml tags in the text that the user inputs to messages.
     */
    public void escapeChars() {
        for (int i = 0; i < message.length(); i++) {
            message = message.replace("<","&lt;");
            message = message.replace(">", "&gt;");
        }
    }

    public void unEscapeChars() {
        for (int i = 0; i < message.length(); i++) {
            message = message.replace("&lt;", "<");
            message = message.replace("&gt;", "<");
        }
    }
}
