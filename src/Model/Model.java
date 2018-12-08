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

    void removeChat(Chat chat) {
        int chatIndex = chats.indexOf(chat);
        chats.remove(chat);
        view.removeChat(chatIndex);
    }

    public void closeChat(Chat chat) {
        //Interrupts connection threads. This effectively closes the connection.
        for (Connection connection : chat.connections) {
            connection.closeSocket();
        }

        removeChat(chat);
    }

    public void addToChat(Connection connection, Chat chat) {
        if (chats.contains(chat)) {
            chat.addConnection(connection);
            connection.addObserver(chat);
            connection.setChat(chat);
        } else {
            throw new IllegalArgumentException("Model doesn't have this chat");
        }
    }

    public void createConnectionReceiver(int port) {
        connectionReceiver = new ConnectionReceiver(port, this);
        Thread t = new Thread(connectionReceiver);
        t.start();
    }

    public void createConnectionReceiver(int port, Chat chat) {
        connectionReceiver = new ConnectionReceiver(port, this, chat);
        Thread t = new Thread(connectionReceiver);
        t.start();
    }

    public Chat getActiveChat() {
        return activeChat;
    }

    void notifyView(Message message) {
        view.updateView(message);
    }
}
