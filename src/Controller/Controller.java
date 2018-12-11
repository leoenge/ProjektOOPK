package Controller;

import Model.*;
import View.*;
import com.sun.org.apache.bcel.internal.classfile.Unknown;
import jdk.nashorn.internal.scripts.JO;
//import Model.*;

import javax.swing.*;
import javax.xml.bind.DatatypeConverter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
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

    /**
     * Queries the user when a new connection is received if the connection is to be allowed or not.
     * @param request The request message from the new connection, can be null.
     * @return The users response, true if allowed, false otherwise.
     */
    public boolean askUser(Request request) {
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

    /**
     * Sends an encrypted text message to the currently active chat from the content specified in msText.
     * @param msText The text conetnt of the message.
     */
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
                if (!connection.supportsCaesar()) {
                    view.displayMessage("Not all other users support the selected type of encryption.");
                    return;
                }
            }
        }

        TextMessage message =
                new TextMessage(msText, activeChat.getSettings().getFontColor(), activeChat.getSettings().getUserName());
        //Update the message history.
        view.updateView(message);
        //Encrypt the message and send it.
        activeChat.sendEncryptedMessage(message);
    }

    /**
     * Sends an unecncrypted text message to the currently active chat.
     * @param msText The text content of the message.
     */
    public void sendMessage(String msText) {
        Chat activeChat = model.getActiveChat();

        //If user presses send button but there are no chats, then let nothing hapṕen.
        if (activeChat == null) {
            return;
        }

        TextMessage message =
                new TextMessage(msText, activeChat.getSettings().getFontColor(), activeChat.getSettings().getUserName());
        //Updates the text box with the message history.
        view.updateView(message);
        activeChat.sendTextMessage(message);
    }

    /**
     * Lets the user select an IP-address/hostname and port to connect to with a request.
     */
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

    /**
     * Makes the currently active chat into a multi part chat. Lets the user select a port to listen to new
     * connections to this chat on.
     */
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

    /**
     * Lets the user update the setting for the currently active chat.
     */
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

    /**
     * Closes down the currently active chat and disconnects from the hosts connected.
     */
    public void closeActiveChat() {
        Chat chat = model.getActiveChat();
        model.closeChat(chat);
    }

    /**
     * Closes down a specific connection in a chat.
     * @param connection The connection to close.
     */
    public void closeConnection(Connection connection) {
        connection.closeSocket();
        model.getActiveChat().closeConnection(connection);
    }

    /**
     * Changes the currently active chat-
     * @param chat The chat to make the new active chat.
     */
    public void changeActiveChat(Chat chat) {
        model.setActiveChat(chat);

        view.updateWindows(model.getActiveChat().getMessageHistory());
    }

    /**
     * Handles the sending of a file by letting the user select a file of a GUI, sending the
     * file request to the remote host and sending the file if the request is accepted.
     * @param connection
     */
    public void sendFile(Connection connection) {
        File file = view.requestFile();
        if (file == null) {
            return;
        }
        long size = file.length();
        String name = file.getName();

        String messageStr = view.requestString("Include a message in file request.");
        connection.fileRequestHandler = new FileRequestHandler(file, connection, false);
        FileRequest fileRequest = new FileRequest(messageStr, model.getActiveChat().getSettings().userName, name, size);
        connection.sendMessage(fileRequest);
        connection.waitingForFileResponse = true;
    }

    /**
     * Handles sending a file but with its data encrypted using AES.
     * @param connection The connection to send the file on.
     */

    public void sendEncryptedFile(Connection connection) {
        File file = view.requestFile();
        if (file == null) {
            return;
        }

        if (!connection.supportsAES()) {
            view.displayMessage("Other side does not support encryption");
        }

        long size = file.length();
        String name = file.getName();

        String messageStr = view.requestString("Include a message in file request");

        String keyString = DatatypeConverter.printHexBinary(connection.AESEncryption.getLocalKey().getEncoded());

        connection.fileRequestHandler = new FileRequestHandler(file, connection, true);
        FileRequest fileRequest = new FileRequest(messageStr, model.getActiveChat().getSettings().userName,
                name, size, "aes", keyString);

        connection.sendMessage(fileRequest);
    }
}
