package cz.cuni.mff.danekji1.calendar.core.responses;

import cz.cuni.mff.danekji1.calendar.core.commands.QuitCommand;
import cz.cuni.mff.danekji1.calendar.core.responses.error.ErrorResponse;
import cz.cuni.mff.danekji1.calendar.core.responses.success.*;

import java.io.IOException;

/**
 * Visitor interface for processing responses on client side.
 */
public interface ResponseVisitor<R, C> {
    R visit(SuccessResponse response, C session) throws IOException;
    R visit(SuccessLoginResponse response, C session)throws IOException;
    R visit(SuccessLogoutResponse response, C session)throws IOException;
    R visit(SuccessEventListResponse response, C session)throws IOException;
    R visit(SuccessQuit response, C session) throws IOException;

    R visit(ErrorResponse response, C session)throws IOException;
}
