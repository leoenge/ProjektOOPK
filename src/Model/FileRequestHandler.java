package Model;

import java.io.*;
import java.net.Socket;

public class FileRequestHandler extends Thread {
    public int port;
    public Connection connection;
    private final long startTime;
    private final boolean encrypt;
    private final File file;

    public FileRequestHandler(File file, Connection connection, boolean encrypt) {
        startTime = System.currentTimeMillis();
        this.encrypt = encrypt;
        this.connection = connection;
        this.file = file;
    }

    long timeElapsed() {
        return System.currentTimeMillis() - startTime;
    }

    private void sendFile() {
        Socket socket;
        try {
            socket = new Socket(connection.getRemoteAddress(), port);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        OutputStream socketOutStream;
        byte[] buffer = new byte[(int) file.getTotalSpace()];

        try {
            socketOutStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        int numberOfBytesRead;
        try {
            while ((numberOfBytesRead = fileInputStream.read(buffer)) > 0) {
                socketOutStream.write(buffer, 0, numberOfBytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

    }

    @Override
    public void run() {
        sendFile();
    }
}
