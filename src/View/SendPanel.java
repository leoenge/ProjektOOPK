package View;

import Model.Model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SendPanel extends JPanel {
    private JPanel buttonPanel;
    JButton sendButton;
    JButton sendEncryptedButton;
    JButton fileButton;
    JButton sendEncryptedFileButton;
    JTextPane messageTextPane;

    SendPanel() {
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 2));
        buttonPanel.setPreferredSize(new Dimension(400, 100));

        this.setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
        this.setPreferredSize(new Dimension(700,100));

        sendButton = new JButton("Send");
        fileButton = new JButton("Send file");
        sendEncryptedButton = new JButton("Send encrypted");
        sendEncryptedFileButton = new JButton("Send encrypted file");
        messageTextPane = new JTextPane();
        messageTextPane.setPreferredSize(new Dimension(300, 100));

        buttonPanel.add(sendButton);
        buttonPanel.add(sendEncryptedButton);
        buttonPanel.add(fileButton);
        buttonPanel.add(sendEncryptedFileButton);

        this.add(messageTextPane);
        this.add(buttonPanel);

        this.setVisible(true);
    }
}
