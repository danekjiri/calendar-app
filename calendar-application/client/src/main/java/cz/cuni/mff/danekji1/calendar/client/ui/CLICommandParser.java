package cz.cuni.mff.danekji1.calendar.client.ui;

import cz.cuni.mff.danekji1.calendar.core.commands.Command;
import cz.cuni.mff.danekji1.calendar.core.commands.CreateAccountCommand;
import cz.cuni.mff.danekji1.calendar.core.commands.LoginCommand;
import cz.cuni.mff.danekji1.calendar.core.exceptions.InvalidInputException;
import cz.cuni.mff.danekji1.calendar.core.exceptions.UnknownCommandException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class CLICommandParser {
    private static final Logger LOGGER = LogManager.getLogger(CLICommandParser.class);

    // Define a functional interface for the builder, allowing checked exceptions
    @FunctionalInterface
    interface CommandBuilder {
        Command build(UserInterface ui) throws IOException, InvalidInputException;
    }

    // Map from command name (String) to a builder function
    private static final Map<String, CommandBuilder> commandRegistry = new HashMap<>();

    // Static initializer block to register known commands
    static {
        registerCommand(CreateAccountCommand.COMMAND_NAME, CLICommandParser::buildCreateAccountCommand);
        registerCommand(LoginCommand.COMMAND_NAME, CLICommandParser::buildLoginCommand);
        // Add other commands here
    }

    /**
     * Registers a command name with its corresponding builder function.
     * Can be called from the static initializer or potentially elsewhere
     * if dynamic registration is needed.
     * @param commandName The name used to invoke the command.
     * @param builder The function to build the command.
     */
    public static void registerCommand(String commandName, CommandBuilder builder) {
        Objects.requireNonNull(commandName, "Command name cannot be null");
        Objects.requireNonNull(builder, "Command builder cannot be null");

        String normalizedName = commandName.toLowerCase().trim();
        if (commandRegistry.put(normalizedName, builder) != null) {
            LOGGER.warn("Command name '{}' is already registered. Overwriting.", normalizedName);
        }

        LOGGER.debug("Registered command: {}", normalizedName);
    }


    public static Command parse(String input, UserInterface ui) throws IOException, InvalidInputException, UnknownCommandException {
        String commandName = input.toLowerCase().trim();

        CommandBuilder builder = commandRegistry.get(commandName);
        if (builder == null) {
            LOGGER.warn("Unknown command received: '{}'", input);
            throw new UnknownCommandException("Unknown command: '" + input + "'", input);
        }
        return builder.build(ui);
    }

    private static CreateAccountCommand buildCreateAccountCommand(UserInterface ui) throws IOException, InvalidInputException {
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

    private static LoginCommand buildLoginCommand(UserInterface ui) throws IOException, InvalidInputException {
        String username = ui.promptForInput("Enter username: ").trim();
        if (username.isEmpty() || username.equals("unlogged")) {
            throw new InvalidInputException("Username cannot be empty or 'unlogged'");
        }

        String password = ui.promptForInput("Enter password: ");
        if (password.isEmpty()) {
            throw new InvalidInputException("Password cannot be empty");
        }

        int passwordHash = password.hashCode();
        return new LoginCommand(username, passwordHash);
    }
}