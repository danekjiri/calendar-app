package com.tesco.calendar.core.responses;

import com.tesco.calendar.core.models.Event;
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
    public <R> R accept(ResponseVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
