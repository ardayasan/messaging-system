package org.yasanarda.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.mockito.Mockito.*;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class MessageHandlerTest {

    @Mock
    private Map<String, SessionManager> mockClients;

    @Mock
    private SessionManager mockSenderSession;

    @Mock
    private SessionManager mockRecipientSession;

    @Mock
    private ConnectionManager mockSenderConnectionManager;

    @Mock
    private ConnectionManager mockRecipientConnectionManager;

    @Mock
    private PrintWriter mockSenderWriter;

    @Mock
    private PrintWriter mockRecipientWriter;

    private MessageHandler messageHandler;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        messageHandler = new MessageHandler(mockClients);

        when(mockSenderSession.getConnectionManager()).thenReturn(mockSenderConnectionManager);
        when(mockSenderConnectionManager.getWriter()).thenReturn(mockSenderWriter);

        when(mockRecipientSession.getConnectionManager()).thenReturn(mockRecipientConnectionManager);
        when(mockRecipientConnectionManager.getWriter()).thenReturn(mockRecipientWriter);

        Map<String, SessionManager> clientsMap = new HashMap<>();
        clientsMap.put("sender", mockSenderSession);
        clientsMap.put("recipient", mockRecipientSession);
        when(mockClients.get("sender")).thenReturn(mockSenderSession);
        when(mockClients.get("recipient")).thenReturn(mockRecipientSession);
    }

    @Test
    public void testSendMessage_Successful() {
        messageHandler.sendMessage("sender", "recipient", "Hello");

        verify(mockRecipientWriter).println("sender: Hello");
    }

    @Test
    public void testSendMessage_RecipientNotFound() {
        when(mockClients.get("recipient")).thenReturn(null);

        messageHandler.sendMessage("sender", "recipient", "Hello");

        verify(mockSenderWriter).println("404:recipient");
    }
}