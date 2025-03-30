package com.tesco.calendar.core.models;

import java.io.Serializable;

// might be a record?
public final class User implements Serializable {
    // should be unique
    private final String username;
    private final String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
}
