package cz.cuni.mff.danekji1.calendar.core.exceptions.client;

/**
 * Exception thrown when an unknown command is encountered.
 * <p>
 * This exception is a subclass of {@link ClientException} and is used to indicate
 * errors related to unrecognized commands in the calendar application.
 */
public class UnknownCommandException extends ClientException {
    private final String command;

    public UnknownCommandException(String message, String command) {
        super(message);
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
