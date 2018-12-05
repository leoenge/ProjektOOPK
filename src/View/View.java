package View;

import javax.swing.*;

import Controller.Controller;
import Model.*;
import jdk.nashorn.internal.scripts.JO;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class View implements ActionListener {
    private Model model;
    public JFrame frame;
    private ControlPanel controlPanel;
    private ChatPanel chatPanel;
    private SendPanel sendPanel;

    public String getChatMessage() {
        return sendPanel.messageTextPane.getText();
    }

    public View(Model modelIn) {
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

    public void updateView() {
    }

    //Funderar på att sätta lyssnaren på knapparna i View istället för i Controller så att controller inte behöver känna till
    //Vilka knappar som finns i View.
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
            Controller.getInstance().closeConnection();
        } else if (srcButton == sendPanel.sendButton) {
            Controller.getInstance().sendMessage();
        } else if (srcButton == sendPanel.fileButton) {
            Controller.getInstance().sendFile();
        } else if (srcBox == controlPanel.chooseChatBox) {
            //do stuff
        }
    }

    public void updateActiveChatBox(int chatNumber) {
        controlPanel.chooseChatBox.addItem("Chat no " + chatNumber);
    }
}
