package cz.cuni.mff.danekji1.calendar.core.exceptions.server;

import cz.cuni.mff.danekji1.calendar.core.exceptions.CalendarException;

/**
 * Exception thrown when a general server-side error occurs.
 * <p>
 * This exception is a subclass of {@link CalendarException} and is used to indicate
 * errors that occur on the server side of the application.
 */
public class ServerException extends CalendarException {
    public ServerException(String message) {
        super(message);
    }
}
