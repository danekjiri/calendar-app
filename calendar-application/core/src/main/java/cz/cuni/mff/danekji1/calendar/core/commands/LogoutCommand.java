package cz.cuni.mff.danekji1.calendar.core.commands;

public record LogoutCommand() implements Command {
    public static final String COMMAND_NAME = "logout";

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
}
