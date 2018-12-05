package View;

import Model.Model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SendPanel extends JPanel {
    JButton sendButton;
    JButton fileButton;
    JTextPane messageTextPane;

    SendPanel() {
        this.setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
        this.setPreferredSize(new Dimension(500,100));

        sendButton = new JButton("Send");
        fileButton = new JButton("Send file");
        messageTextPane = new JTextPane();
        messageTextPane.setPreferredSize(new Dimension(400, 100));

        this.add(messageTextPane);
        this.add(sendButton);
        this.add(fileButton);

        this.setVisible(true);
    }
}
