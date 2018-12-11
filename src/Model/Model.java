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

    /**
     * @return The default user name of this model that the user set when the program started.
     */
    public String getDefaultUsername() {
        return defaultUsername;
    }

    /**
     * Creates a new chat in this model and notifies view so that view can be cleared.
     * @param isServer True if we are server for the new chat.
     * @return The created chat.
     */
    public Chat createChat(boolean isServer) {
        Chat newChat =  new Chat(new ChatSettings(defaultUsername), this, isServer);
        chats.add(newChat);

        if (chats.size() == 1) {
            activeChat = chats.get(0);
        }

        view.updateActiveChatBox(newChat);

        return newChat;
    }

    /**
     * Removes a Chat from this model.
     * @param chat The chat to be removed
     */
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

    /**
     * Closes a chat in this model, by sending disconnect messages to all connected hosts and closing the sockets.
     * @param chat
     */
    public void closeChat(Chat chat) {
        //Interrupts connection threads. This effectively closes the connection.
        for (Connection connection : chat.connections) {
            connection.closeSocket();
        }

        removeChat(chat);
    }

    /**
     * Adds a connection to an existing chat
     * @param connection The connection to add to a chat
     * @param chat The chat which the connection is to be added to
     */
    public void addToChat(Connection connection, Chat chat) {
        if (chats.contains(chat)) {
            chat.addConnection(connection);
            connection.addObserver(chat);
            connection.setChat(chat);
        } else {
            throw new IllegalArgumentException("Model doesn't have this chat");
        }
    }

    /**
     * Creates a new connection receiver on a new thread.
     * @param port The port which the connection receiver will listen to connections on.
     */
    public void createConnectionReceiver(int port) {
        connectionReceiver = new ConnectionReceiver(port, this);
        Thread t = new Thread(connectionReceiver);
        t.start();
    }

    /**
     * Creates a new connection receiver for an existing chat. The connection from this receiver will be added to
     * the chat.
     * @param port Port to listen on.
     * @param chat Chat to add the new connection to.
     */
    public void createConnectionReceiver(int port, Chat chat) {
        connectionReceiver = new ConnectionReceiver(port, this, chat);
        Thread t = new Thread(connectionReceiver);
        t.start();
    }

    /**
     * Gets the currently active chat, which is the chat the user has selected as active, and which is
     * shown to the user.
     * @return The currently active chat.
     */
    public Chat getActiveChat() {
        return activeChat;
    }

    /**
     * Sets the currently active chat, which should be the chat the user is currently shown.
     * @param chat The chat to set as the active chat.
     */
    public void setActiveChat(Chat chat) { this.activeChat = chat; }

    void notifyView(Message message) {
        view.updateView(message);
    }
    void displayMessage(String message) { view.displayMessage(message);}
}
