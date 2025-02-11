package org.yasanarda.server;

import java.io.IOException;
import java.net.Socket;

public class SessionManager {
    private String username;
    private ConnectionManager connectionManager;

    public SessionManager(String username, Socket socket) throws IOException {
        this.username = username;
        this.connectionManager = new ConnectionManager(socket);
    }

    public String getUsername() {
        return username;
    }

    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public void closeSession() {
        connectionManager.closeConnection();
    }
}
