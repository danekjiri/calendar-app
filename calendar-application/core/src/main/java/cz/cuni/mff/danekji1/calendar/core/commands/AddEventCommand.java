package cz.cuni.mff.danekji1.calendar.core.commands;

import cz.cuni.mff.danekji1.calendar.core.exceptions.client.InsufficientCommandPrivilegesException;
import cz.cuni.mff.danekji1.calendar.core.exceptions.client.InvalidInputException;
import cz.cuni.mff.danekji1.calendar.core.models.Event;
import cz.cuni.mff.danekji1.calendar.core.ui.ClientState;
import cz.cuni.mff.danekji1.calendar.core.ui.UserInterface;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public final class AddEventCommand implements Command {
    public static final String COMMAND_NAME = "add_event";
    private final Event event;

    public AddEventCommand(Event event) {
        this.event = event;
    }

    private AddEventCommand() {
        this(null);
    }

    @Override
    public Command buildCommand(UserInterface ui, ClientState context) throws IOException {
        if (!context.isLoggedIn()) {
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
    public <R, C> R accept(CommandVisitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }

    public Event getEvent() {
        return event;
    }
}
