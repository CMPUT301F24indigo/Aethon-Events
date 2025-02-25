package com.example.aethoneventsapp;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import android.Manifest;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Button;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends NavActivity {

    Button admin_btn;
    Button signup_btn;
    Button dash_btn;
    ImageView profile_img;
    String deviceId;
    private FirebaseFirestore db;
    boolean notificationPreference;
    private MainEventAdapter adapter;
    private List<Event> entrantEvents = new ArrayList<>();
    private ListView eventsListView;

    // Request notification permission from user using this
    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean o) {
            if (o) {
                Toast.makeText(MainActivity.this, "Post notification permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Post notification permission not granted", Toast.LENGTH_SHORT).show();
            }
        }
    });



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity", "onCreate called");
        getLayoutInflater().inflate(R.layout.activity_main, findViewById(R.id.container));
        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        db = FirebaseFirestore.getInstance();
        adapter = new MainEventAdapter(this, entrantEvents);
        eventsListView = findViewById(R.id.my_events);
        eventsListView.setAdapter(adapter);
        fetchUserEvents();
    }

    private void handleNotifications(){

        //NotificationController notificationController = new NotificationController(deviceId);

        //List<String> allMessages = notificationController.pullAndDeleteAllNotifications();
        NotificationManager notificationManager = getSystemService(NotificationManager.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = getString(R.string.app_name);
                String description = "Example Notification";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel("test", name, importance);
                channel.setDescription(description);
                notificationManager.createNotificationChannel(channel);
            }
        }

        db.collection("users")
                .whereEqualTo("deviceId", deviceId)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot != null && !querySnapshot.isEmpty()){
                        QueryDocumentSnapshot document = (QueryDocumentSnapshot) querySnapshot.getDocuments().get(0);
                        notificationPreference = Boolean.TRUE.equals(document.getBoolean("enableNotifications"));
                        Log.d("Firestore00", "Inside notifications");
                        if (notificationPreference) {
                            List<String> allEvents = new ArrayList<>();
                            AtomicInteger _id = new AtomicInteger();

                            db.collection("users")
                                    .whereEqualTo("deviceId", deviceId)
                                    .limit(1)
                                    .get()
                                    .addOnSuccessListener(querySnapshot1 -> {
                                        QueryDocumentSnapshot document2 = (QueryDocumentSnapshot) querySnapshot1.getDocuments().get(0);

                                        if (document2.contains("Events")) {
                                            List<String> _tmp = (List<String>) document2.get("Events");
                                            if (_tmp != null && !_tmp.isEmpty()) {
                                                allEvents.addAll(_tmp);

                                                for (String eventId : allEvents) {
                                                    // Fetch eventName from the event document
                                                    db.collection("Events")
                                                            .document(eventId)
                                                            .get()
                                                            .addOnSuccessListener(eventDocument -> {
                                                                if (eventDocument.exists()) {
                                                                    String eventName = eventDocument.getString("name");

                                                                    // Fetch notifications for the device
                                                                    db.collection("Events")
                                                                            .document(eventId)
                                                                            .collection("Notifications")
                                                                            .document(deviceId)
                                                                            .get()
                                                                            .addOnSuccessListener(notificationDocument -> {
                                                                                if (notificationDocument.exists()) {
                                                                                    List<String> _tmp_messages = (List<String>) notificationDocument.get("messages");

                                                                                    if (_tmp_messages != null && !_tmp_messages.isEmpty()) {
                                                                                        for (String message : _tmp_messages) {
                                                                                            char title_code = message.charAt(0);
                                                                                            String title = eventName + ": " + getTitle(title_code) + "\t"  ; // Include eventName in the title
                                                                                            String body = message.substring(2);

                                                                                            // Create a notification
                                                                                            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "test")
                                                                                                    .setSmallIcon(R.drawable.baseline_notifications_active_24)
                                                                                                    .setContentTitle(title)
                                                                                                    .setContentText(body)
                                                                                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                                                                                            int notId = _id.addAndGet(1);
                                                                                            notificationManager.notify(notId, builder.build());
                                                                                        }
                                                                                        _tmp_messages.clear();
                                                                                        notificationDocument.getReference().update("messages", _tmp_messages)
                                                                                                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Messages updated successfully"))
                                                                                                .addOnFailureListener(e -> Log.e("Firestore", "Failed to update messages", e));
                                                                                    }
                                                                                } else {
                                                                                    Log.d("Firestore", "No document found for the given deviceId.");
                                                                                }
                                                                            })
                                                                            .addOnFailureListener(e -> Log.e("Firestore", "Failed to fetch notifications", e));
                                                                } else {
                                                                    Log.d("Firestore", "Event document does not exist.");
                                                                }
                                                            })
                                                            .addOnFailureListener(e -> Log.e("Firestore", "Failed to fetch event document", e));
                                                }
                                            }
                                        }
                                    })
                                    .addOnFailureListener(e -> Log.e("Firestore", "Failed to fetch user profile", e));
                        }
                    }

                })
                .addOnFailureListener(e -> Log.e("Firestore", "Failed to fetch user profile", e));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // call for notifications function.
        handleNotifications();

    }


    public String getTitle(char firstChar){
        if (firstChar == 'W'){
            return "Waiting List";
        } else if (firstChar == 'A') {
            return "Accepted List";
        } else if (firstChar == 'D') {
            return "Declined";
        } else if (firstChar == 'P') {
            return "Pending Decision";
        }
        return " Aethon Events";
    }

    private void fetchUserEvents() {
        db.collection("users").whereEqualTo("deviceId", deviceId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            QueryDocumentSnapshot document = (QueryDocumentSnapshot) querySnapshot.getDocuments().get(0);

                            List<String> eventIds = (List<String>) document.get("Events");

                            if (eventIds != null && !eventIds.isEmpty()) {
                                fetchEvents(eventIds);
                            } else {
                                Toast.makeText(MainActivity.this, "No events found for this user.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "User not found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("MainActivity", "Error getting user document: ", task.getException());
                        Toast.makeText(MainActivity.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchEvents(List<String> eventIds) {
        List<Long> eventIdsAsLong = new ArrayList<>();
        for (String id : eventIds) {
            try {
                eventIdsAsLong.add(Long.parseLong(id));
            } catch (NumberFormatException e) {
                Log.e("MainActivity", "Invalid event ID format: " + id);
            }
        }

        db.collection("Events")
                .whereIn("eventId", eventIdsAsLong)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        entrantEvents.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Event event = new Event(
                                    document.getLong("eventId").intValue(),
                                    document.getString("name"),
                                    document.getString("location"),
                                    document.getString("organizerId"),
                                    document.getString("eventDate")
                            );
                            event.setImageUrl(document.getString("imageUrl"));
                            entrantEvents.add(event);
                        }

                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e("MainActivity", "Error fetching events: ", task.getException());
                        Toast.makeText(MainActivity.this, "Error fetching events", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}