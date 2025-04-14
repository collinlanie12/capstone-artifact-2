package com.example.trackit_enhanced_artifact;

/* EventBuilder.java
 *
 * EventBuilder helps create Event objects using the Builder design pattern.
 *
 * Author: Collin Lanier
 * Date: 2025-03-26
 */

public class EventBuilder {
    private int id = -1; // Default: no ID set
    private String name;
    private String description;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;

    public EventBuilder setId(int id) {
        this.id = id;
        return this;
    }

    public EventBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public EventBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public EventBuilder setYear(int year) {
        this.year = year;
        return this;
    }

    public EventBuilder setMonth(int month) {
        this.month = month;
        return this;
    }

    public EventBuilder setDay(int day) {
        this.day = day;
        return this;
    }

    public EventBuilder setHour(int hour) {
        this.hour = hour;
        return this;
    }

    public EventBuilder setMinute(int minute) {
        this.minute = minute;
        return this;
    }

    /**
     * Builds and returns the final Event object. Validates required fields.
     *
     * @return the constructed Event object
     * @throws IllegalStateException if required fields are missing
     */
    public Event build() {
        if (name == null || description == null || year == 0 || month == 0 || day == 0) {
            throw new IllegalStateException("Missing required fields for Event creation.");
        }

        if (id >= 0) {
            return new Event(id, name, description, year, month, day, hour, minute);
        } else {
            return new Event(name, description, year, month, day, hour, minute);
        }
    }

    /**
     * Helper method to create an Event from raw input.
     */
    public static Event fromInput(String name, String description, String date, String time) {
        String[] dateParts = date.split("-");
        String[] timeParts = time.split(":");

        return new EventBuilder()
                .setName(name)
                .setDescription(description)
                .setYear(Integer.parseInt(dateParts[0]))
                .setMonth(Integer.parseInt(dateParts[1]))
                .setDay(Integer.parseInt(dateParts[2]))
                .setHour(Integer.parseInt(timeParts[0]))
                .setMinute(Integer.parseInt(timeParts[1]))
                .build();
    }

    /**
     * Helper method to update an existing Event object with new input.
     */
    public static void updateEventFromInput(Event event, String name, String description, String date, String time) {
        event.setName(name);
        event.setDescription(description);

        String[] dateParts = date.split("-");
        String[] timeParts = time.split(":");

        event.setYear(Integer.parseInt(dateParts[0]));
        event.setMonth(Integer.parseInt(dateParts[1]));
        event.setDay(Integer.parseInt(dateParts[2]));
        event.setHour(Integer.parseInt(timeParts[0]));
        event.setMinute(Integer.parseInt(timeParts[1]));
    }
}
