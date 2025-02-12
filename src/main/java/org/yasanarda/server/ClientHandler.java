package org.yasanarda.server;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;

public class ClientHandler extends Thread {
    private String username;
    private SessionManager sessionManager;
    private Map<String, SessionManager> clients;
    private MessageHandler messageHandler;

    public ClientHandler(String username, Socket socket, Map<String, SessionManager> clients) throws IOException {
        this.username = username;
        this.clients = clients;
        this.sessionManager = new SessionManager(username, socket);
        this.messageHandler = new MessageHandler(clients);
    }

    @Override
    public void run() {
        try {
            String message;
            while ((message = sessionManager.getConnectionManager().getReader().readLine()) != null) {
                System.out.println(username + " message send: " + message);
                String[] parts = message.split(":", 2);
                if (parts.length == 2) {
                    String recipient = parts[0].trim();
                    String msg = parts[1].trim();
                    messageHandler.sendMessage(username, recipient, msg);
                } else {
                    sessionManager.getConnectionManager().getWriter().println("Format error!");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            clients.remove(username);
            sessionManager.closeSession();
        }
    }
}
