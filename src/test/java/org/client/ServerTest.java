package org.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.server.ClientHandler;
import org.server.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static org.mockito.Mockito.*;

public class ServerTest {

    private ServerSocket mockServerSocket;
    private Socket mockSocket;
    private InputStream mockInputStream;
    private OutputStream mockOutputStream;
    private BufferedReader mockBufferedReader;
    private Server server;

    @BeforeEach
    public void setUp() throws IOException {
        mockServerSocket = mock(ServerSocket.class);
        mockSocket = mock(Socket.class);

        mockInputStream = mock(InputStream.class);
        mockOutputStream = mock(OutputStream.class);

        mockBufferedReader = mock(BufferedReader.class);

        when(mockSocket.getInputStream()).thenReturn(mockInputStream);
        when(mockSocket.getOutputStream()).thenReturn(mockOutputStream);
        when(mockBufferedReader.readLine()).thenReturn("TestUser");

        server = new Server() {
            @Override
            public void startServer() throws IOException {
                Socket socket = mockServerSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket, Server.getClients());
                clientHandler.start();
            }
        };
    }

    @Test
    public void testServerStart() throws IOException {
        when(mockServerSocket.accept()).thenReturn(mockSocket);

        when(mockSocket.getInputStream())
                .thenReturn(new ByteArrayInputStream("TestUser".getBytes()));

        Thread serverThread = new Thread(() -> {
            try {
                server.startServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        serverThread.start();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        verify(mockServerSocket, times(1)).accept();
        serverThread.interrupt();
    }
}
