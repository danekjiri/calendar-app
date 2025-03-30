package com.tesco.calendar.core.models;

import java.io.Serializable;
import java.time.LocalDateTime;

public final class Event implements Serializable {
    // assigned by the server
    private Long id = null;
    private String title;
    private String description;
    private LocalDateTime startTime;

    // needed for reflection serialization from xml (Class.forName().getDeclaredConstructor().newInstance())
    private Event() {}

    public Event(String title, String description, LocalDateTime startTime, LocalDateTime endTime) {
        this.title = title;
        this.description = description;
        this.startTime = startTime;
    }

    public Long getId() { return id; }
    // should be used only by server once -> maybe nullable and server recreate it?
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDateTime getStartTime() { return startTime; }
}
