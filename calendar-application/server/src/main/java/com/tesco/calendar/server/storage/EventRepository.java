package com.tesco.calendar.server.storage;

import com.tesco.calendar.core.models.Event;
import java.util.List;

/**
 * Repository interface for event persistence.
 */
public interface EventRepository {
    void addEvent(String username, Event event);
    void removeEvent(String username, Long eventId);
    List<Event> getAllEvents(String username);
    List<Event> getFutureEvents(String username);
    void updateEvent(String username, Event event);
}
