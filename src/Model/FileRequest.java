package Model;

public class FileRequest extends Message {
    public String fileName;
    public int size;
    public String type;
    public String AESKey;
    public int caesarKey;

    FileRequest(String messageIn, String fileNameIn, int sizeIn, String AESKeyIn, int caesarKeyIn) {
        message = messageIn;
        fileName = fileNameIn;
        size = sizeIn;
        AESKey = AESKeyIn;
        caesarKey = caesarKeyIn;
    }

    @Override
    public String toXML() {

        return null;
    }
}
