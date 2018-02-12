package View;

import javax.swing.*;

public class View extends JFrame {
    Model model;
    ControlPanel controlPanel;
    ChatPanel chatPanel;
    SendPanel sendPanel;

    public View(Model modelIn) {
        model = modelIn;
        controlPanel = new ControlPanel();
        chatPanel = new ChatPanel();
        sendPanel = new SendPanel();

        this.add(controlPanel);
        this.add(chatPanel);
        this.add(sendPanel);
        this.pack();
        this.setVisible(true);
    }

    public void updateView() {

    }

    //TEST METHOD DON'T TOUCH
    public static void main(String[] args) {

    }
}
