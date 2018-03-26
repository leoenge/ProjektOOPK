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
    public String toXML() {
        return null;
    }
}
