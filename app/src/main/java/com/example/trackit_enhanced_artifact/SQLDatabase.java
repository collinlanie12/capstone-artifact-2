package com.example.trackit_enhanced_artifact;

/*
 * SQLDatabase.java
 *
 * Handles all database operations related to users and events within the TrackIt app.
 * This class SQLiteOpenHelper to to provide functionality for creating,
 * updating, and managing the database.
 *
 * Author: Collin Lanier
 * Date: 2025-03-27
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLDatabase extends SQLiteOpenHelper {

    private static final String TAG = "SQLDatabase";

    // Database configuration
    private static final String DATABASE_NAME = "eventsApp.db";
    private static final int DATABASE_VERSION = 1;

    // Event table and columns
    public static final String TABLE_EVENTS = "events";
    public static final String COLUMN_EVENT_ID = "id";
    public static final String COLUMN_EVENT_NAME = "name";
    public static final String COLUMN_EVENT_DESCRIPTION = "description";
    public static final String COLUMN_EVENT_DATE = "date";
    public static final String COLUMN_EVENT_TIME = "time";
    public static final String COLUMN_EVENT_USER_ID = "user_id";

    // User table and columns
    public static final String USER_TABLE = "users";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";

    // SQL for creating the events table
    private static final String CREATE_EVENTS_TABLE =
            "CREATE TABLE " + TABLE_EVENTS + " (" +
                    COLUMN_EVENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_EVENT_NAME + " TEXT NOT NULL, " +
                    COLUMN_EVENT_DESCRIPTION + " TEXT, " +
                    COLUMN_EVENT_DATE + " TEXT NOT NULL, " +
                    COLUMN_EVENT_TIME + " TEXT NOT NULL, " +
                    COLUMN_EVENT_USER_ID + " INTEGER, " +
                    "FOREIGN KEY(" + COLUMN_EVENT_USER_ID + ") REFERENCES " + USER_TABLE + "(" + COLUMN_USER_ID + "));";

    // SQL for creating the users table
    private static final String CREATE_USERS_TABLE =
            "CREATE TABLE " + USER_TABLE + " (" +
                    COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USERNAME + " TEXT NOT NULL UNIQUE, " +
                    COLUMN_PASSWORD + " TEXT NOT NULL);";

    public SQLDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Creates database tables when first initialized.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Creating events table");
        db.execSQL(CREATE_EVENTS_TABLE);
        Log.d(TAG, "Creating users table");
        db.execSQL(CREATE_USERS_TABLE);
    }

    /**
     * Handles database upgrades by dropping and recreating tables.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        onCreate(db);
    }

    /**
     * Inserts a new user into the database.
     */
    public boolean insertUser(String username, String password) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_USERNAME, username);
            values.put(COLUMN_PASSWORD, password);
            long result = db.insert(USER_TABLE, null, values);
            Log.d(TAG, "User insert result: " + result);
            return result != -1;
        } catch (Exception e) {
            Log.e(TAG, "Error inserting user", e);
            return false;
        }
    }

    /**
     * Inserts a new event associated with a specific user.
     */
    public boolean insertEvent(String name, String description, String date, String time, int userId) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_EVENT_NAME, name);
            values.put(COLUMN_EVENT_DESCRIPTION, description);
            values.put(COLUMN_EVENT_DATE, date);
            values.put(COLUMN_EVENT_TIME, time);
            values.put(COLUMN_EVENT_USER_ID, userId);
            long result = db.insert(TABLE_EVENTS, null, values);
            Log.d(TAG, "Event insert result: " + result);
            return result != -1;
        } catch (Exception e) {
            Log.e(TAG, "Error inserting event", e);
            return false;
        }
    }

    /**
     * Updates an existing event by ID.
     */
    public boolean updateEvent(int id, String name, String description, String date, String time) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_EVENT_NAME, name);
            values.put(COLUMN_EVENT_DESCRIPTION, description);
            values.put(COLUMN_EVENT_DATE, date);
            values.put(COLUMN_EVENT_TIME, time);
            int result = db.update(TABLE_EVENTS, values, COLUMN_EVENT_ID + " = ?", new String[]{String.valueOf(id)});
            Log.d(TAG, "Event update result: " + result);
            return result > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error updating event", e);
            return false;
        }
    }

    /**
     * Deletes an event from the database by ID.
     */
    public boolean deleteEvent(int id) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            int result = db.delete(TABLE_EVENTS, COLUMN_EVENT_ID + " = ?", new String[]{String.valueOf(id)});
            Log.d(TAG, "Event delete result: " + result);
            return result > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error deleting event", e);
            return false;
        }
    }

    /**
     * Retrieves all events associated with a given user.
     */
    public Cursor getUserEvents(int userId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor;
        try {
            cursor = db.rawQuery(
                    "SELECT * FROM " + TABLE_EVENTS + " WHERE " + COLUMN_EVENT_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)}
            );
            Log.d(TAG, "Retrieved events for user ID: " + userId);
            return cursor;
        } catch (Exception e) {
            Log.e(TAG, "Error getting user events", e);
            return null;
        }
    }
}