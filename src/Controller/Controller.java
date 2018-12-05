package Controller;

import Model.*;
import View.*;
import jdk.nashorn.internal.scripts.JO;
//import Model.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Controller implements ActionListener {

    //Singleton since we only want one controller
    private static Controller theInstance = new Controller();

    private Controller() {}

    public static Controller getInstance() { return theInstance;}

    public void setModel(Model model) {
        this.model = model;
    }
    public void setView(View view) {this.view = view;}

    public View view;
    private Model model;

    //Returns the chat to connect the new connection to or null if user doesn't want to establish the connection
    public boolean askUser(Request request) {
        //TODO: Fixa så att detta inte blockar eventtråden. Det som är skrivet nu är endast en testversion.
        int answer;
        if (request != null) {
            answer = JOptionPane.showConfirmDialog(null,
                    "Someone wants to connect. Their message: " + request.message,
                    "New connection attempt", JOptionPane.YES_NO_OPTION);
        } else {
            answer = JOptionPane.showConfirmDialog(null, "Someone wants to connect with a simpler " +
                    "client", "New connection attempt", JOptionPane.YES_NO_OPTION);
        }
        return answer == JOptionPane.YES_OPTION;
    }

    public void askUserFileRequest() {

    }

    public void establishServerPort() {
        String inputStr = JOptionPane.showInputDialog("Which port do you want to listen to connections from?" +
                "(1500 - 65535)");
        int portNumber;
        try {
            portNumber = Integer.parseInt(inputStr);
        } catch (NumberFormatException e){
            JOptionPane.showMessageDialog(null,
                    "port needs to be an integer value", "Error", JOptionPane.ERROR_MESSAGE);
            establishServerPort();
            return;
        }

        if (portNumber < 1500 || portNumber > 65535) {
            JOptionPane.showMessageDialog(null,
                    "port number out of range", "Error", JOptionPane.ERROR_MESSAGE);
            establishServerPort();
            return;
        }

        model.createConnectionReceiver(portNumber);
    }

    public void actionPerformed(ActionEvent e) {
    }

    public void sendMessage(String msText) {
        //Create message
        Chat activeChat = model.getActiveChat();

        activeChat.sendTextMessage(msText);
        //Updates the text box with the message history.
        view.updateView();
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
