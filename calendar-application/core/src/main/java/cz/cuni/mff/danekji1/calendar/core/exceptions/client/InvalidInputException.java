package cz.cuni.mff.danekji1.calendar.core.exceptions.client;

import cz.cuni.mff.danekji1.calendar.core.exceptions.CalendarException;

public class InvalidInputException extends CalendarException {
    public InvalidInputException(String message) {
        super(message);
    }
}
