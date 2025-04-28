package cz.cuni.mff.danekji1.calendar.core.responses;

/**
 * Visitor interface for processing responses on client side.
 */
public interface ResponseVisitor<R> {
    R visit(SuccessResponse response);
    R visit(SuccessLoginResponse response);
    R visit(ErrorResponse response);
    R visit(EventListResponse response);
}
