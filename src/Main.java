import Controller.Controller;
import Model.Model;
import View.View;
import Model.Message;
import Model.MessageFactory;
import Model.TextMessage;
import Model.FileRequest;
import Model.FileResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Model model = new Model();
        Controller.getInstance().setModel(model);
        Controller.getInstance().setView(model.view);

        //Controller.getInstance().establishServerPort();
    }
}
