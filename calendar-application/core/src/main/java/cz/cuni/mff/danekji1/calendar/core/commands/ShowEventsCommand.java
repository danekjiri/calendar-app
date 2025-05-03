package cz.cuni.mff.danekji1.calendar.core.commands;

import cz.cuni.mff.danekji1.calendar.core.exceptions.client.InsufficientCommandPrivilegesException;
import cz.cuni.mff.danekji1.calendar.core.ui.ClientState;
import cz.cuni.mff.danekji1.calendar.core.ui.UserInterface;

public final class ShowEventsCommand implements Command {
    public static final String COMMAND_NAME = "show_events";

    private ShowEventsCommand() {}

    @Override
    public Command buildCommand(UserInterface ui, ClientState context) {
        if (!context.isLoggedIn()) {
            throw new InsufficientCommandPrivilegesException("You must be logged in to list events.");
        }

        return new ShowEventsCommand();
    }

    @Override
    public String getName() {
        return COMMAND_NAME;
    }

    @Override
    public String getDescription() {
        return "Displays a list of all events inserted.";
    }

    @Override
    public Privileges getPrivileges() {
        return Privileges.LOGGED;
    }

    @Override
    public <R, C> R accept(CommandVisitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
