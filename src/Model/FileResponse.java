package Model;

public class FileResponse extends Message {
    public boolean reply;
    public int port;
    public String AESkey;
    public int caesarKey;

    FileResponse(boolean replyIn, int portIn, String AESkeyIn, int caesarKeyIn) {
        reply = replyIn;
        port = portIn;
        AESkey = AESkeyIn;
        caesarKey = caesarKeyIn;
    }

    @Override
    public String toXML() {
        return null;
    }
}
