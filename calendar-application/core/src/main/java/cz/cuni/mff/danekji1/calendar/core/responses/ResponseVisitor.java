package cz.cuni.mff.danekji1.calendar.core.responses;

import cz.cuni.mff.danekji1.calendar.core.responses.error.ErrorResponse;
import cz.cuni.mff.danekji1.calendar.core.responses.success.SuccessEventListResponse;
import cz.cuni.mff.danekji1.calendar.core.responses.success.SuccessLoginResponse;
import cz.cuni.mff.danekji1.calendar.core.responses.success.SuccessLogoutResponse;
import cz.cuni.mff.danekji1.calendar.core.responses.success.SuccessResponse;

import java.io.IOException;

/**
 * Visitor interface for processing responses on client side.
 */
public interface ResponseVisitor<R, C> {
    R visit(SuccessResponse response, C context) throws IOException;
    R visit(SuccessLoginResponse response, C context)throws IOException;
    R visit(SuccessLogoutResponse response, C context)throws IOException;

    R visit(ErrorResponse response, C context)throws IOException;
    R visit(SuccessEventListResponse response, C context)throws IOException;
}
