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

//TODO: Add warning to the user that something went wrong if MessageFactory returns null.
//TODO: Create support for disconnect messages.

public class MessageFactory {

    static Message messageFactory(InputStream inputStream){

        //Instantiate factory and DocumentBuilder
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        Document dom = null;
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
            return null;
        } catch (SAXException e) {
            e.printStackTrace();
            return null;
        }

        //Apparently this is important.
        dom.getDocumentElement().normalize();

        Element firstTag = dom.getDocumentElement();

        //Check if tag is a TextMessage.
        switch (firstTag.getTagName().toLowerCase()) {
            case "message":
                return createTextMessage(firstTag);
            case "filerequest":
                return createFileRequest(firstTag);
            case "fileresponse":
                return createFileResponse(firstTag);
            case "keyrequest":
                return createKeyRequest(firstTag);
            case "request":
                return createRequest(firstTag);
            default:
                return null;
        }

        /*
        if (firstTag.getTagName().equals("message")) {
            return createTextMessage(firstTag);
        }

        else if (firstTag.getTagName().equals("filerequest")) {
            return createFileRequest(firstTag);
        }

        else if (firstTag.getTagName().equals("fileresponse")) {
            return createFileResponse(firstTag);
        }
        */
    }

    private static Message createTextMessage(Element textElement) {

        String text = null;
        String encryptedText = null;
        String name = textElement.getAttribute("name");

        NodeList childNodes = textElement.getChildNodes();
        Node textTag;

        try {
            textTag = childNodes.item(0);
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }

        //The only child should be <text></text>, if it is not the message is not correctly formatted.
        //and we return null.
        if (textTag.getNodeName().equals("text") && textTag.getChildNodes().getLength() == 1) {

            //Check for encryption tag.
            if (textTag.hasChildNodes()) {
                //If it has children, there should be only one child which should be the encrypted tag.
                //If it isn't, we return null
                if (textTag.getChildNodes().getLength() == 1
                        && textTag.getFirstChild().getNodeName().equals("encrypted")) {
                    encryptedText = textTag.getFirstChild().getNodeValue();
                } else {
                    return null;
                }
            }

            text = textTag.getNodeValue();

        } else {
            return null;
        }


        return new TextMessage(text, encryptedText, name);

    }

    private static Message createFileRequest(Element fileRequestElement) {
        String fileName = fileRequestElement.getAttribute("name");
        String userText = fileRequestElement.getNodeValue();
        String AESKey = null;
        int caesarKey = 0;
        int fileSize;

        try {
            fileSize = Integer.parseInt(fileRequestElement.getAttribute("size"));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }

        if (fileRequestElement.hasAttribute("type") && fileRequestElement.hasAttribute("key")) {
            if (fileRequestElement.getAttribute("type").equals("AES")) {
                AESKey = fileRequestElement.getAttribute("key");
            } else if (fileRequestElement.getAttribute("type").equals("caesar")) {
                try {
                    caesarKey = Integer.parseInt(fileRequestElement.getAttribute("key"));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }

        return new FileRequest(userText, fileName, fileSize, AESKey, caesarKey);
    }

    private static Message createFileResponse(Element fileResponseElement) {
        String replyString;
        boolean reply;
        int portNumber;
        String AESkey = null;
        int caesarKey = 0;

        if (fileResponseElement.hasAttribute("reply") && fileResponseElement.hasAttribute("port")) {
            replyString = fileResponseElement.getAttribute("reply");

            if (replyString.equals("yes")) {
                reply = true;
            } else if (replyString.equals("no")) {
                reply = false;
            } else {
                return null;
            }

            try {
                portNumber = Integer.parseInt(fileResponseElement.getAttribute("port"));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return null;
            }

        } else {
            return null;
        }

        if (fileResponseElement.hasAttribute("key")) {
            AESkey = fileResponseElement.getAttribute("key");
        } else {
            return null;
        }

        return new FileResponse(reply, portNumber, AESkey, caesarKey);

    }

    private static Message createKeyRequest(Element keyRequestElement) {
        String message = keyRequestElement.getNodeValue();
        String encryptionString;
        Encryption encryption;

        if (keyRequestElement.hasAttribute("type")) {
            encryptionString = keyRequestElement.getAttribute("type");
        } else {
            return null;
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
                return null;
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
