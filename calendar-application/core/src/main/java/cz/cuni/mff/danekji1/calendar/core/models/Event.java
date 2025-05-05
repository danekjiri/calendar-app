package cz.cuni.mff.danekji1.calendar.core.models;

import cz.cuni.mff.danekji1.calendar.core.exceptions.server.XmlDatabaseException;
import cz.cuni.mff.danekji1.calendar.core.xml.XMLCalendarTags;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Element;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Represents an event in the calendar.
 * <p>
 * This class contains information about the event, including its title, date, time,
 * location, and description. It also provides methods for converting the event to and
 * from XML format for serialization and deserialization.
 */
public final class Event implements Serializable {
    private final static Logger LOGGER = LogManager.getLogger(Event.class);

    private final Long id;
    private final String title;
    private final LocalDate date;
    private final LocalTime time;
    private final String location;
    private final String description;

    /**
     * Constructor for creating an Event object with id null
     *
     * @param title       The title of the event.
     * @param date        The date of the event.
     * @param time        The time of the event.
     * @param location    The location of the event.
     * @param description A description of the event.
     */
    public Event(String title, LocalDate date, LocalTime time, String location, String description) {
        this(null, title, date, time, location, description);
    }

    /**
     * Factory method for creating an Event object with a specified id.
     *
     * @param id The ID of the event.
     * @param event The Event object to copy.
     */
    public static Event withId(Long id, Event event) {
        return new Event(id, event.title, event.date, event.time, event.location, event.description);
    }

    private Event(Long id, String title, LocalDate date, LocalTime time, String location, String description) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.time = time;
        this.location = location;
        this.description = description;
    }

    /**
     * Converts the Event object to an XML element for serialization.
     *
     * @return An XML element representing the event.
     */
    public Element toXMLElement() {
        assert id != null;
        Element eventElement = new Element(XMLCalendarTags.EVENT_TAG);

        Element idElement = new Element(XMLCalendarTags.ID_TAG);
        idElement.setText(String.valueOf(id));
        Element titleElement = new Element(XMLCalendarTags.TITLE_TAG);
        titleElement.setText(title);
        Element dateElement = new Element(XMLCalendarTags.DATE_TAG);
        dateElement.setText(date.toString());
        Element timeElement = new Element(XMLCalendarTags.TIME_TAG);
        timeElement.setText(time.toString());
        Element locationElement = new Element(XMLCalendarTags.LOCATION_TAG);
        locationElement.setText(location);
        Element descriptionElement = new Element(XMLCalendarTags.DESCRIPTION_TAG);
        descriptionElement.setText(description);

        eventElement.addContent(List.of(idElement, titleElement, dateElement, timeElement, locationElement, descriptionElement));
        return eventElement;
    }

    /**
     * Converts an XML element to an Event object for deserialization.
     *
     * @param eventElement The XML element representing the event.
     * @return An Event object created from the XML element.
     */
    public static Event fromXMLElement(Element eventElement) {
        assert eventElement != null && eventElement.getName().equals(XMLCalendarTags.EVENT_TAG);

        try {
            Long id = Long.parseLong(eventElement.getChildText(XMLCalendarTags.ID_TAG));
            String title = eventElement.getChildText(XMLCalendarTags.TITLE_TAG);
            LocalDate date = LocalDate.parse(eventElement.getChildText(XMLCalendarTags.DATE_TAG));
            LocalTime time = LocalTime.parse(eventElement.getChildText(XMLCalendarTags.TIME_TAG));
            String location = eventElement.getChildText(XMLCalendarTags.LOCATION_TAG);
            String description = eventElement.getChildText(XMLCalendarTags.DESCRIPTION_TAG);

            return new Event(id, title, date, time, location, description);
        } catch (NumberFormatException | DateTimeParseException e) {
            LOGGER.error("Failed to parse event from XML: {}", e.getMessage());
            throw new XmlDatabaseException("Failed to parse event from XML");
        }
    }

    /**
     * Gets the ID of the event.
     *
     * @return The ID of the event.
     */
    public Long getId() {
        assert id != null;
        return id;
    }

    /**
     * Gets the title of the event.
     *
     * @return The title of the event.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the date of the event.
     *
     * @return The date of the event.
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Gets the time of the event.
     *
     * @return The time of the event.
     */
    public LocalTime getTime() {
        return time;
    }

    /**
     * Gets the location of the event.
     *
     * @return The location of the event.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Gets the description of the event.
     *
     * @return The description of the event.
     */
    public String getDescription() {
        return description;
    }


    /**
     * Coverts the Event object to a string representation.
     *
     * @return A string representation of the Event object.
     */
    @Override
    public String toString() {
        return "Event { id=%d, title='%s', date='%s', time='%s', location='%s', description='%s'}"
                .formatted(id, title, date, time, location, description);
    }
}
