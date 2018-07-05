package Model;

public class ChatSettings {
    public Encryption encryption;
    public String fontType;
    public String fontColor;
    public String userName;

    private static ChatSettings theInstance = new ChatSettings();

    public static ChatSettings getInstance() {
        return theInstance;
    }

    private ChatSettings() {
    }

}