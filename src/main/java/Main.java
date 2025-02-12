import org.yasanarda.client.ClientGUI;
import org.yasanarda.server.Server;

import javax.swing.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Server server = new Server(12345);
        try{
            Runtime.getRuntime().addShutdownHook(new Thread(() -> server.stopServer()));
            server.startServer();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}