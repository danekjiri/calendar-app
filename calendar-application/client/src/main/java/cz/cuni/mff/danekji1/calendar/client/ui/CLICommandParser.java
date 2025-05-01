package cz.cuni.mff.danekji1.calendar.client.ui;

import cz.cuni.mff.danekji1.calendar.core.commands.AddEventCommand;
import cz.cuni.mff.danekji1.calendar.core.commands.Command;
import cz.cuni.mff.danekji1.calendar.core.commands.CreateAccountCommand;
import cz.cuni.mff.danekji1.calendar.core.commands.LoginCommand;
import cz.cuni.mff.danekji1.calendar.core.exceptions.client.InsufficientCommandPrivilegesException;
import cz.cuni.mff.danekji1.calendar.core.exceptions.client.InvalidInputException;
import cz.cuni.mff.danekji1.calendar.core.exceptions.client.UnknownCommandException;

import cz.cuni.mff.danekji1.calendar.core.models.Event;
import cz.cuni.mff.danekji1.calendar.core.models.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class CLICommandParser {
    private static final Logger LOGGER = LogManager.getLogger(CLICommandParser.class);
    private static final Map<String, CLICommandBuilder> COMMAND_REGISTRY = new HashMap<>();

    static {
        registerCommand(CreateAccountCommand.COMMAND_NAME, CLICommandParser::buildCreateAccountCommand);
        registerCommand(LoginCommand.COMMAND_NAME, CLICommandParser::buildLoginCommand);
        registerCommand(AddEventCommand.COMMAND_NAME, CLICommandParser::buildAddEventCommand);
        // Add other commands here
    }

    @FunctionalInterface
    public interface CLICommandBuilder {
        Command build(CLIUserInterface ui) throws IOException, InvalidInputException;
    }

    /**
     * Registers a command name with its corresponding builder function.
     * Can be called from the static initializer or potentially elsewhere
     * if dynamic registration is needed.
     * @param commandName The name used to invoke the command.
     * @param builder The function to build the command.
     */
    public static void registerCommand(String commandName, CLICommandBuilder builder) {
        Objects.requireNonNull(commandName, "Command name cannot be null");
        Objects.requireNonNull(builder, "Command builder cannot be null");

        String normalizedName = commandName.toLowerCase().trim();
        if (COMMAND_REGISTRY.put(normalizedName, builder) != null) {
            LOGGER.warn("Command name '{}' is already registered. Overwriting.", normalizedName);
        }

        LOGGER.debug("Registered command: {}", normalizedName);
    }


    public static Command parse(String input, CLIUserInterface ui) throws IOException, InvalidInputException, UnknownCommandException {
        String commandName = input.toLowerCase().trim();

        CLICommandBuilder builder = COMMAND_REGISTRY.get(commandName);
        if (builder == null) {
            throw new UnknownCommandException("Unknown command: '" + input + "'", input);
        }
        return builder.build(ui);
    }

    private static CreateAccountCommand buildCreateAccountCommand(CLIUserInterface ui) throws IOException, InvalidInputException {
        String username = ui.promptForInput("Enter username: ").trim();
        if (username.isEmpty() || username.equalsIgnoreCase("unlogged")) {
            throw new InvalidInputException("Username cannot be empty or 'unlogged'");
        }

        String password = ui.promptForInput("Enter password: ");
        if (password.isEmpty()) {
            throw new InvalidInputException("Password cannot be empty");
        }

        User user = new User(username, password.hashCode());
        return new CreateAccountCommand(user);
    }

    private static LoginCommand buildLoginCommand(CLIUserInterface ui) throws IOException, InvalidInputException {
        String username = ui.promptForInput("Enter username: ").trim();
        if (username.isEmpty() || username.equalsIgnoreCase("unlogged")) {
            throw new InvalidInputException("Username cannot be empty or 'unlogged'");
        }

        String password = ui.promptForInput("Enter password: ");
        if (password.isEmpty()) {
            throw new InvalidInputException("Password cannot be empty");
        }

        User user = new User(username, password.hashCode());
        return new LoginCommand(user);
    }

    private static AddEventCommand buildAddEventCommand(CLIUserInterface ui) throws IOException, InvalidInputException {
        if (ui.getUser() == null) {
            throw new InsufficientCommandPrivilegesException("You must be logged in to add an event");
        }

        String eventName = ui.promptForInput("Enter event name: ").trim();
        if (eventName.isEmpty()) {
            throw new InvalidInputException("Event name cannot be empty");
        }

        String dateInput = ui.promptForInput("Enter event date (YYYY-MM-DD): ").trim();
        if (dateInput.isEmpty()) {
            throw new InvalidInputException("Event date cannot be empty");
        }
        LocalDate date;
        try {
            date = LocalDate.parse(dateInput);
        } catch (DateTimeParseException e) {
            throw new InvalidInputException("Event date is not a valid date: " + dateInput);
        }

        String timeInput = ui.promptForInput("Enter event time (HH:MM): ").trim();
        if (timeInput.isEmpty()) {
            throw new InvalidInputException("Event time cannot be empty");
        }
        LocalTime time;
        try {
            time = LocalTime.parse(timeInput);
        } catch (DateTimeParseException e) {
            throw new InvalidInputException("Event time is not a valid time: " + timeInput);
        }

        String location = ui.promptForInput("Enter event location (optional): ").trim();
        String description = ui.promptForInput("Enter event description (optional): ").trim();

        var event = new Event(eventName, date, time, location, description);
        return new AddEventCommand(event);
    }

}