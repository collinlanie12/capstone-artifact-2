package com.example.comp360_final_project;

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

/**
 * DialogManager class to handle displaying and managing event-related dialogs.
 */
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
     * Show the dialog to add a new event.
     */
    public void showAddEventDialog() {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.add_event_details, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();

        // References to input fields
        EditText eventNameInput = dialogView.findViewById(R.id.editTextEventName);
        EditText eventDateInput = dialogView.findViewById(R.id.editTextEventDate);
        EditText eventTimeInput = dialogView.findViewById(R.id.editTextEventTime);
        EditText eventDescriptionInput = dialogView.findViewById(R.id.editTextEventDescription);
        Button addButton = dialogView.findViewById(R.id.buttonAddEvent);

        Calendar calendar = Calendar.getInstance();

        // Handle Date input, preventing past dates
        eventDateInput.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                    (view, year, month, dayOfMonth) -> eventDateInput.setText(year + "-" + (month + 1) + "-" + dayOfMonth),
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000); // Prevent past dates
            datePickerDialog.show();
        });

        // Handle Time input
        eventTimeInput.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                    (view, hourOfDay, minute) -> eventTimeInput.setText(String.format("%02d:%02d", hourOfDay, minute)),
                    calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        });

        // Handle Add button click
        addButton.setOnClickListener(v -> {
            String name = eventNameInput.getText().toString();
            String description = eventDescriptionInput.getText().toString();
            String date = eventDateInput.getText().toString();
            String time = eventTimeInput.getText().toString();

            if (validateInputs(name, date, time, description)) {
                // Create the event object to add
                Event newEvent = new Event(name, description, getYearFromDate(date), getMonthFromDate(date), getDayFromDate(date),
                        getHourFromTime(time), getMinuteFromTime(time));

                // Add event to the database and check for success
                boolean isAdded = eventManager.addEvent(name, description, date, time, currentUserId);

                if (isAdded) {
                    // Notify UI to update the event list immediately
                    if (context instanceof EventListActivity) {
                        ((EventListActivity) context).addEventToRecyclerView(newEvent);
                    }

                    // Send SMS notification
                    smsNotifier.sendSMSNotification("1234567890", smsNotifier.createEventNotificationMessage(newEvent));

                    dialog.dismiss();
                } else {
                    Toast.makeText(context, "Failed to add event. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Show the dialog to edit an existing event.
     *
     * @param event The event to edit
     */
    public void showEditEventDialog(Event event) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.add_event_details, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();

        // References to input fields
        EditText eventNameInput = dialogView.findViewById(R.id.editTextEventName);
        EditText eventDateInput = dialogView.findViewById(R.id.editTextEventDate);
        EditText eventTimeInput = dialogView.findViewById(R.id.editTextEventTime);
        EditText eventDescriptionInput = dialogView.findViewById(R.id.editTextEventDescription);
        Button updateButton = dialogView.findViewById(R.id.buttonAddEvent);
        updateButton.setText("Update Event");

        // Pre-fill inputs with event data
        eventNameInput.setText(event.getName());
        eventDateInput.setText(event.getYear() + "-" + event.getMonth() + "-" + event.getDay());
        eventTimeInput.setText(String.format("%02d:%02d", event.getHour(), event.getMinute()));
        eventDescriptionInput.setText(event.getDescription());

        Calendar calendar = Calendar.getInstance();

        // Handle Date input
        eventDateInput.setOnClickListener(v -> {
            int year = event.getYear();
            int month = event.getMonth() - 1;
            int day = event.getDay();

            DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                    (view, selectedYear, selectedMonth, dayOfMonth) -> eventDateInput.setText(selectedYear + "-" + (selectedMonth + 1) + "-" + dayOfMonth),
                    year, month, day);
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000); // Prevent past dates
            datePickerDialog.show();
        });

        // Handle Time input
        eventTimeInput.setOnClickListener(v -> {
            int hour = event.getHour();
            int minute = event.getMinute();

            TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                    (view, selectedHour, selectedMinute) -> eventTimeInput.setText(String.format("%02d:%02d", selectedHour, selectedMinute)),
                    hour, minute, true);
            timePickerDialog.show();
        });

        // Handle Update button click
        updateButton.setOnClickListener(v -> {
            String name = eventNameInput.getText().toString();
            String description = eventDescriptionInput.getText().toString();
            String date = eventDateInput.getText().toString();
            String time = eventTimeInput.getText().toString();

            if (validateInputs(name, date, time, description)) {
                // Update event in the database
                event.setName(name);
                event.setDescription(description);
                String[] dateParts = date.split("-");
                String[] timeParts = time.split(":");
                event.setYear(Integer.parseInt(dateParts[0]));
                event.setMonth(Integer.parseInt(dateParts[1]));
                event.setDay(Integer.parseInt(dateParts[2]));
                event.setHour(Integer.parseInt(timeParts[0]));
                event.setMinute(Integer.parseInt(timeParts[1]));

                eventManager.updateEvent(event);

                // Notify UI to update the event list immediately
                if (context instanceof EventListActivity) {
                    ((EventListActivity) context).updateEventInRecyclerView(event);
                }

                // Send SMS notification for updated event
                smsNotifier.sendSMSNotification("1234567890", "Updated Event: " + smsNotifier.createEventNotificationMessage(event));

                dialog.dismiss();
            }
        });
    }

    /**
     * Validate the inputs for event details.
     *
     * @param name        The event name
     * @param date        The event date
     * @param time        The event time
     * @param description The event description
     * @return True if the inputs are valid, false otherwise
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

    // Helper methods to extract date and time components
    private int getYearFromDate(String date) {
        return Integer.parseInt(date.split("-")[0]);
    }

    private int getMonthFromDate(String date) {
        return Integer.parseInt(date.split("-")[1]);
    }

    private int getDayFromDate(String date) {
        return Integer.parseInt(date.split("-")[2]);
    }

    private int getHourFromTime(String time) {
        return Integer.parseInt(time.split(":")[0]);
    }

    private int getMinuteFromTime(String time) {
        return Integer.parseInt(time.split(":")[1]);
    }
}
