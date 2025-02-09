package org.client;

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

public class ClientGUI {
    private JFrame frame;
    private JTextField messageField;
    private JTextField recipientField;
    private JPanel chatPanel;
    private JScrollPane scrollPane;
    private PrintWriter out;
    private BufferedReader in;
    private String username;
    private Map<String, JPanel> userMessagePanels; // Kullanıcı bazlı mesaj panelleri

    public ClientGUI() {
        frame = new JFrame("Mesajlaşma Uygulaması");
        frame.setSize(400, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());


        // 📜 Mesajları içeren dinamik panel
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

        frame.setVisible(true);

        connectToServer();
    }

    private void connectToServer() {
        try {
            Socket socket = new Socket("localhost", 12345);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Kullanıcı adı giriş ekranı
            username = JOptionPane.showInputDialog(frame, "Kullanıcı adınızı girin:");
            out.println(username);

            // Gelen mesajları dinleyen thread
            Thread readerThread = new Thread(() -> {
                try {
                    String serverResponse;
                    while ((serverResponse = in.readLine()) != null) {
                        displayMessage(serverResponse);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            readerThread.start();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Sunucuya bağlanılamadı!", "Hata", JOptionPane.ERROR_MESSAGE);
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
            displayMessage(username + " -> " + recipient + ": " + message);  // Gönderdiğimiz mesajı da ekrana yaz
            messageField.setText("");
        } else {
            JOptionPane.showMessageDialog(frame, "Alıcı ve mesaj boş olamaz!", "Hata", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void displayMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            // Mesaj formatı: "Gönderen -> Alıcı: Mesaj İçeriği"
            String[] parts = message.split(": ", 2);
            if (parts.length < 2) return; // Hatalı mesaj formatı

            String senderAndRecipient = parts[0]; // "Gönderen -> Alıcı"
            String content = parts[1]; // Mesaj içeriği

            String[] senderRecipientParts = senderAndRecipient.split(" -> ");
            String sender = senderRecipientParts[0]; // Gönderen
            String recipient = (senderRecipientParts.length > 1) ? senderRecipientParts[1] : "Bilinmeyen"; // Alıcı

            // Eğer mesaj bana geldiyse, gönderene göre grupla
            String targetUser = sender.equals(username) ? recipient : sender;

            // Hedef kullanıcı için panel var mı?
            if (!userMessagePanels.containsKey(targetUser)) {
                JPanel userPanel = new JPanel();
                userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
                userPanel.setBorder(BorderFactory.createTitledBorder(targetUser)); // Başlık ekle
                userMessagePanels.put(targetUser, userPanel);
                chatPanel.add(userPanel);
            }

            JPanel userPanel = userMessagePanels.get(targetUser);

            // Mesaj baloncuğu oluştur
            JLabel messageLabel;
            if (sender.equals(username)) {
                messageLabel = new JLabel("<html><p style='padding:5px;'><b>Ben:</b> " + content + "</p></html>");
                messageLabel.setBackground(Color.GREEN);
            } else {
                messageLabel = new JLabel("<html><p style='padding:5px;'><b>" + sender + ":</b> " + content + "</p></html>");
                messageLabel.setBackground(Color.LIGHT_GRAY);
            }

            messageLabel.setOpaque(true);
            messageLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            // Mesajı panel içine ekle
            userPanel.add(messageLabel);
            chatPanel.revalidate();
            chatPanel.repaint();
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClientGUI::new);
    }
}