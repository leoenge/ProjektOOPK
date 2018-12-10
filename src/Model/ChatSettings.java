package Model;

import java.awt.*;

public class ChatSettings {
    public Color fontColor;
    public String userName;
    public String encryptionType = null;

    public String getUserName() { return userName; }
    public Color getFontColor() { return fontColor; }

    ChatSettings(String userName) {
        this.userName = userName;
        this.fontColor = null;
    }
}