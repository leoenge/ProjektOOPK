package Controller;

import Model.*;
import View.*;
//import Model.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Controller implements ActionListener {

    private static Controller theInstance = new Controller();

    private Controller() { }

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

    public void actionPerformed(ActionEvent e) {

    }

    private void sendMessage() {

    }

    private void requestConnection() {

    }

    private void updateSettings() {

    }

    private void closeActiveChat() {

    }

    private void closeSingleChat() {

    }

    private void changeActiveChat() {

    }

    private void sendFile() {

    }
}
