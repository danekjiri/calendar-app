package cz.cuni.mff.danekji1.calendar.core.commands;

import cz.cuni.mff.danekji1.calendar.core.models.Event;

import java.io.InputStream;
import java.io.OutputStream;

//  might be record?
public final class AddEventCommand implements Command {
    private final Event event;

    private AddEventCommand(Event event) {
        this.event = event;
    }

    public Event getEvent() { return event; }

    /**
     * Helps to handle the command on the server side.
     */
    @Override
    public <R> R accept(CommandVisitor<R> visitor) {
        return visitor.visit(this);
    }

    /**
     * Creates a finished AddEventCommand by prompting the user.
     */
    public static AddEventCommand create(InputStream in, OutputStream out) {
        throw new UnsupportedOperationException("Not implemented yet");
        // todo: implement this method
    }
}
