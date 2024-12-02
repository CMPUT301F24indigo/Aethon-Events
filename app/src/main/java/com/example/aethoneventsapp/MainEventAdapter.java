package com.example.aethoneventsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class MainEventAdapter extends ArrayAdapter<Event> {
    private Context context;
    private List<Event> events;

    // Constructor to initialize context and list of events
    public MainEventAdapter(Context context, List<Event> events) {
        super(context, R.layout.event_item, events);  // event_item layout is used for each item
        this.context = context;
        this.events = events;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Reuse the existing view if it's available, otherwise inflate a new view
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.event_item, parent, false);
        }

        // Get the current event from the list
        Event event = events.get(position);

        // Get references to the views in the layout (event_item.xml)
        TextView eventName = convertView.findViewById(R.id.event_name);
        TextView eventDate = convertView.findViewById(R.id.event_date);
        TextView eventLocation = convertView.findViewById(R.id.event_location);
        ImageView eventImage = convertView.findViewById(R.id.event_image);

        // Set the event details to the respective views
        eventName.setText(event.getName());
        eventDate.setText(event.getEventDate());
        eventLocation.setText(event.getLocation());

        // Return the view to display
        return convertView;
    }
}
