package Model;

public class TextMessage extends Message {

    //Idea: add array containing message strings in order, so that part of messages can be encrypted.
    String textColor;
    String fontType;
    String encryptedText;

    public TextMessage(String message, String encryptedIn, String senderName) {
        this.message = message;
        this.senderName = senderName;
        textColor = null;
        fontType = null;
        encryptedText = encryptedIn;
    }

    public String getSenderName() {
        return senderName;
    }

    @Override
    public String toXML() {
        //Escape XML-characters in the user inputted text.
        this.escapeChars();
        String outXML = "";

        if (senderName != null) {
            outXML += "<message sender=\"" + senderName + "\">";
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
