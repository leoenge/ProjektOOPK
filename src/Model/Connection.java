package Model;

import javax.management.modelmbean.XMLParseException;
import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
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
            ArrayList<Message> incMessages;

            while ((inputLine = socketReader.readLine()) != null) {
                //Create inputstream from bufferedreader.
                InputStream is = new ByteArrayInputStream(inputLine.getBytes(Charset.defaultCharset()));
                //Parse xml-message and create a Message instance.
                try {
                    incMessages = MessageFactory.messageFactory(is);
                    for (Message incMessage : incMessages) {
                        //Notifies chat that message has been received, so that view can be updated etc.
                        setChanged();
                        notifyObservers(incMessage);
                        clearChanged();
                    }
                } catch (XMLParseException e) {
                    e.printStackTrace();
                    return;
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }
    }
}

