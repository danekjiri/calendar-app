package cz.cuni.mff.danekji1.calendar.client.cli.ui;

import cz.cuni.mff.danekji1.calendar.core.Client;
import cz.cuni.mff.danekji1.calendar.core.ui.ClientState;
import cz.cuni.mff.danekji1.calendar.core.ui.UserInterface;
import cz.cuni.mff.danekji1.calendar.core.commands.Command;
import cz.cuni.mff.danekji1.calendar.core.exceptions.client.InvalidInputException;
import cz.cuni.mff.danekji1.calendar.core.exceptions.client.UnknownCommandException;
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
    private final ResponseVisitor<Void, ClientState> responseDispatcher;

    public CLIUserInterface(InputStream userInput, OutputStream userOutput) {
        this.userInput = new Scanner(userInput);
        this.userOutput = new OutputStreamWriter(userOutput);
        this.responseDispatcher = new DefaultCLIResponseDispatcher();
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
                String prompt = formatUserPrompt(client.getCurrentSession());
                String inputCommand = promptForInput(prompt).trim();

                Command command = CLICommandParser.parse(inputCommand, this, client.getCurrentSession());
                Response response = client.sendCommand(command);
                displayResponse(response, client.getCurrentSession());
            } catch (IOException e) {
                LOGGER.error("Failed to read command from user");
            } catch (UnknownCommandException | InvalidInputException e) {
                LOGGER.error(e.getMessage());
            } catch (Exception e) {
                LOGGER.error("An unexpected error occurred: {}", e.getMessage());
            }
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
    public void displayResponse(Response response, ClientState session) {
        response.accept(responseDispatcher, session);
    }

    @Override
    public String promptForInput(String message) throws IOException {
        userOutput.write(message);
        userOutput.flush();

        return userInput.nextLine();
    }

//    public void displayHelp() {
//        tr {
//            userOutput.write("Available commands:\n");
//        } catch (IOException e) {
//            LOGGER.error("Failed to write help message");
//            return;
//        }
//
//        for (var commandClass : Command.class.getDeclaredClasses()) {
//            try {
//                var commandInstance = (Command) commandClass.getDeclaredConstructor().newInstance();
//                Privileges currentPrivileges = user == null ? Privileges.UNLOGGED : Privileges.LOGGED;
//                if (commandInstance.getPrivileges() == Privileges.ALL || currentPrivileges == commandInstance.getPrivileges()) {
//                    userOutput.write(commandInstance.getDescription() + "\n");
//                }
//            } catch (Exception e) {
//                LOGGER.error("Failed to write help message");
//            }
//        }
//
//    }

}
