package cz.cuni.mff.danekji1.calendar.core.responses.error;

import cz.cuni.mff.danekji1.calendar.core.responses.Response;
import cz.cuni.mff.danekji1.calendar.core.responses.ResponseVisitor;
import cz.cuni.mff.danekji1.calendar.core.ui.ClientState;

//  might be record?
public final class ErrorResponse implements Response {
    private final String errorMessage;

    public ErrorResponse(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() { return errorMessage; }

    /**
     * Helps to handle the command on the client side when displaying response.
     */
    @Override
    public <R, C> R accept(ResponseVisitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
