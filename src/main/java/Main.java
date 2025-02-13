import org.yasanarda.client.ClientCreator;
import org.yasanarda.server.Server;
import org.yasanarda.server.ServerImpl;

import javax.swing.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        startServerThread();
        startClientCreatorThread();
    }
    private static void startServerThread() {
        new Thread(() -> {
            Server server = new ServerImpl();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> server.stopServer()));

            try {
                server.startServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    private static void startClientCreatorThread() {
        new Thread(() -> {
            JFrame mainFrame = new JFrame("Main GUI");
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setSize(400, 200);

            ClientCreator clientCreator = new ClientCreator();
            mainFrame.add(clientCreator.getButton());
            mainFrame.setVisible(true);
        }).start();
    }
}