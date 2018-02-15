package Model;

public class FileRequest extends Message {
    public String fileName;
    public int size;

    FileRequest(String messageIn, String fileNameIn, int sizeIn) {
        message = messageIn;
        fileName = fileNameIn;
        size = sizeIn;
    }

    @Override
    public String toXML() {

        return null;
    }
}
