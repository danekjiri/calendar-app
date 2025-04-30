package cz.cuni.mff.danekji1.calendar.core.exceptions.client;

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
