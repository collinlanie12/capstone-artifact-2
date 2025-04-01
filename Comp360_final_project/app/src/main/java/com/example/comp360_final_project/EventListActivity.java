package com.example.comp360_final_project;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/**
 * EventListActivity handles the display and management of the event list screen.
 */
public class EventListActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_CODE = 100;

    // UI components
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;

    // Data source
    private ArrayList<Event> eventList;

    // EventManager for handling event operations
    private EventManager eventManager;

    // SMSNotifier for handling SMS notifications
    private SMSNotifier smsNotifier;

    // DialogManager for handling dialogs
    private DialogManager dialogManager;

    // The currently logged-in user ID
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        // Get the current logged-in user ID
        currentUserId = getIntent().getIntExtra("userId", -1);

        // Initialize the toolbar with logout functionality
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize event manager and SMS notifier
        eventManager = new EventManager(this);
        smsNotifier = new SMSNotifier(this);

        // Initialize dialog manager
        dialogManager = new DialogManager(this, eventManager, smsNotifier, currentUserId);

        // Check SMS permissions on start
        checkSMSPermission();

        // Initialize UI components and data
        eventList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up adapter with delete and edit callbacks
        eventAdapter = new EventAdapter(this, eventList,
                position -> {
                    // Delete event from both list and database
                    Event event = eventList.get(position);
                    if (eventManager.deleteEvent(event.getId())) {
                        eventList.remove(position);
                        eventAdapter.notifyItemRemoved(position);
                    }
                },
                (position, event) -> dialogManager.showEditEventDialog(event) // Use DialogManager to show edit dialog
        );

        recyclerView.setAdapter(eventAdapter);

        // Floating button to add events
        FloatingActionButton addEventButton = findViewById(R.id.addEventButton);
        addEventButton.setOnClickListener(v -> dialogManager.showAddEventDialog()); // Use DialogManager to show add dialog

        // Load events from database into the list for the current user
        loadEventsFromDatabase(currentUserId);
    }

    /**
     * Inflate the menu with the logout button.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event_list, menu);
        return true;
    }

    /**
     * Handle logout action when selected from the toolbar.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            handleLogout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Handle logout action: clear event list and redirect to login screen.
     */
    private void handleLogout() {
        // Clear event list and reset session data
        eventList.clear();
        eventAdapter.notifyDataSetChanged();

        // Redirect to the login activity
        Intent intent = new Intent(EventListActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear the back stack
        startActivity(intent);
        finish(); // Close current activity
    }

    /**
     * Load events for the current user from the database and populate the eventList.
     */
    private void loadEventsFromDatabase(int userId) {
        eventList.clear(); // Clear the current list
        ArrayList<Event> events = eventManager.getUserEvents(userId); // Get events from EventManager
        if (events != null) {
            eventList.addAll(events); // Add all events to the list
            eventAdapter.notifyDataSetChanged(); // Notify the adapter about the data changes
        }
    }

    /**
     * Add a new event to the RecyclerView.
     */
    public void addEventToRecyclerView(Event event) {
        eventList.add(event);
        eventAdapter.notifyItemInserted(eventList.size() - 1);
    }

    /**
     * Update an event in the RecyclerView.
     */
    public void updateEventInRecyclerView(Event event) {
        int position = eventList.indexOf(event);
        if (position >= 0) {
            eventList.set(position, event);
            eventAdapter.notifyItemChanged(position);
        }
    }

    /**
     * Check and request SMS permissions if not already granted.
     */
    private void checkSMSPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
        }
    }

    /**
     * Handle SMS permission result.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SMS permission granted.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "SMS permission denied. You won't receive notifications.", Toast.LENGTH_LONG).show();
            }
        }
    }
}