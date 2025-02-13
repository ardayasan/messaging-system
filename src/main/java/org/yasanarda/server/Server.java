package org.yasanarda.server;

import java.io.IOException;

public interface Server  {
    public void startServer() throws IOException;
    public void stopServer();
}