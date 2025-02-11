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

    public Server(){/* */}

    public Server(int port){
        PORT = port;
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
                if (username != null && !username.trim().isEmpty() && !clients.containsKey(username)) {
                    // Create a SessionManager for this username and socket
                    SessionManager sessionManager = new SessionManager(username, socket);

                    // Create the ClientHandler and pass the SessionManager
                    ClientHandler clientHandler = new ClientHandler(username, socket, clients);

                    // Store the session manager in the clients map
                    clients.put(username, sessionManager);

                    // Start the ClientHandler thread
                    clientHandler.start();
                }
            } catch (IOException e) {
                if (isRunning) {
                    System.out.println("Bir hata oluştu: " + e.getMessage());
                    e.printStackTrace();
                } else {
                    // Server düzgün bir şekilde kapatıldı, accept()'in çıkması
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
