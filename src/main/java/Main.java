import org.yasanarda.client.ClientCreator;
import org.yasanarda.server.Server;

import javax.swing.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> server.stopServer()));

        new Thread(() -> {
            try {
                server.startServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        JFrame mainFrame = new JFrame("Main GUI");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(400, 200);

        ClientCreator clientCreator = new ClientCreator();
        mainFrame.add(clientCreator.getButton());

        mainFrame.setVisible(true);
    }
}
