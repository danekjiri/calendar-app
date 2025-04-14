package cz.cuni.mff.danekji1.calendar.core.commands;

import java.io.Serializable;

/**
 * Marker interface for all commands.
 * It uses the Visitor pattern to decouple execution.
 */
public interface Command extends Serializable {

    /**
     * Accepts a visitor which handles the command.
     * @param visitor the command visitor
     * @param <R> the response type
     * @return a response after execution
     */
    <R> R accept(CommandVisitor<R> visitor);
}