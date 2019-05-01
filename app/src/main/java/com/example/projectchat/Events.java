package com.example.projectchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
    FloatingActionButton newEvent;
    Firebase reference;
    ScrollView scrollviewEvents;
    LinearLayout layoutEvents;
    int keyLength = 15;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        /* Define toolbar settings. */
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(Utility.languageSwitch("Events", "Arrangement"));
        setSupportActionBar(toolbar);

        eventText = findViewById(R.id.eventInfo);
        scrollviewEvents = findViewById(R.id.scrollViewEvents);
        layoutEvents = findViewById(R.id.layoutEvents);
        newEvent = findViewById(R.id.newEvent);

        downloadEvents();

        newEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserDetails.event == 0) {
                    startActivityForResult(new Intent(Events.this, CreateEvent.class), REQUEST_NEW_EVENT);
                } else {
                    String t = Utility.languageSwitch("You already have an event.", "Du har alt et arrangement.");
                    Toast.makeText(Events.this, t, Toast.LENGTH_SHORT).show();
                }
            }
        });

        eventText.setText(Utility.languageSwitch(getString(R.string.content_events_info), getString(R.string.content_events_info_1)));
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

    /* Download event items from the database. */
    private void downloadEvents() {
        String url = "https://projectchat-bf300.firebaseio.com/rooms/" + UserDetails.roomId + "/events.json";
        final ProgressDialog pd = new ProgressDialog(Events.this);
        String t = Utility.languageSwitch("Downloading events...", "Laster ned arrangementer...");
        pd.setMessage(t);
        pd.show();

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (s.equals("null")) {
                    Toast.makeText(Events.this, "No Events To Download.", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        JSONObject json = new JSONObject(s);

                        /* Loop through objects in the events.json folder. */
                        Iterator<?> keys = json.keys();

                        while (keys.hasNext()) {
                            String key = (String) keys.next();
                            JSONObject obj = json.getJSONObject(key);

                            boolean ownEvent = false;
                            String userCheck = obj.getString("user");
                            if (userCheck.equals(UserDetails.showName)) {
                                ownEvent = true;
                            }

                            /* Getting data from current object, if not already completed. */
                            String removed = obj.getString("hidden");
                            if (removed.equals("0")) {

                                String type = obj.getString("type");
                                String where = obj.getString("where");
                                String time = obj.getString("time");
                                String date = obj.getString("date");
                                String user = obj.getString("user");

                                addNewEvent(type, where, time, date, user, ownEvent);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                pd.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
                pd.dismiss();
            }
        });
        RequestQueue rQueue = Volley.newRequestQueue(Events.this);
        rQueue.add(request);
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
    }

    /* Set event as removed in database. */
    private void hideEvent(String key) {
        reference = new Firebase("https://projectchat-bf300.firebaseio.com/rooms/" + UserDetails.roomId + "/events");
        reference.child(key).child("hidden").setValue("1");

        UserDetails.event = 0;
    }

    /* Add new task item to the Room view. */
    private void addNewEvent(final String type, final String where, final String time,
                             final String date, final String user, boolean ownEvent) {

        final TextView textFull = new TextView(Events.this);
        final Button removeButton = new Button(Events.this);

        textFull.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(600, 125);

        lp1.gravity = Gravity.START;
        lp2.gravity = Gravity.CENTER;

        textFull.setLayoutParams(lp1);
        removeButton.setLayoutParams(lp2);

        /* Adding custom string and button to list as a new layout. */
        final String full = type + " ,  " + where + "  -  " + user + ".\n" + time + " - " + date;
        textFull.setText(full);
        layoutEvents.addView(textFull);

        UserDetails.event = 0;

        if (ownEvent) {
            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    layoutEvents.removeView(textFull);
                    layoutEvents.removeView(removeButton);
                    hideEvent(formatKey(type, time));
                }
            });
            removeButton.setText(Utility.languageSwitch("Remove Own Event", "Slett Eget Arr."));
            layoutEvents.addView(removeButton);

            UserDetails.event = 1;
        }
        scrollviewEvents.fullScroll(View.FOCUS_DOWN);
    }

    /* Format strings for custom Firebase key ID. */
    private String formatKey(String type, String time) {
        String t = type.replaceAll("[^a-zA-Z0-9]", "");
        String tt = time.replaceAll("[^a-zA-Z0-9]", "");
        t = t.substring(0, Math.min(t.length(), keyLength));
        return (UserDetails.showName + "_" + t + "_" + tt);
    }
}
