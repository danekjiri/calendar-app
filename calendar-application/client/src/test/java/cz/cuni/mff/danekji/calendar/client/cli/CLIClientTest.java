package cz.cuni.mff.danekji.calendar.client.cli;

import cz.cuni.mff.danekji.calendar.core.client.Client;
import cz.cuni.mff.danekji.calendar.core.client.session.ClientSession;
import cz.cuni.mff.danekji.calendar.core.client.ui.UserInterface;
import cz.cuni.mff.danekji.calendar.core.commands.Command;
import cz.cuni.mff.danekji.calendar.core.responses.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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

    @InjectMocks
    private CLIClient cliClient;

    private final String TEST_HOST = "localhost";
    private final int TEST_PORT = 1234;
    private final int TEST_SESSION_ID = 789;

    @Test
    void connectSuccessfullyInitializesSession() throws IOException, ClassNotFoundException {
        // arrange
        when(mockNetworkHandler.connect(TEST_HOST, TEST_PORT)).thenReturn(TEST_SESSION_ID);

        // act
        cliClient.connect(TEST_HOST, TEST_PORT);
        ClientSession session = cliClient.getCurrentSession();

        // assert
        assertNotNull(session);
        assertEquals(TEST_SESSION_ID, session.getSessionId());
        assertEquals(new InetSocketAddress(TEST_HOST, TEST_PORT), session.getClientAddress());
        assertTrue(session.isActive());
    }

    @Test
    void connectHandlesIOExceptionFromNetworkHandler() throws IOException, ClassNotFoundException {
        // arrange
        when(mockNetworkHandler.connect(TEST_HOST, TEST_PORT)).thenThrow(new IOException("Connection failed"));

        // act
        cliClient.connect(TEST_HOST, TEST_PORT);

        // assert
        assertThrows(AssertionError.class, () -> cliClient.getCurrentSession(),
                "Session should not be initialized after a failed connection.");
    }

    @Test
    void connectHandlesClassNotFoundExceptionFromNetworkHandler() throws IOException, ClassNotFoundException {
        // arrange
        when(mockNetworkHandler.connect(TEST_HOST, TEST_PORT)).thenThrow(new ClassNotFoundException("Session ID class issue"));

        // act
        cliClient.connect(TEST_HOST, TEST_PORT);

        // assert
        assertThrows(AssertionError.class, () -> cliClient.getCurrentSession(),
                "Session should not be initialized after a ClassNotFoundException.");
    }

    @Test
    void sendCommandDelegatesToNetworkHandler() throws IOException, ClassNotFoundException {
        // arrange
        when(mockNetworkHandler.sendCommand(mockCommand)).thenReturn(mockResponse);

        // act
        Response actualResponse = cliClient.sendCommand(mockCommand);

        // assert
        assertSame(mockResponse, actualResponse);
        verify(mockNetworkHandler).sendCommand(mockCommand);
    }

    @Test
    void sendCommandPropagatesIOException() throws IOException, ClassNotFoundException {
        // arrange
        when(mockNetworkHandler.sendCommand(mockCommand)).thenThrow(new IOException("Network send error"));

        // act & assert
        assertThrows(IOException.class, () -> cliClient.sendCommand(mockCommand));
    }

    @Test
    void sendCommandPropagatesClassNotFoundException() throws IOException, ClassNotFoundException {
        // arrange
        when(mockNetworkHandler.sendCommand(mockCommand)).thenThrow(new ClassNotFoundException("Response class error"));

        // act & assert
        assertThrows(ClassNotFoundException.class, () -> cliClient.sendCommand(mockCommand));
    }

    @Test
    void isConnectionOpenDelegatesToNetworkHandler() {
        // arrange
        when(mockNetworkHandler.isConnected()).thenReturn(true);
        when(mockNetworkHandler.isConnected()).thenReturn(false);

        // act & assert
        assertFalse(cliClient.isConnectionOpen());
        assertFalse(cliClient.isConnectionOpen());
        verify(mockNetworkHandler, times(2)).isConnected();
    }

    @Test
    void getCurrentSessionReturnsSessionAfterSuccessfulConnect() throws IOException, ClassNotFoundException {
        // arrange
        when(mockNetworkHandler.connect(TEST_HOST, TEST_PORT)).thenReturn(TEST_SESSION_ID);

        // act
        cliClient.connect(TEST_HOST, TEST_PORT);
        ClientSession session = cliClient.getCurrentSession();

        // assert
        assertNotNull(session);
        assertEquals(TEST_SESSION_ID, session.getSessionId());
    }

    @Test
    void getCurrentSessionThrowsAssertionErrorIfNotConnected() {
        // assert
        assertThrows(AssertionError.class, () -> cliClient.getCurrentSession());
    }

    @Test
    void startCallsUiStartWhenConnected() throws IOException, ClassNotFoundException {
        // arrange
        when(mockNetworkHandler.connect(anyString(), anyInt())).thenReturn(TEST_SESSION_ID);

        // act && assert
        cliClient.connect(TEST_HOST, TEST_PORT);
        when(mockNetworkHandler.isConnected()).thenReturn(true);

        cliClient.start();
        verify(mockUi).start(cliClient);
    }

    @Test
    void startDoesNotCallUiStartWhenNotConnected() {
        // arrange
        when(mockNetworkHandler.isConnected()).thenReturn(false);

        // act
        cliClient.start();

        // assert
        verify(mockUi, never()).start(cliClient);
    }

    @Test
    void disconnectDelegatesToNetworkHandler() throws IOException {
        // act
        cliClient.disconnect();

        // arrange
        verify(mockNetworkHandler).disconnect();
    }

    @Test
    void disconnectHandlesIOExceptionFromNetworkHandler() throws IOException {
        // arrange
        doThrow(new IOException("Disconnect error")).when(mockNetworkHandler).disconnect();

        // act && assert
        assertDoesNotThrow(() -> cliClient.disconnect());
    }
}