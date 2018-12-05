package Model;

import Controller.Controller;

import javax.management.modelmbean.XMLParseException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ConnectionReceiver implements Runnable {
    private ServerSocket openSocket;
    private int port;
    private Model model;

    public ConnectionReceiver(int port, Model model) {
        this.model = model;
        this.port = port;
    }

    public void handleRequest(Socket socket) throws IOException {
        Connection connection = new Connection(socket);
        Request request = null;

        if (connection.socketReader.ready()) {
            //Reads the stream to see if there is a request message there.
            String requestStr = connection.socketReader.readLine();

            //If there is a message there we read it and see if it is of correct type.
            if (requestStr != null) {
                InputStream is = new ByteArrayInputStream(requestStr.getBytes());
                ArrayList<Message> messages = new ArrayList<Message>();
                try {
                    messages = MessageFactory.messageFactory(is);
                } catch (XMLParseException e) {
                }
                //Check that there is only one request message.
                if (messages.size() == 1 && messages.get(0) instanceof Request) {
                    request = (Request) messages.get(0);
                } else {
                    request = null;
                }
            } else {
                request = null;
            }
        }
        //askUser returns which chat user wants to add connection to, or null if user doesn't want to establish new connection.
        //If user wants to create a new chat with incoming connection, askUser will create a chat and return that chat.
        if (Controller.getInstance().askUser(request)) {
            model.addToChat(connection, model.createChat());
            //Create a new connectionreceiver so that we are always ready for new connection attempts.
            model.connectionReceiver = new ConnectionReceiver(0, model);
        } else if (request == null) { //When user answers no and connection attempt was from simpler client
            connection.sendMessage(new TextMessage("Connection refused", "",
                    model.default_settings.userName));
            socket.close();
        } else { //When user answers no and connection attempt was from B1-implementing lient
            connection.sendMessage(new Request(false, ""));
            socket.close();
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
