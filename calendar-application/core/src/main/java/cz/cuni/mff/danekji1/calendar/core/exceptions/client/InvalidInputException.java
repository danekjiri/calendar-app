package cz.cuni.mff.danekji1.calendar.core.exceptions.client;

/*
 * Exception thrown when an invalid input is provided by the user.
 * <p>
 * This exception is a subclass of {@link ClientException} and is used to indicate
 * errors related to invalid input in the calendar application. Eg: when the user
 * provides an invalid event ID or date format.
 */
public class InvalidInputException extends ClientException {
    public InvalidInputException(String message) {
        super(message);
    }
}
