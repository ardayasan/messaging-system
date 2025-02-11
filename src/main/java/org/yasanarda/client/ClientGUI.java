package org.yasanarda.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.net.SocketException;

public class ClientGUI {
    private Socket socket;
    private static final int PORT = 12345;
    private static final String IP = "127.0.0.1";
    private JFrame frame;
    private JTextField messageField;
    private JTextField recipientField;
    private JPanel chatPanel;
    private JScrollPane scrollPane;
    private PrintWriter out;
    private BufferedReader in;
    private String username;
    private Map<String, JPanel> userMessagePanels;

    public ClientGUI() {
        frame = new JFrame("Messaging System");
        frame.setSize(400, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Mesajları içeren panel
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        userMessagePanels = new HashMap<>();

        scrollPane = new JScrollPane(chatPanel);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Alıcı kullanıcı adı girme alanı
        recipientField = new JTextField("Alıcı Adı");
        recipientField.setPreferredSize(new Dimension(100, 30));

        // Mesaj gönderme alanı
        JPanel bottomPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        bottomPanel.add(recipientField, BorderLayout.WEST);
        bottomPanel.add(messageField, BorderLayout.CENTER);

        JButton sendButton = new JButton("Gönder");
        bottomPanel.add(sendButton, BorderLayout.EAST);

        frame.add(bottomPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                disconnectFromServer();
                System.exit(0);
            }
        });

        frame.setVisible(true);
        connectToServer();
    }

    private void connectToServer() {
        try {
            socket = new Socket(IP, PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            username = JOptionPane.showInputDialog(frame, "Kullanıcı adınızı girin:");


            // WILL BE FIXED
            if (username == null || username.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Kullanıcı adı boş olamaz. " +
                        "Lütfen geçerli bir kullanıcı adı girin.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            System.out.printf("Kullanıcı adı: %s\n", username);
            out.println(username);

            Thread readerThread = new Thread(() -> {
                try {
                    String serverResponse;
                    while ((serverResponse = in.readLine()) != null) {
                        displayMessage(serverResponse);
                    }
                } catch (SocketException e) {
                    System.out.println("Socket kapalı, okuma işlemi sonlandırılıyor.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            readerThread.start();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Sunucuya bağlanılamadı!", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void disconnectFromServer() {
        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            System.out.println("Bağlantı kapatıldı.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        String recipient = recipientField.getText().trim();
        String message = messageField.getText().trim();

        if (!recipient.isEmpty() && !message.isEmpty()) {
            // Kullanıcı kendi kendine mesaj gönderemez
            if (recipient.equals(username)) {
                JOptionPane.showMessageDialog(frame, "Kendinize mesaj gönderemezsiniz!", "Hata", JOptionPane.WARNING_MESSAGE);
                return;
            }

            out.println(recipient + ": " + message);
            // kendi attığım mesajı görmem gerek
            displayMessage(username + " -> " + recipient + ": " + message);  // Gönderdiğimiz mesajı da ekrana yaz
            messageField.setText("");
        } else {
            JOptionPane.showMessageDialog(frame, "Alıcı ve mesaj boş olamaz!", "Hata", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void displayMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("Gelen Mesaj: " + message);

            // Eğer mesaj "404:" hatası içeriyorsa, bu durumda kullanıcıyı uyaracağız
            if (message.startsWith("404:")) {
                String[] parts = message.split(":", 2);
                if (parts.length > 1) {
                    String recipient = parts[1].trim();  // Alıcının adı
                    System.out.println("Hata mesajı alındı: Kullanıcı bulunamadı. Alıcı: " + recipient);
                    JOptionPane.showMessageDialog(frame, "Kullanıcı " + recipient + " bulunamadı", "Hata", JOptionPane.ERROR_MESSAGE);

                    // Alıcı panelini sohbet kısmından kaldırdım
                    removeRecipientPanel(recipient);

                    return;  // Hata mesajı alındığında sohbet kısmında gösterme
                }
            }

            // Diğer mesajları işlemeye devam ediyor
            String[] parts = message.split(": ", 2);
            if (parts.length < 2) {
                System.out.println("Hatalı format: " + message);
                return;
            }

            String senderAndRecipient = parts[0]; // "Gönderen -> Alıcı"
            String content = parts[1]; // Mesaj içeriği


            String[] senderRecipientParts = senderAndRecipient.split(" -> ");
            String sender = senderRecipientParts[0]; // Gönderen
            String recipient = (senderRecipientParts.length > 1) ?
                    senderRecipientParts[1] : "Bilinmeyen"; // Alıcı

            // Eğer mesaj bana geldiyse, gönderene göre grupla
            String targetUser = sender.equals(username) ? recipient : sender;

            // Hedef kullanıcı için panel var mı kontrol et
            if (!userMessagePanels.containsKey(targetUser)) {
                JPanel userPanel = new JPanel();
                userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
                userPanel.setBorder(BorderFactory.createTitledBorder(targetUser));
                userMessagePanels.put(targetUser, userPanel);
                chatPanel.add(userPanel);
            }

            JPanel userPanel = userMessagePanels.get(targetUser);

            JLabel messageLabel;
            if (sender.equals(username)) {
                messageLabel = new JLabel("<html><p style='padding:5px;'><b>Me:</b> " + content + "</p></html>");
                messageLabel.setBackground(Color.GREEN);
            } else {
                messageLabel = new JLabel("<html><p style='padding:5px;'><b>" + sender + ":</b> " + content + "</p></html>");
                messageLabel.setBackground(Color.LIGHT_GRAY);
            }

            messageLabel.setOpaque(true);
            messageLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            userPanel.add(messageLabel);
            chatPanel.revalidate();
            chatPanel.repaint();
        });
    }

    private void removeRecipientPanel(String recipient) {
        JPanel userPanel = userMessagePanels.get(recipient);
        if (userPanel != null) {
            chatPanel.remove(userPanel);
            userMessagePanels.remove(recipient);
            chatPanel.revalidate();
            chatPanel.repaint();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClientGUI::new);
    }
}