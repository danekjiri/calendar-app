package cz.cuni.mff.danekji1.calendar.core.commands;

/**
 * Enum representing the privileges required to execute a command.
 * <p>
 * The privileges are as follows:
 * <ul>
 *     <li>LOGGED: The command can be executed by logged-in users.</li>
 *     <li>UNLOGGED: The command can be executed by both logged-in and non-logged-in users.</li>
 *     <li>ALL: The command can be executed by all users, regardless of their login status.</li>
 * </ul>
 */
public enum Privileges {
    LOGGED,
    UNLOGGED,
    ALL,
}
