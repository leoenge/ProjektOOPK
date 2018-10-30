import Model.Message;
import Model.MessageFactory;
import Model.TextMessage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        while (true) {
            Scanner sc = new Scanner(System.in);
            String line = sc.nextLine();
            InputStream is = new ByteArrayInputStream(line.getBytes());

            Message ms = MessageFactory.messageFactory(is);
            ms.unEscapeChars();
            if(ms != null)
                System.out.println(ms.toString());
        }

    }
}
