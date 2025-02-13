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
    private static final int PORT = 1234;
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

        // Message panel
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        userMessagePanels = new HashMap<>();

        scrollPane = new JScrollPane(chatPanel);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Username field
        recipientField = new JTextField("Receiver Name");
        recipientField.setPreferredSize(new Dimension(100, 30));

        // Message send field
        JPanel bottomPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        bottomPanel.add(recipientField, BorderLayout.WEST);
        bottomPanel.add(messageField, BorderLayout.CENTER);

        JButton sendButton = new JButton("Send");
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


            while (username == null || username.trim().isEmpty()) {
                username = JOptionPane.showInputDialog(frame, " Enter username:");

                if (username == null) {
                    System.exit(0);
                } else if (username.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Username can't be empty. Please enter a valid username.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            System.out.printf("Username: %s\n", username);
            out.println(username);

            Thread readerThread = new Thread(() -> {
                try {
                    String serverResponse;
                    while ((serverResponse = in.readLine()) != null) {
                        displayMessage(serverResponse);
                    }
                } catch (SocketException e) {
                    System.out.println("Socket closed.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            readerThread.start();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Server connection error!", "Error", JOptionPane.ERROR_MESSAGE);
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
            System.out.println("Connection closed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void sendMessage() {
        String recipient = recipientField.getText().trim();
        String message = messageField.getText().trim();

        if (!recipient.isEmpty() && !message.isEmpty()) {
            if (recipient.equals(username)) {
                JOptionPane.showMessageDialog(frame, "You can't send message to yourself!", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            out.println(recipient + ": " + message);
            displayMessage(username + " -> " + recipient + ": " + message);
            messageField.setText("");
        } else {
            JOptionPane.showMessageDialog(frame, "Username or message field can't be empty.", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }
    private void displayMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("Received Message: " + message);

            if (message.startsWith("404:")) {
                String[] parts = message.split(":", 2);
                if (parts.length > 1) {
                    String recipient = parts[1].trim();  // Receiver name
                    System.out.println("Error: user couldn't found. Receiver: " + recipient);
                    JOptionPane.showMessageDialog(frame, "User " + recipient + " couldn't found", "Error", JOptionPane.ERROR_MESSAGE);

                    removeRecipientPanel(recipient);
                    return;
                }
            }

            if (message.startsWith("200")) {
                frame.dispose();
                return;
            }

            String[] parts = message.split(": ", 2);
            if (parts.length < 2) {
                System.out.println("Format error: " + message);
                return;
            }

            String senderAndRecipient = parts[0]; // "Sender -> Receiver" part
            String content = parts[1]; // Message content part


            String[] senderRecipientParts = senderAndRecipient.split(" -> ");
            String sender = senderRecipientParts[0]; // Sender
            String recipient = (senderRecipientParts.length > 1) ?
                    senderRecipientParts[1] : "Unknown"; // Receiver

            String targetUser = sender.equals(username) ? recipient : sender;

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
}