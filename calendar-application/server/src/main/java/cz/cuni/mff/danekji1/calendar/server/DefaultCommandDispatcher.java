package cz.cuni.mff.danekji1.calendar.server;

import cz.cuni.mff.danekji1.calendar.core.commands.*;
import cz.cuni.mff.danekji1.calendar.core.exceptions.CalendarException;
import cz.cuni.mff.danekji1.calendar.core.exceptions.server.XmlDatabaseException;
import cz.cuni.mff.danekji1.calendar.core.responses.error.ErrorResponse;
import cz.cuni.mff.danekji1.calendar.core.responses.success.SuccessEventListResponse;
import cz.cuni.mff.danekji1.calendar.core.responses.success.SuccessLoginResponse;
import cz.cuni.mff.danekji1.calendar.core.responses.success.SuccessLogoutResponse;
import cz.cuni.mff.danekji1.calendar.core.responses.success.SuccessResponse;
import cz.cuni.mff.danekji1.calendar.server.storage.EventRepository;
import cz.cuni.mff.danekji1.calendar.core.responses.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Constructor;

public class DefaultCommandDispatcher implements CommandVisitor<Response, Session> {
    private static final Logger LOGGER = LogManager.getLogger(DefaultCommandDispatcher.class);
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

        if (eventRepository.authenticate(command.getUser())) {
            session.setCurrentUser(command.getUser());
            return new SuccessLoginResponse("Login as user '" + command.getUser().username() + "' is successful.", command.getUser());
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
            eventRepository.createAccount(command.getUser());
        } catch (CalendarException e) {
            return new ErrorResponse(e.getMessage());
        }
        return new SuccessResponse("Account for account '" + command.getUser().username() + "' created successfully.");
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
            long newEventId = eventRepository.addEvent(context.getCurrentUser(), command.getEvent());
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

    @Override
    public Response visit(HelpCommand command, Session context) {
        StringBuilder helpMessage = new StringBuilder();
        helpMessage.append("Available commands:\n");

        for (var commandClass : command.getAvailableCommands().values()) {
            try {
                Constructor<? extends Command> constructor = commandClass.getDeclaredConstructor();
                constructor.setAccessible(true);
                Command tempCommand = constructor.newInstance();

                Privileges currentPrivileges = context.isLoggedIn() ? Privileges.LOGGED : Privileges.UNLOGGED;
                if (tempCommand.getPrivileges() == Privileges.ALL || tempCommand.getPrivileges() == currentPrivileges) {
                    helpMessage.append(tempCommand.getName()).append(": ").append(tempCommand.getDescription()).append("\n");
                }
            } catch (Exception e) {
                LOGGER.error("Failed to create command instance for: {}", commandClass.getSimpleName(), e);
                return new ErrorResponse("Failed to generate help message.");
            }
        }
        return new SuccessResponse(helpMessage.toString());
    }

    @Override
    public Response visit(ShowEventsCommand command, Session context) {
        if (!context.isLoggedIn()) {
            return new ErrorResponse("You must be logged in to show events.");
        }

        try {
            var events = eventRepository.getAllEvents(context.getCurrentUser());
            return new SuccessEventListResponse(events);
        } catch (XmlDatabaseException e) {
            return new ErrorResponse("Failed to retrieve events: " + e.getMessage());
        }

    }

    @Override
    public Response visit(DeleteEventCommand command, Session context) {
        if (!context.isLoggedIn()) {
            return new ErrorResponse("You must be logged in to delete an event.");
        }

        try {
            eventRepository.deleteEvent(context.getCurrentUser(), command.getEventId());
            return new SuccessResponse("Event with id '" + command.getEventId() + "' deleted successfully.");
        } catch (XmlDatabaseException e) {
            return new ErrorResponse("Failed to delete event: " + e.getMessage());
        }
    }

    @Override
    public Response visit(UpdateEventCommand command, Session context) {
        if (!context.isLoggedIn()) {
            return new ErrorResponse("You must be logged in to update an event.");
        }

        try {
            eventRepository.updateEvent(context.getCurrentUser(), command.getEvent());
            return new SuccessResponse("Event with id '" + command.getEvent().getId() + "' updated successfully.");
        } catch (XmlDatabaseException e) {
            return new ErrorResponse("Failed to update event: " + e.getMessage());
        }
    }
}
