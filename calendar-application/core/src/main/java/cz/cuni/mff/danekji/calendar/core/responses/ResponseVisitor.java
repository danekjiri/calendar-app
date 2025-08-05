package cz.cuni.mff.danekji.calendar.core.responses;

import cz.cuni.mff.danekji.calendar.core.commands.QuitCommand;
import cz.cuni.mff.danekji.calendar.core.responses.error.ErrorResponse;
import cz.cuni.mff.danekji.calendar.core.responses.success.*;

import java.io.IOException;

/**
 * Visitor interface for processing responses on client side.
 * @param <R> The type of the result returned by the visitor methods.
 * @param <S> The type of the client session used for context.
 */
public interface ResponseVisitor<R, S> {
    /**
     * Base method for presenting success responses to client
     *
     * @param response The {@link SuccessResponse} to be processed
     * @param session  The client session to be give a context
     * @return The result of processing the response (in this case Void)
     * @throws IOException if an I/O error occurs
     */
    R visit(SuccessResponse response, S session) throws IOException;

    /**
     * Successful login response handle update of session to current user
     *
     * @param response The {@link SuccessLoginResponse} to be processed
     * @param session  The client session to be give a context for update
     * @return The result of processing the response (in this case Void)
     * @throws IOException if an I/O error occurs
     */
    R visit(SuccessLoginResponse response, S session)throws IOException;

    /**
     * Successful logout response handle update of session to null user marking unlogged state
     *
     * @param response The {@link SuccessLogoutResponse} to be processed
     * @param session  The client session to be give a context for update
     * @return The result of processing the response (in this case Void)
     * @throws IOException if an I/O error occurs
     */
    R visit(SuccessLogoutResponse response, S session)throws IOException;

    /**
     * Successful event list response handle presentation of given events to user
     *
     * @param response The {@link QuitCommand} to be processed
     * @param session The client session
     * @return The result of processing the response (in this case Void)
     * @throws IOException if an I/O error occurs
     */
    R visit(SuccessEventListResponse response, S session)throws IOException;

    /**
     * Successful quit response handle update of session active state to null marking dead session
     *
     * @param response The {@link SuccessQuit} to be processed
     * @param session  The client session to be give a context for update
     * @return The result of processing the response (in this case Void)
     * @throws IOException if an I/O error occurs
     */
    R visit(SuccessQuit response, S session) throws IOException;

    /**
     * Error response handle presentation of error message to user
     *
     * @param response The {@link ErrorResponse} to be processed
     * @param session  The client session to be give a context for update
     * @return The result of processing the response (in this case Void)
     * @throws IOException if an I/O error occurs
     */
    R visit(ErrorResponse response, S session)throws IOException;

    /**
     * Successful event deletion response handle presentation of success message to user
     *
     * @param response The {@link SuccessDeleteUserResponse} to be processed
     * @param session  The client session to be give a context for update
     * @return The result of processing the response (in this case Void)
     * @throws IOException if an I/O error occurs
     */
    R visit(SuccessDeleteUserResponse response, S session) throws IOException;
}
