package Model;

import View.View;

import java.util.ArrayList;

public class Model {

    //Singleton since only one is needed.
    private static Model modelInstance = new Model();

    public static Model getInstance() {
        return modelInstance;
    }

    ArrayList<Chat> chats;
    Chat activeChat;
    ConnectionReceiver connectionReceiver;
    View view;

    private Model() {
        chats = new ArrayList<Chat>();
        view = new View(this);
    }


    public void createChat() {
        Chat newChat =  new Chat(new ChatSettings());
        chats.add(newChat);

        if (chats.size() == 1) {
            activeChat = chats.get(0);
        }
    }

    public void addToChat(Connection connection, Chat chat) {
        if (chats.contains(chat)) {
            chat.addConnection(connection);
        } else {
            throw new IllegalArgumentException("Model doesn't have this chat");
        }
    }

    void notifyView() {
        view.updateView();
    }
}
