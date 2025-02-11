import org.yasanarda.server.Server;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Server server = new Server(12345);
        try{
            server.startServer();
            // JVM kapanırken stop server çağırılacak
            Runtime.getRuntime().addShutdownHook(new Thread(() -> server.stopServer()));
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}