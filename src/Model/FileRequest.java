package Model;

public class FileRequest extends Message {
    public String fileName;
    public long size;
    public String type = null;
    public String AESKey = null;

    public FileRequest(String message, String senderName,
                String fileName, long size, String type, String AESKey) {
        this.senderName = senderName;
        this.message = message;
        this.fileName = fileName;
        this.size = size;
        this.AESKey = AESKey;
        this.type = type;
    }

    public FileRequest(String message, String senderName,
                String fileName, long size) {
        this.senderName = senderName;
        this.message = message;
        this.fileName = fileName;
        this.size = size;
    }
    @Override
    public String toXML(boolean escapeChars) {
        if (escapeChars) {
            this.escapeChars();
        }
        String res = "";

        if (this.senderName == null) {
            res += "<message>";
        } else {
            res += "<message sender=\"" + this.senderName + "\">";
        }

        if (this.AESKey == null) {
            res += "<filerequest name=\"" + this.fileName + "\" size=\"" + size + "\">";
        } else {
            res += "<filerequest name=\"" + this.fileName + "\" size=\"" + size + "\" " +
                    "type=\"" + this.type + "\" key=\"" + AESKey + "\">";
        }

        res += message + "</filerequest></message>";
        return res;
    }
}
