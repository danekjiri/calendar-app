package cz.cuni.mff.danekji1.calendar.core.commands;

import cz.cuni.mff.danekji1.calendar.core.session.ClientSession;
import cz.cuni.mff.danekji1.calendar.core.ui.UserInterface;

import java.util.Map;

public final class HelpCommand implements Command {
    public static final String COMMAND_NAME = "help";
    private final Map<String, Class<? extends Command>> availableCommands;

    public HelpCommand(Map<String, Class<? extends Command>> availableCommands) {
        this.availableCommands = availableCommands;
    }

    private HelpCommand() {
        this(null);
    }

    @Override
    public Command buildCommand(UserInterface ui, ClientSession session) {
        return new HelpCommand(ui.getCommandRegistry());
    }

    @Override
    public String getName() {
        return COMMAND_NAME;
    }

    @Override
    public String getDescription() {
        return "Displays a list of available commands for current privilege status.";
    }

    @Override
    public Privileges getPrivileges() {
        return Privileges.ALL;
    }

    @Override
    public <R, C> R accept(CommandVisitor<R, C> visitor, C session) {
        return visitor.visit(this, session);
    }

    public Map<String, Class<? extends Command>> getAvailableCommands() {
        return availableCommands;
    }
}
