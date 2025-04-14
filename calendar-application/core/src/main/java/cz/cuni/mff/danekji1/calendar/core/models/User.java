package cz.cuni.mff.danekji1.calendar.core.models;

import java.io.Serializable;

/**
 * Represents a user in the system.
 * Contains username and password hash.
 */
public record User(String username, String passwordHash) implements Serializable {  }
