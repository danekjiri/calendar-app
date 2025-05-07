package cz.cuni.mff.danekji.calendar.core.commands;

import cz.cuni.mff.danekji.calendar.core.exceptions.InsufficientCommandPrivilegesException;
import cz.cuni.mff.danekji.calendar.core.exceptions.client.InvalidInputException;
import cz.cuni.mff.danekji.calendar.core.models.Event;
import cz.cuni.mff.danekji.calendar.core.client.session.ClientSession;
import cz.cuni.mff.danekji.calendar.core.client.ui.UserInterface;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

/**
 * Command to add an event to the calendar.
 */
public final class AddEventCommand implements Command {

    /**
     * The name of the command.
     */
    public static final String COMMAND_NAME = "add_event";

    /**
     * The event to be added.
     */
    private final Event event;

    /**
     * Constructor for creating an AddEventCommand with the specified event.
     *
     * @param event The event to be added.
     */
    public AddEventCommand(Event event) {
        this.event = event;
    }

    // Default constructor for reflection API to build command
    AddEventCommand() {
        this(null);
    }

    /**
     * The method builds the command by prompting the user for input.
     * If the user is not logged in or invalid input is provided, an exception is thrown.
     * Location and description are optional.
     *
     * @param ui The user interface
     * @param session The client session
     * @return The {@link AddEventCommand} command
     * @throws IOException If an I/O error occurs
     * @throws InsufficientCommandPrivilegesException If the user is not logged in
     * @throws InvalidInputException If the input is invalid for given prompt
     */
    @Override
    public Command buildCommand(UserInterface ui, ClientSession session)
            throws IOException, InsufficientCommandPrivilegesException,  InvalidInputException {
        if (!session.isLoggedIn()) {
            throw new InsufficientCommandPrivilegesException("You must be logged in to add an event");
        }

        String title = ui.promptForInput("Enter event name: ").trim();
        if (title.isEmpty()) {
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

        var event = new Event(title, date, time, location, description);
        return new AddEventCommand(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return COMMAND_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Add a new event to the calendar for currently logged user.";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Privileges getPrivileges() {
        return Privileges.LOGGED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R, S> R accept(CommandVisitor<R, S> visitor, S session) {
        return visitor.visit(this, session);
    }

    /**
     * Gets the event stored in this command.
     * The event is without id, as it is not yet stored in the database.
     *
     * @return The event without id
     */
    public Event getEvent() {
        return event;
    }
}
