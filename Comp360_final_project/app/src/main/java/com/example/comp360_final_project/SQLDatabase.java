package com.example.comp360_final_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * SQLDatabase class to handle database operations for managing events and users.
 * This class extends SQLiteOpenHelper to provide functionality for creating,
 * updating, and managing the database.
 */
public class SQLDatabase extends SQLiteOpenHelper {

    // Database information
    private static final String DATABASE_NAME = "eventsApp.db";  // Name of the database
    private static final int DATABASE_VERSION = 1;               // Database version

    // Table and columns for storing event data
    public static final String TABLE_EVENTS = "events";           // Table name
    public static final String COLUMN_EVENT_ID = "id";            // Event ID (primary key)
    public static final String COLUMN_EVENT_NAME = "name";        // Event name
    public static final String COLUMN_EVENT_DESCRIPTION = "description";  // Event description
    public static final String COLUMN_EVENT_DATE = "date";        // Event date (in YYYY-MM-DD format)
    public static final String COLUMN_EVENT_TIME = "time";        // Event time (in HH:MM format)
    public static final String COLUMN_EVENT_USER_ID = "user_id";  // User ID to link events to users

    // Table and columns for storing user data
    public static final String USER_TABLE = "users";              // User table name
    public static final String COLUMN_USER_ID = "id";             // User ID (primary key)
    public static final String COLUMN_USERNAME = "username";      // Username
    public static final String COLUMN_PASSWORD = "password";      // Password

    // SQL query to create the events table
    private static final String CREATE_EVENTS_TABLE =
            "CREATE TABLE " + TABLE_EVENTS + " (" +
                    COLUMN_EVENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_EVENT_NAME + " TEXT NOT NULL, " +
                    COLUMN_EVENT_DESCRIPTION + " TEXT, " +
                    COLUMN_EVENT_DATE + " TEXT NOT NULL, " +
                    COLUMN_EVENT_TIME + " TEXT NOT NULL, " +
                    COLUMN_EVENT_USER_ID + " INTEGER, " +  // User ID column to associate event with a user
                    "FOREIGN KEY(" + COLUMN_EVENT_USER_ID + ") REFERENCES " + USER_TABLE + "(" + COLUMN_USER_ID + "));";

    // SQL query to create the users table
    private static final String CREATE_USERS_TABLE =
            "CREATE TABLE " + USER_TABLE + " (" +
                    COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USERNAME + " TEXT NOT NULL UNIQUE, " +
                    COLUMN_PASSWORD + " TEXT NOT NULL);";

    /**
     * Constructor for SQLDatabase.
     *
     * @param context The context in which the database is created and managed.
     */
    public SQLDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time.
     * Executes the SQL queries to create the necessary tables.
     *
     * @param db The database to create.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("SQLDatabase", "Creating events table");
        db.execSQL(CREATE_EVENTS_TABLE);  // Create events table with user_id

        Log.d("SQLDatabase", "Creating users table");
        db.execSQL(CREATE_USERS_TABLE);   // Create users table
    }

    /**
     * Called when the database needs to be upgraded.
     * Handles database schema changes.
     *
     * @param db         The database being upgraded.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the existing tables if they exist, and recreate them
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        onCreate(db);
    }

    /**
     * Inserts a new user into the users table.
     *
     * @param username The username for the new user.
     * @param password The password for the new user.
     * @return True if the user was inserted successfully, false otherwise.
     */
    public boolean insertUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USERNAME, username);
        contentValues.put(COLUMN_PASSWORD, password);

        // Insert user and check for success
        long result = db.insert(USER_TABLE, null, contentValues);
        db.close();  // Close the database after operation
        return result != -1;
    }

    /**
     * Inserts a new event into the events table, associating it with a specific user.
     *
     * @param name        The name of the event.
     * @param description The description of the event.
     * @param date        The date of the event (in YYYY-MM-DD format).
     * @param time        The time of the event (in HH:MM format).
     * @param userId      The ID of the user who created the event.
     * @return True if the event was inserted successfully, false otherwise.
     */
    public boolean insertEvent(String name, String description, String date, String time, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_EVENT_NAME, name);
        contentValues.put(COLUMN_EVENT_DESCRIPTION, description);
        contentValues.put(COLUMN_EVENT_DATE, date);
        contentValues.put(COLUMN_EVENT_TIME, time);
        contentValues.put(COLUMN_EVENT_USER_ID, userId);  // Associate the event with a user

        long result = db.insert(TABLE_EVENTS, null, contentValues);
        db.close();  // Close the database after operation
        return result != -1;
    }

    /**
     * Updates an existing event in the database.
     *
     * @param id          The ID of the event to update.
     * @param name        The new name of the event.
     * @param description The new description of the event.
     * @param date        The new date of the event (in YYYY-MM-DD format).
     * @param time        The new time of the event (in HH:MM format).
     * @return True if the event was updated successfully, false otherwise.
     */
    public boolean updateEvent(int id, String name, String description, String date, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_EVENT_NAME, name);
        contentValues.put(COLUMN_EVENT_DESCRIPTION, description);
        contentValues.put(COLUMN_EVENT_DATE, date);
        contentValues.put(COLUMN_EVENT_TIME, time);

        // Update the event and check for success
        int result = db.update(TABLE_EVENTS, contentValues, COLUMN_EVENT_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();  // Close the database after operation
        return result > 0;
    }

    /**
     * Deletes an event from the database.
     *
     * @param id The ID of the event to delete.
     * @return True if the event was deleted successfully, false otherwise.
     */
    public boolean deleteEvent(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_EVENTS, COLUMN_EVENT_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();  // Close the database after operation
        return result > 0;
    }

    /**
     * Retrieves all events for a specific user from the database.
     *
     * @param userId The ID of the user whose events are being fetched.
     * @return A Cursor pointing to the list of events related to the specific user.
     */
    public Cursor getUserEvents(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        // Query to fetch all events that belong to the specified user ID
        return db.rawQuery("SELECT * FROM " + TABLE_EVENTS + " WHERE " + COLUMN_EVENT_USER_ID + " = ?", new String[]{String.valueOf(userId)});
    }
}