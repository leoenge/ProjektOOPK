package View;

import javax.swing.*;
import java.awt.*;

public class ChatPanel extends JPanel {
    JTextPane messageHistoryPane;

    ChatPanel() {
        this.setPreferredSize(new Dimension(400,400));
        messageHistoryPane = new JTextPane();
        messageHistoryPane.setEditable(false);
        messageHistoryPane.setPreferredSize(new Dimension(350, 350));
        this.add(messageHistoryPane);
        this.setVisible(true);
    }
}
