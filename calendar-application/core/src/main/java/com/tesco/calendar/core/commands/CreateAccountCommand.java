package com.tesco.calendar.core.commands;

import java.io.InputStream;
import java.io.OutputStream;

//  might be record?
public final class CreateAccountCommand implements Command {
    private final String username;
    private final String passwordHash;

    private CreateAccountCommand(String username, String password) {
        this.username = username;
        // todo: hash the password
        this.passwordHash = password;
    }

    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }

    /**
     * Helps to handle the command on the server side.
     */
    @Override
    public <R> R accept(CommandVisitor<R> visitor) {
        return visitor.visit(this);
    }

    /**
     * Creates a finished CreateAccountCommand by prompting the user.
     */
    public static CreateAccountCommand create(InputStream in, OutputStream out) {
        throw new UnsupportedOperationException("Not implemented yet");
        // todo: implement this method
    }
}
