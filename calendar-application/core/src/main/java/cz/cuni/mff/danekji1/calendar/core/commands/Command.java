package cz.cuni.mff.danekji1.calendar.core.commands;

import java.io.Serializable;

/**
 * Marker interface for all commands.
 * It uses the Visitor pattern to decouple execution.
 */
public interface Command extends Serializable {

    /**
     * Returns the privileges in which the command operates.
     * @return the privileges
     */
    Privileges getPrivileges();

    /**
     * Accepts a visitor which handles the command on sever-side.
     * @param visitor the command visitor
     * @param <R> the response type
     * @return a response after execution
     */
    <R, C> R accept(CommandVisitor<R, C> visitor, C context);
}