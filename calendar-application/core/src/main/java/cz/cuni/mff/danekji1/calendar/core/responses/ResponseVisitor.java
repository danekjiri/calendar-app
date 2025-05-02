package cz.cuni.mff.danekji1.calendar.core.responses;

import cz.cuni.mff.danekji1.calendar.core.responses.error.ErrorResponse;
import cz.cuni.mff.danekji1.calendar.core.responses.error.EventListResponse;
import cz.cuni.mff.danekji1.calendar.core.responses.success.SuccessLoginResponse;
import cz.cuni.mff.danekji1.calendar.core.responses.success.SuccessLogoutResponse;
import cz.cuni.mff.danekji1.calendar.core.responses.success.SuccessResponse;

/**
 * Visitor interface for processing responses on client side.
 */
public interface ResponseVisitor<R, C> {
    R visit(SuccessResponse response, C context);
    R visit(SuccessLoginResponse response, C context);
    R visit(SuccessLogoutResponse response, C context);

    R visit(ErrorResponse response, C context);
    R visit(EventListResponse response, C context);
}
