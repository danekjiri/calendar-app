package cz.cuni.mff.danekji1.calendar.client.ui;

import cz.cuni.mff.danekji1.calendar.client.Client;
import cz.cuni.mff.danekji1.calendar.core.commands.Command;
import cz.cuni.mff.danekji1.calendar.core.exceptions.InvalidInputException;
import cz.cuni.mff.danekji1.calendar.core.exceptions.UnknownCommandException;
import cz.cuni.mff.danekji1.calendar.core.models.User;
import cz.cuni.mff.danekji1.calendar.core.responses.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Scanner;

/**
 * A simple command-line user interface.
 */
public final class CLIUserInterface implements UserInterface {
    private static final Logger LOGGER = LogManager.getLogger(CLIUserInterface.class);

    private final Scanner userInput;
    private final OutputStreamWriter userOutput;
    private final ResponseVisitor<Void> responseDispatcher;

    private User user = null;

    public CLIUserInterface(InputStream userInput, OutputStream userOutput) {
        this.userInput = new Scanner(userInput);
        this.userOutput = new OutputStreamWriter(userOutput);
        this.responseDispatcher = new DefaultCLIResponseDispatcher(this);
    }

    public void setUser(User user) {
        this.user = user;
    }

    /**
     * The main entry point for the CLI.
     * It reads commands from the console and sends them to the client.
     */
    @Override
    public void start(Client client) {
        if (!client.isConnectionOpen()) {
           LOGGER.fatal("Connection to server is not open. Please connect to the server first.");
           return;
        }

        LOGGER.debug("Starting user interface, waiting for commands...");
        while(client.isConnectionOpen()) {
            try {
                String prompt = formatUserPrompt();
                String inputCommand = promptForInput(prompt).trim();
                Command command = CLICommandParser.parse(inputCommand, this);

                Response response = client.sendCommand(command);
                displayResponse(response);
            } catch (IOException e) {
                LOGGER.error("Failed to read command from user");
            } catch (UnknownCommandException | InvalidInputException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    /**
     * Formats the prompt for the user.
     * The prompt includes the username and a fixed string.
     */
    private String formatUserPrompt() {
        return String.format("\n$%s@calendar> ", user != null ? user.username() : "unlogged");
    }

    /**
     * Displays the response from the server.
     * This method uses the Visitor pattern to handle different types of responses.
     */
    @Override
    public void displayResponse(Response response) {
        response.accept(responseDispatcher);
    }

    @Override
    public String promptForInput(String message) throws IOException {
        userOutput.write(message);
        userOutput.flush();

        return userInput.nextLine();
    }
}
