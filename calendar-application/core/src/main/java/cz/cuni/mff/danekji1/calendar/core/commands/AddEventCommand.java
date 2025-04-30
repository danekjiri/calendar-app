package cz.cuni.mff.danekji1.calendar.core.commands;

import cz.cuni.mff.danekji1.calendar.core.models.Event;

public record AddEventCommand(Event event) implements Command {
    public static final String COMMAND_NAME = "add_event";

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
        return null;
    }
}
