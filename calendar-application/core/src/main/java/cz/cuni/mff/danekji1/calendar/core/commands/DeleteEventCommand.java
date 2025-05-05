package cz.cuni.mff.danekji1.calendar.core.commands;

import cz.cuni.mff.danekji1.calendar.core.exceptions.client.InsufficientCommandPrivilegesException;
import cz.cuni.mff.danekji1.calendar.core.exceptions.client.InvalidInputException;
import cz.cuni.mff.danekji1.calendar.core.session.ClientSession;
import cz.cuni.mff.danekji1.calendar.core.ui.UserInterface;

import java.io.IOException;

public final class DeleteEventCommand implements Command {
    public static final String COMMAND_NAME = "delete_event";
    private final Long eventId;

    public DeleteEventCommand(Long eventId) {
        this.eventId = eventId;
    }

    private DeleteEventCommand() {
        this(null);
    }

    @Override
    public Command buildCommand(UserInterface ui, ClientSession session) throws IOException {
        if (!session.isLoggedIn()) {
            throw new InsufficientCommandPrivilegesException("You must be logged in to delete an event");
        }

        String inputEventId = ui.promptForInput("Enter the ID of the event to delete: ");

        try {
            long id = Long.parseLong(inputEventId);
            return new DeleteEventCommand(id);
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Event ID is not a valid number: " + inputEventId);
        }
    }

    @Override
    public String getName() {
        return COMMAND_NAME;
    }

    @Override
    public String getDescription() {
        return "Delete an event specified by id from the logged users calendar.";
    }

    @Override
    public Privileges getPrivileges() {
        return Privileges.LOGGED;
    }

    @Override
    public <R, C> R accept(CommandVisitor<R, C> visitor, C session) {
        return visitor.visit(this, session);
    }

    public Long getEventId() {
        return eventId;
    }
}
