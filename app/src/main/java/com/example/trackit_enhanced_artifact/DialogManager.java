package com.example.trackit_enhanced_artifact;

/* DialogManager.java
 *
 * This class manages the dialogs related to adding and editing events in the TrackIt mobile application.
 * It handles user input, performs validation, updates UI components, and interacts with EventManager and SMSNotifier.
 *
 * Author: Collin Lanier
 * Date: 2025-03-26
 */

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

public class DialogManager {

    private final Context context;
    private final EventManager eventManager;
    private final SMSNotifier smsNotifier;
    private final int currentUserId;

    /**
     * Constructor for DialogManager.
     *
     * @param context      Application context
     * @param eventManager EventManager for handling event operations
     * @param smsNotifier  SMSNotifier for handling SMS notifications
     * @param currentUserId The ID of the currently logged-in user
     */
    public DialogManager(Context context, EventManager eventManager, SMSNotifier smsNotifier, int currentUserId) {
        this.context = context;
        this.eventManager = eventManager;
        this.smsNotifier = smsNotifier;
        this.currentUserId = currentUserId;
    }

    /**
     * Displays a dialog for adding a new event after clicking the "+" button.
     */
    public void showAddEventDialog() {
        View dialogView = inflateDialogView();
        AlertDialog dialog = buildDialog(dialogView);
        dialog.show();

        EditText eventNameInput = dialogView.findViewById(R.id.editTextEventName);
        EditText eventDateInput = dialogView.findViewById(R.id.editTextEventDate);
        EditText eventTimeInput = dialogView.findViewById(R.id.editTextEventTime);
        EditText eventDescriptionInput = dialogView.findViewById(R.id.editTextEventDescription);
        Button addButton = dialogView.findViewById(R.id.buttonAddEvent);

        Calendar calendar = Calendar.getInstance();

        setupDatePicker(eventDateInput, calendar);
        setupTimePicker(eventTimeInput, calendar);

        addButton.setOnClickListener(v -> {
            String name = eventNameInput.getText().toString();
            String description = eventDescriptionInput.getText().toString();
            String date = eventDateInput.getText().toString();
            String time = eventTimeInput.getText().toString();

            if (validateInputs(name, date, time, description)) {
                Event newEvent = EventBuilder.fromInput(name, description, date, time);
                boolean isAdded = eventManager.addEvent(name, description, date, time, currentUserId);

                if (isAdded) {
                    if (context instanceof EventListActivity) {
                        ((EventListActivity) context).addEventToRecyclerView();
                    }
                    smsNotifier.sendSMSNotification("1234567890", smsNotifier.createEventNotificationMessage(newEvent));
                    dialog.dismiss();
                } else {
                    Toast.makeText(context, "Failed to add event. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Displays a dialog for editing an existing event.
     */
    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    public void showEditEventDialog(Event event) {
        View dialogView = inflateDialogView();
        AlertDialog dialog = buildDialog(dialogView);
        dialog.show();

        EditText eventNameInput = dialogView.findViewById(R.id.editTextEventName);
        EditText eventDateInput = dialogView.findViewById(R.id.editTextEventDate);
        EditText eventTimeInput = dialogView.findViewById(R.id.editTextEventTime);
        EditText eventDescriptionInput = dialogView.findViewById(R.id.editTextEventDescription);
        Button updateButton = dialogView.findViewById(R.id.buttonAddEvent);
        updateButton.setText("Update Event");

        eventNameInput.setText(event.getName());
        eventDateInput.setText(event.getYear() + "-" + event.getMonth() + "-" + event.getDay());
        eventTimeInput.setText(String.format("%02d:%02d", event.getHour(), event.getMinute()));
        eventDescriptionInput.setText(event.getDescription());

        setupDatePicker(eventDateInput, Calendar.getInstance());
        setupTimePicker(eventTimeInput, Calendar.getInstance());

        updateButton.setOnClickListener(v -> {
            String name = eventNameInput.getText().toString();
            String description = eventDescriptionInput.getText().toString();
            String date = eventDateInput.getText().toString();
            String time = eventTimeInput.getText().toString();

            if (validateInputs(name, date, time, description)) {
                EventBuilder.updateEventFromInput(event, name, description, date, time);
                eventManager.updateEvent(event);

                if (context instanceof EventListActivity) {
                    ((EventListActivity) context).updateEventInRecyclerView(event);
                }
                smsNotifier.sendSMSNotification("1234567890", "Updated Event: " + smsNotifier.createEventNotificationMessage(event));
                dialog.dismiss();
            }
        });
    }

    /**
     * Validate the inputs for event details.
     */
    private boolean validateInputs(String name, String date, String time, String description) {
        if (name.isEmpty()) {
            Toast.makeText(context, "Event Name is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (date.isEmpty()) {
            Toast.makeText(context, "Event Date is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (time.isEmpty()) {
            Toast.makeText(context, "Event Time is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (description.isEmpty()) {
            Toast.makeText(context, "Event Description is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void setupDatePicker(EditText input, Calendar calendar) {
        input.setOnClickListener(v -> {
            @SuppressLint("SetTextI18n") DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                    (view, year, month, dayOfMonth) -> input.setText(year + "-" + (month + 1) + "-" + dayOfMonth),
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        });
    }

    private void setupTimePicker(EditText input, Calendar calendar) {
        input.setOnClickListener(v -> {
            @SuppressLint("DefaultLocale") TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                    (view, hourOfDay, minute) -> input.setText(String.format("%02d:%02d", hourOfDay, minute)),
                    calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        });
    }

    @SuppressLint("InflateParams")
    private View inflateDialogView() {
        return LayoutInflater.from(context).inflate(R.layout.add_event_details, null);
    }

    private AlertDialog buildDialog(View dialogView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        return builder.create();
    }
}
