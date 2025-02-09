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

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // ✅ Kullanıcı adını sadece 1 kez iste
            out.println("Kullanıcı adınızı girin: ");
            username = in.readLine();

            // Eğer boşsa veya geçersizse tekrar isteme
            while (username == null || username.trim().isEmpty() || clients.containsKey(username)) {
                out.println("⚠ Kullanıcı adı boş olamaz veya zaten kullanımda! Lütfen başka bir kullanıcı adı girin:");
                username = in.readLine();
            }

            synchronized (clients) {
                clients.put(username, this);
            }
            System.out.println(username + " bağlandı.");

            // Kullanıcıya giriş başarılı mesajını gönder
            out.println("✅ Giriş başarılı! Artık mesaj gönderebilirsiniz.");

            String message;
            while ((message = in.readLine()) != null) {
                System.out.println(username + " mesaj gönderdi: " + message);
                String[] parts = message.split(":", 2);
                if (parts.length == 2) {
                    String recipient = parts[0].trim();
                    String msg = parts[1].trim();
                    sendMessage(recipient, username + ": " + msg);
                } else {
                    out.println("⚠ Hatalı format! Mesajınızı '<alıcı>: <mesaj>' şeklinde yazmalısınız.");
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

    private void sendMessage(String recipient, String message) {
        synchronized (clients) {
            ClientHandler recipientHandler = clients.get(recipient);
            if (recipientHandler != null) {
                recipientHandler.out.println(message);
            } else {
                out.println("⚠ Kullanıcı bulunamadı: " + recipient);
            }
        }
    }
}