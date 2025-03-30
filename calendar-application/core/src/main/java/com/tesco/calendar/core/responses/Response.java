package com.tesco.calendar.core.responses;

import java.io.Serializable;

/**
 * Marker interface for all responses.
 * Also supports the Visitor pattern.
 */
public interface Response extends Serializable {
    <R> R accept(ResponseVisitor<R> visitor);
}
