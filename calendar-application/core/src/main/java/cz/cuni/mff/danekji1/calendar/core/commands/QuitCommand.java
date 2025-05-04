package cz.cuni.mff.danekji1.calendar.core.commands;

import cz.cuni.mff.danekji1.calendar.core.session.Session;
import cz.cuni.mff.danekji1.calendar.core.ui.UserInterface;

import java.io.IOException;

public final class QuitCommand implements Command {
    public static final String COMMAND_NAME = "quit";

    @Override
    public Command buildCommand(UserInterface ui, Session context) throws IOException {
        return new QuitCommand();
    }

    @Override
    public String getName() {
        return COMMAND_NAME;
    }

    @Override
    public String getDescription() {
        return "Inform the server about quitting and exit the client application.";
    }

    @Override
    public Privileges getPrivileges() {
        return Privileges.ALL;
    }

    @Override
    public <R, C> R accept(CommandVisitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
