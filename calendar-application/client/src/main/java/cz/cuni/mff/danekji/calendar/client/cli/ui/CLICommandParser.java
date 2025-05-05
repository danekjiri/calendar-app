package cz.cuni.mff.danekji.calendar.client.cli.ui;

import cz.cuni.mff.danekji.calendar.core.commands.Command;
import cz.cuni.mff.danekji.calendar.core.exceptions.client.InvalidInputException;
import cz.cuni.mff.danekji.calendar.core.exceptions.client.UnknownCommandException;

import cz.cuni.mff.danekji.calendar.core.client.session.ClientSession;
import cz.cuni.mff.danekji.calendar.core.client.ui.UserInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Parses command-line input and maps it to the corresponding command classes.
 * This class is responsible for registering commands and creating instances of them using reflection API.
 */
public final class CLICommandParser {
    private static final Logger LOGGER = LogManager.getLogger(CLICommandParser.class);

    /**
     * Default constructor for CLICommandParser.
     */
    public CLICommandParser() {}

    private final Map<String, Class<? extends Command>> commandRegistry = new HashMap<>();

    /**
     * Registers a command name with its corresponding builder function.
     * Can be called from the static initializer or potentially elsewhere
     * if dynamic registration is needed.
     *
     * @param commandName The name used to invoke the command.
     * @param clazz The class of the command to be registered.
     */
    public void registerCommand(String commandName, Class<? extends Command> clazz) {
        assert commandName != null && clazz != null;

        if (commandRegistry.put(commandName, clazz) != null) {
            LOGGER.warn("Command name '{}' is already registered. Overwriting...", commandName);
        }
        LOGGER.debug("Registered command: {}", commandName);
    }

    /**
     * Gets an unmodifiable view of the command registry.
     *
     * @return An unmodifiable map of command names to their corresponding classes.
     */
    public Map<String, Class<? extends Command>> getUnmodifiableCommandRegistry() {
        return Collections.unmodifiableMap(commandRegistry);
    }

    /**
     * Parses the input string and returns the corresponding command instance
     *  using reflection API and the private no argument constructor of corresponding class.
     *
     * @param input The input string to parse.
     * @param ui The user interface instance.
     * @param session The client session.
     * @return An instance of the command corresponding to the input string.
     * @throws IOException If an I/O error occurs during command creation.
     * @throws InvalidInputException If the input is invalid or cannot be parsed.
     * @throws UnknownCommandException If the command is not recognized.
     */
    public Command parse(String input, UserInterface ui, ClientSession session) throws IOException, InvalidInputException, UnknownCommandException {
        String commandName = input.toLowerCase().trim();

        var commandClass = commandRegistry.get(commandName);
        if (commandClass == null) {
            throw new UnknownCommandException("Unknown command: '" + input + "'", input);
        }

        try {
            // todo: add annotation to command class that it should have default constructor for reflection
            Constructor<? extends Command> constructor = commandClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            Command tempCommand = constructor.newInstance();
            return tempCommand.buildCommand(ui, session);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to create command instance for: " + commandName, e);
        }
    }
}