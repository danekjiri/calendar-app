package cz.cuni.mff.danekji1.calendar.core.commands;

import cz.cuni.mff.danekji1.calendar.core.exceptions.InsufficientCommandPrivilegesException;
import cz.cuni.mff.danekji1.calendar.core.exceptions.client.InvalidInputException;
import cz.cuni.mff.danekji1.calendar.core.models.Event;
import cz.cuni.mff.danekji1.calendar.core.session.ClientSession;
import cz.cuni.mff.danekji1.calendar.core.ui.UserInterface;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

/**
 * Command to update an event in the calendar.
 * This command allows the user to modify an existing event by providing its ID and new details,
 *  if details are not provided, the previous values are used.
 */
public final class UpdateEventCommand implements Command {
    public static final String COMMAND_NAME = "update_event";
    private final Event event;

    /**
     * Constructor for creating an UpdateEventCommand with the specified event.
     *
     * @param event The event to be updated.
     */
    public UpdateEventCommand(Event event) {
        this.event = event;
    }

    // Default constructor for reflection API to build command
    private UpdateEventCommand() {
        this(null);
    }

    /**
     * The method builds the command by prompting the user for input.
     * If the user is not logged in or invalid input is provided, an exception is thrown.
     * All fields are optional, if the user does not provide a value, the previous value is used.
     *
     * @param ui The user interface
     * @param session The client session
     * @return The {@link UpdateEventCommand} command
     * @throws IOException If an I/O error occurs
     * @throws InsufficientCommandPrivilegesException If the user is not logged in
     * @throws InvalidInputException If the input is invalid for given prompt (e.g. empty event ID)
     */
    @Override
    public Command buildCommand(UserInterface ui, ClientSession session)
            throws IOException, InsufficientCommandPrivilegesException, InvalidInputException {
        if (!session.isLoggedIn()) {
            throw new InsufficientCommandPrivilegesException("You must be logged in to modify an event");
        }

        String inputEventId = ui.promptForInput("Enter the ID of the event to be updated: ");
        long eventId;
        try {
            eventId = Long.parseLong(inputEventId);
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Entered event ID is not a valid number: " + inputEventId);
        }

        String title = ui.promptForInput("Enter event name or left empty for previous value: ").trim();
        if (title.isEmpty()) {
            title = null;
        }

        LocalDate date;
        String dateInput = ui.promptForInput("Enter event date (YYYY-MM-DD) or left empty for previous value: ").trim();
        if (dateInput.isEmpty()) {
            date =  null;
        } else {
            try {
                date = LocalDate.parse(dateInput);
            } catch (DateTimeParseException e) {
                throw new InvalidInputException("Entered event date is not a valid date: " + dateInput);
            }
        }

        LocalTime time;
        String timeInput = ui.promptForInput("Enter event time (HH:MM) or left empty for previous value: ").trim();
        if (timeInput.isEmpty()) {
            time = null;
        } else {
            try {
                time = LocalTime.parse(timeInput);
            } catch (DateTimeParseException e) {
                throw new InvalidInputException("Entered event time is not a valid time: " + timeInput);
            }
        }

        String location = ui.promptForInput("Enter event location or left empty for previous value: ").trim();
        if (location.isEmpty()) {
            location = null;
        }

        String description = ui.promptForInput("Enter event description or left empty for previous value: ").trim();
        if (description.isEmpty()) {
            description = null;
        }

        Event modifiedEvent = new Event(title, date, time, location, description);
        return new UpdateEventCommand(Event.withId(eventId, modifiedEvent));
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
        return "Updates the specified event by id in the currently logged users calendar.";
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
    public <R, C> R accept(CommandVisitor<R, C> visitor, C session) {
        return visitor.visit(this, session);
    }

    /**
     * Returns the event to be updated.
     * The event contains the new details for the update and the ID of the event to be updated.
     *
     * @return The event to be updated.
     */
    public Event getEvent() {
        return event;
    }
}
