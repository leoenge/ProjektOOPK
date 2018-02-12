package Model;

import java.io.File;

public class Chat {
    public Connection[] connections;
    public ChatSettings settings;
    public Message[] messages;
    public Connection fileConnection;

    public Chat() {
    }

    public Chat(Connection[] connections, ChatSettings settings) {
        this.connections = connections;
        this.settings = settings;
    }

    public void receiveMessage(Message message, Connection srcConnection){}
    public void sendMessage(TextMessage textMessage){}
    public void sendMessage(Message message, Connection toConnection){}
    public void sendFile(){}
}
