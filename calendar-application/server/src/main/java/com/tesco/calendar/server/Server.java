package com.tesco.calendar.server;


import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.tesco.calendar.server.storage.UserRepository;
import com.tesco.calendar.server.storage.EventRepository;
import com.tesco.calendar.core.commands.Command;
import com.tesco.calendar.core.responses.Response;

/**
 * Main server class that manages client connections and dispatches commands.
 */
public class Server {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

    public Server(UserRepository userRepository, EventRepository eventRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    /**
     * Starts the server and listens for incoming connections.
     * Each connection is handled in a separate thread.
     */
    public void start(int port) {
//        // example of very simple implementation
//        try (ServerSocket serverSocket = new ServerSocket(port)) {
//            while (true) {
//                Socket clientSocket = serverSocket.accept();
//                new Thread(() -> handleClient(clientSocket)).start();
//            }
//        } catch (Exception e) {
//            // Log exception.
//            e.printStackTrace();
//        }
    }

    private void handleClient(Socket clientSocket) {
        // new session for each client
        Session session = new Session();

        try (ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {

            Object clientsCommand;
            while ((clientsCommand = in.readObject()) != null) {
                Command command = (Command) clientsCommand;

                Response response = dispatchCommand(command, session);
                out.writeObject(response);
                out.flush();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error while handling client command", e);
        }
    }

    /**
     * Dispatch the command using a CommandDispatcher (visitor pattern).
     */
    public Response dispatchCommand(Command command, Session session) {
        CommandDispatcher dispatcher = new CommandDispatcher(session, userRepository, eventRepository);
        return command.accept(dispatcher);
    }

    public static void main(String[] args) {
//        // Example usage
//        UserRepository userRepository = new ...
//        EventRepository eventRepository = new ...
//        Server server = new Server(userRepository, eventRepository);
//        server.start(8080);
    }
}
