package cz.cuni.mff.danekji.calendar.core.commands;

import cz.cuni.mff.danekji.calendar.core.exceptions.client.InvalidInputException;
import cz.cuni.mff.danekji.calendar.core.models.User;
import cz.cuni.mff.danekji.calendar.core.client.session.ClientSession;
import cz.cuni.mff.danekji.calendar.core.client.ui.UserInterface;

import java.io.IOException;

/**
 * Command to log in to the calendar system.
 * This command is used to authenticate a user on the server and if successful,
 *  the user will be logged in and can access their calendar.
 */
public final class LoginCommand implements Command {
    /**
     * The name of the command.
     */
    public static final String COMMAND_NAME = "login";
    /**
     * The user object containing the username and password.
     */
    private final User user;

    /**
     * Constructor for LoginCommand.
     *
     * @param user The user object containing the username and password.
     */
    public LoginCommand(User user) {
        this.user = user;
    }

    // Default constructor for reflection API to build command
    LoginCommand() {
        this(null);
    }

    /**
     * The method builds the command by prompting the user for input.
     *
     * @param ui The user interface
     * @param session The client session
     * @return The {@link LoginCommand} command
     * @throws IOException If an I/O error occurs
     * @throws InvalidInputException If the input is invalid for given prompt (e.g. empty username or password)
     */
    @Override
    public Command buildCommand(UserInterface ui, ClientSession session)
            throws IOException, InvalidInputException {
        User user = getUserFromPromptingUI(ui);
        return new LoginCommand(user);
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
    public <R, C> R accept(CommandVisitor<R, C> visitor, C session) {
        return visitor.visit(this, session);
    }

    /**
     * Returns the user object associated with this command.
     *
     * @return the {@link User} object.
     */
    public User getUser() {
        return user;
    }
}
