package cz.cuni.mff.danekji.calendar.client;

import cz.cuni.mff.danekji.calendar.client.network.NetworkHandler;
import cz.cuni.mff.danekji.calendar.core.client.Client;
import cz.cuni.mff.danekji.calendar.core.client.session.ClientSession;
import cz.cuni.mff.danekji.calendar.core.commands.Command;
import cz.cuni.mff.danekji.calendar.core.responses.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * An abstract base class for client implementations.
 * It handles the common logic for network communication and session management.
 */
public abstract class AbstractClient implements Client {
    private static final Logger LOGGER = LogManager.getLogger(AbstractClient.class);

    /**
     * The network handler responsible for managing the connection to the server.
     */
    protected final NetworkHandler networkHandler;
    /**
     * The current client session, which contains session-specific information.
     */
    private ClientSession session;

    /**
     * Constructs an AbstractClient with a given network handler.
     *
     * @param networkHandler The network handler for managing the server connection.
     */
    public AbstractClient(NetworkHandler networkHandler) {
        this.networkHandler = networkHandler;
    }

    /**
     * Connects to the server at the specified host and port.
     * This method initializes a session and logs the connection status.
     */
    @Override
    public boolean connect(String host, int port) {
        try {
            int sessionId = networkHandler.connect(host, port);
            session = new ClientSession(sessionId, new InetSocketAddress(host, port));
            LOGGER.info("Connected to '{}:{}' with sessionId '{}'", host, port, sessionId);
        } catch (IOException e) {
            LOGGER.fatal("Failed to connect to '{}:{}' - {}", host, port, e.getMessage());
            return false;
        } catch (ClassNotFoundException e) {
            LOGGER.fatal("Failed to process session ID from '{}:{}'", host, port, e);
            return false;
        }
        return true;
    }

    /**
     * Sends a command to the server and returns the response.
     *
     * @param command The command to be sent.
     * @return The response from the server.
     * @throws IOException If an I/O error occurs during communication.
     * @throws ClassNotFoundException If the response class cannot be found.
     */
    @Override
    public Response sendCommand(Command command) throws IOException, ClassNotFoundException {
        LOGGER.debug("Sending command '{}'", command);
        return networkHandler.sendCommand(command);
    }

    /**
     * Checks if the client is currently connected to the server.
     *
     * @return true if the connection is open, false otherwise.
     */
    @Override
    public boolean isConnectionOpen() {
        return networkHandler.isConnected();
    }

    /**
     * Retrieves the current client session.
     *
     * @return The current ClientSession.
     * @throws IllegalStateException if the session has not been initialized.
     */
    public ClientSession getCurrentSession() {
        if (session == null) {
            throw new IllegalStateException("Session has not been initialized. Client might not be connected.");
        }
        return session;
    }

    /**
     * Disconnects the client from the server by closing the network connection.
     */
    public void disconnect() {
        try {
            if (networkHandler != null && networkHandler.isConnected()) {
                networkHandler.disconnect();
                LOGGER.info("Disconnected from the server.");
            }
        } catch (IOException e) {
            LOGGER.error("Failed to disconnect cleanly.", e);
        }
    }
}