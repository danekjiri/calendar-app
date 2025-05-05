package cz.cuni.mff.danekji1.calendar.core.responses;

import java.io.IOException;
import java.io.Serializable;

/**
 * Marker interface for all responses.
 * Also supports the Visitor pattern.
 */
public interface Response extends Serializable {

    /**
     * Helps to handle the command on the client side when displaying response.
     *
     * @param visitor The visitor
     * @param session The session
     * @param <R>     The return type of the visitor
     * @param <S>     The type of the session
     * @return The result of visiting this response (in this case Void)
     * @throws IOException if an I/O error occurs (displaying the response)
     */
    <R, S> R accept(ResponseVisitor<R, S> visitor, S session) throws IOException;
}
