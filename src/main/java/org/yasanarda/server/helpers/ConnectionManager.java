package org.yasanarda.server.helpers;

import java.io.BufferedReader;
import java.io.PrintWriter;

public interface ConnectionManager {
    public BufferedReader getReader();
    public PrintWriter getWriter();
    public void closeConnection();
}