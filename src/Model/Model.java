package Model;

public class Model {
    public Chat[] chats;
    public Chat activeChat;
    public ConnectionReceiver connectionReceiver;

    public Model() {
    }
    public void createChat(Connection connection){}
    public void addToChat(Connection connection, Chat chat){}
}
