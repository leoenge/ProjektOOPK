package Controller;

import Model.*;
import View.*;
//import Model.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Controller implements ActionListener {

    private static Controller theInstance = new Controller();

    private Controller() {}

    public static Controller getInstance() {
        return theInstance;
    }

    private Model model = Model.getInstance();
    public View view;

    //Returns the chat to connect the new connection to or null if user doesn't want to establish the connection
    public Chat askUser(Connection requestConnection, Request request) {
        return null;
    }

    public void askUserFileRequest() {

    }

    public void establishServerPort() {
        String inputStr = JOptionPane.showInputDialog("Which port do you want to listen to connections from?" +
                "(49152-65535)");
        int portNumber = 0;
        try {
            portNumber = Integer.parseInt(inputStr);
        } catch (NumberFormatException e){
            JOptionPane.showMessageDialog(null,
                    "port needs to be an integer value", "Error", JOptionPane.ERROR_MESSAGE);
            establishServerPort();
            return;
        }

        if (portNumber < 49152 || portNumber > 65535) {
            JOptionPane.showMessageDialog(null,
                    "port number out of range", "Error", JOptionPane.ERROR_MESSAGE);
            establishServerPort();
            return;
        }

        Model.getInstance().createConnectionReceiver(portNumber);
    }

    public void actionPerformed(ActionEvent e) {

    }

    public void sendMessage() {
        Model.getInstance().getActiveChat().sendTextMessage("text goes here");
    }

    public void requestConnection() {
    }

    public void updateSettings() {

    }

    public void closeActiveChat() {

    }

    public void closeConnection() {

    }

    public void changeActiveChat() {

    }

    public void sendFile() {

    }
}
