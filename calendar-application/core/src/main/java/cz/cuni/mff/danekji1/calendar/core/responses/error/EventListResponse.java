package cz.cuni.mff.danekji1.calendar.core.responses.error;

import cz.cuni.mff.danekji1.calendar.core.models.Event;
import cz.cuni.mff.danekji1.calendar.core.responses.Response;
import cz.cuni.mff.danekji1.calendar.core.responses.ResponseVisitor;

import java.util.List;

// might be record?
public final class EventListResponse implements Response {
    private final List<Event> events;

    public EventListResponse(List<Event> events) {
        this.events = events;
    }

    public List<Event> getEvents() { return events; }

    /**
     * Helps to handle the command on the client side when displaying response.
     */
    @Override
    public <R, C> R accept(ResponseVisitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
