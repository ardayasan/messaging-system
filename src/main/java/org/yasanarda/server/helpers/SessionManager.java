package org.yasanarda.server.helpers;


public interface SessionManager {
    public String getUsername();
    public ConnectionManager getConnectionManager();
    public void closeSession();

}