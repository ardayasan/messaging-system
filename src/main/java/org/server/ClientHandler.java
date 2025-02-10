package org.server;
import java.io.*;
import java.net.Socket;
import java.util.Map;

public class ClientHandler extends Thread {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;
    private Map<String, ClientHandler> clients;

    public ClientHandler(Socket socket, Map<String, ClientHandler> clients) {
        this.socket = socket;
        this.clients = clients;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void setOut(PrintWriter out) {
        this.out = out;
    }

    public void setIn(BufferedReader in) {
        this.in = in;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    // Getters for fields
    public Socket getSocket() {
        return socket;
    }

    public PrintWriter getOut() {
        return out;
    }

    public BufferedReader getIn() {
        return in;
    }

    public String getUsername() {
        return username;
    }

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            username = in.readLine(); // Kullanıcı adını sadece bir kez istiyorum


            while (username == null || username.trim().isEmpty() || clients.containsKey(username)) {
                out.println("Kullanıcı adı boş olamaz veya zaten kullanımda! " +
                        "Lütfen başka bir kullanıcı adı girin:");
                username = in.readLine();
            }

            synchronized (clients) {
                clients.put(username, this);
            }

            System.out.println(username + " bağlandı.");
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println(username + " mesaj gönderdi: " + message);
                String[] parts = message.split(":", 2);
                if (parts.length == 2) {
                    String recipient = parts[0].trim();
                    String msg = parts[1].trim();
                    sendMessage(recipient, username + ": " + msg);
                } else {
                    out.println("Hatalı format! Mesajınızı '<alıcı>: <mesaj>' şeklinde yazmalısınız.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            synchronized (clients) {
                clients.remove(username);
            }
            System.out.println(username + " ayrıldı.");
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String recipient, String message) {
        synchronized (clients) {
            ClientHandler recipientHandler = clients.get(recipient);
            if (recipientHandler == null) {
                // Alıcı yoksa mesajı gönderme
                System.out.println("Kullanıcı bulunamadı: " + recipient);
                out.println("404:" + recipient); // oluşan framei silmek için yanlış ismi de yolluyorum
                return;
            }
            // Alıcı varsa, mesajı alıcıya gönder
            recipientHandler.out.println(message);
        }
    }
}