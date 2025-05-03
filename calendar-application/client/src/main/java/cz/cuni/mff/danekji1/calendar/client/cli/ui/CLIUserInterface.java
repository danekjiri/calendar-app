package cz.cuni.mff.danekji1.calendar.client.cli.ui;

import cz.cuni.mff.danekji1.calendar.core.Client;
import cz.cuni.mff.danekji1.calendar.core.commands.*;
import cz.cuni.mff.danekji1.calendar.core.ui.ClientState;
import cz.cuni.mff.danekji1.calendar.core.ui.UserInterface;
import cz.cuni.mff.danekji1.calendar.core.exceptions.client.InvalidInputException;
import cz.cuni.mff.danekji1.calendar.core.exceptions.client.UnknownCommandException;
import cz.cuni.mff.danekji1.calendar.core.responses.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Map;
import java.util.Scanner;

/**
 * A simple command-line user interface.
 */
public final class CLIUserInterface implements UserInterface {
    private static final Logger LOGGER = LogManager.getLogger(CLIUserInterface.class);

    private final Scanner userInput;
    private final OutputStreamWriter userOutput;
    private final ResponseVisitor<Void, ClientState> responseDispatcher;
    private final CLICommandParser parser;

    public CLIUserInterface(InputStream userInput, OutputStream userOutput) {
        this.userInput = new Scanner(userInput);
        this.userOutput = new OutputStreamWriter(userOutput);
        this.responseDispatcher = new DefaultCLIResponseDispatcher(userOutput);

        this.parser = new CLICommandParser();
        // Register all commands
        parser.registerCommand(CreateAccountCommand.COMMAND_NAME, CreateAccountCommand.class);
        parser.registerCommand(LoginCommand.COMMAND_NAME, LoginCommand.class);
        parser.registerCommand(AddEventCommand.COMMAND_NAME, AddEventCommand.class);
        parser.registerCommand(LogoutCommand.COMMAND_NAME, LogoutCommand.class);
        parser.registerCommand(HelpCommand.COMMAND_NAME, HelpCommand.class);
        parser.registerCommand(ShowEventsCommand.COMMAND_NAME, ShowEventsCommand.class);
        parser.registerCommand(DeleteEventCommand.COMMAND_NAME, DeleteEventCommand.class);
        // ... add more commands as needed
    }

    @Override
    public Map<String, Class<? extends Command>> getCommandRegistry() {
        return parser.getCommandRegistry();
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

        displayHelpMessage(client);

        LOGGER.debug("Starting user interface, waiting for commands...");
        while(client.isConnectionOpen()) {
            try {
                String prompt = formatUserPrompt(client.getCurrentSession());
                String inputCommand = promptForInput(prompt).trim();

                Command command = parser.parse(inputCommand, this, client.getCurrentSession());
                Response response = client.sendCommand(command);
                displayResponse(response, client.getCurrentSession());
            } catch (IOException e) {
                LOGGER.error("Failed to read command from user or display response: {}", e.getMessage());
            } catch (UnknownCommandException | InvalidInputException e) {
                LOGGER.error(e.getMessage());
            } catch (Exception e) {
                LOGGER.error("An unexpected error occurred: {}", e.getMessage());
            }
        }
    }

    private void displayHelpMessage(Client client) {
        try {
            Command helpCommand = new HelpCommand(parser.getCommandRegistry());
            Response helpResponse = client.sendCommand(helpCommand);
            displayResponse(helpResponse, client.getCurrentSession());
        } catch (Exception e) {
            LOGGER.error("Failed to display help message");
        }
    }

    /**
     * Formats the prompt for the user.
     * The prompt includes the username and a fixed string.
     */
    private String formatUserPrompt(ClientState session) {
        return String.format("\n$%s@calendar> ", session.getCurrentUser() != null
                ? session.getCurrentUser().username() : "unlogged");
    }

    /**
     * Displays the response from the server.
     * This method uses the Visitor pattern to handle different types of responses.
     */
    @Override
    public void displayResponse(Response response, ClientState session) throws IOException {
        response.accept(responseDispatcher, session);
    }

    @Override
    public String promptForInput(String message) throws IOException {
        userOutput.write(message);
        userOutput.flush();

        return userInput.nextLine();
    }
}
