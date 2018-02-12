package Model;

import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionReceiver implements Runnable {
    private ServerSocket openSocket;
    private int port;

    public ConnectionReceiver(int port) {
        this.port = port;
    }

    public void handleRequest(Socket socket){}

    @Override
    public void run() {

    }
}
