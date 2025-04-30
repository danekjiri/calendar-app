package cz.cuni.mff.danekji1.calendar.core.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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

    public String toXML() {
        assert id != null;
        return """
                <event>
                    <id>%d</id>
                    <title>%s</title>
                    <date>%s</date>
                    <time>%s</time>
                    <location>%s</location>
                    <description>%s</description>
                </event>
                """.formatted(id, title, date, time, location, description);
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
