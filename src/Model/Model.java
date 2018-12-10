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
    private String defaultUsername;

    public Model() {
        chats = new ArrayList<Chat>();
        view = View.init_view(this);
        defaultUsername = view.requestString("What default username do you want?");
        int portNumber =
                view.requestNumber("What port number do you want to use? (1500 - 65535)", 1500, 65535);

        createConnectionReceiver(portNumber);
    }

    public String getDefaultUsername() {
        return defaultUsername;
    }

    public Chat createChat(boolean isServer) {
        Chat newChat =  new Chat(new ChatSettings(defaultUsername), this, isServer);
        chats.add(newChat);

        if (chats.size() == 1) {
            activeChat = chats.get(0);
        }

        view.updateActiveChatBox(newChat);

        return newChat;
    }

    void removeChat(Chat chat) {
        chats.remove(chat);
        view.removeChat(chat);

        //Set a new active chat and updates the message history window. If there are no more active chats,
        //set the new active chat to null.
        if (chats.size() > 0) {
            activeChat = chats.get(0);
            //Adds the message history of the new chat to the message history pane.
            view.updateWindows(activeChat.getMessageHistory());
        } else {
            activeChat = null;
            //Clear the chat windows from text.
            view.clearWindows();
        }

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
    public void setActiveChat(Chat chat) { this.activeChat = chat; }

    void notifyView(Message message) {
        view.updateView(message);
    }
    void displayMessage(String message) { view.displayMessage(message);}
}
