package org.yasanarda.server;

import org.yasanarda.server.helpers.ConnectionManagerImpl;
import org.yasanarda.server.helpers.SessionManager;
import org.yasanarda.server.helpers.SessionManagerImpl;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerImpl implements Server{
    private static int PORT = 1234;
    private Map<String, SessionManager> clients = new HashMap<>();
    private ServerSocket serverSocket;
    private boolean isRunning;

    public ServerImpl(){
        isRunning = false;
    }

    public ServerImpl(int port){
        ServerImpl.PORT = port;
        isRunning = false;
    }

    @Override
    public void startServer() throws IOException {
        System.out.println("Server starting...");
        serverSocket = new ServerSocket(PORT);
        isRunning = true;
        System.out.println("Server started.");

        while (isRunning) {
            try {
                Socket socket = serverSocket.accept();
                String username = new ConnectionManagerImpl(socket).getReader().readLine();

                if (username != null && !username.trim().isEmpty()) {
                    // Check if username already exists
                    if (clients.containsKey(username)) {
                        // Send a message indicating the username is taken
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        out.println("200");
                        socket.close();
                    } else {
                        // Proceed with the connection if username is unique
                        SessionManager sessionManager = new SessionManagerImpl(username, socket);
                        ClientHandler clientHandler = new ClientHandlerImpl(username, socket, clients);
                        clients.put(username, sessionManager);
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

    @Override
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
