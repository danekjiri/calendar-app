package cz.cuni.mff.danekji.calendar.core.commands;

import cz.cuni.mff.danekji.calendar.core.client.session.ClientSession;
import cz.cuni.mff.danekji.calendar.core.client.ui.UserInterface;
import cz.cuni.mff.danekji.calendar.core.exceptions.InsufficientCommandPrivilegesException;
import cz.cuni.mff.danekji.calendar.core.exceptions.client.InvalidInputException;
import cz.cuni.mff.danekji.calendar.core.models.User;

import java.io.IOException;

/**
 * Command to delete the currently logged-in user's account.
 * This command requires password confirmation for security.
 */
public final class DeleteUserCommand implements Command {
    /**
     * The name of the command.
     */
    public static final String COMMAND_NAME = "delete_user";

    /**
     * The user object containing the username and password hash for verification.
     */
    private final User user;

    /**
     * Constructor for creating the command with a user object.
     * @param user The user object containing the username and password hash for verification.
     */
    public DeleteUserCommand(User user) {
        this.user = user;
    }

    // Default constructor for reflection
    DeleteUserCommand() {
        this(null);
    }

    /**
     * Builds the command by prompting the user for password confirmation.
     * If the user is not logged in or invalid input is provided, an exception is thrown.
     *
     * @param ui The user interface
     * @param session The client session
     * @return The {@link DeleteUserCommand} command
     * @throws IOException If an I/O error occurs
     * @throws InsufficientCommandPrivilegesException If the user is not logged in
     * @throws InvalidInputException If the input password is empty
     */
    @Override
    public Command buildCommand(UserInterface ui, ClientSession session) throws IOException, InsufficientCommandPrivilegesException, InvalidInputException {
        if (!session.isLoggedIn()) {
            throw new InsufficientCommandPrivilegesException("You must be logged in to delete your account.");
        }

        String password = ui.promptForPassword("For security, please re-enter your password to confirm deletion: ").trim();
        if (password.isEmpty()) {
            throw new InvalidInputException("Password cannot be empty.");
        }

        User verificationUser = new User(session.getCurrentUser().username(), password.hashCode());
        return new DeleteUserCommand(verificationUser);
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
        return "Deletes your account permanently. This action cannot be undone.";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Privileges getPrivileges() {
        return Privileges.LOGGED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R, C> R accept(CommandVisitor<R, C> visitor, C session) {
        return visitor.visit(this, session);
    }

    /**
     * Returns the user object for verification.
     * @return The User object.
     */
    public User getUser() {
        return user;
    }
}