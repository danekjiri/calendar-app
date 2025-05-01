package cz.cuni.mff.danekji1.calendar.core.commands;


import cz.cuni.mff.danekji1.calendar.core.models.User;

public record CreateAccountCommand(User user) implements Command {
    public static final String COMMAND_NAME = "create_account";

    /**
     * {@inheritDoc}
     */
    @Override
    public Privileges getPrivileges() {
        return Privileges.ALL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R, C> R accept(CommandVisitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
