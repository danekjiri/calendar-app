package cz.cuni.mff.danekji1.calendar.core.exceptions.client;

import cz.cuni.mff.danekji1.calendar.core.exceptions.CalendarException;

public class InsufficientCommandPrivilegesException extends CalendarException {
    public InsufficientCommandPrivilegesException(String message) {
        super(message);
    }
}
