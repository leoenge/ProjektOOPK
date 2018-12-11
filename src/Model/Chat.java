package Model;

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
    Connection fileConnection;
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

    void receiveMessage(Message message, Connection srcConnection){
        if (message instanceof TextMessage) {
            messages.add((TextMessage) message);
        } else if (message instanceof KeyRequest) {
            KeyRequest keyRequest = (KeyRequest) message;
            if (keyRequest.type.toLowerCase().equals("aes")) {
                srcConnection.AESEncryption.generateKey();
                byte[] rawKey = srcConnection.AESEncryption.getLocalKey().getEncoded();

                KeyResponse response = new KeyResponse(rawKey, "AES");
                srcConnection.sendMessage(response);
            }
        }
        //Check if we have multipart conversation
        if (connections.size() > 1) {
            //Broadcast message to the others.
            for (Connection connection : connections) {
                if (connection != srcConnection) {
                    connection.sendMessage(message);
                }
            }
        }

        //If this is the currently active chat, we display the message in the message panel.
        if (model.getActiveChat() == this) {
            model.notifyView(message);
        }

        //TODO: Add support for other message types.
    }

    void addConnection(Connection connection) {
        connections.add(connection);
    }

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

    //Called when a connection receives a new message from the socket.
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

    public void sendTextMessage(TextMessage textMessage) {
        this.messages.add(textMessage);
        for (Connection connection:connections) {
            connection.sendMessage(textMessage);
        }
    }

    public void sendEncryptedMessage(TextMessage textMessage) {
        this.messages.add(textMessage);
        TextMessage originalMessage;
        for (Connection connection : connections) {
            //Pass copies of the textmessage because sendEncryptedMessage will modify its arguments.
            connection.sendEncryptedMessage(new TextMessage(textMessage));
        }
    }
    public void sendFile(){}
}
