package cz.cuni.mff.danekji.calendar.core.commands;

import cz.cuni.mff.danekji.calendar.core.exceptions.InsufficientCommandPrivilegesException;
import cz.cuni.mff.danekji.calendar.core.client.session.ClientSession;
import cz.cuni.mff.danekji.calendar.core.client.ui.UserInterface;

/**
 * Command to retrieve and display a list of all events added to the calendar.
 */
public final class ShowEventsCommand implements Command {
    /**
     * The name of the command.
     */
    public static final String COMMAND_NAME = "show_events";

    /**
     * The default constructor for the ShowEventsCommand class.
     */
    ShowEventsCommand() {}

    /**
     * The method builds the command by checking if the user is logged in.
     *
     * @param ui The user interface
     * @param session The client session
     * @return The {@link ShowEventsCommand} command
     * @throws InsufficientCommandPrivilegesException If the user is not logged in
     */
    @Override
    public Command buildCommand(UserInterface ui, ClientSession session) throws InsufficientCommandPrivilegesException {
        if (!session.isLoggedIn()) {
            throw new InsufficientCommandPrivilegesException("You must be logged in to list events.");
        }

        return new ShowEventsCommand();
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
        return "Displays a list of all events inserted.";
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
}
