package com.tesco.calendar.client;

import com.tesco.calendar.core.responses.*;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * A simple command-line user interface.
 */
public final class CLIUserInterface implements UserInterface {
    private final InputStream userInput;
    private final OutputStream userOutput;

    public CLIUserInterface(InputStream userInput, OutputStream userOutput) {
        this.userInput = userInput;
        this.userOutput = userOutput;
    }

    /**
     * The main entry point for the CLI.
     * It reads commands from the console and sends them to the client.
     */
    @Override
    public void start(Client client) {
        throw new UnsupportedOperationException("Not implemented yet");
        // todo: implement this method
    }

    /**
     * Displays the response from the server.
     * This method uses the Visitor pattern to handle different types of responses.
     */
    @Override
    public void displayResponse(Response response) {
//        // example of simple implementation of visitor pattern using anonymous class
//        response.accept(new ResponseVisitor<Void>() {
//            @Override
//            public Void visit(SuccessResponse resp) {
//                System.out.println("SUCCESS: " + resp.getMessage());
//                // ...
//            }
//
//            @Override
//            public Void visit(ErrorResponse resp) {
//                System.out.println("ERROR: " + resp.getErrorMessage());
//                // ...
//            }
//
//            @Override
//            public Void visit(EventListResponse resp) {
//                System.out.println("Events:");
//                resp.getEvents().forEach(event ->
//                        System.out.println(" - " + event.getTitle())
//                );
//                // ...
//            }
//        });
        throw new UnsupportedOperationException("Not implemented yet");
        // todo: implement this method
    }
}
