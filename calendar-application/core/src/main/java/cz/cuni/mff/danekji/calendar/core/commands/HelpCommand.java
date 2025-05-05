package cz.cuni.mff.danekji.calendar.core.commands;

import cz.cuni.mff.danekji.calendar.core.client.session.ClientSession;
import cz.cuni.mff.danekji.calendar.core.client.ui.UserInterface;

import java.util.Map;

/**
 * The HelpCommand class gets a list of all available commands and the server process them
 *  and returns formated string with all commands and their descriptions for clients privilege status.
 */
public final class HelpCommand implements Command {
    /**
     * The name of the command.
     */
    public static final String COMMAND_NAME = "help";
    /**
     * The map of available commands.
     */
    private final Map<String, Class<? extends Command>> availableCommands;

    /**
     * Constructor for HelpCommand.
     *
     * @param availableCommands a map of available commands
     */
    public HelpCommand(Map<String, Class<? extends Command>> availableCommands) {
        this.availableCommands = availableCommands;
    }

    // Default constructor for reflection API to build command
    private HelpCommand() {
        this(null);
    }

    /**
     * The method builds the command by getting a list of all available commands.
     *
     * @param ui The user interface
     * @param session The client session
     * @return The {@link HelpCommand} command
     */
    @Override
    public Command buildCommand(UserInterface ui, ClientSession session) {
        return new HelpCommand(ui.getUnmodifiableCommandRegistry());
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
        return "Displays a list of available commands for current privilege status.";
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

    /**
     * Returns the map of available commands.
     *
     * @return a map of available commands
     */
    public Map<String, Class<? extends Command>> getAvailableCommands() {
        return availableCommands;
    }
}
