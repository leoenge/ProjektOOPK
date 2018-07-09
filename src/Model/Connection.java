package Model;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Observable;

//TODO: Fixa felhantering om något går fel med inkommande meddelanden. Trådsäkerhet!

public class Connection extends Observable implements Runnable {
    private Chat chat;
    private Socket socket;
    public String IP;
    private PrintWriter socketWriter;
    BufferedReader socketReader;

    public Connection(Socket socket) throws IOException{
        this.socket = socket;

        OutputStream socketOutStream;
        InputStream socketInStream;

        try {
            socketOutStream = socket.getOutputStream();
            socketInStream = socket.getInputStream();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new IOException();
        }

        socketWriter = new PrintWriter(socketOutStream, true);
        socketReader = new BufferedReader(new InputStreamReader(socketInStream));
    }


    //Den här konstruktorn kanske är onödig?
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

        socketWriter = new PrintWriter(socketOutStream, true);
        socketReader = new BufferedReader(new InputStreamReader(socketInStream));

        //The chat is notified when new messages are received over the soccet.
        addObserver(chat);
    }

    public void sendMessage(Message message) {
        socketWriter.println(message.toXML());
    }

    @Override
    public void run() {
        listen();
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
                    //Notifies chat that message has been received, so that view can be updated etc.
                    setChanged();
                    notifyObservers(incMessage);
                    clearChanged();
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

