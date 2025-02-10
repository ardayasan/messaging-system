package org.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private static final int PORT = 1234;
    private static Map<String, ClientHandler> clients = new HashMap<>();
    private ServerSocket serverSocket;

    public static Map<String, ClientHandler> getClients() {
        return clients;
    }

    public void startServer() throws IOException {
        System.out.println("Server başlatılıyor...");
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server başlatıldı.");

            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket, clients);
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopServer() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("Server kapatıldı.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        try{
            server.startServer();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
