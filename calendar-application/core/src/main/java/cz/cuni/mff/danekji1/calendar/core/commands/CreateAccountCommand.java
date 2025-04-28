package cz.cuni.mff.danekji1.calendar.core.commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public record CreateAccountCommand(String username, int passwordHash) implements Command {
    private static final Logger LOGGER = LogManager.getLogger(CreateAccountCommand.class);

    public static final String COMMAND_NAME = "create_account";

    /**
     * Helps to handle the command on the server side.
     */
    @Override
    public <R, C> R accept(CommandVisitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
