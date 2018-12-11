package Model;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;
import java.security.KeyException;

public class FileReceiver extends Thread{
    int port;
    long size;
    ServerSocket serverSocket;
    Socket socket;
    Connection srcConnection;
    boolean encrypted;

    FileReceiver(Connection srcConnection, int port, long size, boolean encrypted) {
        this.port = port;
        this.size = size;
        this.encrypted = encrypted;
        this.srcConnection = srcConnection;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a server socket to listen for connections and handles the reception of a file over the
     * sockets inputstream.
     */
    public void run() {
        try {
            socket = serverSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        File targetFile = new File("/home/felix/Desktop/test.txt");
        FileOutputStream fileOutputStream;

        try {
            fileOutputStream = new FileOutputStream(targetFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        byte[] buffer = new byte[(int) size];

        //Set up progress bar.
        JFrame frame = new JFrame("Progress");
        JProgressBar progressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, (int) size);
        progressBar.setValue(0);
        frame.add(progressBar);
        frame.setSize(new Dimension(300, 100));
        frame.setVisible(true);

        int totalBytesRead = 0;
        int numberOfBytesRead;
        try {
            while ((numberOfBytesRead = socket.getInputStream().read(buffer)) > 0) {
                totalBytesRead += numberOfBytesRead;
                //Swingutilities invokeLater requires a final variable.
                final int tmpTotalBytesRead = totalBytesRead;
                SwingUtilities.invokeLater(
                        new Runnable() {
                            public void run() {
                                progressBar.setValue(tmpTotalBytesRead);
                                frame.repaint();
                            }
                        });
            }

            frame.dispose();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        if (encrypted) {
            try {
                srcConnection.AESEncryption.decrypt(buffer);
            } catch (KeyException e) {
                e.printStackTrace();
                return;
            }
        }


        try {
            fileOutputStream.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            fileOutputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

    }
}
