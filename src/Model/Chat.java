package Model;

import java.io.File;
import java.util.ArrayList;

public class Chat {
    Connection[] connections;
    ChatSettings settings;
    ArrayList<TextMessage> messages;
    Connection fileConnection;

    public Chat() {
    }

    public Chat(Connection[] connections, ChatSettings settings) {
        this.connections = connections;
        this.settings = settings;
        this.messages = new ArrayList<TextMessage>();
    }

    void receiveMessage(Message message, Connection srcConnection){
        if (message instanceof TextMessage) {
            messages.add((TextMessage) message);
        }

        //TODO: Add support for other message types.
    }
    public void sendMessage(TextMessage textMessage){}
    public void sendMessage(Message message, Connection toConnection){}
    public void sendFile(){}
}
