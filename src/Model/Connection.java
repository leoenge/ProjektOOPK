package Model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Connection implements Runnable {
    private Socket socket;
    public String IP;
    private PrintWriter socketWriter;
    private BufferedReader socketReader;

    public Connection(Socket socketIn, String IPIn){
        socket = socketIn;
        IP = IPIn;
        try {
            socketWriter = new PrintWriter(socket.getOutputStream(), true);
            socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        catch (IOException e){
            System.out.println("Something went wrong in connection " + e);
        }
    }
    public void sendMessage(String message){}

    @Override
    public void run() {

    }
}
