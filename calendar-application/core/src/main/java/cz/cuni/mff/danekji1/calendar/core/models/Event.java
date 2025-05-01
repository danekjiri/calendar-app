package cz.cuni.mff.danekji1.calendar.core.models;

import cz.cuni.mff.danekji1.calendar.core.xml.XMLCalendarTags;
import org.jdom2.Element;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public final class Event implements Serializable {
    private Long id;
    private String title;
    private LocalDate date;
    private LocalTime time;
    private String location;
    private String description;

    // needed for reflection serialization from xml (Class.forName().getDeclaredConstructor().newInstance())
    private Event() {}

    public Event(String title, LocalDate date, LocalTime time, String location, String description) {
        this(null, title, date, time, location, description);
    }

    public static Event withId(Long id, Event e) {
        return new Event(id, e.title, e.date, e.time, e.location, e.description);
    }

    private Event(Long id, String title, LocalDate date, LocalTime time, String location, String description) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.time = time;
        this.location = location;
        this.description = description;
    }

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

    public Long getId() {
        assert id != null;
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }
}
