package cz.cuni.mff.danekji1.calendar.server.storage;

import cz.cuni.mff.danekji1.calendar.core.models.Event;
import java.util.List;

/**
 * Repository interface for event persistence.
 */
public interface EventRepository {
    void createAccount(String username, int passwordHash);
    boolean authenticate(String username, int passwordHash);

    void addEvent(String username, Event event);
    void removeEvent(String username, Long eventId);
    List<Event> getAllEvents(String username);
    List<Event> getFutureEvents(String username);
    void updateEvent(String username, Event event);
}
