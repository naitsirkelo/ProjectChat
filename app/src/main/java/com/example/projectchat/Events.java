package com.example.projectchat;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

public class Events extends AppCompatActivity {

    Toolbar toolbar;
    TextView eventText;
    DatePicker picker;
    Button saveButton, removeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        toolbar = findViewById(R.id.toolbar);
        switch (UserDetails.language) {
            case "English":
                toolbar.setTitle("Start An Event");
                break;
            case "Norsk":
                toolbar.setTitle("Lag Arrangement");
                break;
            default:
                break;
        }
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        eventText = findViewById(R.id.eventInfo);
        picker = findViewById(R.id.datePicker);

        saveButton = findViewById(R.id.saveDate);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDetails.event = 1;
                String date = "Selected Date: " + picker.getDayOfMonth() + "/" + (picker.getMonth() + 1) + "/" + picker.getYear();
                eventText.setText(date);
            }
        });

        removeButton = findViewById(R.id.removeEvent);
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDetails.event = 0;
                setLanguage(UserDetails.language);
            }
        });

        setLanguage(UserDetails.language);

    }

    private void showInfo() {
        if (UserDetails.event == 1) {
            eventText.setVisibility(View.GONE);
        } else {
            eventText.setVisibility(View.VISIBLE);
        }
    }

    /* Update text boxes based on user settings. */
    private void setLanguage(String l) {
        if (l.equals("")) {
            l = "English";
        }
        switch (l) {
            case "English":
                eventText.setText(R.string.content_events_info);
                break;
            case "Norsk":
                eventText.setText(R.string.content_events_info_1);
                break;
            default:
                break;
        }
    }
}
