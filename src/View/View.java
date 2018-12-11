package View;

import javax.swing.*;
import javax.swing.text.*;
import javax.xml.soap.Text;

import Controller.Controller;
import Model.*;
import jdk.nashorn.internal.scripts.JO;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.CancellationException;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class View implements ActionListener, ItemListener {
    private Model model;
    private JFrame frame;
    private ControlPanel controlPanel;
    private ChatPanel chatPanel;
    SendPanel sendPanel;

    public static View init_view(Model modelIn) {
        View view = new View(modelIn);

        view.sendPanel.sendButton.addActionListener(view);
        view.sendPanel.fileButton.addActionListener(view);
        view.sendPanel.sendEncryptedButton.addActionListener(view);
        view.sendPanel.sendEncryptedFileButton.addActionListener(view);
        view.controlPanel.closeChatButton.addActionListener(view);
        view.controlPanel.closeConnectionButton.addActionListener(view);
        view.controlPanel.chooseChatBox.addItemListener(view);
        view.controlPanel.chatSettingsButton.addActionListener(view);
        view.controlPanel.createMultiPartButton.addActionListener(view);
        view.controlPanel.connectButton.addActionListener(view);

        //TODO: Lägg till andra knappar så att view lyssnar på dem.

        return view;
    }

    private View(Model modelIn) {
        model = modelIn;
        frame = new JFrame();
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

        controlPanel = new ControlPanel();
        chatPanel = new ChatPanel();
        sendPanel = new SendPanel();

        frame.add(controlPanel);
        frame.add(chatPanel);
        frame.add(sendPanel);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public String requestString(String promptMessage) {
        return JOptionPane.showInputDialog(promptMessage);
    }

    public int requestNumber(String promptMessage) {
        int res;
        try {
            res = Integer.parseInt(JOptionPane.showInputDialog(promptMessage));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this.frame, "Input needs to be a number.");
            return requestNumber(promptMessage);
        }

        return res;
    }

    public int requestNumber(String promptMessage, int lower, int upper) {
        int res;

        try {
            res = Integer.parseInt(JOptionPane.showInputDialog(promptMessage));
            if (res < lower || res > upper) {
                JOptionPane.showMessageDialog(this.frame, "Value not in specified range. Try again.");
                return requestNumber(promptMessage, lower, upper);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this.frame, "Value needs to be an integer. Try again.");
            return requestNumber(promptMessage, lower, upper);
        }

        return res;
    }

    public File requestFile() {
        JFileChooser fileChooser = new JFileChooser();
        int returnVal = fileChooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        } else {
            return null;
        }
    }

    public boolean yesNoRequest(String message) {
        int choice = JOptionPane.showConfirmDialog(null, message, "Choose", JOptionPane.YES_NO_OPTION);
        return (choice == JOptionPane.YES_OPTION);
    }

    public void updateView(Message message) {
        StyledDocument doc = chatPanel.messageHistoryPane.getStyledDocument();
        if (message instanceof TextMessage) {
            try {
                //If there is no color specified for the message, just put it in as default
                if (((TextMessage) message).getTextColor() == null) {
                    doc.insertString(doc.getLength(),
                            ((TextMessage) message).getSenderName() + ": " + message.message + "\n", null);
                } else {
                    SimpleAttributeSet style = new SimpleAttributeSet();
                    StyleConstants.setForeground(style, ((TextMessage) message).getTextColor());
                    //Show the username without color as the default black.
                    doc.insertString(doc.getLength(), ((TextMessage) message).getSenderName() + ": ", null);
                    //Show the message with color.
                    doc.insertString(doc.getLength(), message.message + "\n", style);
                }
            } catch (BadLocationException e) {
                JOptionPane.showMessageDialog(null, "Error in message text insertion.");
                return;
            }

            sendPanel.messageTextPane.setText("");
        } else if (message instanceof DisconnectMessage) {
            if (!message.senderName.equals("")) {
                this.displayMessage(message.senderName + " has disconnected.");
            } else {
                this.displayMessage("The other side has diconnected without sending a disconnect message.");
            }
        }
    }

    /**
     * Clears the sending message panel and the message history panel.
     * Adds the messages in the messages parameter to the message history panel.
     */
    public void updateWindows(Iterable<TextMessage> messages) {
        //Clears the message text from user input panel and message history panels.
        this.clearWindows();

        for (TextMessage message : messages) {
            updateView(message);
        }
    }

    public void clearWindows() {
        chatPanel.messageHistoryPane.setText("");
        sendPanel.messageTextPane.setText("");
    }

    /**
     * Called when one of the buttons attached to the frame of this view are pressed.
     * Calls methods in controller corresponding to the pressed buttons.
     * @param e The actionevent from the button.
     */
    public void actionPerformed(ActionEvent e) {
        JButton srcButton = null;
        JComboBox srcBox = null;
        if (e.getSource() instanceof JButton) {
            srcButton = (JButton) e.getSource();
        } else if (e.getSource() instanceof JComboBox){
            srcBox = (JComboBox) e.getSource();
        } else {
            return;
        }

        if (srcButton == controlPanel.connectButton) {
            Controller.getInstance().requestConnection();
        } else if (srcButton == controlPanel.chatSettingsButton) {
            Controller.getInstance().updateSettings();
        } else if (srcButton == controlPanel.closeChatButton) {
            Controller.getInstance().closeActiveChat();
        } else if (srcButton == controlPanel.closeConnectionButton) {
            ArrayList<Connection> connections = model.getActiveChat().getConnections();
            Object[] connectionArr = new Object[connections.size()];
            for (int i = 0; i < connections.size(); i++) {
                connectionArr[i] = connections.get(i);
            }
            int connIndex = JOptionPane.showOptionDialog(null, "Choose a connection",
                    "connection choice", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                    connectionArr, null);
            Connection connection = connections.get(connIndex);
            Controller.getInstance().closeConnection(connection);
        } else if (srcButton == controlPanel.createMultiPartButton) {
            Controller.getInstance().makeMultiPart();
        } else if (srcButton == sendPanel.sendButton) {
            Controller.getInstance().sendMessage(sendPanel.messageTextPane.getText());
        } else if (srcButton == sendPanel.fileButton) {
            ArrayList<Connection> connections = model.getActiveChat().getConnections();
            Object[] connectionArr = new Object[connections.size()];
            for (int i = 0; i < connections.size(); i++) {
                connectionArr[i] = connections.get(i);
            }
            int connIndex = JOptionPane.showOptionDialog(null, "Choose a connection",
                    "connection choice", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                    connectionArr, null);
            Connection connection = connections.get(connIndex);
            Controller.getInstance().sendFile(connection);
        } else if (srcButton == sendPanel.sendEncryptedFileButton) {
            ArrayList<Connection> connections = model.getActiveChat().getConnections();
            Object[] connectionArr = new Object[connections.size()];
            for (int i = 0; i < connections.size(); i++) {
                connectionArr[i] = connections.get(i);
            }
            int connIndex = JOptionPane.showOptionDialog(null, "Choose a connection",
                    "connection choice", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                    connectionArr, null);
            Connection connection = connections.get(connIndex);

            Controller.getInstance().sendEncryptedFile(connection);
        }

        else if (srcButton == sendPanel.sendEncryptedButton) {
            Controller.getInstance().sendEncryptedMessage(sendPanel.messageTextPane.getText());
        }
    }

    /**
     * Called when an item is selected from a JComboBox. Changes the active chat
     * to the one selected in the JComboBox.
     * @param e The action event.
     */
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            Chat selectedChat = (Chat) e.getItem();
            Controller.getInstance().changeActiveChat(selectedChat);
        }
    }

    /**
     * Adds a new chat to the JComboBox containing the chats we currently have.
     * @param chat The new chat.
     */
    public void updateActiveChatBox(Chat chat) {
        controlPanel.chooseChatBox.addItem(chat);
    }

    /**
     * Displays a system message on the message panel.
     * @param text The text to display.
     */
    public void displayMessage(String text) {
        StyledDocument doc = chatPanel.messageHistoryPane.getStyledDocument();
        try {
            doc.insertString(doc.getLength(), "<system>: " + text + "\n", null);
        } catch (BadLocationException e) {
            System.err.println(e.getStackTrace());
        }
    }

    /**
     * Removes a chat from this view by clearing it from the Combo Box with the chats and clearing the
     * text panes.
     * @param chat The chat to remove.
     */
    public void removeChat(Chat chat) {
        //Remove the chat from the choicebox
        controlPanel.chooseChatBox.removeItem(chat);
        //Clear the text from this chat in the panels.
        sendPanel.messageTextPane.setText("");
        chatPanel.messageHistoryPane.setText("");
    }
}
