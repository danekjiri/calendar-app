package cz.cuni.mff.danekji.calendar.core.exceptions.server;

import cz.cuni.mff.danekji.calendar.core.exceptions.CalendarException;

/**
 * Exception thrown when a general server-side error occurs.
 * <p>
 * This exception is a subclass of {@link CalendarException} and is used to indicate
 * errors that occur on the server side of the application.
 */
public class ServerException extends CalendarException {
    /**
     * Constructs a new ServerException with the specified detail message.
     *
     * @param message the detail message
     */
    public ServerException(String message) {
        super(message);
    }
}
