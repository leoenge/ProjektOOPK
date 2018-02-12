package View;

import javax.swing.*;

public class SendPanel extends JPanel   {
    JButton sendButton;
    JButton fileButton;
    JTextPane messageTextArea;

    SendPanel() {
        sendButton = new JButton("Send");
        fileButton = new JButton("Send file");
        messageTextArea = new JTextPane();

        this.add(sendButton);
        this.add(fileButton);
        this.add(messageTextArea);
        this.setVisible(true);
    }
}
