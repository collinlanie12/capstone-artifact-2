package com.example.comp360_final_project;

/**
 * Represents an Event with a name, description, date, time, and a unique ID.
 */
public class Event {

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

    /**
     * Constructor to initialize an event without an ID.
     * @param name        the name of the event
     * @param description a brief description of the event
     * @param year        the year of the event
     * @param month       the month of the event
     * @param day         the day of the event
     * @param hour        the hour of the event
     * @param minute      the minute of the event
     */
    public Event(String name, String description, int year, int month, int day, int hour, int minute) {
        this.name = name;
        this.description = description;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
    }

    // Getters

    /**
     * Get the unique ID of the event.
     *
     * @return event ID
     */
    public int getId() {
        return id;
    }

    /**
     * Get the name of the event.
     *
     * @return event name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the description of the event.
     *
     * @return event description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the year of the event.
     *
     * @return event year
     */
    public int getYear() {
        return year;
    }

    /**
     * Get the month of the event.
     *
     * @return event month
     */
    public int getMonth() {
        return month;
    }

    /**
     * Get the day of the event.
     *
     * @return event day
     */
    public int getDay() {
        return day;
    }

    /**
     * Get the hour of the event.
     *
     * @return event hour
     */
    public int getHour() {
        return hour;
    }

    /**
     * Get the minute of the event.
     *
     * @return event minute
     */
    public int getMinute() {
        return minute;
    }

    // Setters

    /**
     * Set the unique ID of the event.
     *
     * @param id the new ID of the event
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Set the name of the event.
     *
     * @param name the new name of the event
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the description of the event.
     *
     * @param description the new description of the event
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Set the year of the event.
     *
     * @param year the new year of the event
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * Set the month of the event.
     *
     * @param month the new month of the event
     */
    public void setMonth(int month) {
        this.month = month;
    }

    /**
     * Set the day of the event.
     *
     * @param day the new day of the event
     */
    public void setDay(int day) {
        this.day = day;
    }

    /**
     * Set the hour of the event.
     *
     * @param hour the new hour of the event
     */
    public void setHour(int hour) {
        this.hour = hour;
    }

    /**
     * Set the minute of the event.
     *
     * @param minute the new minute of the event
     */
    public void setMinute(int minute) {
        this.minute = minute;
    }
}