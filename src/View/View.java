package View;

import javax.swing.*;
import Model.*;
import java.awt.*;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class View {
    private Model model;
    JFrame frame;
    private ControlPanel controlPanel;
    private ChatPanel chatPanel;
    private SendPanel sendPanel;

    public View(Model modelIn) {
        model = modelIn;
        frame = new JFrame();
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

        controlPanel = new ControlPanel();
        chatPanel = new ChatPanel();
        sendPanel = new SendPanel();

        frame.add(controlPanel);
        frame.add(chatPanel);
        frame.add(sendPanel);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void updateView() {

    }

    //TEST METHOD DON'T TOUCH
    public static void main(String[] args) {
        View view = new View(Model.getInstance());
    }
    //I touched it xD
}
