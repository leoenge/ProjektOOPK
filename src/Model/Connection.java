package Model;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Connection implements Runnable {
    private Socket socket;
    public String IP;
    private PrintWriter socketWriter;
    private BufferedReader socketReader;

    public Connection(Socket socket){}
    public void sendMessage(String message){}

    @Override
    public void run() {

    }
}
