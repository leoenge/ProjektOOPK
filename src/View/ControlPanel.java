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
        this.setPreferredSize(new Dimension(500,75));
        this.setLayout(new FlowLayout());

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
        this.setVisible(true);
    }


}
