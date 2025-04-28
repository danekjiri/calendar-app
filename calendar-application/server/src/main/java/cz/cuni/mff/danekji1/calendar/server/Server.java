package cz.cuni.mff.danekji1.calendar.server;


import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cz.cuni.mff.danekji1.calendar.core.commands.CommandVisitor;
import cz.cuni.mff.danekji1.calendar.server.storage.XMLEventRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.danekji1.calendar.server.storage.EventRepository;
import cz.cuni.mff.danekji1.calendar.core.commands.Command;
import cz.cuni.mff.danekji1.calendar.core.responses.Response;

/**
 * Main server class that manages client connections and dispatches commands.
 */
public class Server {
    private static final Logger LOGGER = LogManager.getLogger(Server.class.getName());

    private final EventRepository eventRepository;
    // todo: think about more logical solution
    private static final Random RANDOM = new Random(42);
    private final Map<Integer, Session> sessions = Collections.synchronizedMap(new HashMap<>());

    public Server(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
        LOGGER.info("Server initialized.");
    }

    /**
     * Creates a new session for a client.
     * Each session is identified by a unique session ID.
     */
    private synchronized int getUniqueSessionId() {
        int sessionId;
        do {
            sessionId = RANDOM.nextInt(Integer.MAX_VALUE);
        } while (sessions.containsKey(sessionId));

        return sessionId;
    }

    /**
     * Starts the server and listens for incoming connections.
     * Each connection is handled in a separate thread.
     */
    public void start(int port) {
        // example of very simple implementation
        try (ServerSocket serverSocket = new ServerSocket(port);
             ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            LOGGER.info("Server started on port {}", port);

            while (true) {
                // accept
                Socket clientSocket = serverSocket.accept();

                // create session
                int sessionId = getUniqueSessionId();
                var prev = sessions.put(sessionId, new Session(sessionId));
                assert prev == null;

                // todo: clients limitation
                executor.submit(() -> handleClient(clientSocket, sessionId));
            }
        } catch (Exception e) {
            LOGGER.fatal("Failed to start server on port '{}', because of '{}'", port, e);
        }
    }

    /**
     * Handles client connections in separate thread.
     * Each client is assigned a session ID and can send commands to the server.
     */
    private void handleClient(Socket clientSocket, int sessionId) {
        // new session for each client
        CommandVisitor<Response, Session> commandDispatcher = new DefaultCommandDispatcher(eventRepository);
        try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {
            // send sessionId to client
            out.writeObject(sessionId);
            LOGGER.info("Client connected: '{}' with sessionId '{}'", clientSocket.getRemoteSocketAddress(), sessionId);

            while (sessions.containsKey(sessionId)) {
                Command command = (Command) in.readObject();
                LOGGER.debug("Received command: {}", command);

                Response response = command.accept(commandDispatcher, sessions.get(sessionId));
                out.writeObject(response);
                out.flush();
                LOGGER.debug("Sent response: {}", response);
            }
        } catch (Exception e) {
            LOGGER.error("Error handling client: {}", e.getMessage());
        }
    }


    public static void main(String[] args) {
        Server server = new Server(
                new XMLEventRepository()
        );

        server.start(8080);
    }
}
