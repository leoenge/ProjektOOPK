package Model;

import Controller.Controller;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.security.Key;
import java.security.KeyException;

import javax.swing.*;

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

    /**
     * @return The time elapsed since this request handler was created.
     */
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
        byte[] buffer = new byte[(int) file.length()];
        byte[] encrypted;

        if (encrypt) {
            try {
                //Copy the file into the buffer;
                while ((fileInputStream.read(buffer)) > 0) {
                }
                try {
                    encrypted = connection.AESEncryption.encrypt(buffer);
                } catch (KeyException e) {
                    e.printStackTrace();
                    return;
                }

                try {
                    socketOutStream = socket.getOutputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

                socketOutStream.write(encrypted);
                return;
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        try {
            socketOutStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        JFrame frame = new JFrame("Progress");
        JProgressBar progressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, (int) file.length());
        progressBar.setValue(0);
        frame.add(progressBar);
        frame.setSize(new Dimension(300, 100));
        frame.setVisible(true);

        int numberOfBytesRead;
        int totalBytesRead = 0;
        try {
            while ((numberOfBytesRead = fileInputStream.read(buffer)) > 0) {
                totalBytesRead += numberOfBytesRead;
                //Swingutilities invokeLater requires a final variable.
                final int tmpTotalBytesRead = totalBytesRead;
                socketOutStream.write(buffer, 0, numberOfBytesRead);
                SwingUtilities.invokeLater(
                        new Runnable() {
                    public void run() {
                        progressBar.setValue(tmpTotalBytesRead);
                        frame.repaint();
                    }
                });
            }

            Controller.getInstance().view.displayMessage("File sending complete");
            frame.dispose();
            socket.close();
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
