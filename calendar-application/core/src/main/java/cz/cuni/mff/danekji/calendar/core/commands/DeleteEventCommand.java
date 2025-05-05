package cz.cuni.mff.danekji.calendar.core.commands;

import cz.cuni.mff.danekji.calendar.core.exceptions.InsufficientCommandPrivilegesException;
import cz.cuni.mff.danekji.calendar.core.exceptions.client.InvalidInputException;
import cz.cuni.mff.danekji.calendar.core.client.session.ClientSession;
import cz.cuni.mff.danekji.calendar.core.client.ui.UserInterface;

import java.io.IOException;

/**
 * Command to delete an event from the user's calendar.
 * This command requires the user to be logged in. It prompts the user for the ID of the event to delete.
 */
public final class DeleteEventCommand implements Command {
    /**
     * The name of the command.
     */
    public static final String COMMAND_NAME = "delete_event";
    /**
     * The ID of the event to delete.
     */
    private final Long eventId;

    /**
     * Constructor for DeleteEventCommand.
     *
     * @param eventId the ID of the event to delete
     */
    public DeleteEventCommand(Long eventId) {
        this.eventId = eventId;
    }

    // Default constructor for reflection API to build command
    private DeleteEventCommand() {
        this(null);
    }

    /**
     * Builds the command by prompting the user for event id.
     * If the user is not logged in or invalid input id is provided, an exception is thrown.
     *
     * @param ui The user interface
     * @param session The client session
     * @return The {@link DeleteEventCommand} command
     * @throws IOException If an I/O error occurs
     * @throws InsufficientCommandPrivilegesException If the user is not logged in
     * @throws InvalidInputException If the input id is not a valid number
     */
    @Override
    public Command buildCommand(UserInterface ui, ClientSession session)
            throws IOException, InsufficientCommandPrivilegesException, InvalidInputException {
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
        return "Delete an event specified by id from the logged users calendar.";
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
     * Returns the ID of the event to delete.
     *
     * @return The ID of the event.
     */
    public Long getEventId() {
        return eventId;
    }
}
