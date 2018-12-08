package Model;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import javax.management.modelmbean.XMLParseException;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Observable;

//TODO: Fixa felhantering om något går fel med inkommande meddelanden. Trådsäkerhet!

public class Connection extends Observable implements Runnable {
    private Chat chat;
    private Socket socket;
    public String IP;
    PrintWriter socketWriter;
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

    public void sendMessage(Message message) {
        socketWriter.println(message.toXML());
    }

    void setChat(Chat chat) {
        this.chat = chat;
    }

    void closeSocket() {
        socketWriter.println(new DisconnectMessage(chat.getSettings().userName).toXML());
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Something went wrong when interrupting the socket");
        }
    }

    @Override
    public void run() {
        listen();
    }

    void listen() {
        try {
            String inputLine;
            ArrayList<Message> incMessages;
            DisconnectMessage dcmessage = null;

            while ((inputLine = socketReader.readLine()) != null) {
                //Create inputstream from bufferedreader.
                InputStream is = new ByteArrayInputStream(inputLine.getBytes(Charset.defaultCharset()));
                //Parse xml-message and create a Message instance.
                try {
                    incMessages = MessageFactory.messageFactory(is);
                    if (incMessages.size() == 1 && incMessages.get(0) instanceof DisconnectMessage) {
                        dcmessage = (DisconnectMessage) incMessages.get(0);
                        break;
                    }

                    for (Message incMessage : incMessages) {
                        //Notifies chat that message has been received, so that view can be updated etc.
                        setChanged();
                        notifyObservers(incMessage);
                        clearChanged();
                    }
                } catch (XMLParseException e) {
                    //TODO:Notifiera användaren att konstigt XML-meddelande kommit in.
                    e.printStackTrace();
                }
            }
            //If we got here with no disconnect message, the remote connection was closed without the
            //other host sending disconnect. Then just create a placeholder disconnect message.
            if (dcmessage == null) {
                dcmessage = new DisconnectMessage("");
            }

            //Notifies chat that the connection has been closed here.
            setChanged();
            notifyObservers(dcmessage);
            clearChanged();
            socket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }
    }
}

