package Model;

import View.View;
import Controller.Controller;
import jdk.nashorn.internal.scripts.JO;

import javax.swing.*;
import java.util.ArrayList;

import static javax.swing.JOptionPane.showInputDialog;

public class Model {
    public View view;

    ArrayList<Chat> chats;
    Chat activeChat;
    ConnectionReceiver connectionReceiver;
    ChatSettings default_settings;

    public Model() {
        chats = new ArrayList<Chat>();
        view = View.init_view(this);
        String username = view.requestString("What default username do you want?");
        int portNumber =
                view.requestNumber("What port number do you want to use? (1500 - 65535)", 1500, 65535);

        default_settings = new ChatSettings(username);
        createConnectionReceiver(portNumber);
        System.out.println("kek1");
    }

    public Chat createChat() {
        Chat newChat =  new Chat(default_settings, this);
        chats.add(newChat);

        if (chats.size() == 1) {
            activeChat = chats.get(0);
        }

        view.updateActiveChatBox(chats.size());

        return newChat;
    }

    public void addToChat(Connection connection, Chat chat) {
        if (chats.contains(chat)) {
            chat.addConnection(connection);
        } else {
            throw new IllegalArgumentException("Model doesn't have this chat");
        }
    }

    public void createConnectionReceiver(int port) {
        connectionReceiver = new ConnectionReceiver(port, this);
        connectionReceiver.run();
    }

    public Chat getActiveChat() {
        return activeChat;
    }

    void notifyView() {
        view.updateView();
    }
}
