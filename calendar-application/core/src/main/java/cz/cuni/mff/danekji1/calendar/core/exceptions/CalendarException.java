package cz.cuni.mff.danekji1.calendar.core.exceptions;

/**
 * Exception thrown when a general error occurs in the calendar application.
 * It is a base class for all exceptions related to the calendar application.
 * <p>
 * This exception is a subclass of {@link RuntimeException} and is used to indicate
 * errors that occur within the calendar application.
 */
public class CalendarException extends RuntimeException {
    public CalendarException(String message) {
        super(message);
    }
}
