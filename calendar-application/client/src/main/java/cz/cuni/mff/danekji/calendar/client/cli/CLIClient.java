package cz.cuni.mff.danekji.calendar.client.cli;

import cz.cuni.mff.danekji.calendar.client.cli.ui.CLIUserInterface;
import cz.cuni.mff.danekji.calendar.core.client.session.ClientSession;
import cz.cuni.mff.danekji.calendar.core.commands.Command;
import cz.cuni.mff.danekji.calendar.core.responses.Response;
import cz.cuni.mff.danekji.calendar.core.client.Client;
import cz.cuni.mff.danekji.calendar.core.client.ui.UserInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;

public final class CLIClient implements Client {
    private static final Logger LOGGER = LogManager.getLogger(CLIClient.class);

    private final NetworkHandler networkHandler;
    private final UserInterface ui;
    private ClientSession session;

    public CLIClient(UserInterface ui, NetworkHandler networkHandler) {
        this.ui = ui;
        this.networkHandler = networkHandler;
    }

    @Override
    public void connect(String host, int port) {
        try {
            int sessionId = networkHandler.connect(host, port);
            session = new ClientSession(sessionId, new InetSocketAddress(host, port));
            LOGGER.info("Connected to '{}:{}' with sessionId '{}'", host, port, sessionId);
        } catch (IOException e) {
            LOGGER.fatal("Failed to connect to '{}:{}' - {}", host, port, e.getMessage());
        } catch (ClassNotFoundException e) {
            LOGGER.fatal("Failed to process session ID from '{}:{}'", host, port, e);
        }
    }

    @Override
    public Response sendCommand(Command command) throws IOException, ClassNotFoundException {
        LOGGER.debug("Sending command '{}'", command);
        return networkHandler.sendCommand(command);
    }

    @Override
    public boolean isConnectionOpen() {
        return networkHandler.isConnected();
    }

    @Override
    public ClientSession getCurrentSession() {
        assert session != null;
        return session;
    }

    @Override
    public void start() {
        if (!isConnectionOpen()) {
            LOGGER.fatal("Connection to server is not open. Please connect to the server first.");
            return;
        }
        ui.start(this);
    }

    public void disconnect() {
        try {
            networkHandler.disconnect();
        } catch (IOException e) {
            LOGGER.error("Failed to disconnect", e);
        }
    }

    public static void main(String[] args) {
        if (args.length > 2 || args.length == 1) {
            LOGGER.error("Usage: mvn exec:java -Dexec.args=\"<server-address> <port>\"");
            return;
        }

        final String address = args.length == 0 ? "127.0.0.1" : args[0];
        final int port = args.length == 0 ? 8080 : Integer.parseInt(args[1]);

        UserInterface ui = new CLIUserInterface(System.in, System.out);
        Client client = new CLIClient(ui, new SocketNetworkHandler());
        client.connect(address, port);
        client.start();
    }
}