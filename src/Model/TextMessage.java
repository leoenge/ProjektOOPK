package Model;

public class TextMessage extends Message {
    String textColor;
    String senderName;
    String fontType;
    String encryptedText;

    TextMessage(String text, String encryptedIn, String name) {
        message = text;
        senderName = name;
        textColor = null;
        fontType = null;
        encryptedText = encryptedIn;
    }

    @Override
    public String toXML() {
        return null;
    }
}
