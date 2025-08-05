package cz.cuni.mff.danekji.calendar.client.cli;

import cz.cuni.mff.danekji.calendar.core.commands.Command;
import cz.cuni.mff.danekji.calendar.core.responses.Response;

import java.io.IOException;

/**
 * Interface for handling network operations in the calendar client.
 * This interface defines methods for connecting to a server, sending commands,
 * receiving responses, and managing the connection state.
 */
public interface NetworkHandler {
    /**
     * Connects to the server at the specified host and port.
     *
     * @param host The hostname or IP address of the server.
     * @param port The port number on which the server is listening.
     * @return The session ID received from the server.
     * @throws IOException If an I/O error occurs during connection.
     * @throws ClassNotFoundException If the session ID cannot be deserialized.
     */
    int connect(String host, int port) throws IOException, ClassNotFoundException;

    /**
     * Sends a command to the server and waits for a response.
     *
     * @param command The command to send.
     * @return The response from the server.
     * @throws IOException If an I/O error occurs while sending or receiving data.
     * @throws ClassNotFoundException If the response cannot be deserialized.
     */
    Response sendCommand(Command command) throws IOException, ClassNotFoundException;

    /**
     * Closes the connection to the server.
     *
     * @throws IOException If an I/O error occurs while closing the connection.
     */
    void disconnect() throws IOException;

    /**
     * Checks if the network handler is currently connected to the server.
     *
     * @return true if connected, false otherwise.
     */
    boolean isConnected();
}