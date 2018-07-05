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

    //private Model model;
    public View view;

    public void askUser(Connection requestConnection, Request request) {

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
