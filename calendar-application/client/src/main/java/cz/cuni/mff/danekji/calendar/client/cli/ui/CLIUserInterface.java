package cz.cuni.mff.danekji.calendar.client.cli.ui;

import cz.cuni.mff.danekji.calendar.core.client.Client;
import cz.cuni.mff.danekji.calendar.core.commands.*;
import cz.cuni.mff.danekji.calendar.core.responses.Response;
import cz.cuni.mff.danekji.calendar.core.responses.ResponseVisitor;
import cz.cuni.mff.danekji.calendar.core.exceptions.InsufficientCommandPrivilegesException;
import cz.cuni.mff.danekji.calendar.core.client.session.ClientSession;
import cz.cuni.mff.danekji.calendar.core.client.ui.UserInterface;
import cz.cuni.mff.danekji.calendar.core.exceptions.client.InvalidInputException;
import cz.cuni.mff.danekji.calendar.core.exceptions.client.UnknownCommandException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.SocketException;
import java.util.Map;
import java.util.Scanner;

/**
 * Command Line Interface (CLI) for the calendar client.
 * This class handles user input and output, command parsing, and response handling.
 */
public final class CLIUserInterface implements UserInterface {
    private static final Logger LOGGER = LogManager.getLogger(CLIUserInterface.class);

    private final Scanner userInput;
    private final OutputStreamWriter userOutput;
    private final ResponseVisitor<Void, ClientSession> responseDispatcher;
    private final CLICommandParser parser;

    /**
     * Constructor for CLIUserInterface.
     * Initializes the input and output streams and registers commands.
     *
     * @param userInput  Input stream for user input (e.g., System.in).
     * @param userOutput Output stream for user output (e.g., System.out).
     */
    public CLIUserInterface(InputStream userInput, OutputStream userOutput) {
        this.userInput = new Scanner(userInput);
        this.userOutput = new OutputStreamWriter(userOutput);
        this.responseDispatcher = new CLIResponseDispatcher(userOutput);

        this.parser = new CLICommandParser();
        // Register all commands
        parser.registerCommand(CreateAccountCommand.COMMAND_NAME, CreateAccountCommand.class);
        parser.registerCommand(LoginCommand.COMMAND_NAME, LoginCommand.class);
        parser.registerCommand(AddEventCommand.COMMAND_NAME, AddEventCommand.class);
        parser.registerCommand(LogoutCommand.COMMAND_NAME, LogoutCommand.class);
        parser.registerCommand(HelpCommand.COMMAND_NAME, HelpCommand.class);
        parser.registerCommand(ShowEventsCommand.COMMAND_NAME, ShowEventsCommand.class);
        parser.registerCommand(DeleteEventCommand.COMMAND_NAME, DeleteEventCommand.class);
        parser.registerCommand(UpdateEventCommand.COMMAND_NAME, UpdateEventCommand.class);
        parser.registerCommand(QuitCommand.COMMAND_NAME, QuitCommand.class);
        // ... add more commands as needed
    }

    /**
     * Gets an unmodifiable view of the command registry.
     *
     * @return An unmodifiable map of command names to their corresponding classes.
     */
    @Override
    public Map<String, Class<? extends Command>> getUnmodifiableCommandRegistry() {
        return parser.getUnmodifiableCommandRegistry();
    }

    /**
     * The main entry point for the CLI.
     * It reads commands from the console and sends them to the client.
     *
     * @param client The client that will handle the network communication.
     */
    @Override
    public void start(Client client) {
        if (!client.isConnectionOpen()) {
           LOGGER.fatal("Connection to server is not open. Please connect to the server first.");
           return;
        }

        displayHelpMessage(client);

        LOGGER.debug("Starting user interface, waiting for commands...");
        while(client.isConnectionOpen() && client.getCurrentSession().isActive()) {
            try {
                String prompt = formatUserPrompt(client.getCurrentSession());
                String inputCommand = promptForInput(prompt).trim();

                Command command = parser.parse(inputCommand, this, client.getCurrentSession());
                Response response = client.sendCommand(command);
                displayResponse(response, client.getCurrentSession());
            } catch (SocketException e) {
                LOGGER.fatal("Failed to send command to the server, try reconnection to the server");
                break;
            } catch (UnknownCommandException | InsufficientCommandPrivilegesException | InvalidInputException e) {
                LOGGER.error(e.getMessage());
            } catch(IOException e) {
                LOGGER.error("Failed to read command from user or display response: {}", e.getMessage());
            } catch (Exception e) {
                LOGGER.error("An unexpected error occurred: {}", e.getMessage());
            }
        }
    }

    /**
     * Displays the help message to the user.
     * This method sends a request to the server for help information and displays the response.
     *
     * @param client The client instance to send the help command.
     */
    private void displayHelpMessage(Client client) {
        try {
            Command helpCommand = new HelpCommand(parser.getUnmodifiableCommandRegistry());
            Response helpResponse = client.sendCommand(helpCommand);
            displayResponse(helpResponse, client.getCurrentSession());
        } catch (Exception e) {
            LOGGER.error("Failed to display help message");
        }
    }

    /**
     * Formats the prompt for the user.
     * The prompt includes the username and a fixed string.
     *
     * @param session The current client session.
     * @return The formatted prompt string.
     */
    private String formatUserPrompt(ClientSession session) {
        return String.format("\n$%s@calendar> ", session.getCurrentUser() != null
                ? session.getCurrentUser().username() : "unlogged");
    }

    /**
     * Displays the response from the server.
     * This method uses the Visitor pattern to handle different types of responses.
     *
     * @param response The response to display.
     * @param session The current client session.
     */
    @Override
    public void displayResponse(Response response, ClientSession session) throws IOException {
        response.accept(responseDispatcher, session);
    }

    /**
     * Displays a message to the user and returns the users input.
     * This method writes the message to the output stream and flushes it.
     *
     * @param message The message to display to the user.
     * @return The user's input as a string.
     */
    @Override
    public String promptForInput(String message) throws IOException {
        userOutput.write(message);
        userOutput.flush();

        return userInput.nextLine();
    }
}
