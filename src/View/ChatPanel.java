package View;

import javax.swing.*;
import java.awt.*;

public class ChatPanel extends JPanel {
    JTextPane messageHistoryPane;
    JScrollPane scrollableMessagePane;

    ChatPanel() {
        this.setPreferredSize(new Dimension(600,400));
        messageHistoryPane = new JTextPane();
        messageHistoryPane.setEditable(false);
        messageHistoryPane.setPreferredSize(new Dimension(550, 350));
        scrollableMessagePane = new JScrollPane(messageHistoryPane);
        this.add(scrollableMessagePane);
        this.setVisible(true);
    }
}
