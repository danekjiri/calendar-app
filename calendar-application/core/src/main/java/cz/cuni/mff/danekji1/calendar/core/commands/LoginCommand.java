package cz.cuni.mff.danekji1.calendar.core.commands;

import cz.cuni.mff.danekji1.calendar.core.models.User;
import cz.cuni.mff.danekji1.calendar.core.ui.ClientState;
import cz.cuni.mff.danekji1.calendar.core.ui.UserInterface;

import java.io.IOException;

public final class LoginCommand implements Command {
    public static final String COMMAND_NAME = "login";
    private final User user;

    public LoginCommand(User user) {
        this.user = user;
    }

    private LoginCommand() {
        this(null);
    }


    @Override
    public Command buildCommand(UserInterface ui, ClientState context) throws IOException {
        User user = getUserFromPromptingUI(ui);
        return new LoginCommand(user);
    }

    @Override
    public String getName() {
        return COMMAND_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Login to the calendar system with your username and password.";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Privileges getPrivileges() {
        return Privileges.UNLOGGED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R, C> R accept(CommandVisitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }

    public User getUser() {
        return user;
    }
}
