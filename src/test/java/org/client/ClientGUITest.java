package org.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ClientGUITest {

    private ClientGUI clientGUI;
    private Socket mockSocket;
    private BufferedReader mockBufferedReader;
    private PrintWriter mockPrintWriter;
    private ByteArrayOutputStream mockOutputStream;

    @BeforeEach
    public void setUp() throws IOException {
        mockSocket = mock(Socket.class);
        mockBufferedReader = mock(BufferedReader.class);
        mockOutputStream = new ByteArrayOutputStream();
        mockPrintWriter = new PrintWriter(mockOutputStream, true); // Mocked PrintWriter

        // Initialize clientGUI
        clientGUI = new ClientGUI();

        // Mock the socket's input and output streams
        when(mockSocket.getInputStream()).thenReturn(new ByteArrayInputStream("user2 -> user1: Hello from user2!\n".getBytes()));
        when(mockSocket.getOutputStream()).thenReturn(mockOutputStream);

        // Inject the mocked PrintWriter and BufferedReader
        clientGUI.setPrintWriter(mockPrintWriter);  // Ensure this is correctly injecting the mock
        clientGUI.setBufferedReader(mockBufferedReader);

        // Mock the behavior of the BufferedReader
        when(mockBufferedReader.readLine()).thenReturn("user2 -> user1: Hello from user2!");
    }


    @Test
    public void testSendMessage() {
        String recipient = "user2";
        String message = "Hello, how are you?";

        // Set the username and simulate setting text in the fields
        clientGUI.setUsername("user1");

        // Set values for recipient and message
        clientGUI.getRecipientField().setText(recipient);
        clientGUI.getMessageField().setText(message);

        // Simulate sending the message directly without needing user input
        clientGUI.sendMessage();

        // Verify PrintWriter's println method is called with the correct message
        verify(mockPrintWriter).println(recipient + ": " + message);
    }


    @Test
    public void testMessageReceived() {
        String serverMessage = "user2 -> user1: Hello from user2!";

        // Simulate reading the server message
        clientGUI.displayMessage(serverMessage);

        // Assert that the message has been displayed correctly in the appropriate panel
        Map<String, JPanel> userPanels = clientGUI.getUserMessagePanels();
        assertTrue(userPanels.containsKey("user2")); // Check that user2's panel exists

        // We can verify if the message was added as expected by checking the label count in the panel
        JPanel user2Panel = userPanels.get("user2");
        assertEquals(1, user2Panel.getComponentCount()); // There should be exactly one message

        JLabel messageLabel = (JLabel) user2Panel.getComponent(0);
        assertTrue(messageLabel.getText().contains("Hello from user2"));
    }

    @Test
    public void testDisconnect() {
        // Call the disconnect method and verify that resources are closed
        try {
            clientGUI.disconnectFromServer();

            // Verify that the socket and streams are closed properly
            verify(mockSocket).close();
            verify(mockPrintWriter).close();
            verify(mockBufferedReader).close();
        } catch (IOException e) {
            fail("Error during disconnection test: " + e.getMessage());
        }
    }

    @Test
    public void testConnectionFailure() {
        // Set up a failure in connection (e.g., IOException)
        try {
            when(mockSocket.getInputStream()).thenThrow(new IOException("Connection failed"));

            clientGUI.connectToServer();

            // Verify that the connection failed by checking that PrintWriter was never used
            verify(mockSocket, times(0)).getOutputStream();
        } catch (IOException e) {
            fail("Unexpected exception during connection failure simulation: " + e.getMessage());
        }
    }
}
