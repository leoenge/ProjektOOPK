package Model;

/**
 * Abstract class for representing different types of messages as XML.
 * Requires subclasses to implement a toXML method to convert the message to an
 * XML representation.
 */
public abstract class Message  {
    public String message = null;
    public String senderName;
    public String srcIP;

    public abstract String toXML(boolean escapeChars);

    public String toString() { return message; }

    //Adds the encryption tags required around the message.
    //!!DOES NOT DO THE ACTUAL ENCRYPTION!!
    void addEncryptionTags(String key, String type) {
        message = "<encrypted type=\"" + type + "\" key=\"" + key + "\">" + message + "</encrypted>";
        System.err.println(message);
    }

    /**
     * Escapes xml tags in the text that the user inputs to messages.
     */
    void escapeChars() {
        if (message == null) {
            return;
        }
        message = message.replace("<","&lt;");
        message = message.replace(">", "&gt;");
    }

    public void unEscapeChars() {
        if (message == null) {
            return;
        }
        message = message.replace("&lt;", "<");
        message = message.replace("&gt;", "<");
    }
}
