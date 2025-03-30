package com.tesco.calendar.client;

import com.tesco.calendar.core.commands.Command;
import com.tesco.calendar.core.responses.Response;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Handles network communication with the server.
 */
public class Client {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private final UserInterface ui;

    public Client(UserInterface ui) {
        this.ui = ui;
    }

    /**
     * Connects to the server at the specified host and port.
     * Initializes input and output streams for communication.
     */
    public void connect(String host, int port) {
        throw new UnsupportedOperationException("Not implemented yet");
        // todo: implement this method
    }

    /**
     * Sends a command to the server and receives the response.
     */
    public Response sendCommand(Command command) {
        throw new UnsupportedOperationException("Not implemented yet");
        // todo: implement this method
    }

    public void start() {
        ui.start(this);
    }

    public static void main(String[] args) {
//        // Example usage - the server should be running
//        UserInterface ui = new CLIUserInterface(System.in, System.out);
//        Client client = new Client(ui);
//        client.connect("127.0.0.1", 8080);
//        client.start();
    }
}
