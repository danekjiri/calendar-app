package cz.cuni.mff.danekji1.calendar.client.cli;

import cz.cuni.mff.danekji1.calendar.client.cli.ui.CLIUserInterface;
import cz.cuni.mff.danekji1.calendar.core.commands.Command;
import cz.cuni.mff.danekji1.calendar.core.responses.error.ErrorResponse;
import cz.cuni.mff.danekji1.calendar.core.responses.Response;
import cz.cuni.mff.danekji1.calendar.core.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import cz.cuni.mff.danekji1.calendar.core.ui.ClientState;
import cz.cuni.mff.danekji1.calendar.core.ui.UserInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Handles network communication with the server.
 */
public final class CLIClient implements Client {
    private static final Logger LOGGER = LogManager.getLogger(CLIClient.class);

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private final UserInterface ui;
    private ClientSession session;

    public boolean isConnectionOpen() {
        return socket != null &&  socket.isConnected() && !socket.isClosed();
    }

    public CLIClient(UserInterface ui) {
        this.ui = ui;
    }

    @Override
    public ClientState getCurrentSession() {
        assert session != null;
        return session;
    }

    @Override
    public void disconnect() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Connects to the server at the specified host and port.
     * Initializes input and output streams for communication.
     */
    public void connect(String host, int port) {
        try {
            socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            int sessionId = (int) in.readObject();
            session = new ClientSession(sessionId);
            LOGGER.info("Connected to '{}:{}' with sessionId '{}'", host, port, sessionId);
        } catch (Exception e) {
            LOGGER.fatal("Failed to connect to '{}:{}', because of '{}'", host, port, e);
        }
    }

    /**
     * Sends a command to the server and receives the response.
     */
    public Response sendCommand(Command command) {
        LOGGER.debug("Sending command '{}'", command);

        try {
            out.writeObject(command);
            out.flush();

            return (Response) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.fatal("Failed to send command", e);
            return new ErrorResponse("Failed to send command");
        }
    }

    public void start() {
        ui.start(this);
    }

    /**
     * Main method to run the client.
     * @param args unused
     */
    public static void main(String[] args) {
        UserInterface ui = new CLIUserInterface(System.in, System.out);

        Client client = new CLIClient(ui);
        client.connect("127.0.0.1", 8080);
        client.start();
    }
}
