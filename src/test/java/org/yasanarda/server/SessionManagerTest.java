package org.yasanarda.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.yasanarda.server.helpers.ConnectionManager;
import org.yasanarda.server.helpers.ConnectionManagerImpl;
import org.yasanarda.server.helpers.SessionManagerImpl;

import java.io.*;
import java.net.Socket;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class SessionManagerTest {

    @Mock
    private Socket mockSocket;

    @Mock
    private InputStream mockInputStream;

    @Mock
    private OutputStream mockOutputStream;

    @Mock
    private ConnectionManagerImpl mockConnectionManager;

    private SessionManagerImpl sessionManager;

    @BeforeEach
    public void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);

        when(mockSocket.getInputStream()).thenReturn(mockInputStream);
        when(mockSocket.getOutputStream()).thenReturn(mockOutputStream);

        sessionManager = new SessionManagerImpl("testUser", mockSocket);
        sessionManager = spy(sessionManager);

        doReturn(mockConnectionManager).when(sessionManager).getConnectionManager();
    }

    @Test
    public void testGetUsername() {
        assertEquals("testUser", sessionManager.getUsername(), "Username should be 'testUser'");
    }

    @Test
    public void testGetConnectionManager() {
        ConnectionManager connectionManager = sessionManager.getConnectionManager();
        assertNotNull(connectionManager, "ConnectionManager should not be null");
    }

    @Test
    public void testCloseSession() {
        doNothing().when(mockConnectionManager).closeConnection();
        sessionManager.closeSession();
        verify(sessionManager, times(1)).closeSession();
    }

    @Test
    public void testConstructor() throws IOException {
        SessionManagerImpl newSessionManager = new SessionManagerImpl("newUser", mockSocket);
        assertEquals("newUser", newSessionManager.getUsername(), "Username should be 'newUser'");
        assertNotNull(newSessionManager.getConnectionManager(), "ConnectionManager should not be null");
    }
}