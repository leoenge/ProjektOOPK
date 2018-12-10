package Model;

public class FileRequest extends Message {
    public String fileName;
    public int size;
    public String type;
    public String AESKey;
    public int caesarKey;

    FileRequest(String message, String senderName,
                String fileName, int size, String type, String AESKey, int caesarKey) {
        this.senderName = senderName;
        this.message = message;
        this.fileName = fileName;
        this.size = size;
        this.AESKey = AESKey;
        this.caesarKey = caesarKey;
        this.type = type;

    }

    @Override
    public String toXML(boolean escapeChars) {
        return null;
    }
}
