package com.example.comp360_final_project;

import android.content.Context;
import android.database.Cursor;
import java.util.ArrayList;

/**
 * EventManager class to handle all event-related operations, such as adding,
 * updating, deleting, and retrieving events from the database.
 */
public class EventManager {

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
     * @param date        The date of the event.
     * @param time        The time of the event.
     * @param userId      The ID of the user who created the event.
     * @return True if the event was successfully added, false otherwise.
     */
    public boolean addEvent(String name, String description, String date, String time, int userId) {
        return databaseHelper.insertEvent(name, description, date, time, userId);
    }

    /**
     * Updates an existing event in the database.
     *
     * @param event The event object containing the updated event details.
     * @return True if the event was successfully updated, false otherwise.
     */
    public boolean updateEvent(Event event) {
        return databaseHelper.updateEvent(
                event.getId(),
                event.getName(),
                event.getDescription(),
                event.getYear() + "-" + event.getMonth() + "-" + event.getDay(),
                String.format("%02d:%02d", event.getHour(), event.getMinute())
        );
    }

    /**
     * Deletes an event from the database.
     *
     * @param eventId The ID of the event to delete.
     * @return True if the event was successfully deleted, false otherwise.
     */
    public boolean deleteEvent(int eventId) {
        return databaseHelper.deleteEvent(eventId);
    }

    /**
     * Retrieves a list of events for a specific user.
     *
     * @param userId The ID of the user whose events to retrieve.
     * @return A list of events for the user.
     */
    public ArrayList<Event> getUserEvents(int userId) {
        ArrayList<Event> eventList = new ArrayList<>();
        Cursor cursor = databaseHelper.getUserEvents(userId);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(SQLDatabase.COLUMN_EVENT_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(SQLDatabase.COLUMN_EVENT_NAME));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(SQLDatabase.COLUMN_EVENT_DESCRIPTION));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(SQLDatabase.COLUMN_EVENT_DATE));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(SQLDatabase.COLUMN_EVENT_TIME));

                // Parse date and time
                String[] dateParts = date.split("-");
                String[] timeParts = time.split(":");

                Event event = new Event(
                        id, name, description,
                        Integer.parseInt(dateParts[0]), Integer.parseInt(dateParts[1]), Integer.parseInt(dateParts[2]),
                        Integer.parseInt(timeParts[0]), Integer.parseInt(timeParts[1])
                );
                eventList.add(event);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return eventList;
    }
}