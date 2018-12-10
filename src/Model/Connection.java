package Model;

import com.sun.crypto.provider.*;

import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;
import javax.management.modelmbean.XMLParseException;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.security.Key;
import java.security.KeyException;
import java.util.ArrayList;
import java.util.Observable;

//TODO: Fixa felhantering om något går fel med inkommande meddelanden. Trådsäkerhet!

public class Connection extends Observable implements Runnable {
    private Chat chat;
    private Socket socket;
    PrintWriter socketWriter;
    BufferedReader socketReader;
    AESEncryption AESEncryption = null;
    CaesarEncryption caesarEncryption = null;

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
        socketWriter.println(message.toXML(true));
    }
    public void sendEncryptedMessage(Message message) {
        //Escape xml-syntax in the text body before encryption.
        message.escapeChars();

        String text = ((TextMessage) message).message;
        if (chat.getSettings().encryptionType.toLowerCase().equals("aes")) {
            //If no local key has been initialized, generate a new one.
            if (AESEncryption.getLocalKey() == null) {
                AESEncryption.generateKey();
            }

            String encrypted;
            try {
                encrypted = AESEncryption.encrypt(text);
            } catch (KeyException e) {
                System.err.println("No key found");
                return;
            }

            message.message = encrypted;
            //Creates the hex representation of the key.
            String keyHex = DatatypeConverter.printHexBinary(AESEncryption.getLocalKey().getEncoded());
            message.addEncryptionTags(keyHex, "AES");
            //Convert the message to XML, but don't escape characters in the message part, since they are just
            //Hex code anyway, and the encryption tags are there.
            socketWriter.println(message.toXML(false));
        } else if (chat.getSettings().encryptionType.toLowerCase().equals("caesar")) {

        }


    }
    public void sendNewKeyRequest(String type, String message) throws IllegalStateException {
        KeyRequest keyRequest = new KeyRequest(type, message);
        sendMessage(keyRequest);
    }
    public boolean supportsAES() { return AESEncryption != null; }
    public boolean supportsCaesar() { return caesarEncryption != null; }

    public InetAddress getRemoteAddress() {
        return socket.getInetAddress();
    }

    void setChat(Chat chat) {
        this.chat = chat;
    }

    void closeSocket() {
        socketWriter.println(new DisconnectMessage(chat.getSettings().userName).toXML(true));
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

    private void listen() {
        try {
            String inputLine;
            ArrayList<Message> incMessages;
            DisconnectMessage dcmessage = null;

            /* We send key requests when we start this connection to test for compatibility of encryptions. *
             * We wait one minute for response. If we get a response within one minute we setup instances   *
             * of the corresponding encryption instances. If not, these instances will be null and the      *
             * encryption type will be considered unsupported.                                              */
            long startTime = System.currentTimeMillis();
            sendNewKeyRequest("aes", "");
            sendNewKeyRequest("caesar", "");

            while ((inputLine = socketReader.readLine()) != null) {
                //Create inputstream from bufferedreader.
                InputStream is = new ByteArrayInputStream(inputLine.getBytes(Charset.defaultCharset()));
                //Parse xml-message and create a Message instance.
                try {
                    incMessages = MessageFactory.messageFactory(is, this);
                    if (incMessages.size() == 1 && incMessages.get(0) instanceof DisconnectMessage) {
                        dcmessage = (DisconnectMessage) incMessages.get(0);
                        break;
                    }

                    for (Message incMessage : incMessages) {
                        //Check for aforementioned key response.
                        if (incMessage instanceof KeyResponse && System.currentTimeMillis() - startTime < 60000) {
                            if (((KeyResponse) incMessage).type.toLowerCase().equals("aes")) {
                                AESEncryption = new AESEncryption();
                                try {
                                    AESEncryption.setKey(((KeyResponse) incMessage).rawKey);
                                } catch (IllegalBlockSizeException e) {
                                    AESEncryption = null;
                                }

                                continue;
                            }

                            else if (((KeyResponse) incMessage).type.toLowerCase().equals("caesar")) {
                                caesarEncryption = new CaesarEncryption();
                                int key = ((KeyResponse) incMessage).caesarKey;
                                caesarEncryption.setKey(key);
                                continue;
                            }
                        }
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

