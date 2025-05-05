package cz.cuni.mff.danekji1.calendar.core.commands;

import cz.cuni.mff.danekji1.calendar.core.exceptions.client.InvalidInputException;
import cz.cuni.mff.danekji1.calendar.core.models.User;
import cz.cuni.mff.danekji1.calendar.core.session.ClientSession;
import cz.cuni.mff.danekji1.calendar.core.ui.UserInterface;

import java.io.IOException;

/**
 * Command to create a new account.
 * This command is used to create a new user account in the calendar system.
 * It requires the user to provide a username and password.
 */
public final class CreateAccountCommand implements Command {
    public static final String COMMAND_NAME = "create_account";
    private final User user;

    /**
     * Constructor for CreateAccountCommand.
     *
     * @param user The user object containing the username and password.
     */
    public CreateAccountCommand(User user) {
        this.user = user;
    }

    // Default constructor for reflection API to build command
    private CreateAccountCommand() {
        this(null);
    }

    /**
     * The method builds the command by prompting the user for input.
     *
     * @param ui The user interface
     * @param session The client session
     * @return The {@link CreateAccountCommand} command
     * @throws IOException If an I/O error occurs
     * @throws InvalidInputException If the input is invalid for given prompt (e.g. empty username or password)
     */
    @Override
    public Command buildCommand(UserInterface ui, ClientSession session) throws IOException, InvalidInputException {
        User user = getUserFromPromptingUI(ui);
        return new CreateAccountCommand(user);
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
    public <R, C> R accept(CommandVisitor<R, C> visitor, C session) {
        return visitor.visit(this, session);
    }

    /**
     * Returns the user object associated with this command.
     *
     * @return The {@link User} object.
     */
    public User getUser() {
        return user;
    }
}
