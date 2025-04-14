package com.example.trackit_enhanced_artifact;

/* Event.java
 *
 * This class represents an Event object, containing details such as
 * a unique ID, name, description, and scheduled date/time components.
 * It is used throughout the TrackIt mobile app for managing event data.
 *
 * Author: Collin Lanier
 * Date: 2025-03-26
 */

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

public class Event implements Comparable<Event> {

    // Unique ID for the event (used for database operations)
    private int id;

    // Event details
    private String name;
    private String description;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;

    /**
     * Constructor to initialize an event with a unique ID.
     *
     * @param id          the unique ID of the event
     * @param name        the name of the event
     * @param description a brief description of the event
     * @param year        the year of the event
     * @param month       the month of the event
     * @param day         the day of the event
     * @param hour        the hour of the event
     * @param minute      the minute of the event
     */
    public Event(int id, String name, String description, int year, int month, int day, int hour, int minute) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
    }

    // Constructor without ID
    public Event(String name, String description, int year, int month, int day, int hour, int minute) {
        this.name = name;
        this.description = description;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
    }

    /**
     * Compares the event to another to determine correct order (chronological).
     * This method is used for the PriorityQueue, which prioritizes date and time.
     *
     * @param other the object to be compared.
     * @return A negative number if the event happens before the other,
     *         zero if both of the events are at the same time,
     *         or a positive number if the event happens after the other
     */
    @Override
    public int compareTo(Event other) {
        if (this.getYear() != other.getYear()) return this.getYear() - other.getYear();
        if (this.getMonth() != other.getMonth()) return this.getMonth() - other.getMonth();
        if (this.getDay() != other.getDay()) return this.getDay() - other.getDay();
        if (this.getHour() != other.getHour()) return this.getHour() - other.getHour();
        return this.getMinute() - other.getMinute();
    }


    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Event name cannot be empty.");
        this.name = name;
    }

    public void setDescription(String description) {
        if (description == null) throw new IllegalArgumentException("Description cannot be null.");
        this.description = description;
    }

    public void setYear(int year) {
        if (year < 2000) throw new IllegalArgumentException("Year is invalid.");
        this.year = year;
    }

    public void setMonth(int month) {
        if (month < 1 || month > 12) throw new IllegalArgumentException("Month must be between 1 and 12.");
        this.month = month;
    }

    public void setDay(int day) {
        if (day < 1 || day > 31) throw new IllegalArgumentException("Day must be between 1 and 31.");
        this.day = day;
    }

    public void setHour(int hour) {
        if (hour < 0 || hour > 23) throw new IllegalArgumentException("Hour must be between 0 and 23.");
        this.hour = hour;
    }

    public void setMinute(int minute) {
        if (minute < 0 || minute > 59) throw new IllegalArgumentException("Minute must be between 0 and 59.");
        this.minute = minute;
    }

    @NonNull
    @SuppressLint("DefaultLocale")
    @Override
    /*
      Returns a string representation of the Event object.
      Used for logging and showing all event details in a readable format.

      @return a formatted string containing the event’s information
     */
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", date=" + year + "-" + month + "-" + day +
                ", time=" + String.format("%02d:%02d", hour, minute) +
                '}';
    }

    /**
     * Compares this Event with another object for equality.
     * Two events are considered equal if they have the same unique ID.
     *
     * @param obj the object to compare with
     * @return true if the other object is an Event with the same ID, false if not
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Event)) return false;
        Event other = (Event) obj;
        return this.id == other.id;
    }
}