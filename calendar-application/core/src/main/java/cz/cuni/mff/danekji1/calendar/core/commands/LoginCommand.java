package cz.cuni.mff.danekji1.calendar.core.commands;

public record LoginCommand(String username, int passwordHash) implements Command {

    public static final String COMMAND_NAME = "login";

    /**
     * Helps to handle the command on the server side.
     */
    @Override
    public <R, C> R accept(CommandVisitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
