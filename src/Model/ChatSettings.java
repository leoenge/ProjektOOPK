package Model;

public class ChatSettings {
    Encryption encryption;
    String fontType;
    String fontColor;
    String userName;

    ChatSettings() {

    }

    public String getUserName() { return userName; }

    ChatSettings(String userName) {
        this.userName = userName;
    }


}