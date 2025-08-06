package cz.cuni.mff.danekji.calendar.core.client;

import cz.cuni.mff.danekji.calendar.core.commands.Command;
import cz.cuni.mff.danekji.calendar.core.responses.Response;
import cz.cuni.mff.danekji.calendar.core.client.session.ClientSession;

import java.io.IOException;

/**
 * Interface for the client application.
 * This interface defines the methods that the client should implement.
 */
public interface Client {
    /**
     * Connects to the server.
     *
     * @param host The host name or IP address of the server
     * @param port The port number of the server
     */
    boolean connect(String host, int port);

    /**
     * Starts the client application in the UI.
     */
    void start();

    /**
     * Checks if the connection to the server is open.
     *
     * @return true if the connection is open, false otherwise
     */
    boolean isConnectionOpen();

    /**
     * Gets the current session of the client.
     *
     * @return The current client session
     */
    ClientSession getCurrentSession();

    /**
     * Sends a command to the server using the network.
     *
     * @param command The command to be sent
     * @return The response from the server
     * @throws ClassNotFoundException if the class of a serialized object cannot be found
     * @throws IOException if an I/O error occurs
     */
    Response sendCommand(Command command) throws ClassNotFoundException, IOException;
}
