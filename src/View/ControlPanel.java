package View;

import Controller.Controller;
import Model.Model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControlPanel extends JPanel {

    JButton connectButton;
    JButton chatSettingsButton;
    JButton closeChatButton;
    JButton closeConnectionButton;
    JButton createMultiPartButton;
    JComboBox chooseChatBox;

    ControlPanel() {
        this.setPreferredSize(new Dimension(500,75));
        this.setLayout(new FlowLayout());

        connectButton = new JButton("Connect");
        chatSettingsButton = new JButton("Chat Settings");
        closeChatButton = new JButton("Close Chat");
        closeConnectionButton = new JButton("Close connection");
        createMultiPartButton = new JButton("Make into multi-user");
        chooseChatBox = new JComboBox();

        /** connectButton.addActionListener(Model.getInstance().view);
        chatSettingsButton.addActionListener(Model.getInstance().view);
        closeChatButton.addActionListener(Model.getInstance().view);
        closeConnectionButton.addActionListener(Model.getInstance().view);
        chooseChatBox.addActionListener(Model.getInstance().view);
        **/
        this.add(connectButton);
        this.add(chatSettingsButton);
        this.add(closeChatButton);
        this.add(closeConnectionButton);
        this.add(chooseChatBox);
        this.add(createMultiPartButton);
        this.setVisible(true);
    }


}
