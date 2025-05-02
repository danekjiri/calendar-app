package cz.cuni.mff.danekji1.calendar.core.responses;

import cz.cuni.mff.danekji1.calendar.core.responses.error.ErrorResponse;
import cz.cuni.mff.danekji1.calendar.core.responses.error.EventListResponse;
import cz.cuni.mff.danekji1.calendar.core.responses.success.SuccessLoginResponse;
import cz.cuni.mff.danekji1.calendar.core.responses.success.SuccessLogoutResponse;
import cz.cuni.mff.danekji1.calendar.core.responses.success.SuccessResponse;

/**
 * Visitor interface for processing responses on client side.
 */
public interface ResponseVisitor<R> {
    R visit(SuccessResponse response);
    R visit(SuccessLoginResponse response);
    R visit(SuccessLogoutResponse response);

    R visit(ErrorResponse response);
    R visit(EventListResponse response);
}
