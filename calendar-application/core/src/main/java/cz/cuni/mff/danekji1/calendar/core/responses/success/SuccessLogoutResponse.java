package cz.cuni.mff.danekji1.calendar.core.responses.success;

import cz.cuni.mff.danekji1.calendar.core.responses.Response;
import cz.cuni.mff.danekji1.calendar.core.responses.ResponseVisitor;

public record SuccessLogoutResponse(String message) implements Response {

    @Override
    public <R, C> R accept(ResponseVisitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
