package com.example.trackit_enhanced_artifact;

/*
 * LoginActivity.java
 *
 * This activity handles user authentication for the TrackIt application.
 * It provides login and signup functionality using a SQLite database.
 *
 * Author: Collin Lanier
 * Date: 2025-03-27
 */

import android.app.ActivityOptions;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    // UI components
    private EditText usernameField;
    private EditText passwordField;

    // Database helper instance
    private SQLDatabase databaseHelper;

    private static final String TAG = "LoginActivity"; // Tag for logging

    private static final String ERROR_USERNAME_REQUIRED = "Username is required";
    private static final String ERROR_PASSWORD_REQUIRED = "Password is required";
    private static final String ERROR_LOGIN_FAILED = "An error occurred during login";
    private static final String ERROR_SIGNUP_FAILED = "An error occurred during signup";

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState state information to restore the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI elements
        usernameField = findViewById(R.id.usernameField);
        passwordField = findViewById(R.id.passwordField);
        Button loginButton = findViewById(R.id.loginButton);
        Button signupButton = findViewById(R.id.signupButton);

        // Initialize database helper
        databaseHelper = new SQLDatabase(this);

        // Set up login button click event
        loginButton.setOnClickListener(v -> handleLogin());

        // Set up signup button click event
        signupButton.setOnClickListener(v -> handleSignup());
    }

    /**
     * Handles the login action.
     * Checks the username and password against the database.
     */
    private void handleLogin() {
        String username = usernameField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        Log.d(TAG, "Trying login for user: " + username);

        if (validateInputs(username, password)) {
            try {
                if (checkUserCredentials(username, password)) {
                    int userId = getUserId(username);
                    if (userId != -1) {
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, EventListActivity.class);
                        intent.putExtra("userId", userId);
                        startActivity(intent, ActivityOptions.makeCustomAnimation(
                                LoginActivity.this, R.anim.slide_in_right, R.anim.slide_out_left).toBundle());
                    } else {
                        Toast.makeText(this, "Error getting user ID", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e(TAG, ERROR_LOGIN_FAILED, e);
                Toast.makeText(this, ERROR_LOGIN_FAILED, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Retrieves the user ID based on the username.
     *
     * @param username the entered username
     * @return the user ID if found
     */
    private int getUserId(String username) {
        int userId = -1;
        String query = "SELECT " + SQLDatabase.COLUMN_USER_ID + " FROM " + SQLDatabase.USER_TABLE +
                " WHERE " + SQLDatabase.COLUMN_USERNAME + " = ?";

        try (SQLiteDatabase db = databaseHelper.getReadableDatabase();
             Cursor cursor = db.rawQuery(query, new String[]{username})) {
            if (cursor.moveToFirst()) {
                userId = cursor.getInt(cursor.getColumnIndexOrThrow(SQLDatabase.COLUMN_USER_ID));
            }
            Log.d(TAG, "User ID obtained " + userId);
        } catch (Exception e) {
            Log.e(TAG, "Error getting user ID", e);
        }

        return userId;
    }


    /**
     * Handles the signup action.
     * Adds a new user to the database.
     */
    private void handleSignup() {
        String username = usernameField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (validateInputs(username, password)) {
            // Insert new user into the database
            try {
                if (databaseHelper.insertUser(username, password)) {
                    Toast.makeText(this, "Signup successful. You can now log in.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Signup failed. Username may already exist.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e(TAG, ERROR_SIGNUP_FAILED, e);
                Toast.makeText(this, ERROR_SIGNUP_FAILED, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Validates the user inputs.
     * Checks if both fields are non-empty.
     *
     * @param username the entered username
     * @param password the entered password
     * @return true if inputs are valid, false if not
     */
    private boolean validateInputs(String username, String password) {
        if (username.isEmpty()) {
            usernameField.setError(ERROR_USERNAME_REQUIRED);
            return false;
        }
        if (password.isEmpty()) {
            passwordField.setError(ERROR_PASSWORD_REQUIRED);
            return false;
        }
        return true;
    }

    /**
     * Checks if the user's credentials exist in the database.
     *
     * @param username the entered username
     * @param password the entered password
     * @return true if user credentials are found, if not
     */
    private boolean checkUserCredentials(String username, String password) {
        boolean userExists = false;
        String query = "SELECT * FROM " + SQLDatabase.USER_TABLE + " WHERE " +
                SQLDatabase.COLUMN_USERNAME + " = ? AND " +
                SQLDatabase.COLUMN_PASSWORD + " = ?";

        try (SQLiteDatabase db = databaseHelper.getReadableDatabase();
             Cursor cursor = db.rawQuery(query, new String[]{username, password})) {
            userExists = cursor.getCount() > 0;
            Log.d(TAG, "User exists: " + userExists);
        } catch (Exception e) {
            Log.e(TAG, "Error checking user credentials", e);
        }

        return userExists;
    }
}