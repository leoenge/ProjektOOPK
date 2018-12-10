package Model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.crypto.IllegalBlockSizeException;
import javax.management.modelmbean.XMLParseException;
import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyException;
import java.util.ArrayList;

//TODO: Add warning to the user that something went wrong if MessageFactory returns null.

public class MessageFactory {

    /** Parses incoming messages into the different parts it contains and returns a list of those message
     *  types.
     * @param inputStream The stream containing the message.
     * @return A list of messages, corresponding to each of the different tags the message contains.
     *
     * @throws XMLParseException if message is not correctly formatted
     */
    public static ArrayList<Message> messageFactory(InputStream inputStream, Connection srcConnection) throws XMLParseException {

        //Instantiate factory and DocumentBuilder
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        Document dom;
        ArrayList<Message> messages = new ArrayList<Message>();
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        //Parse the XML input stream.
        try {
            dom = db.parse(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            throw new XMLParseException("Cannot read input stream");
        } catch (SAXException e) {
            e.printStackTrace();
            throw new XMLParseException("Error when parsing XML");
        }

        //Apparently this is important.
        dom.getDocumentElement().normalize();

        Element firstTag = dom.getDocumentElement();

        if (firstTag.getTagName().equals("message")) {
            String username = firstTag.getAttribute("sender");
            if (firstTag.getElementsByTagName("disconnect").item(0) != null) {
                messages.add(new DisconnectMessage(username));
                return messages;
            }

            if (firstTag.getElementsByTagName("text").item(0) != null) {
                messages.add(createTextMessage((Element) firstTag.getElementsByTagName("text").item(0), username, srcConnection));
            }

            if (firstTag.getElementsByTagName("encrypted").item(0) != null) {
                messages.add(createKeyResponse((Element) firstTag.getElementsByTagName("encrypted").item(0)));
            }

            if (firstTag.getElementsByTagName("filerequest").item(0) != null) {
                messages.add(
                        createFileRequest((Element) firstTag.getElementsByTagName("filerequest").item(0), username));
            }

            if (firstTag.getElementsByTagName("fileresponse").item(0) != null) {
                messages.add(createFileResponse((Element) firstTag.getElementsByTagName("fileresponse").item(0)));
            }

            if (firstTag.getElementsByTagName("keyrequest").item(0) != null) {
                messages.add(createKeyRequest((Element) firstTag.getElementsByTagName("keyrequest").item(0)));
            }
        } else if (firstTag.getTagName().equals("request")) {
            messages.add(createRequest(firstTag));
        } else {
            throw new XMLParseException("First tag type not supported");
        }

        return messages;
    }

    private static TextMessage createTextMessage(Element textElement, String username, Connection srcConnection) throws XMLParseException {
        String text;
        if (textElement.getElementsByTagName("encrypted").item(0) != null) {
            Element encryptElement = (Element) textElement.getElementsByTagName("encrypted").item(0);
            text = decryptMessage(encryptElement, encryptElement.getTextContent(), srcConnection);
        } else {
            text = textElement.getTextContent();
        }

        String colorString = textElement.getAttribute("color");
        Color color;
        try {
            color = Color.decode(colorString);
        } catch (NumberFormatException e) {
            //If the color hex-string is badly formatted just set the color to null.
            color = null;
            System.err.println("Failed to decode color.");
        }

        TextMessage textMessage = new TextMessage(text, color, username);
        textMessage.unEscapeChars();

        return textMessage;
    }

    private static FileRequest createFileRequest(Element fileRequestElement, String username) throws XMLParseException {
        String fileName = fileRequestElement.getAttribute("name");
        String userText = fileRequestElement.getTextContent();
        String AESKey = null;
        String type = "";
        int caesarKey = 0;
        int fileSize;

        try {
            fileSize = Integer.parseInt(fileRequestElement.getAttribute("size"));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            throw new XMLParseException("Invalid file request received: file size not an integer");
        }

        if (fileRequestElement.hasAttribute("type") && fileRequestElement.hasAttribute("key")) {
            if (fileRequestElement.getAttribute("type").toUpperCase().equals("AES")) {
                type = "AES";
                AESKey = fileRequestElement.getAttribute("key");
            } else if (fileRequestElement.getAttribute("type").equals("caesar")) {
                try {
                    caesarKey = Integer.parseInt(fileRequestElement.getAttribute("key"));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    throw new XMLParseException("Invalid file request received: caesar key not an integer.");
                }
            }
        }

        return new FileRequest(userText, username, fileName, fileSize, type, AESKey, caesarKey);
    }

    private static KeyResponse createKeyResponse(Element keyResponseElement) throws XMLParseException{
        String type = keyResponseElement.getAttribute("type");
        byte[] rawKey = null;
        int caesarKey = 0;
        KeyResponse response;

        if (type.toLowerCase().equals("aes")) {
            String rawKeyHex = keyResponseElement.getAttribute("key");
            try {
                rawKey = DatatypeConverter.parseHexBinary(rawKeyHex);
                response = new KeyResponse(rawKey, type);
            } catch (IllegalArgumentException e) {
                throw new XMLParseException("Invalid cipher key received.");
            }
        } else if (type.toLowerCase().equals("caesar")) {
            try {
                caesarKey = Integer.parseInt(type);
                response = new KeyResponse(caesarKey, type);
            } catch (NumberFormatException e) {
                throw new XMLParseException("Invalid cipher key received.");
            }
        } else {
            throw new XMLParseException("Encryption type not supported");
        }

        return response;
    }

    private static FileResponse createFileResponse(Element fileResponseElement) throws XMLParseException {
        String replyString;
        boolean reply;
        int portNumber;
        String key;
        int caesarKey = 0;

        //Check for the required tags.
        if (fileResponseElement.hasAttribute("reply") && fileResponseElement.hasAttribute("port")) {
            replyString = fileResponseElement.getAttribute("reply");

            if (replyString.equals("yes")) {
                reply = true;
            } else if (replyString.equals("no")) {
                reply = false;
            } else {
                throw new XMLParseException("Invalid file response received: invalid reply type");
            }

            try {
                portNumber = Integer.parseInt(fileResponseElement.getAttribute("port"));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                throw new XMLParseException("Invalid file response received: invalid port number");
            }
        } else { //Else the message does not contain required attributes
            throw new XMLParseException("Invalid file response received: required attributes missing.");
        }

        key = fileResponseElement.getAttribute("key");

        return new FileResponse(reply, portNumber, key, caesarKey);

    }

    private static KeyRequest createKeyRequest(Element keyRequestElement) throws XMLParseException {
        String message = keyRequestElement.getTextContent();
        String type;

        if (keyRequestElement.hasAttribute("type")) {
            type = keyRequestElement.getAttribute("type");
        } else {
            throw new XMLParseException("Invalid key request received: no type attribute found");
        }

        if (type.toLowerCase().equals("aes") || type.toLowerCase().equals("caesar")) {
            return new KeyRequest(type, message);
        } else {
            throw new XMLParseException("Incoming encryption type not supported.");
        }
    }

    private static Request createRequest(Element requestElement) throws XMLParseException{
        String message = requestElement.getTextContent();
        boolean reply;

        //If request contains reply attribute, we need to see if the reply was affirmative or not
        if (requestElement.hasAttribute("reply")) {
            if (requestElement.getAttribute("reply").equals("yes")) {
                reply = true;
            } else if (requestElement.getAttribute("reply").equals("no")) {
                reply = false;
            } else {
                throw new XMLParseException("Malformed request received");
            }

            return new Request(reply, message);
        }

        //Else it was a new request containing only a message.
        else {
            return new Request(message);
        }
    }

    private static String decryptMessage(Element encryptElement, String message, Connection srcConnection) throws XMLParseException {
        if (encryptElement.getAttribute("type").toLowerCase().equals("aes")) {
            byte[] rawKey;
            try {
                rawKey = DatatypeConverter.parseHexBinary(encryptElement.getAttribute("key"));
            } catch (IllegalArgumentException e) {
                throw new XMLParseException("Couldn't parse AES key.");
            }

            try {
                srcConnection.AESEncryption.setKey(rawKey);
            } catch (IllegalBlockSizeException e) {
                throw new XMLParseException("AES key of incorrect size");
            }

            String decrypted;
            try {
                decrypted = srcConnection.AESEncryption.decrypt(message);
            } catch (KeyException e) {
                throw new XMLParseException("Key not found when attempting to decrypt incoming message");
            }

            return decrypted;
        }

        else if (encryptElement.getAttribute("type").toLowerCase().equals("caesar")) {
            int key;

            try {
                key = Integer.parseInt(encryptElement.getAttribute("key"));
            } catch (NumberFormatException e) {
                throw new XMLParseException("Could not parse a given caesar key");
            }

            srcConnection.caesarEncryption.setKey(key);
            String decrypted = srcConnection.caesarEncryption.decrypt(message);
            return decrypted;
        } else {
            throw new XMLParseException("Received bad encryption type.");
        }
    }
}
