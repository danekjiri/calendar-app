package cz.cuni.mff.danekji.calendar.client.gui.controllers;

public enum FutureInterval {
    TODAY("Today"),
    TOMORROW("Tomorrow"),
    THIS_WEEK("This Week"),
    THIS_MONTH("This Month"),
    THIS_YEAR("This Year"),
    SHOW_ALL("Show All");

    private final String label;
    private FutureInterval(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
