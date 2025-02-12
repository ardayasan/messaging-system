package org.yasanarda.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private static int PORT = 1234;
    private static Map<String, SessionManager> clients = new HashMap<>();
    private ServerSocket serverSocket;
    private boolean isRunning;

    public Server(){
        isRunning = false;
    }

    public Server(int port){
        PORT = port;
        isRunning = false;
    }

    public static Map<String, SessionManager> getClients(){
        return Server.clients;
    }

    public void startServer() throws IOException {
        System.out.println("Server başlatılıyor...");
        serverSocket = new ServerSocket(PORT);
        isRunning = true;
        System.out.println("Server başlatıldı.");

        while (isRunning) {
            try {
                Socket socket = serverSocket.accept();
                String username = new ConnectionManager(socket).getReader().readLine();
                if (username != null && !username.trim().isEmpty() && !Server.clients.containsKey(username)) {
                    SessionManager sessionManager = new SessionManager(username, socket);
                    ClientHandler clientHandler = new ClientHandler(username, socket, Server.clients);
                    Server.clients.put(username, sessionManager);
                    clientHandler.start();
                } else{
                    socket.close();
                }
            } catch (IOException e) {
                if (isRunning) {
                    System.out.println("Bir hata oluştu: " + e.getMessage());
                    // GUI daki problem serverı kapatıyor.
                    e.printStackTrace();
                } else {
                    System.out.println("Server durdu.");
                }
            }
        }
    }

    public void stopServer() {
        try {
            isRunning = false;
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("Server kapatıldı.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
