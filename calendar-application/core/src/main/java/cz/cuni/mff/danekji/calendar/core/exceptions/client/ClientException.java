package cz.cuni.mff.danekji.calendar.core.exceptions.client;

import cz.cuni.mff.danekji.calendar.core.exceptions.CalendarException;

/**
 * Exception thrown when a general client-side error occurs.
 * <p>
 * This exception is a subclass of {@link CalendarException} and is used to indicate
 * errors that occur on the client side of the application.
 */
public class ClientException extends CalendarException {
    /**
     * Constructs a new ClientException with the specified detail message.
     *
     * @param message the detail message
     */
    public ClientException(String message) {
        super(message);
    }
}
