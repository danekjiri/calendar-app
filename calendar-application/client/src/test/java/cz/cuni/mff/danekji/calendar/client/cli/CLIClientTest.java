package cz.cuni.mff.danekji.calendar.client.cli;

import cz.cuni.mff.danekji.calendar.client.network.NetworkHandler;
import cz.cuni.mff.danekji.calendar.core.client.Client;
import cz.cuni.mff.danekji.calendar.core.client.session.ClientSession;
import cz.cuni.mff.danekji.calendar.core.client.ui.UserInterface;
import cz.cuni.mff.danekji.calendar.core.commands.Command;
import cz.cuni.mff.danekji.calendar.core.responses.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CLIClientTest {

    @Mock
    private UserInterface mockUi;

    @Mock
    private NetworkHandler mockNetworkHandler;

    @Mock
    private Command mockCommand;

    @Mock
    private Response mockResponse;

    private CLIClient cliClient;

    private final String TEST_HOST = "localhost";
    private final int TEST_PORT = 1234;

    @BeforeEach
    void setUp() {
        cliClient = new CLIClient(mockUi, mockNetworkHandler);
    }

    @Test
    void connectSuccessfullyInitializesSession() throws IOException, ClassNotFoundException {
        // Arrange
        int TEST_SESSION_ID = 789;
        when(mockNetworkHandler.connect(TEST_HOST, TEST_PORT)).thenReturn(TEST_SESSION_ID);

        // Act
        cliClient.connect(TEST_HOST, TEST_PORT);
        ClientSession session = cliClient.getCurrentSession();

        // Assert
        assertNotNull(session);
        assertEquals(TEST_SESSION_ID, session.getSessionId());
        assertEquals(new InetSocketAddress(TEST_HOST, TEST_PORT), session.getClientAddress());
        assertTrue(session.isActive());
        verify(mockNetworkHandler).connect(TEST_HOST, TEST_PORT);
    }

    @Test
    void connectHandlesIOExceptionFromNetworkHandler() throws IOException, ClassNotFoundException {
        // Arrange
        when(mockNetworkHandler.connect(TEST_HOST, TEST_PORT)).thenThrow(new IOException("Connection failed"));

        // Act
        cliClient.connect(TEST_HOST, TEST_PORT);

        // Assert
        assertThrows(IllegalStateException.class, () -> cliClient.getCurrentSession(),
                "Session should not be initialized after a failed connection.");
    }

    @Test
    void connectHandlesClassNotFoundExceptionFromNetworkHandler() throws IOException, ClassNotFoundException {
        // Arrange
        when(mockNetworkHandler.connect(TEST_HOST, TEST_PORT)).thenThrow(new ClassNotFoundException("Session ID class issue"));

        // Act
        cliClient.connect(TEST_HOST, TEST_PORT);

        // Assert
        assertThrows(IllegalStateException.class, () -> cliClient.getCurrentSession(),
                "Session should not be initialized after a ClassNotFoundException.");
    }

    @Test
    void sendCommandDelegatesToNetworkHandler() throws IOException, ClassNotFoundException {
        // Arrange
        when(mockNetworkHandler.sendCommand(mockCommand)).thenReturn(mockResponse);

        // Act
        Response actualResponse = cliClient.sendCommand(mockCommand);

        // Assert
        assertSame(mockResponse, actualResponse);
        verify(mockNetworkHandler).sendCommand(mockCommand);
    }

    @Test
    void sendCommandPropagatesIOException() throws IOException, ClassNotFoundException {
        // Arrange
        when(mockNetworkHandler.sendCommand(mockCommand)).thenThrow(new IOException("Network send error"));

        // Act & Assert
        assertThrows(IOException.class, () -> cliClient.sendCommand(mockCommand));
    }

    @Test
    void sendCommandPropagatesClassNotFoundException() throws IOException, ClassNotFoundException {
        // Arrange
        when(mockNetworkHandler.sendCommand(mockCommand)).thenThrow(new ClassNotFoundException("Response class error"));

        // Act & Assert
        assertThrows(ClassNotFoundException.class, () -> cliClient.sendCommand(mockCommand));
    }

    @Test
    void isConnectionOpenDelegatesToNetworkHandler() {
        // Arrange
        when(mockNetworkHandler.isConnected()).thenReturn(true).thenReturn(false);

        // Act & Assert
        assertTrue(cliClient.isConnectionOpen());
        assertFalse(cliClient.isConnectionOpen());
        verify(mockNetworkHandler, times(2)).isConnected();
    }

    @Test
    void getCurrentSessionThrowsIllegalStateExceptionIfNotConnected() {
        // Act & Assert
        assertThrows(IllegalStateException.class, () -> cliClient.getCurrentSession());
    }

    @Test
    void startCallsUiStartAndDisconnectsWhenConnected() throws IOException {
        // Arrange
        when(mockNetworkHandler.isConnected()).thenReturn(true);
        InOrder inOrder = inOrder(mockUi, mockNetworkHandler);

        // Act
        cliClient.start();

        // Assert
        inOrder.verify(mockUi).start(cliClient);
        inOrder.verify(mockNetworkHandler).disconnect();
    }

    @Test
    void disconnectDelegatesToNetworkHandler() throws IOException {
        // Arrange
        when(mockNetworkHandler.isConnected()).thenReturn(true);

        // Act
        cliClient.disconnect();

        // Assert
        verify(mockNetworkHandler).disconnect();
    }

    @Test
    void disconnectHandlesIOExceptionFromNetworkHandler() throws IOException {
        // Arrange
        when(mockNetworkHandler.isConnected()).thenReturn(true);
        doThrow(new IOException("Disconnect error")).when(mockNetworkHandler).disconnect();

        // Act & Assert
        assertDoesNotThrow(() -> cliClient.disconnect());
    }
}