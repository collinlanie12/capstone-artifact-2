package com.example.trackit_enhanced_artifact;

/* EventAdapter.java
 *
 * Adapter for displaying a list of events in a RecyclerView.
 * Applies MVC pattern to bind event data to UI components.
 *
 * Author: Collin Lanier
 * Date: 2025-03-26
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    // Context and event list data source
    private final Context context;
    private final List<Event> eventList; // List for RecyclerView

    // Click listeners for delete and edit actions
    private final OnDeleteClickListener deleteClickListener;
    private final OnEditClickListener editClickListener;

    /**
     * Constructor for the EventAdapter.
     *
     * @param context             the context
     * @param eventList           list of events to display
     * @param deleteClickListener listener for delete button clicks
     * @param editClickListener   listener for edit button clicks
     */
    public EventAdapter(Context context, List<Event> eventList,
                        OnDeleteClickListener deleteClickListener,
                        OnEditClickListener editClickListener) {
        this.context = context;
        this.eventList = eventList;
        this.deleteClickListener = deleteClickListener;
        this.editClickListener = editClickListener;
    }

    /**
     * Creates and returns the ViewHolder for an event item.
     *
     * @param parent   the parent ViewGroup
     * @param viewType the view type
     * @return the EventViewHolder
     */
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    /**
     * Binds the event data to the ViewHolder.
     *
     * @param holder   the ViewHolder to bind data to
     * @param position the position of the event in the list
     */
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);

        // Set event details
        holder.eventName.setText(event.getName());
        holder.eventDescription.setText(event.getDescription());
        holder.eventDate.setText(formatDate(event));
        holder.eventTime.setText(formatTime(event));

        // Set click listeners for delete and edit actions
        holder.deleteButton.setOnClickListener(v -> deleteClickListener.onDeleteClick(position));
        holder.editButton.setOnClickListener(v -> editClickListener.onEditClick(position, event));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    /**
     * Formats the date string for display.
     */
    @SuppressLint("DefaultLocale")
    private String formatDate(Event event) {
        return String.format("%04d-%02d-%02d", event.getYear(), event.getMonth(), event.getDay());
    }

    /**
     * Formats the time string for display.
     */
    @SuppressLint("DefaultLocale")
    private String formatTime(Event event) {
        return String.format("%02d:%02d", event.getHour(), event.getMinute());
    }

    /**
     * ViewHolder for an event item.
     */
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventName, eventDate, eventTime, eventDescription;
        Button deleteButton, editButton;

        /**
         * Constructor for the ViewHolder.
         *
         * @param itemView the item view
         */
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.eventName);
            eventDate = itemView.findViewById(R.id.eventDate);
            eventTime = itemView.findViewById(R.id.eventTime);
            eventDescription = itemView.findViewById(R.id.eventDescription);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            editButton = itemView.findViewById(R.id.editButton);
        }
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public interface OnEditClickListener {
        void onEditClick(int position, Event event);
    }
}