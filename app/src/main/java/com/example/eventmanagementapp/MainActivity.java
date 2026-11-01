
package com.example.eventmanagementapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.ArrayList;

public class MainActivity extends Activity {

    EditText eventName, eventDate, eventVenue;
    Button addEventButton;
    ListView eventList;

    ArrayList<String> events;
    ArrayAdapter<String> adapter;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Connect XML components with Java
        eventName = findViewById(R.id.eventName);
        eventDate = findViewById(R.id.eventDate);
        eventVenue = findViewById(R.id.eventVenue);

        addEventButton = findViewById(R.id.addEventButton);
        eventList = findViewById(R.id.eventList);

        // Initialize event list
        events = new ArrayList<>();

        // Initialize SharedPreferences
        preferences = getSharedPreferences(
                "EventData", MODE_PRIVATE);

        // Load previously saved events
        loadEvents();

        // Display events in ListView
        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                events
        );

        eventList.setAdapter(adapter);

        // Add Event Button
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = eventName.getText().toString().trim();
                String date = eventDate.getText().toString().trim();
                String venue = eventVenue.getText().toString().trim();

                // Check empty fields
                if (name.isEmpty() || date.isEmpty()
                        || venue.isEmpty()) {

                    Toast.makeText(
                            MainActivity.this,
                            "Please fill all the details",
                            Toast.LENGTH_SHORT
                    ).show();

                } else {

                    // Create event details
                    String event = "Event: " + name
                            + "\nDate: " + date
                            + "\nVenue: " + venue;

                    // Add event to ArrayList
                    events.add(event);

                    // Update ListView
                    adapter.notifyDataSetChanged();

                    // Save events
                    saveEvents();

                    // Clear input fields
                    eventName.setText("");
                    eventDate.setText("");
                    eventVenue.setText("");

                    Toast.makeText(
                            MainActivity.this,
                            "Event Added Successfully!",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });

        // Delete event using long click
        eventList.setOnItemLongClickListener(
                (parent, view, position, id) -> {

                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Delete Event")
                            .setMessage("Do you want to delete this event?")
                            .setPositiveButton("Yes", (dialog, which) -> {

                                events.remove(position);

                                adapter.notifyDataSetChanged();

                                saveEvents();

                                Toast.makeText(
                                        MainActivity.this,
                                        "Event Deleted",
                                        Toast.LENGTH_SHORT
                                ).show();
                            })
                            .setNegativeButton("No", null)
                            .show();

                    return true;
                }
        );
    }

    // Save events in SharedPreferences
    private void saveEvents() {

        JSONArray jsonArray = new JSONArray();

        for (String event : events) {
            jsonArray.put(event);
        }

        preferences.edit()
                .putString("events", jsonArray.toString())
                .apply();
    }

    // Load saved events
    private void loadEvents() {

        String savedData = preferences.getString(
                "events", "[]");

        try {

            JSONArray jsonArray = new JSONArray(savedData);

            for (int i = 0; i < jsonArray.length(); i++) {
                events.add(jsonArray.getString(i));
            }

        } catch (Exception e) {

            events.clear();
        }
    }
}
