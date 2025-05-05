package cz.cuni.mff.danekji1.calendar.core.commands;

import cz.cuni.mff.danekji1.calendar.core.exceptions.client.InsufficientCommandPrivilegesException;
import cz.cuni.mff.danekji1.calendar.core.session.ClientSession;
import cz.cuni.mff.danekji1.calendar.core.ui.UserInterface;

public final class LogoutCommand implements Command {
    public static final String COMMAND_NAME = "logout";

    @Override
    public Command buildCommand(UserInterface ui, ClientSession session) {
        if (! session.isLoggedIn()) {
            throw new InsufficientCommandPrivilegesException("You must be logged to logout");
        }

        return new LogoutCommand();
    }

    public LogoutCommand() {
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
