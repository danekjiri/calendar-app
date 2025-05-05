package cz.cuni.mff.danekji1.calendar.core.commands;

import cz.cuni.mff.danekji1.calendar.core.session.ClientSession;
import cz.cuni.mff.danekji1.calendar.core.ui.UserInterface;

import java.io.IOException;

/**
 * Command to quit the client application.
 * This command informs the server about quitting and exits the client application.
 */
public final class QuitCommand implements Command {
    public static final String COMMAND_NAME = "quit";

    /**
     * Default constructor for QuitCommand.
     */
    public QuitCommand() { }

    /**
     * The method builds the command.
     *
     * @param ui The user interface
     * @param session The client session
     * @return The {@link QuitCommand} command
     */
    @Override
    public Command buildCommand(UserInterface ui, ClientSession session) throws IOException {
        return new QuitCommand();
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
        return "Inform the server about quitting and exit the client application.";
    }

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
    public <R, C> R accept(CommandVisitor<R, C> visitor, C session) {
        return visitor.visit(this, session);
    }
}
