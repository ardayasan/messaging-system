package org.yasanarda.server.helpers;

import java.io.*;
import java.net.Socket;

public class ConnectionManagerImpl implements ConnectionManager {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public ConnectionManagerImpl(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }
    @Override
    public BufferedReader getReader() {
        return in;
    }
    @Override
    public PrintWriter getWriter() {
        return out;
    }
    @Override
    public void closeConnection() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
