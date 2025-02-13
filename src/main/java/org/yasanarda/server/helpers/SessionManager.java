package org.yasanarda.server.helpers;

import org.yasanarda.server.helpers.ConnectionManager;

public interface SessionManager {
    public String getUsername();
    public ConnectionManager getConnectionManager();
    public void closeSession();

}
