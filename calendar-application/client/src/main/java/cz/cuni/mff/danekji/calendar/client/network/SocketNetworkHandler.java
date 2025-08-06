package cz.cuni.mff.danekji.calendar.client.network;

import cz.cuni.mff.danekji.calendar.core.commands.Command;
import cz.cuni.mff.danekji.calendar.core.responses.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Represents a network handler that communicates using object streams over a socket.
 * This handler is responsible for connecting to a server, sending commands, receiving responses,
 */
public class SocketNetworkHandler implements NetworkHandler {
    private Socket socket = null;
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;

    /**
     * Default constructor for SocketNetworkHandler.
     * The initialization of the object sockets will be done in the connect method.
     */
    public SocketNetworkHandler() {}

    /**
     * Establishes a connection to the server at the specified host and port.
     *
     * @param host The hostname or IP address of the server.
     * @param port The port number on which the server is listening.
     * @return The session ID received from the server.
     * @throws IOException While invalid session ID is received or if an I/O error occurs during connection.
     * @throws ClassNotFoundException If the session ID cannot be deserialized.
     */
    @Override
    public int connect(String host, int port) throws IOException, ClassNotFoundException {
        socket = new Socket(host, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

        Object obj = in.readObject();
        if (obj instanceof Integer sessionId) {
            return sessionId;
        } else {
            throw new IOException("Expected session ID but received: " + obj);
        }
    }

    /**
     * Sends a command to the server and waits for a response.
     *
     * @param command The command to send.
     * @return The response from the server.
     * @throws IOException            If an I/O error occurs while sending or receiving data.
     * @throws ClassNotFoundException If the response cannot be deserialized.
     */
    @Override
    public Response sendCommand(Command command) throws IOException, ClassNotFoundException {
        out.writeObject(command);
        out.flush();
        return (Response) in.readObject();
    }

    /**
     * Closes the connection to the server.
     *
     * @throws IOException If an I/O error occurs while closing the socket.
     */
    @Override
    public void disconnect() throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    /**
     * Checks if the network handler is currently connected to the server.
     *
     * @return true if connected, false otherwise.
     */
    @Override
    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }
}