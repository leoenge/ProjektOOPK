package View;

import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JPanel {

    JButton connectButton;
    JButton chatSettingsButton;
    JButton closeChatButton;
    JButton closeConnectionButton;
    JComboBox chooseChatBox;

    ControlPanel() {
        this.setLayout(new GridLayout(1,5));

        connectButton = new JButton("Connect");
        chatSettingsButton = new JButton("Chat Settings");
        closeChatButton = new JButton("Close Chat");
        closeConnectionButton = new JButton("Close connection");
        chooseChatBox = new JComboBox();

        this.add(connectButton);
        this.add(chatSettingsButton);
        this.add(closeChatButton);
        this.add(closeConnectionButton);
        this.add(chooseChatBox);
    }
}
