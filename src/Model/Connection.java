package Model;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

//TODO: Fixa felhantering om något går fel med inkommande meddelanden.

public class Connection implements Runnable {
    private Chat chat;
    private Socket socket;
    public String IP;
    private PrintWriter socketWriter;
    private BufferedReader socketReader;

    public Connection(Socket socket, Chat chat) throws IOException {
        this.socket = socket;
        this.chat = chat;

        OutputStream socketOutStream;
        InputStream socketInStream;

        try {
            socketOutStream = socket.getOutputStream();
            socketInStream = socket.getInputStream();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new IOException();
        }

        socketWriter = new PrintWriter(socketOutStream);
        socketReader = new BufferedReader(new InputStreamReader(socketInStream));
    }

    public void sendMessage(String message) {
    }

    @Override
    public void run() {
        listen();
    }

    void sendMessage() {

    }

    void listen() {
        try {
            String inputLine;
            Message incMessage;

            while ((inputLine = socketReader.readLine()) != null) {
                //Create inputstream from bufferedreader.
                InputStream is = new ByteArrayInputStream(inputLine.getBytes(Charset.defaultCharset()));
                //Parse xml-message and create a Message instance.
                if ((incMessage = MessageFactory.messageFactory(is)) != null) {
                    chat.receiveMessage(incMessage, this);
                } else {
                    System.out.println("Something went wrong");
                    return;
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }
    }
}

