package cz.cuni.mff.danekji.calendar.core.commands;

import cz.cuni.mff.danekji.calendar.core.exceptions.InsufficientCommandPrivilegesException;
import cz.cuni.mff.danekji.calendar.core.client.session.ClientSession;
import cz.cuni.mff.danekji.calendar.core.client.ui.UserInterface;

/**
 * Command to log out from the current logged account.
 * The command is available only for logged users and unset the current user in the session.
 */
public final class LogoutCommand implements Command {
    /**
     * The name of the command.
     */
    public static final String COMMAND_NAME = "logout";

    /**
     * The default constructor for the LogoutCommand.
     */
    public LogoutCommand() {}

    /**
     * The method builds the command by checking if the user is logged in.
     *
     * @param ui The user interface
     * @param session The client session
     * @return The {@link LogoutCommand} command
     * @throws InsufficientCommandPrivilegesException If the user is not logged in
     */
    @Override
    public Command buildCommand(UserInterface ui, ClientSession session) throws InsufficientCommandPrivilegesException {
        if (!session.isLoggedIn()) {
            throw new InsufficientCommandPrivilegesException("You must be logged to logout");
        }

        return new LogoutCommand();
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
        return "Logout from current logged account.";
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
