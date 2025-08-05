package cz.cuni.mff.danekji.calendar.core.commands;

import cz.cuni.mff.danekji.calendar.core.exceptions.client.InvalidInputException;
import cz.cuni.mff.danekji.calendar.core.models.User;
import cz.cuni.mff.danekji.calendar.core.client.session.ClientSession;
import cz.cuni.mff.danekji.calendar.core.client.ui.UserInterface;

import java.io.IOException;
import java.io.Serializable;

/**
 * Marker interface for all commands.
 * It uses the Visitor pattern to decouple execution.
 */
public interface Command extends Serializable {

   /**
    * Builds the command, allowing the command to be created with a user interface and a session.
    *
    * @param ui The user interface
    * @param session The client session
    * @return The build command with its information
    * @throws IOException if an I/O error occurs
    */
   Command buildCommand(UserInterface ui, ClientSession session) throws IOException;

   /**
    * Returns the name of the command.
    *
    * @return the command name
    */
    String getName();

    /**
     * Returns the usage of the command.
     *
     * @return the command usage
     */
    String getDescription();

    /**
     * Returns the privileges in which the command operates.
     * @return the privileges
     */
    Privileges getPrivileges();

    /**
     * Accept the visitor, allowing the command to be executed by the visitor implementation on the server.
     *
     * @param visitor The visitor that will execute the command
     * @param session The session that will be used to execute the command
     * @return the result of the command execution
     * @param <R> Return type of the visitor
     * @param <S> Session type of the visitor
     */
    <R, S> R accept(CommandVisitor<R, S> visitor, S session);

    /**
     * Helper method to get a user from the prompting UI.
     *
     * @param ui The user interface
     * @return The user
     * @throws IOException if an I/O error occurs
     */
    default User getUserFromPromptingUI(UserInterface ui) throws IOException {
        String username = ui.promptForInput("Enter username: ").trim();
        if (username.isEmpty() || username.equalsIgnoreCase("unlogged")) {
            throw new InvalidInputException("Username cannot be empty or 'unlogged'");
        }

        String password = ui.promptForPassword("Enter password: ");
        if (password.isEmpty()) {
            throw new InvalidInputException("Password cannot be empty");
        }
        return  new User(username, password.hashCode());
    }
}