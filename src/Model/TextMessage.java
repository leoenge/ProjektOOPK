package Model;

public class TextMessage extends Message {

    //Idea: add array containing message strings in order, so that part of messages can be encrypted.
    String textColor;
    String senderName;
    String fontType;
    String encryptedText;

    public TextMessage(String text, String encryptedIn, String name) {
        message = text;
        senderName = name;
        textColor = null;
        fontType = null;
        encryptedText = encryptedIn;
    }

    @Override
    public String toXML() {
        String outXML = "";

        if (senderName != null) {
            outXML += "<message name=\"" + senderName + "\">";
        } else {
            outXML += "<message>";
        }

        if (encryptedText != null) {
            //Do handling of encryption here
        }

        if (textColor != null) {
            outXML += "<text color=\"" + textColor + ">" + message + "</text>";
        } else {
            outXML += "<text>" + message + "</text>";
        }

        outXML += "</message>";

        return outXML;

    }
}
