package Model;

import javax.crypto.IllegalBlockSizeException;
import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.net.Socket;
import java.security.Key;
import java.security.KeyException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class Chat implements Observer {
    ArrayList<Connection> connections;
    ChatSettings settings;
    ArrayList<TextMessage> messages;
    Model model;

    //False if we connected to a remote in this chat, true otherwise.
    private boolean isServer;

    public Chat(ChatSettings settings, Model model, boolean isServer) {
        this.connections = new ArrayList<Connection>();
        this.settings = settings;
        this.model = model;
        this.isServer = isServer;
        this.messages = new ArrayList<TextMessage>();
    }
    /*
    public Chat(Connection initialConnection) {

    }
    */
    public ChatSettings getSettings() {
        return settings;
    }
    public ArrayList<TextMessage> getMessageHistory() {
        return this.messages;
    }
    public ArrayList<Connection> getConnections() { return this.connections; }

    /**
     * Handles receiving of different message types. For textMessages, adds message to history and forwards messages
     * to other connections, for FileRequests, creates file response. For key request creates key response.
     * @param message message to receive
     * @param srcConnection connection which the message was received on.
     */
    void receiveMessage(Message message, Connection srcConnection) {
        if (message instanceof TextMessage) {
            messages.add((TextMessage) message);
            //Check if we have multipart conversation
            if (connections.size() > 1) {
                //Broadcast message to the others.
                for (Connection connection : connections) {
                    if (connection != srcConnection) {
                        connection.sendMessage(message);
                    }
                }
            }
        } else if (message instanceof KeyRequest) {
            KeyRequest keyRequest = (KeyRequest) message;
            if (keyRequest.type.toLowerCase().equals("aes")) {
                srcConnection.AESEncryption.generateKey();
                byte[] rawKey = srcConnection.AESEncryption.getLocalKey().getEncoded();

                KeyResponse response = new KeyResponse(rawKey, "AES");
                System.err.println(response.toXML(true));
                srcConnection.sendMessage(response);
            } else if (keyRequest.type.toLowerCase().equals("caesar")) {
                int caesarKey = srcConnection.caesarEncryption.generateRandomKey();
                KeyResponse response = new KeyResponse(caesarKey, "caesar");
                srcConnection.sendMessage(response);
            }
        } else if (message instanceof FileRequest) {
            long size = ((FileRequest)message).size;
            String messageStr = ((FileRequest)message).message;
            boolean reply = model.view.yesNoRequest("File request for file of size: " + size + "\nwith message: " + messageStr);
            String replyString = model.view.requestString("Input an answer message here");

            FileResponse fileResponse;
            if (reply) {
                fileResponse = new FileResponse(replyString, true, 10000);
                srcConnection.sendMessage(fileResponse);
                FileReceiver fileReceiver;
                if (((FileRequest) message).AESKey != null) {
                    byte[] rawKey = DatatypeConverter.parseHexBinary(((FileRequest) message).AESKey);

                    try {
                        srcConnection.AESEncryption.setKey(rawKey);
                        fileReceiver = new FileReceiver(srcConnection,10000, size, true);
                        fileReceiver.start();
                    } catch (IllegalBlockSizeException e) {
                        e.printStackTrace();
                        return;
                    }

                } else {
                    fileReceiver = new FileReceiver(srcConnection,10000, size, false);
                    fileReceiver.start();
                }
            } else {
                fileResponse = new FileResponse(replyString, false);
                srcConnection.sendMessage(fileResponse);
            }
        }

        //If this is the currently active chat, we display the message in the message panel.
        if (model.getActiveChat() == this) {
            model.notifyView(message);
        }

        //TODO: Add support for other message types.
    }


    /**
     * Adds a connection to this chats connection list
     * @param connection connection to add
     */
    void addConnection(Connection connection) {
        connections.add(connection);
    }


    /**
     * Closes a connection by removing it from connections list, if it is the last connection of this chat
     * remove the chat.
     * @param connection connection to close
     */
    public void closeConnection(Connection connection) {
        connections.remove(connection);

        if (connections.isEmpty()) {
            model.removeChat(this);
        }
    }

    /**
     * String representation of the current chat as the index the chat has in model.
     * Used for showing chats in JComboBox or similar.
     * @return The string representation of the chat
     */
    @Override
    public String toString() {
        int index = model.chats.indexOf(this);
        return "Chat " + (index + 1);
    }

    /**
     * Observer function notified by the connections when messages are received.
     * @param connection Connection that the message was received on
     * @param newMessage The received message
     */
    public void update(Observable connection, Object newMessage) {
        if (newMessage instanceof DisconnectMessage) {
            //Close the connection, clears view and removes the chat if this was the last connection
            this.closeConnection((Connection) connection);
            //Display a message for the user that the other side disconnected.
            model.notifyView((DisconnectMessage) newMessage);
        } else if (newMessage instanceof Message) {
            receiveMessage((Message) newMessage, (Connection) connection);
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Sends a text message to all connections.
     * @param textMessage Text message to send
     */

    public void sendTextMessage(TextMessage textMessage) {
        this.messages.add(textMessage);
        for (Connection connection:connections) {
            connection.sendMessage(textMessage);
        }
    }

    /**
     * Sends an encrypted text message to all connections.
     * @param textMessage Text message to send.
     */
    public void sendEncryptedMessage(TextMessage textMessage) {
        this.messages.add(textMessage);
        TextMessage originalMessage;
        for (Connection connection : connections) {
            //Pass copies of the textmessage because sendEncryptedMessage will modify its arguments.
            connection.sendEncryptedMessage(new TextMessage(textMessage));
        }
    }
}
