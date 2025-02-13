package org.yasanarda.server.helpers;

import java.io.IOException;
import java.net.Socket;

public class SessionManagerImpl implements SessionManager {
    private String username;
    private ConnectionManagerImpl connectionManager;

    public SessionManagerImpl(String username, Socket socket) throws IOException {
        this.username = username;
        this.connectionManager = new ConnectionManagerImpl(socket);
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    @Override
    public void closeSession() {
        connectionManager.closeConnection();
    }
}
