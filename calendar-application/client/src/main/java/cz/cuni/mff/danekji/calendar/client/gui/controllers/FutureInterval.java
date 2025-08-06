package cz.cuni.mff.danekji.calendar.client.gui.controllers;

/**
 * Enum representing different intervals for displaying future events in the calendar GUI.
 * Each interval has a label that can be used for GUI display purposes.
 */
public enum FutureInterval {
    /**
     * Represents the events that are happening today.
     */
    TODAY("Today"),
    /**
     * Represents the events until the end of the current day.
     */
    TOMORROW("Tomorrow"),
    /**
     * Represents the next 7 days, starting from tomorrow.
     */
    THIS_WEEK("This Week"),
    /**
     * Represents the next 7 days, starting from today.
     */
    THIS_MONTH("This Month"),
    /**
     * Represents the current year, including all events from the beginning of the year to today.
     */
    THIS_YEAR("This Year"),
    /**
     * Represents all future events without any specific interval.
     */
    SHOW_ALL("Show All");

    private final String label;
    private FutureInterval(String label) {
        this.label = label;
    }

    /**
     * Returns the label of the future interval for GUI display purposes.
     *
     * @return The label of the future interval.
     */
    public String getLabel() {
        return label;
    }
}
