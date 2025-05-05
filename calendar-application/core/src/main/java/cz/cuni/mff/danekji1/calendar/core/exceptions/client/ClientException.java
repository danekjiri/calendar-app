package cz.cuni.mff.danekji1.calendar.core.exceptions.client;

import cz.cuni.mff.danekji1.calendar.core.exceptions.CalendarException;

/**
 * Exception thrown when a general client-side error occurs.
 * <p>
 * This exception is a subclass of {@link CalendarException} and is used to indicate
 * errors that occur on the client side of the application.
 */
public class ClientException extends CalendarException {
    public ClientException(String message) {
        super(message);
    }
}
