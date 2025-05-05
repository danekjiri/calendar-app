package cz.cuni.mff.danekji1.calendar.core.commands;

import cz.cuni.mff.danekji1.calendar.core.exceptions.client.InvalidInputException;
import cz.cuni.mff.danekji1.calendar.core.models.User;
import cz.cuni.mff.danekji1.calendar.core.session.ClientSession;
import cz.cuni.mff.danekji1.calendar.core.ui.UserInterface;

import java.io.IOException;
import java.io.Serializable;

/**
 * Marker interface for all commands.
 * It uses the Visitor pattern to decouple execution.
 */
public interface Command extends Serializable {

    Command buildCommand(UserInterface ui, ClientSession session) throws IOException;

   String getName();

    /**
     * Returns the usage of the command.
     * @return the command usage
     */
    String getDescription();

    /**
     * Returns the privileges in which the command operates.
     * @return the privileges
     */
    Privileges getPrivileges();

    <R, C> R accept(CommandVisitor<R, C> visitor, C session);

    default User getUserFromPromptingUI(UserInterface ui) throws IOException {
        String username = ui.promptForInput("Enter username: ").trim();
        if (username.isEmpty() || username.equalsIgnoreCase("unlogged")) {
            throw new InvalidInputException("Username cannot be empty or 'unlogged'");
        }

        String password = ui.promptForInput("Enter password: ");
        if (password.isEmpty()) {
            throw new InvalidInputException("Password cannot be empty");
        }
        return  new User(username, password.hashCode());
    }
}