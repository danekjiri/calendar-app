package cz.cuni.mff.danekji1.calendar.core.responses;

import java.io.Serializable;

/**
 * Marker interface for all responses.
 * Also supports the Visitor pattern.
 */
public interface Response extends Serializable {
    <R, C> R accept(ResponseVisitor<R, C> visitor, C context);
}
