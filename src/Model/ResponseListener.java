package Model;

import java.util.Observable;
import java.util.Observer;

public class ResponseListener extends Thread implements Observer {
    private Connection connection;
    private String type;
    private boolean supported;

    ResponseListener(Connection connection, String type) {
        this.connection = connection;
        this.type = type;
    }

    public void update(Observable srcConnection, Object Message) {
        if (srcConnection instanceof Connection && Message instanceof KeyResponse) {
            if (((Connection) srcConnection).AESEncryption == null) {
                ((Connection) srcConnection).AESEncryption = new AESEncryption();
            }
        }
    }
    @Override
    public void run() {
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            System.err.println(e.getStackTrace());
        }
    }
}
