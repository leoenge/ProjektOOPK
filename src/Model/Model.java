package Model;

import View.View;

import java.util.ArrayList;

public class Model {

    static Model modelInstance = new Model();

    ArrayList<Chat> chats;
    Chat activeChat;
    ConnectionReceiver connectionReceiver;
    View view;

    private Model() {
        chats = new ArrayList<Chat>();
        view = new View(this);
    }
    public void createChat(Connection connection) {
        Connection[] connectionArr = new Connection[] {connection};
        chats.add(new Chat(connectionArr, ChatSettings.getInstance());

        if (chats.size() == 1) {
            activeChat = chats.get(0);
        }
    }
    public void addToChat(Connection connection, Chat chat){}
}
