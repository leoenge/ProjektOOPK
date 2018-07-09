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

    public Chat(ChatSettings settings) {
        this.connections = new ArrayList<Connection>();
        this.settings = settings;
        this.messages = new ArrayList<TextMessage>();
    }
    /*
    public Chat(Connection initialConnection) {

    }
    */

    void receiveMessage(Message message, Connection srcConnection){
        if (message instanceof TextMessage) {
            messages.add((TextMessage) message);
        }

        //TODO: Add support for other message types.
    }

    void addConnection(Connection connection) {
        connections.add(connection);
    }

    //Called when a connection receives a new message from the socket.
    public void update(Observable connection, Object newMessage) {
        if (newMessage instanceof Message && connection instanceof Connection) {
            receiveMessage((Message) newMessage, (Connection) connection);
            Model.getInstance().notifyView();
            return;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void sendMessage(TextMessage textMessage){}
    public void sendMessage(Message message, Connection toConnection){
        toConnection.sendMessage(message);
    }
    public void sendFile(){}
}
