package com.example.aethoneventsapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aethoneventsapp.databinding.ActivityMainBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class DisplayActivity extends AppCompatActivity {


    Button scan_btn;

    TextView textView;
    private ActivityMainBinding binding;
    private ImageView eventImage;
    private TextView eventTitle;
    private TextView eventLocation;
    private TextView eventDate;
    private TextView eventDescription;
    private Button joinWaitlistButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_display);
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        eventImage = findViewById(R.id.event_image);
        eventTitle = findViewById(R.id.event_title);
        eventLocation = findViewById(R.id.event_location);
        eventDate = findViewById(R.id.event_date);
        eventDescription = findViewById(R.id.event_description);
        // Retrieve QR code content from Intent
        String qrCodeContent = getIntent().getStringExtra("qrCodeContent");
        updateEventDescription(qrCodeContent); // Pass the qrCodeContent instead of hard-coded ID


        // Set unique ID for the event document
//        String eventUniqueID = "SOjvCZ4onjAfCYO4poo4";

        // Call function to update event description and other details
//        updateEventDescription(qrCodeContent);
        joinWaitlistButton = findViewById(R.id.join_waitlist_button);
        // Todo add functionality to join waitlist
//        joinWaitlistButton.setOnClickListener(v -> {
//
//        });

    }

    /**
     * Fetches event data from Firestore based on the unique event ID and updates the UI.
     *
     * @param eventUniqueID The unique ID of the event document in Firestore.
     */
    private void updateEventDescription(String eventUniqueID) {
        // Access the 'events' collection and get the document with the specified event ID
        db.collection("Events")
                .document(eventUniqueID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Retrieve data from the document
                        String title = documentSnapshot.getString("name");
                        String location = documentSnapshot.getString("location");
//                        String date = documentSnapshot.getString("eventDate");  // Ensure date exists in Firestore
                        String description = documentSnapshot.getString("description");
                        String imageUrl = documentSnapshot.getString("imageUrl");

                        // Update UI components
                        eventTitle.setText(title);
                        eventLocation.setText(location);
//                        eventDate.setText(date);
                        eventDescription.setText(description);

                        // Load image using Picasso if imageUrl exists
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Picasso.get().load(imageUrl).into(eventImage);
                        }
                    } else {
                        // Handle case where event document doesn't exist
                        eventDescription.setText("Event not found.");
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    eventDescription.setText("Failed to load event details.");
                });
    }


}
