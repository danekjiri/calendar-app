package cz.cuni.mff.danekji1.calendar.server;

import cz.cuni.mff.danekji1.calendar.core.exceptions.CalendarException;
import cz.cuni.mff.danekji1.calendar.core.responses.ErrorResponse;
import cz.cuni.mff.danekji1.calendar.core.responses.SuccessResponse;
import cz.cuni.mff.danekji1.calendar.server.storage.EventRepository;
import cz.cuni.mff.danekji1.calendar.core.commands.AddEventCommand;
import cz.cuni.mff.danekji1.calendar.core.commands.CommandVisitor;
import cz.cuni.mff.danekji1.calendar.core.commands.CreateAccountCommand;
import cz.cuni.mff.danekji1.calendar.core.commands.LoginCommand;
import cz.cuni.mff.danekji1.calendar.core.responses.Response;

public class DefaultCommandDispatcher implements CommandVisitor<Response> {
    private final Session session;
    private final EventRepository eventRepository;

    public DefaultCommandDispatcher(Session session, EventRepository eventRepository) {
        this.session = session;
        this.eventRepository = eventRepository;
    }

    /**
     * The implementation of the CommandVisitor endpoint for LoginCommand.
     * Checks if username and password matches any record in user-database and if so,
     *  logs in user and return successful response.
     * Otherwise, returns error response.
     */
    @Override
    public Response visit(LoginCommand command) {
//        // example of simple implementation
//        if (session.isLoggedIn()) {
//            return new ErrorResponse("Already logged in.");
//        }
//        User user = userRepository.authenticate(command.getUsername(), command.getPasswordHash());
//        if (user != null) {
//            session.setCurrentUser(user);
//            return new SuccessResponse("Login successful.");
//        } else {
//            return new ErrorResponse("Invalid credentials.");
//        }
        throw new UnsupportedOperationException("LoginCommand not implemented yet.");
        // todo: implement login command
    }

    /**
     * The implementation of the CommandVisitor endpoint for CreateAccountCommand.
     * Checks if username unique, if username and password good and if so,
     *  then create user in the server (xml) user-database and return successful response.
     * Otherwise, returns error response.
     */
    @Override
    public Response visit(CreateAccountCommand command) {
        try {
            eventRepository.createAccount(command.username(), command.passwordHash());
        } catch (CalendarException e) {
            return new ErrorResponse(e.getMessage());
        }
        return new SuccessResponse("Account for account '" + command.username() + "' created successfully.");
    }

    /**
     * The implementation of the CommandVisitor endpoint for AddEventCommand.
     * Checks if user logged and if so then creates record about event, provided in command,
     *  into the server (xml) event-database and returns successful response.
     * Otherwise, returns error response.
     */
    @Override
    public Response visit(AddEventCommand command) {
        throw new UnsupportedOperationException("AddEventCommand not implemented yet.");
        // todo: implement add command
    }

}
