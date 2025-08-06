package cz.cuni.mff.danekji.calendar.client.gui.controllers;

import cz.cuni.mff.danekji.calendar.client.gui.CalendarGUIApplication;
import cz.cuni.mff.danekji.calendar.core.commands.*;
import cz.cuni.mff.danekji.calendar.core.models.Event;
import cz.cuni.mff.danekji.calendar.core.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

/**
 * Controller for the MainView.fxml screen.
 */
public class MainController extends CalendarController {
    private static final Logger LOGGER = LogManager.getLogger(MainController.class);

    @FXML private Label welcomeLabel;
    @FXML private TableView<Event> eventTable;
    @FXML private TableColumn<Event, Long> idColumn;
    @FXML private TableColumn<Event, String> titleColumn;
    @FXML private TableColumn<Event, LocalDate> dateColumn;
    @FXML private TableColumn<Event, LocalTime> timeColumn;
    @FXML private TableColumn<Event, String> locationColumn;
    @FXML private TableColumn<Event, String> descriptionColumn;
    @FXML private ComboBox<FutureInterval> filterComboBox;

    /**
     * Observable list to hold the event data displayed in the table.
     * It is initialized with an empty list and will be populated with events.
     */
    private final ObservableList<Event> eventData = FXCollections.observableArrayList();

    /**
     * Sets the user for the main controller and initializes the welcome message and event table.
     * @param user The user to be set.
     */
    public void setUser(User user) {
        welcomeLabel.setText(user.username() + "'s Calendar");
        initializeTable();
    }

    /**
     * Initializes the event table with columns and sets up the filter combo box.
     * It also sets the default filter to "SHOW_ALL".
     */
    private void initializeTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        eventTable.setItems(eventData);

        filterComboBox.setItems(FXCollections.observableArrayList(FutureInterval.values()));

        Callback<ListView<FutureInterval>, ListCell<FutureInterval>> cellFactory = lv -> new ListCell<>() {
            @Override
            protected void updateItem(FutureInterval item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getLabel());
            }
        };

        filterComboBox.setCellFactory(cellFactory);
        filterComboBox.setButtonCell(cellFactory.call(null));
        filterComboBox.getSelectionModel().selectedItemProperty().addListener(
                (options, oldValue, newValue) -> {
                    if (newValue != null) {
                        handleFilterSelection(newValue);
                    }
                });
        filterComboBox.getSelectionModel().select(FutureInterval.SHOW_ALL); // Default selection
    }

    /**
     * Handles the action when the "Show All Events" button is clicked.
     * It selects the "SHOW_ALL" option in the filter combo box and triggers the event display.
     */
    public void handleShowAllEvents() {
        filterComboBox.getSelectionModel().select(FutureInterval.SHOW_ALL);
    }

    /**
     * Updates the event table with a new list of events.
     *
     * @param events The list of events to display in the table.
     */
    public void updateEventTable(List<Event> events) {
        eventData.setAll(events);
    }

    /**
     * Handles the selection of a filter from the combo box.
     * It determines the date range based on the selected filter and sends a command to show future events.
     *
     * @param selection The selected FutureInterval filter.
     */
    private void handleFilterSelection(FutureInterval selection) {
        LocalDate today = LocalDate.now();
        LocalDate startDate;
        LocalDate endDate;

        switch (selection) {
            case SHOW_ALL:
                sendCommand(new ShowEventsCommand());
                return;
            case TODAY:
                startDate = today;
                endDate = today;
                break;
            case TOMORROW:
                startDate = today.plusDays(1);
                endDate = startDate;
                break;
            case THIS_WEEK:
                startDate = today;
                endDate = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                break;
            case THIS_MONTH:
                startDate = today;
                endDate = today.with(TemporalAdjusters.lastDayOfMonth());
                break;
            case THIS_YEAR:
                startDate = today;
                endDate = today.with(TemporalAdjusters.lastDayOfYear());
                break;
            default:
                return;
        }
        sendCommand(new ShowFutureEventsCommand(startDate, endDate));
    }

    /**
     * Handles the action when the "Add Event" button is clicked.
     * It opens a dialog to add a new event.
     */
    @FXML
    private void handleAddEvent() {
        showEventDialog(null);
    }

    /**
     * Handles the update of an event selected in the table.
     * It checks if an event is selected, and if so, opens a dialog to update the event details.
     * If no event is selected, it shows an alert to the user.
     */
    @FXML
    private void handleUpdateEvent() {
        Event selectedEvent = eventTable.getSelectionModel().getSelectedItem();
        if (selectedEvent == null) {
            showAlert("No Selection", "Please select an event in the table to update.");
            return;
        }
        showEventDialog(selectedEvent);
    }

    /**
     * Handles the deletion of an event selected in the table.
     * It prompts the user for confirmation before deleting the event and sends a command to the server.
     */
    @FXML
    private void handleDeleteEvent() {
        Event selectedEvent = eventTable.getSelectionModel().getSelectedItem();
        if (selectedEvent == null) {
            showAlert("No Selection", "Please select an event in the table to delete.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Delete Event: " + selectedEvent.getTitle());
        confirmation.setContentText("Are you sure you want to delete this event?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            sendCommand(new DeleteEventCommand(selectedEvent.getId()));
            eventTable.getItems().remove(selectedEvent);
        }
    }

    /**
     * Handles the action when the "Delete User" button is clicked.
     * It prompts the user for confirmation before deleting their account and sends a command to the server.
     */
    @FXML
    private void handleDeleteUser() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Account Deletion");
        confirmation.setHeaderText("Permanently Delete Your Account?");
        confirmation.setContentText("Are you sure? This will delete your user profile and all associated events. This action cannot be undone.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            User currentUser = CalendarGUIApplication.getClient().getCurrentSession().getCurrentUser();
            if (currentUser != null) {
                sendCommand(new DeleteUserCommand(currentUser));
            } else {
                showAlert("Error", "Could not identify the current user to delete.");
            }
        }
    }

    /**
     * Handles the action when the "Logout" button is clicked.
     * It sends a logout command to the server and transitions back to the login screen.
     */
    @FXML
    private void handleLogout() {
        sendCommand(new LogoutCommand());
    }

    /**
     * Displays a dialog for adding or updating an event.
     * If the event is null, it creates a new event; otherwise, it updates the existing one.
     *
     * @param event The event to update, or null to create a new one.
     */
    private void showEventDialog(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/EventDialog.fxml"));
            GridPane page = loader.load();

            Dialog<Event> dialog = new Dialog<>();
            dialog.setTitle(event == null ? "Add New Event" : "Update Event");
            dialog.getDialogPane().setContent(page);

            EventDialogController controller = loader.getController();
            controller.setDialog(dialog, event);

            Optional<Event> result = dialog.showAndWait();
            result.ifPresent(eventData -> {
                if (event == null) {
                    sendCommand(new AddEventCommand(eventData));
                } else {
                    sendCommand(new UpdateEventCommand(Event.withId(event.getId(), eventData)));
                }
                sendCommand(new ShowEventsCommand());
            });

        } catch (IOException e) {
            LOGGER.error("Failed to load the event dialog.", e);
        }
    }
}