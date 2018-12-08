package Model;

import java.io.File;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class Chat implements Observer {
    ArrayList<Connection> connections;
    ChatSettings settings;
    ArrayList<TextMessage> messages;
    Connection fileConnection;
    private Model model;

    public Chat(ChatSettings settings, Model model) {
        this.connections = new ArrayList<Connection>();
        this.settings = settings;
        this.model = model;
        this.messages = new ArrayList<TextMessage>();
    }
    /*
    public Chat(Connection initialConnection) {

    }
    */
    public ChatSettings getSettings() {
        return settings;
    }

    void receiveMessage(Message message, Connection srcConnection){
        if (message instanceof TextMessage) {
            messages.add((TextMessage) message);
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

        model.notifyView(message);

        //TODO: Add support for other message types.
    }

    void addConnection(Connection connection) {
        connections.add(connection);
    }

    void closeConnection(Connection connection) {
        //Send disconnect message to other host
        connections.remove(connection);

        if (connections.isEmpty()) {
            model.removeChat(this);
        }
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

    public void sendTextMessage(String messageText) {
        TextMessage message = new TextMessage(messageText, null, settings.userName);
        for (Connection connection:connections) {
            connection.sendMessage(message);
        }
    }
    public void sendMessage(TextMessage textMessage){}
    public void sendMessage(Message message, Connection toConnection){
        toConnection.sendMessage(message);
    }
    public void sendFile(){}
}
