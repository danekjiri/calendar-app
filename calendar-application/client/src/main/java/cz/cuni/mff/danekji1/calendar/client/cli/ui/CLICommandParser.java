package cz.cuni.mff.danekji1.calendar.client.cli.ui;

import cz.cuni.mff.danekji1.calendar.core.commands.*;
import cz.cuni.mff.danekji1.calendar.core.exceptions.client.InvalidInputException;
import cz.cuni.mff.danekji1.calendar.core.exceptions.client.UnknownCommandException;

import cz.cuni.mff.danekji1.calendar.core.ui.ClientState;
import cz.cuni.mff.danekji1.calendar.core.ui.UserInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public final class CLICommandParser {
    private static final Logger LOGGER = LogManager.getLogger(CLICommandParser.class);
    private static final Map<String, Class<? extends Command>> COMMAND_REGISTRY = new HashMap<>();

    static {
        registerCommand(CreateAccountCommand.COMMAND_NAME, CreateAccountCommand.class);
        registerCommand(LoginCommand.COMMAND_NAME, LoginCommand.class);
        registerCommand(AddEventCommand.COMMAND_NAME, AddEventCommand.class);
        registerCommand(LogoutCommand.COMMAND_NAME, LogoutCommand.class);
        // Add other commands here
    }

    /**
     * Registers a command name with its corresponding builder function.
     * Can be called from the static initializer or potentially elsewhere
     * if dynamic registration is needed.
     * @param commandName The name used to invoke the command.
     * @param clazz The class of the command to be registered.
     */
    public static void registerCommand(String commandName, Class<? extends Command> clazz) {
        assert commandName != null && clazz != null;

        if (COMMAND_REGISTRY.put(commandName, clazz) != null) {
            LOGGER.warn("Command name '{}' is already registered. Overwriting.", commandName);
        }

        LOGGER.debug("Registered command: {}", commandName);
    }


    public static Command parse(String input, UserInterface ui, ClientState context) throws IOException, InvalidInputException, UnknownCommandException {
        String commandName = input.toLowerCase().trim();

        var commandClass = COMMAND_REGISTRY.get(commandName);
        if (commandClass == null) {
            throw new UnknownCommandException("Unknown command: '" + input + "'", input);
        }

        try {
            // todo: add annotation to class that should have default constructor
            Constructor<? extends Command> constructor = commandClass.getDeclaredConstructor();
            // Ensure accessibility
            constructor.setAccessible(true);
            // Create a temporary instance
            Command tempCommand = constructor.newInstance();
            return tempCommand.buildCommand(ui, context);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to create command instance for: " + commandName, e);
        }
    }
}