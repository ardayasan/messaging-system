package org.yasanarda.server;

import java.io.*;
import java.net.Socket;

public class ConnectionManager {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public ConnectionManager(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    public BufferedReader getReader() {
        return in;
    }

    public PrintWriter getWriter() {
        return out;
    }

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
