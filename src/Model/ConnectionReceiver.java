package Model;

import Controller.Controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionReceiver implements Runnable {
    private ServerSocket openSocket;
    private int port;

    public ConnectionReceiver(int port) {
        this.port = port;
    }

    public void handleRequest(Socket socket) throws IOException {
        Connection connection = new Connection(socket);

        Chat chat;
        //Means other user has not implemented multi-part support aka B1.
        if (connection.socketReader.readLine() == null) {
            //askUser returns which chat user wants to add connection to, or null if user doesn't want to establish new connection.
            //If user wants to create a new chat with incoming connection, askUser will create a chat and return that chat.
            if ((chat = Controller.getInstance().askUser(connection, null)) != null) {
                Model.getInstance().addToChat(connection, chat);
            }
        } else {
            //TODO: Handle request message from other client.
        }
    }

    @Override
    public void run() {
        try {
            openSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Couldn't open server socket on port: " + port);
            System.out.println(e.getMessage());
            return;
        }

        try {
            Socket socket = openSocket.accept();
            handleRequest(socket);
        } catch (IOException e) {
            System.out.println("Someone tried to connect but it failed.");
            System.out.println(e.getMessage());
        }
    }
}
