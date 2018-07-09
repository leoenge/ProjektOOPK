package Model;

public class ChatSettings {
    Encryption encryption;
    String fontType;
    String fontColor;
    String userName;

    ChatSettings() {

    }

    ChatSettings(String userName) {
        this.userName = userName;
    }


}