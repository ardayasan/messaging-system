package org.yasanarda.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ConnectionManagerTest {
    private Socket mockSocket;
    private BufferedReader mockBufferedReader;
    private PrintWriter mockPrintWriter;
    private InputStream mockInputStream;
    private OutputStream mockOutputStream;
    private ConnectionManager connectionManager;

    @BeforeEach
    public void setUp() throws Exception {
        mockSocket = mock(Socket.class);
        mockInputStream = mock(InputStream.class);
        mockOutputStream = mock(OutputStream.class);
        mockBufferedReader = new BufferedReader(new InputStreamReader(mockInputStream));
        mockPrintWriter = new PrintWriter(mockOutputStream);
        when(mockSocket.getInputStream()).thenReturn(mockInputStream);
        when(mockSocket.getOutputStream()).thenReturn(mockOutputStream);
        connectionManager = new ConnectionManager(mockSocket);
    }

    @Test
    public void testConstructor() {
        assertNotNull(connectionManager);
        assertNotNull(connectionManager.getReader());
        assertNotNull(connectionManager.getWriter());
    }

    @Test
    public void testGetReader() {
        BufferedReader reader = connectionManager.getReader();
        assertNotNull(reader);
        assertEquals(mockBufferedReader.getClass(), reader.getClass());
    }

    @Test
    public void testGetWriter() {
        PrintWriter writer = connectionManager.getWriter();
        assertNotNull(writer);
        assertEquals(mockPrintWriter.getClass(), writer.getClass());
    }

    @Test
    public void testCloseConnection() {
        connectionManager.closeConnection();
        try {
            verify(mockSocket, times(1)).close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
