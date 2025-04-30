package cz.cuni.mff.danekji1.calendar.server;

import cz.cuni.mff.danekji1.calendar.core.commands.AddEventCommand;
import cz.cuni.mff.danekji1.calendar.core.exceptions.CalendarException;
import cz.cuni.mff.danekji1.calendar.core.models.User;
import cz.cuni.mff.danekji1.calendar.core.responses.ErrorResponse;
import cz.cuni.mff.danekji1.calendar.core.responses.SuccessLoginResponse;
import cz.cuni.mff.danekji1.calendar.core.responses.SuccessResponse;
import cz.cuni.mff.danekji1.calendar.server.storage.EventRepository;
import cz.cuni.mff.danekji1.calendar.core.commands.CommandVisitor;
import cz.cuni.mff.danekji1.calendar.core.commands.CreateAccountCommand;
import cz.cuni.mff.danekji1.calendar.core.commands.LoginCommand;
import cz.cuni.mff.danekji1.calendar.core.responses.Response;

public class DefaultCommandDispatcher implements CommandVisitor<Response, Session> {
    private final EventRepository eventRepository;

    public DefaultCommandDispatcher(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    /**
     * The implementation of the CommandVisitor endpoint for LoginCommand.
     * Checks if username and password matches any record in user-database and if so,
     *  logs in user and return successful response.
     * Otherwise, returns error response.
     */
    @Override
    public Response visit(LoginCommand command, Session session) {
        if (session.isLoggedIn()) {
            return new ErrorResponse("Already logged in as user '" + session.getCurrentUser().username() + "'.");
        }

        if (eventRepository.authenticate(command.username(), command.passwordHash())) {
            User user = new User(command.username(), command.passwordHash());
            session.setCurrentUser(user);
            return new SuccessLoginResponse("Login as user '" + command.username() + "' is successful.", user);
        } else {
            return new ErrorResponse("Invalid credentials.");
        }
    }

    /**
     * The implementation of the CommandVisitor endpoint for CreateAccountCommand.
     * Checks if username unique, if username and password good and if so,
     *  then create user in the server (xml) user-database and return successful response.
     * Otherwise, returns error response.
     */
    @Override
    public Response visit(CreateAccountCommand command, Session session) {
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
    public Response visit(AddEventCommand command, Session context) {
        if (!context.isLoggedIn()) {
            return new ErrorResponse("You must be logged in to add an event.");
        }

        try {
            eventRepository.addEvent(context.getCurrentUser().username(), command.event());
            return new SuccessResponse("Event added successfully.");
        } catch (CalendarException e) {
            return new ErrorResponse(e.getMessage());
        }
    }
}
