package Model;

public class Request extends Message {
    public boolean reply;

    public Request(String messageIn) {
        message = messageIn;
    }

    public Request(boolean replyIn, String messageIn) {
        reply = replyIn;
        message = messageIn;
    }
    @Override
    public String toXML(boolean escapeChars) {
        if (escapeChars) {
            this.escapeChars();
        }
        String outXML = "";

        if (!reply) {
            outXML += "<request reply=\"no\">" + message + "</request>";
        } else {
            outXML += "<request>" + message + "</request>";
        }

        return outXML;
    }
}
