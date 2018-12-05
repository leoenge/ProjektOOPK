package Model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.management.modelmbean.XMLParseException;
import javax.xml.parsers.*;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

//TODO: Add warning to the user that something went wrong if MessageFactory returns null.
//TODO: Create support for disconnect messages.

public class MessageFactory {

    /** Parses incoming messages into the different parts it contains and returns a list of those message
     *  types.
     * @param inputStream The stream containing the message.
     * @return A list of messages, corresponding to each of the different tags the message contains.
     *
     * @throws XMLParseException if message is not correctly formatted
     */
    public static ArrayList<Message> messageFactory(InputStream inputStream) throws XMLParseException{

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
            if (firstTag.getElementsByTagName("text").item(0) != null) {
                messages.add(createTextMessage((Element) firstTag.getElementsByTagName("text").item(0)));
            }

            if (firstTag.getElementsByTagName("filerequest").item(0) != null) {
                messages.add(createFileRequest((Element) firstTag.getElementsByTagName("filerequest").item(0)));
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

    private static Message createTextMessage(Element textElement) throws XMLParseException{

        String text = null;
        StringBuilder encryptedTextSb = new StringBuilder();
        String name = textElement.getAttribute("sender");
        NodeList childNodes = textElement.getElementsByTagName("text");
        NodeList encryptedNodes  = textElement.getElementsByTagName("encrypted");
        Node textTag;
        Node encryptedTag = null;

        if ((textTag = childNodes.item(0)) == null) {
            throw new XMLParseException("Malformed text message received.");
        }

        if (encryptedNodes.item(0) != null) {
            for (int i = 0; i < encryptedNodes.getLength(); i++) {
                encryptedTag = encryptedNodes.item(i);
                encryptedTextSb.append(encryptedTag.getTextContent());
            }
        }

        String encryptedText = encryptedTextSb.toString();
        text = textTag.getTextContent();

        return new TextMessage(text, encryptedText, name);

    }

    private static Message createFileRequest(Element fileRequestElement) throws XMLParseException {
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

        return new FileRequest(userText, fileName, fileSize, type, AESKey, caesarKey);
    }

    private static Message createFileResponse(Element fileResponseElement) throws XMLParseException {
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

    private static Message createKeyRequest(Element keyRequestElement) throws XMLParseException {
        String message = keyRequestElement.getNodeValue();
        String encryptionString;
        Encryption encryption;

        if (keyRequestElement.hasAttribute("type")) {
            encryptionString = keyRequestElement.getAttribute("type");
        } else {
            throw new XMLParseException("Invalid key request received: no type attribute found");
        }

        switch (encryptionString.toLowerCase()) {
            case "rsa":
                encryption = new RSAEncryption();
                break;
            case "caesar":
                encryption = new CeasarEncryption();
                break;
            case "aes":
                encryption = new AESEncryption();
                break;
            default:
                throw new XMLParseException("key request error: encryption type not supported");
        }

        return new KeyRequest(encryption, message);
    }

    private static Message createRequest(Element requestElement) {
        String message = requestElement.getNodeValue();
        boolean reply;

        //If request contains reply attribute, we need to see if the reply was affirmative or not
        if (requestElement.hasAttribute("reply")) {
            if (requestElement.getAttribute("reply").equals("yes")) {
                reply = true;
            }

            else if (requestElement.getAttribute("reply").equals("no")) {
                reply = false;
            }

            else {
                return null;
            }

            return new Request(reply, message);
        }

        //Else it was a new request containing only a message.
        else {
            return new Request(message);
        }
    }
}
