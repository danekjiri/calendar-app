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

public final class UpdateEventCommand implements Command {
    public static final String COMMAND_NAME = "update_event";
    private final Event event;

    public UpdateEventCommand(Event event) {
        this.event = event;
    }

    private UpdateEventCommand() {
        this(null);
    }

    @Override
    public Command buildCommand(UserInterface ui, ClientState context) throws IOException {
        if (!context.isLoggedIn()) {
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

    @Override
    public String getName() {
        return COMMAND_NAME;
    }

    @Override
    public String getDescription() {
        return "Updates the specified event by id in the currently logged users calendar.";
    }

    @Override
    public Privileges getPrivileges() {
        return Privileges.LOGGED;
    }

    @Override
    public <R, C> R accept(CommandVisitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }

    public Event getEvent() {
        return event;
    }
}
