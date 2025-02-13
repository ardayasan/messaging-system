package org.yasanarda.server;

import org.yasanarda.server.helpers.MessageHandler;
import org.yasanarda.server.helpers.MessageHandlerImpl;
import org.yasanarda.server.helpers.SessionManager;
import org.yasanarda.server.helpers.SessionManagerImpl;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;

public class ClientHandlerImpl extends Thread implements ClientHandler {
    private String username;
    private SessionManager sessionManager;
    private Map<String, SessionManager> clients;
    private MessageHandler messageHandler;

    public ClientHandlerImpl(String username, Socket socket, Map<String, SessionManager> clients) throws IOException {
        this.username = username;
        this.clients = clients;
        this.sessionManager = new SessionManagerImpl(username, socket);
        this.messageHandler = new MessageHandlerImpl(clients);
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
