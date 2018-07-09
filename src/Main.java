import Model.Model;
import View.*;
import Controller.*;

public class Main {
    public static void main(String[] args) {
        //Establishes port to listen to new connections on startup.
        Controller.getInstance().establishServerPort();
    }
}
