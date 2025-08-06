package cz.cuni.mff.danekji.calendar.client.gui.controllers;

import cz.cuni.mff.danekji.calendar.core.models.Event;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

/**
 * Controller for the EventDialog.fxml.
 */
public class EventDialogController extends CalendarController {

    @FXML private TextField titleField;
    @FXML private DatePicker datePicker;
    @FXML private TextField timeField;
    @FXML private TextField locationField;
    @FXML private TextField descriptionField;

    /**
     * Sets up the dialog with the provided event data and validation.
     * If the event is null, it initializes the dialog for creating a new event.
     * Else it initializes the dialog for editing an existing event.
     *
     * @param dialog The dialog to set up.
     * @param event The event to edit, or null if creating a new event.
     */
    public void setDialog(Dialog<Event> dialog, Event event) {
        if (event != null) { // Editing an existing event
            titleField.setText(event.getTitle());
            datePicker.setValue(event.getDate());
            timeField.setText(event.getTime().toString());
            locationField.setText(event.getLocation());
            descriptionField.setText(event.getDescription());
        } else {
            datePicker.setValue(LocalDate.now()); // Default day (today) for new events
        }

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        final Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, actionEvent -> {
            if (!validateFields()) {
                actionEvent.consume(); // Prevent dialog from closing if validation fails
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                // By the time this is called, fields have been validated.
                return getEventFromFields();
            }
            return null;
        });
    }

    /**
     * Validates the input fields and shows an alert if there are errors.
     *
     * @return true if all fields are valid, false otherwise.
     */
    private boolean validateFields() {
        StringBuilder errors = new StringBuilder();

        if (titleField.getText().isBlank()) {
            errors.append("Title cannot be empty.\n");
        }
        if (datePicker.getValue() == null) {
            errors.append("A date must be selected.\n");
        }
        if (timeField.getText().isBlank()) {
            errors.append("Time cannot be empty.\n");
        } else {
            try {
                LocalTime.parse(timeField.getText());
            } catch (DateTimeParseException e) {
                errors.append("Invalid time format. Please use HH:MM (e.g., 08:30).\n");
            }
        }

        if (!errors.isEmpty()) {
            showAlert("Invalid Event Data", errors.toString());
            return false;
        }
        return true;
    }

    /**
     * Constructs an Event object from the input fields.
     *
     * @return a new Event object with the data from the fields.
     */
    private Event getEventFromFields() {
        String title = titleField.getText();
        LocalDate date = datePicker.getValue();
        LocalTime time = LocalTime.parse(timeField.getText());
        String location = locationField.getText();
        String description = descriptionField.getText();

        return new Event(title, date, time, location, description);
    }
}