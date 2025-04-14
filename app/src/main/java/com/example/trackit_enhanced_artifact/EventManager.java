package com.example.trackit_enhanced_artifact;

/*
 * EventManager.java
 *
 * This class serves as a service layer that handles operations related to events,
 * including adding, updating, deleting, and retrieving user events from the database.
 * It interacts with the SQLDatabase helper class.
 *
 * Author: Collin Lanier
 * Date: 2025-03-27
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class EventManager {

    private static final String TAG = "EventManager";

    private final SQLDatabase databaseHelper;

    /**
     * Constructor for EventManager.
     *
     * @param context Application context
     */
    public EventManager(Context context) {
        databaseHelper = new SQLDatabase(context);
    }

    /**
     * Adds a new event to the database.
     *
     * @param name        The name of the event.
     * @param description The description of the event.
     * @param date        The date of the event in yyyy-MM-dd format.
     * @param time        The time of the event in HH:mm format.
     * @param userId      The ID of the user who created the event.
     * @return True if the event was successfully added, false if not.
     */
    public boolean addEvent(String name, String description, String date, String time, int userId) {
        boolean success = databaseHelper.insertEvent(name, description, date, time, userId);
        Log.d(TAG, "addEvent: " + (success ? "Success" : "Failed"));
        return success;
    }

    /**
     * Updates an existing event in the database.
     *
     * @param event The updated Event object.
     * @return True if the event was successfully updated, false if not.
     */
    @SuppressLint("DefaultLocale")
    public boolean updateEvent(Event event) {
        String formattedDate = String.format("%04d-%02d-%02d", event.getYear(), event.getMonth(), event.getDay());
        String formattedTime = String.format("%02d:%02d", event.getHour(), event.getMinute());

        boolean success = databaseHelper.updateEvent(
                event.getId(),
                event.getName(),
                event.getDescription(),
                formattedDate,
                formattedTime
        );

        Log.d(TAG, "updateEvent: " + (success ? "Success" : "Failed") + " for ID: " + event.getId());
        return success;
    }

    /**
     * Deletes an event from the database.
     *
     * @param eventId The ID of the event to delete.
     * @return True if the event was successfully deleted, false if not.
     */
    public boolean deleteEvent(int eventId) {
        boolean success = databaseHelper.deleteEvent(eventId);
        Log.d(TAG, "deleteEvent: " + (success ? "Deleted event ID: " + eventId : "Failed to delete event ID: " + eventId));
        return success;
    }

    /**
     * Retrieves all events associated with a given user.
     *
     * @param userId The user ID.
     * @return A list of Event objects for the user.
     */
    public List<Event> getUserEvents(int userId) {
        List<Event> eventList = new ArrayList<>();
        Cursor cursor = databaseHelper.getUserEvents(userId);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(SQLDatabase.COLUMN_EVENT_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(SQLDatabase.COLUMN_EVENT_NAME));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow(SQLDatabase.COLUMN_EVENT_DESCRIPTION));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow(SQLDatabase.COLUMN_EVENT_DATE));
                    String time = cursor.getString(cursor.getColumnIndexOrThrow(SQLDatabase.COLUMN_EVENT_TIME));

                    String[] dateParts = date.split("-");
                    String[] timeParts = time.split(":");

                    Event event = new Event(
                            id,
                            name,
                            description,
                            Integer.parseInt(dateParts[0]),
                            Integer.parseInt(dateParts[1]),
                            Integer.parseInt(dateParts[2]),
                            Integer.parseInt(timeParts[0]),
                            Integer.parseInt(timeParts[1])
                    );

                    eventList.add(event);
                } catch (Exception e) {
                    Log.e(TAG, "Error reading event data", e);
                }
            } while (cursor.moveToNext());

            cursor.close();
        }

        Log.d(TAG, "Loaded " + eventList.size() + " event(s) for user " + userId);
        return eventList;
    }
}