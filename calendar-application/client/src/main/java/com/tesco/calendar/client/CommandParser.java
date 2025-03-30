package com.tesco.calendar.client;

import com.tesco.calendar.core.commands.Command;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Parses user input and returns a finished Command.
 * Each command type has its own static create() method.
 */
public class CommandParser {
    public Command parse(String input, InputStream userInput, OutputStream userOutput) {
        // Based on the input string (e.g. "login", "create_account", "add_event", etc.),
        // call the static create() method on the corresponding command class or get the command in some other way.
        switch (input.toLowerCase()) {
            case "login":
                return com.tesco.calendar.core.commands.LoginCommand.create(userInput, userOutput);
            case "create_account":
                return com.tesco.calendar.core.commands.CreateAccountCommand.create(userInput, userOutput);
            case "add_event":
                return com.tesco.calendar.core.commands.AddEventCommand.create(userInput, userOutput);
            case "remove_event":
                //...
            case "list_all":
                // ...
            case "list_future":
                // ...
            case "update_event":
                // ...
            default:
                throw new IllegalArgumentException("Unknown command: " + input);
        }
    }
}
