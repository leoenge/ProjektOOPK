package Model;

import java.awt.*;

/**
 * Represents a text message used in a chat client.
 */
public class TextMessage extends Message {
    //24 bit bitmask.
    static final int bitMask24 = 0xffffff;

    //Idea: add array containing message strings in order, so that part of messages can be encrypted.
    Color textColor;

    //Copy constructor
    public TextMessage(TextMessage textMessage) {
        this.message = textMessage.message;
        this.textColor = textMessage.textColor;
        this.senderName = textMessage.senderName;
    }

    public TextMessage(String message, Color color, String senderName) {
        this.message = message;
        this.senderName = senderName;
        this.textColor = color;
    }

    public String getSenderName() {
        return senderName;
    }
    public Color getTextColor() { return textColor; }

    public String internalTags() {
        String outXML = "";
        if (textColor != null) {
            outXML += "<text color=\"" + colorHex() + "\">" + message + "</text>";
        } else {
            outXML += "<text>" + message + "</text>";
        }
        return outXML;
    }

    public String toEncryptedXML(String encryptedString, String key, String type) {
        String res = "";
        if (senderName != null) {
            res += "<message sender=\"" + senderName + "\"><encrypted type=\"" + type + "\" key=\"" + key + "\">";
        } else {
            res += "<message><encrypted type=\"" + type + "\" key=\"" + key + "\">";
        }

        res += encryptedString + "</encrypted></message>";

        return res;
    }

    /**
     * Converts the text message to an XML representation.
     * @param escapeChars true if we want to escape XML-characters in the user inputted text.
     * @return
     */
    @Override
    public String toXML(boolean escapeChars) {
        if (escapeChars) {
            //Escape XML-characters in the user inputted text.
            this.escapeChars();
        }
        String outXML = "";

        if (senderName != null) {
            outXML += "<message sender=\"" + senderName + "\">";
        } else {
            outXML += "<message>";
        }

        if (textColor != null) {
            outXML += "<text color=\"" + colorHex() + "\">" + message + "</text>";
        } else {
            outXML += "<text>" + message + "</text>";
        }

        outXML += "</message>";

        return outXML;

    }

    private String colorHex() {
        //Extract only the first 24 bits of the RGB value, since the remaining bits are for alpha
        //which we are not interested in.
        int RGB = textColor.getRGB() & bitMask24;
        String RGBstr = Integer.toHexString(RGB);
        //Pad with leading zeros until we have 6 digits, so that we match the specification.
        while (RGBstr.length() < 6) {
            RGBstr = "0" + RGBstr;
        }
        RGBstr = "#" + RGBstr;
        return RGBstr;
    }
}
