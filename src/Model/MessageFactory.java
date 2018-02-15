package Model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;


//TODO: Add warning to the user that something went wrong if MessageFactory returns null.

public class MessageFactory {
    /*
    private String fileReqRegex = "<filerequest filename=\"(.+)\" size=\"(.+)\">(.+)<\\/filerequest>";
    private String fileRespRegex = "<fileresponse reply=\"([a-z]+)\" port=\"\\d+\">(.+)<\\/fileresponse>";
    private String
    */

    Message messageFactory(InputStream inputStream) {

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
        if (firstTag.getTagName().equals("message")) {
            String text = null;
            String encryptedText = null;
            String name = firstTag.getAttribute("name");

            NodeList childNodes = firstTag.getChildNodes();
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

        else if (firstTag.getTagName().equals("filerequest")) {
            String fileName;
            int portNumber;
        }
    }
}
