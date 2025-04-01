package com.example.comp360_final_project;

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

/**
 * Activity class for handling user login and signup.
 */
public class LoginActivity extends AppCompatActivity {

    // UI components
    private EditText usernameField;
    private EditText passwordField;
    private Button loginButton;
    private Button signupButton;

    // Database helper instance
    private SQLDatabase databaseHelper;

    private static final String TAG = "LoginActivity"; // Tag for logging

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
        loginButton = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.signupButton);

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

        if (validateInputs(username, password)) {
            // Check credentials in the database
            try {
                if (checkUserCredentials(username, password)) {
                    // Get the user ID from the database
                    int userId = getUserId(username);
                    if (userId != -1) {
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, EventListActivity.class);
                        intent.putExtra("userId", userId); // Pass the userId to the EventListActivity
                        startActivity(intent, ActivityOptions.makeCustomAnimation(
                                LoginActivity.this, R.anim.slide_in_right, R.anim.slide_out_left).toBundle());
                    } else {
                        Toast.makeText(this, "Error retrieving user ID", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error logging in", e);
                Toast.makeText(this, "An error occurred during login", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Retrieves the user ID based on the username.
     *
     * @param username the entered username
     * @return the user ID if found, -1 otherwise
     */
    private int getUserId(String username) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String query = "SELECT " + SQLDatabase.COLUMN_USER_ID + " FROM " + SQLDatabase.USER_TABLE + " WHERE " +
                SQLDatabase.COLUMN_USERNAME + " = ?";
        Cursor cursor = null;
        int userId = -1;

        try {
            cursor = db.rawQuery(query, new String[]{username});
            if (cursor != null && cursor.moveToFirst()) {
                userId = cursor.getInt(cursor.getColumnIndexOrThrow(SQLDatabase.COLUMN_USER_ID));
            }
            Log.d(TAG, "User ID retrieved: " + userId); // Log the retrieved user ID
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving user ID", e);
        } finally {
            if (cursor != null) {
                cursor.close(); // Close the cursor to free resources
            }
            db.close(); // Close the database connection
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
                Log.e(TAG, "Error signing up", e);
                Toast.makeText(this, "An error occurred during signup", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Validates the user inputs.
     * Checks if both fields are non-empty.
     *
     * @param username the entered username
     * @param password the entered password
     * @return true if inputs are valid, false otherwise
     */
    private boolean validateInputs(String username, String password) {
        if (username.isEmpty()) {
            usernameField.setError("Username is required");
            return false;
        }
        if (password.isEmpty()) {
            passwordField.setError("Password is required");
            return false;
        }
        return true;
    }

    /**
     * Checks if the user's credentials exist in the database.
     *
     * @param username the entered username
     * @param password the entered password
     * @return true if user credentials are found, false otherwise
     */
    private boolean checkUserCredentials(String username, String password) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String query = "SELECT * FROM " + SQLDatabase.USER_TABLE + " WHERE " +
                SQLDatabase.COLUMN_USERNAME + " = ? AND " +
                SQLDatabase.COLUMN_PASSWORD + " = ?";
        Cursor cursor = null;
        boolean userExists = false;

        try {
            cursor = db.rawQuery(query, new String[]{username, password});
            userExists = cursor.getCount() > 0; // Check if any record was found
            Log.d(TAG, "User exists: " + userExists); // Log the result
        } catch (Exception e) {
            Log.e(TAG, "Error checking user credentials", e);
        } finally {
            if (cursor != null) {
                cursor.close(); // Close the cursor to free resources
            }
            db.close(); // Close the database connection
        }

        return userExists;
    }
}