package cz.cuni.mff.danekji1.calendar.core.commands;


import cz.cuni.mff.danekji1.calendar.core.models.User;
import cz.cuni.mff.danekji1.calendar.core.ui.ClientState;
import cz.cuni.mff.danekji1.calendar.core.ui.UserInterface;

import java.io.IOException;

public final class CreateAccountCommand implements Command {
    public static final String COMMAND_NAME = "create_account";
    private final User user;

    public CreateAccountCommand(User user) {
        this.user = user;
    }

    private CreateAccountCommand() {
        this(null);
    }

    @Override
    public Command buildCommand(UserInterface ui, ClientState context) throws IOException {
        User user = getUserFromPromptingUI(ui);
        return new CreateAccountCommand(user);
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
        return "Create a new account with the provided username and password.";
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
    public <R, C> R accept(CommandVisitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }

    public User getUser() {
        return user;
    }
}
