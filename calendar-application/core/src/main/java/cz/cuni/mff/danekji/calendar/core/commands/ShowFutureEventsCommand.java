package cz.cuni.mff.danekji.calendar.core.commands;

import cz.cuni.mff.danekji.calendar.core.client.session.ClientSession;
import cz.cuni.mff.danekji.calendar.core.client.ui.UserInterface;
import cz.cuni.mff.danekji.calendar.core.exceptions.InsufficientCommandPrivilegesException;
import cz.cuni.mff.danekji.calendar.core.exceptions.client.InvalidInputException;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;

/**
 * Command to retrieve and display a list of events for a specific future period.
 */
public final class ShowFutureEventsCommand implements Command {
    /**
     * The name of the command used to identify it in the command registry.
     */
    public static final String COMMAND_NAME = "show_future_events";

    /**
     * The start date of the period for which events are to be shown.
     */
    private final LocalDate startDate;

    /**
     * The end date of the period for which events are to be shown.
     */
    private final LocalDate endDate;

    /**
     * Constructor for creating the command with a specified date range.
     *
     * @param startDate The start date of the period.
     * @param endDate The end date of the period.
     */
    public ShowFutureEventsCommand(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Default constructor for reflection
    ShowFutureEventsCommand() {
        this(null, null);
    }

    /**
     * Builds the command based on user input and the current session.
     * This method prompts the user to select a period for which they want to see future events,
     * such as tomorrow, this week, this month, this year, or a custom date range.
     *
     * @param ui The user interface for prompting input.
     * @param session The client session to check login status.
     * @return A new instance of ShowFutureEventsCommand with the specified date range.
     * @throws InsufficientCommandPrivilegesException if the user is not logged in.
     * @throws IOException if an I/O error occurs during input.
     * @throws InvalidInputException if the input is invalid or cannot be parsed.
     */
    @Override
    public Command buildCommand(UserInterface ui, ClientSession session) throws InsufficientCommandPrivilegesException, IOException, InvalidInputException {
        if (!session.isLoggedIn()) {
            throw new InsufficientCommandPrivilegesException("You must be logged in to list events.");
        }

        String menu = """
                Select a period to show future events:
                1. Tomorrow
                2. This Week
                3. This Month
                4. This Year
                5. Custom Date Range
                Enter your choice (1-5):\s""";

        String choice = ui.promptForInput(menu).trim();
        LocalDate today = LocalDate.now();
        LocalDate start, end;

        switch (choice) {
            case "1": // Tomorrow
                start = today.plusDays(1);
                end = start;
                break;
            case "2": // This Week
                start = today;
                end = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                break;
            case "3": // This Month
                start = today;
                end = today.with(TemporalAdjusters.lastDayOfMonth());
                break;
            case "4": // This Year
                start = today;
                end = today.with(TemporalAdjusters.lastDayOfYear());
                break;
            case "5": // Custom
                try {
                    String startInput = ui.promptForInput("Enter start date (YYYY-MM-DD): ").trim();
                    start = LocalDate.parse(startInput);
                    String endInput = ui.promptForInput("Enter end date (YYYY-MM-DD): ").trim();
                    end = LocalDate.parse(endInput);

                    if (end.isBefore(start)) {
                        throw new InvalidInputException("End date cannot be before the start date.");
                    }
                } catch (DateTimeParseException e) {
                    throw new InvalidInputException("Invalid date format. Please use YYYY-MM-DD.");
                }
                break;
            default:
                throw new InvalidInputException("Invalid choice. Please enter a number between 1 and 5.");
        }
        return new ShowFutureEventsCommand(start, end);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return COMMAND_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Displays a list of upcoming events for a chosen period.";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Privileges getPrivileges() {
        return Privileges.LOGGED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R, C> R accept(CommandVisitor<R, C> visitor, C session) {
        return visitor.visit(this, session);
    }

    /**
     * Gets the start date of the event period.
     *
     * @return The start date of the period.
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Gets the end date of the event period.
     *
     * @return The end date of the period.
     */
    public LocalDate getEndDate() {
        return endDate;
    }
}