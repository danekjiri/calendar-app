package cz.cuni.mff.danekji1.calendar.server;

import cz.cuni.mff.danekji1.calendar.core.commands.*;
import cz.cuni.mff.danekji1.calendar.core.exceptions.CalendarException;
import cz.cuni.mff.danekji1.calendar.core.responses.error.ErrorResponse;
import cz.cuni.mff.danekji1.calendar.core.responses.success.SuccessLoginResponse;
import cz.cuni.mff.danekji1.calendar.core.responses.success.SuccessLogoutResponse;
import cz.cuni.mff.danekji1.calendar.core.responses.success.SuccessResponse;
import cz.cuni.mff.danekji1.calendar.server.storage.EventRepository;
import cz.cuni.mff.danekji1.calendar.core.responses.Response;

import java.io.IOException;

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

        if (eventRepository.authenticate(command.user())) {
            session.setCurrentUser(command.user());
            return new SuccessLoginResponse("Login as user '" + command.user().username() + "' is successful.", command.user());
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
            eventRepository.createAccount(command.user());
        } catch (CalendarException e) {
            return new ErrorResponse(e.getMessage());
        }
        return new SuccessResponse("Account for account '" + command.user().username() + "' created successfully.");
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
            long newEventId = eventRepository.addEvent(context.getCurrentUser(), command.event());
            return new SuccessResponse("Event with id '" + newEventId + "' added successfully.");
        } catch (CalendarException | IOException e) {
            return new ErrorResponse(e.getMessage());
        }
    }

    @Override
    public Response visit(LogoutCommand command, Session context) {
        if (!context.isLoggedIn()) {
            return new ErrorResponse("You are not logged in.");
        }

        context.setCurrentUser(null);
        return new SuccessLogoutResponse("Logout successful.");
    }

}
