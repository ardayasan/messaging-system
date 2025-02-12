package org.yasanarda.server;

import java.io.IOException;
import java.io.PrintWriter;
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
        System.out.println("Server starting...");
        serverSocket = new ServerSocket(PORT);
        isRunning = true;
        System.out.println("Server started.");

        while (isRunning) {
            try {
                Socket socket = serverSocket.accept();
                String username = new ConnectionManager(socket).getReader().readLine();

                if (username != null && !username.trim().isEmpty()) {
                    // Check if username already exists
                    if (Server.clients.containsKey(username)) {
                        // Send a message indicating the username is taken
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        out.println("200");
                        socket.close();
                    } else {
                        // Proceed with the connection if username is unique
                        SessionManager sessionManager = new SessionManager(username, socket);
                        ClientHandler clientHandler = new ClientHandler(username, socket, Server.clients);
                        Server.clients.put(username, sessionManager);
                        clientHandler.start();
                    }
                }
            } catch (IOException e) {
                if (isRunning) {
                    System.out.println("Error: " + e.getMessage());
                } else {
                    System.out.println("Server stopped.");
                }
            }
        }
    }


    public void stopServer() {
        try {
            isRunning = false;
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("Server closed.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
