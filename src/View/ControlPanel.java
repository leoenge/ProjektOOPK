package View;

import Controller.Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControlPanel extends JPanel implements ActionListener {

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

        connectButton.addActionListener(this);
        chatSettingsButton.addActionListener(this);
        closeChatButton.addActionListener(this);
        closeConnectionButton.addActionListener(this);
        chooseChatBox.addActionListener(this);

        this.add(connectButton);
        this.add(chatSettingsButton);
        this.add(closeChatButton);
        this.add(closeConnectionButton);
        this.add(chooseChatBox);
        this.setVisible(true);
    }

    //Funderar på att sätta lyssnaren på knapparna i View istället för i Controller så att controller inte behöver känna till
    //Vilka knappar som finns i View.
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.connectButton) {
        }
    }
}
