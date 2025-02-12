package org.yasanarda.server;

import java.util.Map;

public class MessageHandler {
    private Map<String, SessionManager> clients;

    public MessageHandler(Map<String, SessionManager> clients) {
        this.clients = clients;
    }

    public void sendMessage(String sender, String recipient, String message) {
        synchronized (clients) {
            SessionManager recipientSession = clients.get(recipient);
            if (recipientSession == null) {
                SessionManager senderSession = clients.get(sender);
                senderSession.getConnectionManager().getWriter().println("404:" + recipient);
                return;
            }
            recipientSession.getConnectionManager().getWriter().println(sender + ": " + message);
        }
    }
}
