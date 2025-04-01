package com.example.trackit_enhanced_artifact;

/*
 * EventListActivity.java
 *
 * This activity handles the event list view in the TrackIt mobile application. It manages
 * displaying user events, handles add/edit/delete, interacts with the database
 * by EventManager, and integrates with DialogManager and SMSNotifier for notifications.
 *
 * Author: Collin Lanier
 * Date: 2025-03-26
 */

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
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
import java.util.List;
import java.util.PriorityQueue;

public class EventListActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_CODE = 100;

    private EventAdapter eventAdapter;

    // Data source
    private List<Event> eventList;

    private PriorityQueue<Event> upcomingEventQueue;

    private TextView upcomingEventBanner;

    // EventManager for database operations
    private EventManager eventManager;

    private DialogManager dialogManager;

    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        currentUserId = getIntent().getIntExtra("userId", -1);

        setupToolbar();
        initializeDependencies();
        setupRecyclerView();
        setupAddEventButton();
        setupUpcomingEventBanner();
        checkSMSPermission();
        loadEventsFromDatabase(currentUserId);
    }

    /**
     * Set up the app bar toolbar for this activity.
     */
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    /**
     * Initialize required components like EventManager, SMSNotifier, and DialogManager.
     */
    private void initializeDependencies() {
        eventManager = new EventManager(this);
        // Utilities
        SMSNotifier smsNotifier = new SMSNotifier(this);
        dialogManager = new DialogManager(this, eventManager, smsNotifier, currentUserId);

        upcomingEventQueue = new PriorityQueue<>();
    }

    /**
     * Set up the RecyclerView and its adapter for displaying event items.
     */
    private void setupRecyclerView() {
        // UI components
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventList = new ArrayList<>();

        eventAdapter = new EventAdapter(this, eventList,
                this::handleDeleteEvent,
                (position, event) -> dialogManager.showEditEventDialog(event));

        recyclerView.setAdapter(eventAdapter);
    }

    /**
     * Set up the floating action button to trigger the Add Event dialog.
     */
    private void setupAddEventButton() {
        FloatingActionButton addEventButton = findViewById(R.id.addEventButton);
        addEventButton.setOnClickListener(v -> dialogManager.showAddEventDialog());
    }

    /**
     * Banner at the top to display the closest upcoming event
     */
    private void setupUpcomingEventBanner() {
        upcomingEventBanner = findViewById(R.id.upcomingEventBanner);
    }

    /**
     * Handle event deletion by removing it from the list and database.
     */
    private void handleDeleteEvent(int position) {
        Event event = eventList.get(position);
        if (eventManager.deleteEvent(event.getId())) {
            eventList.remove(position);
            upcomingEventQueue.remove(event);
            eventAdapter.notifyItemRemoved(position);
            updateUpcomingBanner();
        }
    }

    /**
     * Load events from the database, sort them using merge sort, and update the RecyclerView.
     */
    @SuppressLint("NotifyDataSetChanged")
    private void loadEventsFromDatabase(int userId) {
        eventList.clear();
        upcomingEventQueue.clear();

        List<Event> events = eventManager.getUserEvents(userId);
       if (events != null) {
           List<Event> sortedEvents = mergeSort(events);
           eventList.addAll(sortedEvents);
           upcomingEventQueue.addAll(sortedEvents);
           eventAdapter.notifyDataSetChanged();
           updateUpcomingBanner();
       }
    }

    /**
     * Add a new event, re-sort the list using merge sort, and update the RecyclerView.
     */
    @SuppressLint("NotifyDataSetChanged")
    public void addEventToRecyclerView() {
        loadEventsFromDatabase(currentUserId);
    }

    /**
     * Update an event, re-sort the list using merge sort, and refresh the RecyclerView.
     */
    @SuppressLint("NotifyDataSetChanged")
    public void updateEventInRecyclerView(Event event) {
        for (int i = 0; i < eventList.size(); i++) {
            if (eventList.get(i).getId() == event.getId()) {
                eventList.set(i, event);
                break;
            }
        }
        upcomingEventQueue.clear();
        upcomingEventQueue.addAll(eventList);
        List<Event> sortedEvents = mergeSort(eventList);
        eventList.clear();
        eventList.addAll(sortedEvents);
        eventAdapter.notifyDataSetChanged();
        loadEventsFromDatabase(currentUserId);
        updateUpcomingBanner();
    }

    /**
     * Change the text of the upcoming banner at the top of the RecyclerView
     */
    @SuppressLint("DefaultLocale")
    private void updateUpcomingBanner() {
        Event next = upcomingEventQueue.peek();
        if (next != null && upcomingEventBanner != null) {
            String text = "Upcoming Event: " + next.getName() + " at "
                    + String.format("%04d-%02d-%02d", next.getYear(), next.getMonth(), next.getDay())
                    + " " + String.format("%02d:%02d", next.getHour(), next.getMinute());
            upcomingEventBanner.setText(text);
        }
    }

    /**
     * Request SMS permission if not already granted.
     */
    private void checkSMSPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
        }
    }

    /**
     * Handle the result of the SMS permission request.
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            handleLogout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Handle logout action and navigate to the login screen.
     */
    @SuppressLint("NotifyDataSetChanged")
    private void handleLogout() {
        eventList.clear();
        upcomingEventQueue.clear();
        eventAdapter.notifyDataSetChanged();
        upcomingEventBanner.setText("");
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Merge Sort method to sort events chronologically by year, month, day, hour, and minute.
     * Merge Sort has a time complexity of O(n log n).
     *
     * @param events the unsorted list of events
     * @return a new sorted list of events
     */
    private List<Event> mergeSort(List<Event> events) {
        if (events.size() <= 1) return events;

        int mid = events.size() / 2;
        List<Event> left = mergeSort(new ArrayList<>(events.subList(0, mid)));
        List<Event> right = mergeSort(new ArrayList<>(events.subList(mid, events.size())));

        return merge(left, right);
    }

    /**
     * Merge two sorted event lists into a single sorted list.
     *
     * @param left  the left half
     * @param right the right half
     * @return merged sorted list of events
     */
    private List<Event> merge(List<Event> left, List<Event> right) {
        List<Event> result = new ArrayList<>();
        int i = 0, j = 0;

        while (i < left.size() && j < right.size()) {
            Event e1 = left.get(i);
            Event e2 = right.get(j);

            if (isBefore(e1, e2)) {
                result.add(e1);
                i++;
            } else {
                result.add(e2);
                j++;
            }
        }

        while (i < left.size()) {
            result.add(left.get(i++));
        }

        while (j < right.size()) {
            result.add(right.get(j++));
        }

        return result;
    }

    /**
     * Compare two events and determine if e1 comes before e2.
     *
     * @param e1 the first event
     * @param e2 the second event
     * @return true if e1 is earlier than e2, false otherwise
     */
    private boolean isBefore(Event e1, Event e2) {
        if (e1.getYear() != e2.getYear()) return e1.getYear() < e2.getYear();
        if (e1.getMonth() != e2.getMonth()) return e1.getMonth() < e2.getMonth();
        if (e1.getDay() != e2.getDay()) return e1.getDay() < e2.getDay();
        if (e1.getHour() != e2.getHour()) return e1.getHour() < e2.getHour();
        return e1.getMinute() < e2.getMinute();
    }
}