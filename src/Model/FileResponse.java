package Model;

public class FileResponse extends Message {
    public boolean reply;
    public int port;

    FileResponse(String message, boolean reply, int port) {
        this.message = message;
        this.reply = reply;
        this.port = port;
    }

    FileResponse(String message, boolean reply) {
        this.message = message;
        this.port = -1;
        this.reply = reply;
    }

    @Override
    public String toXML(boolean escapeChars) {
        if (escapeChars) {
            this.escapeChars();
        }

        String res = "";
        String replyString = reply ? "yes" : "no";

        if (senderName != null) {
            res += "<message sender=\"" + senderName + "\">";
        } else {
            res += "<message>";
        }
        res += "<fileresponse reply=\"" + replyString + "\" port=\"" + port + "\"></fileresponse></message>";
        return res;

    }
}
