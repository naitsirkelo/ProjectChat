package com.example.projectchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Events extends AppCompatActivity {

    private static final int REQUEST_NEW_EVENT = 1;
    Toolbar toolbar;
    TextView eventText;
    FloatingActionButton newEvent, removeEvent;
    Firebase reference;
    int keyLength = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        /* Define toolbar settings. */
        toolbar = findViewById(R.id.toolbar);
        switch (UserDetails.language) {
            case "English":
                toolbar.setTitle("Events");
                break;
            case "Norsk":
                toolbar.setTitle("Arrangement");
                break;
            default:
                break;
        }
        setSupportActionBar(toolbar);

        eventText = findViewById(R.id.eventInfo);

        newEvent = findViewById(R.id.newEvent);
        newEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Events.this, CreateEvent.class), REQUEST_NEW_EVENT);
            }
        });

        removeEvent = findViewById(R.id.removeEvent);
        removeEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findEvent();
            }
        });

        downloadEvents();
        updateUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_NEW_EVENT) {
            if (resultCode == RESULT_OK) {
                if (data != null && data.getExtras() != null) {
                    String type = data.getStringExtra("type");
                    String where = data.getStringExtra("where");
                    String date = data.getStringExtra("date");
                    String time = data.getStringExtra("time");
                    uploadEvent(type, where, date, time);
                }
            }
        }
    }

    private void downloadEvents() {


    }

    private void uploadEvent(String type, String where, String date, String time) {
        /* Placing data in map before pushing to Firebase. */
        Map<String, String> map = new HashMap<>();
        map.put("user", UserDetails.showName);
        map.put("type", type);
        map.put("where", where);
        map.put("date", date);
        map.put("time", time);
        map.put("hidden", "0");

        String key = formatKey(type, time);
        reference = new Firebase("https://projectchat-bf300.firebaseio.com/rooms/" + UserDetails.roomId + "/events");
        reference.child(key).setValue(map);

        UserDetails.event = 1;
        updateUI();
    }

    private void findEvent() {
        String url = "https://projectchat-bf300.firebaseio.com/rooms/" + UserDetails.roomId + "/events.json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject json = new JSONObject(s);
                    Iterator iterator = json.keys();

                    /* Looking for event with key like username. */
                    while (iterator.hasNext()) {
                        String key = iterator.next().toString();

                        if (key.contains(UserDetails.showName)) {
                            reference = new Firebase("https://projectchat-bf300.firebaseio.com/rooms/" + UserDetails.roomId + "/events");
                            reference.child(key).child("hidden").setValue("1");
                            UserDetails.event = 0;
                            updateUI();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
            }
        });
        RequestQueue rQueue = Volley.newRequestQueue(Events.this);
        rQueue.add(request);
    }

    private void updateUI() {
        switch (UserDetails.event) {
            case 1:
                eventText.setVisibility(View.GONE);
                newEvent.hide();
                removeEvent.show();
                break;
            case 0:
                setLanguage(UserDetails.language);
                eventText.setVisibility(View.VISIBLE);
                newEvent.show();
                removeEvent.hide();
                break;
            default:
                break;
        }
    }

    /* Format strings for custom Firebase key ID. */
    private String formatKey(String type, String time) {
        String t = type.replaceAll("[^a-zA-Z0-9]", "");
        String tt = time.replaceAll("[^a-zA-Z0-9]", "");
        t = t.substring(0, Math.min(t.length(), keyLength));
        return (UserDetails.showName + "_" + t + "_" + tt);
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
