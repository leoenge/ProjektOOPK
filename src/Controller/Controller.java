package Controller;

import Model.*;
import View.*;
import com.sun.org.apache.bcel.internal.classfile.Unknown;
import jdk.nashorn.internal.scripts.JO;
//import Model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Controller {

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

    public void sendEncryptedMessage(String msText) {
        Chat activeChat = model.getActiveChat();
        if (activeChat.getSettings().encryptionType == null) {
            view.displayMessage("Specify an encryption type in settings first");
            return;
        }

        //Check that all connections on the current chat supports the selected type of encryption.
        for (Connection connection: activeChat.getConnections()) {
            if (activeChat.getSettings().encryptionType.equals("aes")) {
                if (!connection.supportsAES()) {
                    view.displayMessage("Not all other users support the selected type of encryption.");
                    return;
                }
            } else if (activeChat.getSettings().encryptionType.equals("caesar")) {
                if (!connection.supportsAES()) {
                    view.displayMessage("Not all other users support the selected type of encryption.");
                    return;
                }
            }
        }

        TextMessage message =
                new TextMessage(msText, activeChat.getSettings().getFontColor(), activeChat.getSettings().getUserName());

        activeChat.sendEncryptedMessage(message);
    }

    public void sendMessage(String msText) {
        Chat activeChat = model.getActiveChat();

        //If user presses send button but there are no chats, then let nothing hapṕen.
        if (activeChat == null) {
            return;
        }

        TextMessage message =
                new TextMessage(msText, activeChat.getSettings().getFontColor(), activeChat.getSettings().getUserName());

        activeChat.sendTextMessage(message);
        //Updates the text box with the message history.
        view.updateView(message);
    }

    public void requestConnection() {
        String IPstr = view.requestString("Which IP address/hostname do you want to connect to?");
        int port = view.requestNumber("What port do you want to connect to?", 0, 65535);
        String requestMesage = view.requestString("What message do you want to send?");
        Request request = new Request(requestMesage);
        Connection connection;

        try {
            InetAddress IPaddr = InetAddress.getByName(IPstr);
            Socket socket = new Socket(IPaddr, port);
            connection = new Connection(socket);
            connection.sendMessage(request);
        } catch (UnknownHostException e) {
            JOptionPane.showMessageDialog(null, "Cannot resolve the IP address, try again.");
            requestConnection();
            return;
        } catch (IOException e) {
            view.displayMessage("An I/O-error occurred when trying to create connection");
            return;
        }

        Thread conn_thread = new Thread(connection);
        conn_thread.start();
        model.addToChat(connection, model.createChat(false));

    }

    public void makeMultiPart() {
        Chat chat = model.getActiveChat();
        //Check that there are chats.
        if (chat == null) {
            JOptionPane.showMessageDialog(null, "There are no chats");
            return;
        }
        int reply = JOptionPane.showConfirmDialog(null, "Do you want to make current chat into multi-part?");
        if (reply == JOptionPane.YES_OPTION) {
            int port = view.requestNumber("Which port do you want to listen on?", 1500, 65535);
            model.createConnectionReceiver(port, chat);
        }
    }

    public void updateSettings() {
        Chat activeChat = model.getActiveChat();
        if (activeChat == null) {
            view.displayMessage("No chat to change settings in.");
            return;
        }

        String userName = view.requestString("What username do you want? " +
                "Leave empty if you want to keep current.");

        //If user chose a new username
        if (!userName.equals("")) {
            activeChat.getSettings().userName = userName;
        }
        Color color = JColorChooser.showDialog(null, "Choose a color", Color.RED);
        activeChat.getSettings().fontColor = color;

        String encryptionType = view.requestString("What encryption type do you want to use?"
                + "(Supported types are aes/caesar)");

        if (encryptionType.toLowerCase().equals("aes")) {
            activeChat.getSettings().encryptionType = "aes";
        } else if (encryptionType.toLowerCase().equals("caesar")){
            activeChat.getSettings().encryptionType = "caesar";
        }
    }

    public void closeActiveChat() {
        Chat chat = model.getActiveChat();
        model.closeChat(chat);
    }

    public void closeConnection() {

    }

    public void changeActiveChat(Chat chat) {
        model.setActiveChat(chat);

        view.updateWindows(model.getActiveChat().getMessageHistory());
    }

    public void sendFile() {

    }
}
