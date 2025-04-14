package cz.cuni.mff.danekji1.calendar.client.ui;

import cz.cuni.mff.danekji1.calendar.core.commands.Command;
import cz.cuni.mff.danekji1.calendar.core.commands.CreateAccountCommand;
import cz.cuni.mff.danekji1.calendar.core.exceptions.InvalidInputException;
import cz.cuni.mff.danekji1.calendar.core.exceptions.UnknownCommandException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Parses user input and returns a finished Command.
 * Each command type has its own static create() method.
 */
public final class CommandParser {
    private static final Logger LOGGER = LogManager.getLogger();

    public static Command parse(String input, UserInterface ui) throws IOException {
        // Based on the input string (e.g. "login", "create_account", "add_event", etc.),
        var command = switch (input.toLowerCase()) {
            case "create_account" -> buildCreateAccountCommand(ui);
            default -> throw new UnknownCommandException("Unknown command: '" + input + "'", input);
        };

        return command;
    }

    /**
     * Creates a finished CreateAccountCommand by prompting the user.
     */
    public static CreateAccountCommand buildCreateAccountCommand(UserInterface ui) throws IOException {
        String username = ui.promptForInput("Enter username: ").trim();
        if (username.isEmpty() || username.equals("unlogged")) {
            throw new InvalidInputException("Username cannot be empty or 'unlogged'");
        }

        String password = ui.promptForInput("Enter password: ");
        if (password.isEmpty()) {
            throw new InvalidInputException("Password cannot be empty");
        }

        int passwordHash = password.hashCode();
        return new CreateAccountCommand(username, passwordHash);
    }
}


