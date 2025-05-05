package cz.cuni.mff.danekji1.calendar.client.cli.ui;

import cz.cuni.mff.danekji1.calendar.core.responses.*;
import cz.cuni.mff.danekji1.calendar.core.responses.error.ErrorResponse;
import cz.cuni.mff.danekji1.calendar.core.responses.success.*;
import cz.cuni.mff.danekji1.calendar.core.session.ClientSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Implementation of the ResponseDispatcher interface for CLI applications.
 * It handles the dispatching of responses to the appropriate methods based on their type.
 */
public class CLIResponseDispatcher implements ResponseVisitor<Void, ClientSession> {
    private static final Logger LOGGER = LogManager.getLogger(CLIResponseDispatcher.class);

    private final OutputStreamWriter output;

    /**
     * Constructor for CLIResponseDispatcher.
     * Initializes the output stream writer.
     *
     * @param output Output stream for writing responses (e.g., System.out).
     */
    public CLIResponseDispatcher(OutputStream output) {
        this.output = new OutputStreamWriter(output);
    }

    /**
     * The visitor endpoint for SuccessResponse, which is the base class for all success responses.
     * It handles the response by writing the message to the output stream.
     *
     * @param response The {@link SuccessResponse} to be processed.
     * @param session The client session associated with the response.
     * @return Void
     * @throws IOException If an I/O error occurs while writing to the output stream.
     */
    @Override
    public Void visit(SuccessResponse response, ClientSession session) throws IOException {
        output.write(response.message() + "\n");
        output.flush();
        return null;
    }

    /**
     * The visitor endpoint for SuccessLoginResponse.
     * It handles the response by writing the message to the output stream and setting the
     *  currently logged user in the session.
     *
     * @param response The {@link SuccessLoginResponse} to be processed.
     * @param session The client session associated with the response.
     * @return Void
     * @throws IOException If an I/O error occurs while writing to the output stream.
     */
    @Override
    public Void visit(SuccessLoginResponse response, ClientSession session) throws IOException {
        output.write(response.message() + "\n");
        session.setCurrentUser(response.user());
        output.flush();
        return null;
    }

    /**
     * The visitor endpoint for SuccessLogoutResponse.
     * It handles the response by writing the message to the output stream and unsetting the
     *  current user in the session.
     *
     * @param response The {@link SuccessLogoutResponse} to be processed.
     * @param session The client session associated with the response.
     * @return Void
     * @throws IOException If an I/O error occurs while writing to the output stream.
     */
    @Override
    public Void visit(SuccessLogoutResponse response, ClientSession session) throws IOException {
        output.write(response.message() + "\n");
        session.unsetCurrentUser();
        output.flush();
        return null;
    }

    /**
     * The visitor endpoint for SuccessEventListResponse.
     * It handles the response by writing the list of events to the output stream.
     *
     * @param response The {@link SuccessEventListResponse} to be processed.
     * @param session The client session associated with the response.
     * @return Void
     * @throws IOException If an I/O error occurs while writing to the output stream.
     */
    @Override
    public Void visit(SuccessEventListResponse response, ClientSession session) throws IOException {
        output.write("Event list:\n");
        for (var event : response.events()) {
            output.write("\t");
            output.write(event.toString());
            output.write("\n");
        }
        output.flush();
        return null;
    }

    /**
     * The visitor endpoint for SuccessQuit.
     * It handles the response by writing the message 'Quitting...' to the output stream and
     *  terminating the session.
     *
     * @param response The {@link SuccessQuit} to be processed.
     * @param session The client session associated with the response.
     * @return Void
     * @throws IOException If an I/O error occurs while writing to the output stream.
     */
    @Override
    public Void visit(SuccessQuit response, ClientSession session) throws IOException {
        output.write("Quitting...\n");
        output.flush();
        session.terminate();
        return null;
    }

    /**
     * The visitor endpoint for ErrorResponse.
     * It handles the response by writing the error message to the output stream
     *
     * @param response The {@link ErrorResponse} to be processed.
     * @param session The client session associated with the response.
     * @return Void
     * @throws IOException If an I/O error occurs while writing to the output stream.
     */
    @Override
    public Void visit(ErrorResponse response, ClientSession session) throws IOException {
        output.write(response.errorMessage() + "\n");
        output.flush();
        return null;
    }
}
