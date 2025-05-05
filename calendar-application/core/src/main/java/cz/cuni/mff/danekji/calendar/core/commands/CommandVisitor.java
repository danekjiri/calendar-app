package cz.cuni.mff.danekji.calendar.core.commands;

/**
 * Visitor interface for processing commands on server side.
 * @param <R> The type of the result returned by the command.
 * @param <S> The type of the session used to process the command.
 */
public interface CommandVisitor<R, S> {
    /**
     * Login a user in a session so he can use the calendar.
     *
     * @param command The {@link LoginCommand} to be processed.
     * @param session The session in which the command will be processed.
     * @return The result of the command execution.
     */
    R visit(LoginCommand command, S session);

    /**
     * Create a new user account in servers database.
     *
     * @param command The {@link CreateAccountCommand} to be processed.
     * @param session The session in which the command will be processed.
     * @return The result of the command execution.
     */
    R visit(CreateAccountCommand command, S session);

    /**
     * Add an event to the calendar for the logged user.
     *
     * @param command The {@link AddEventCommand} to be processed.
     * @param session The session in which the command will be processed.
     * @return The result of the command execution.
     */
    R visit(AddEventCommand command, S session);

    /**
     * Logout the user from the session, so he can no longer use the calendar.
     *
     * @param command The {@link LogoutCommand} to be processed.
     * @param session The session in which the command will be processed.
     * @return The result of the command execution.
     */
    R visit(LogoutCommand command, S session);

    /**
     * Generate a help message for the user for his current privileges.
     *
     * @param command The {@link HelpCommand} to be processed.
     * @param session The session in which the command will be processed.
     * @return The generated help message response.
     */
    R visit(HelpCommand command, S session);

    /**
     * Show all inserted events for the logged user.
     *
     * @param command The {@link ShowEventsCommand} to be processed.
     * @param session The session in which the command will be processed.
     * @return The result of the command execution.
     */
    R visit(ShowEventsCommand command, S session);

    /**
     * Delete an event from the calendar for the logged user by its id.
     *
     * @param command The {@link DeleteEventCommand} to be processed.
     * @param session The session in which the command will be processed.
     * @return The result of the command execution.
     */
    R visit(DeleteEventCommand command, S session);

    /**
     * Update an event in the calendar for the logged user by its id.
     *
     * @param command The {@link UpdateEventCommand} to be processed.
     * @param session The session in which the command will be processed.
     * @return The result of the command execution.
     */
    R visit(UpdateEventCommand command, S session);

    /**
     * Process a client that wants to quit the application. (eg: by invaliding his session)
     *
     * @param command The {@link QuitCommand} to be processed.
     * @param session The session in which the command will be processed.
     * @return The result of the command execution.
     */
    R visit(QuitCommand command, S session);
    // more to be added as needed...
}
