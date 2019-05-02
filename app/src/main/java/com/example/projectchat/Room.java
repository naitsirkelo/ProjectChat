package com.example.projectchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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

public class Room extends AppCompatActivity {
    TextView createRoom, backToLogin;
    EditText roomId;
    String id;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        createRoom = findViewById(R.id.createRoom);
        backToLogin = findViewById(R.id.backButton);
        roomId = findViewById(R.id.input_id);
        loginButton = findViewById(R.id.joinButton);

        createRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Room.this, CreateRoom.class));
            }
        });

        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Room.this, Login.class));
                finish();
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

        /* Updating UI. */
        SharedPreferences pref = this.getSharedPreferences("Login", MODE_PRIVATE);
        roomId.setText(pref.getString("storedRoom", ""));

        setLanguage(UserDetails.language);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = roomId.getText().toString();

                if (id.equals("")) {
                    roomId.setError("Room ID is required.");
                } else {
                    String url = "https://projectchat-bf300.firebaseio.com/rooms.json";
                    final ProgressDialog pd = new ProgressDialog(Room.this);
                    pd.setMessage("Joining room...");
                    pd.show();

                    StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            if (s.equals("null")) {
                                Toast.makeText(Room.this, "Room not found.", Toast.LENGTH_LONG).show();
                            } else {
                                try {
                                    JSONObject obj = new JSONObject(s);

                                    if (!obj.has(id)) {
                                        Toast.makeText(Room.this, "Room not found.", Toast.LENGTH_LONG).show();
                                    } else if (obj.getJSONObject(id).getString("id").equals(id)) {

                                        UserDetails.roomId = id;
                                        Utility.savePreference_1(Room.this, "Login", "storedRoom", UserDetails.roomId);

                                        checkAdmin(id);

                                        startActivity(new Intent(Room.this, MainActivity.class));
                                        finish();
                                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                    } else {
                                        Toast.makeText(Room.this, "Incorrect Room ID.", Toast.LENGTH_LONG).show();
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

                    RequestQueue rQueue = Volley.newRequestQueue(Room.this);
                    rQueue.add(request);


                    url = "https://projectchat-bf300.firebaseio.com/users/" + UserDetails.username + ".json";

                    request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            Firebase reference = new Firebase("https://projectchat-bf300.firebaseio.com/users/" + UserDetails.username);

                            /* Update stored room ID on user. */
                            reference.child("room").setValue(UserDetails.roomId);
                        }

                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            System.out.println("" + volleyError);

                        }
                    });

                    rQueue = Volley.newRequestQueue(Room.this);
                    rQueue.add(request);
                }
            }
        });
    }

    private void checkAdmin(final String room) {
        String url = "https://projectchat-bf300.firebaseio.com/users.json";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject obj = new JSONObject(s);

                    /* If the user is the admin of the current room joined. */
                    if (obj.getJSONObject(UserDetails.username).getString("admin").equals(room)) {

                        UserDetails.admin = 1;
                        UserDetails.adminRoom = room;
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

        RequestQueue rQueue = Volley.newRequestQueue(Room.this);
        rQueue.add(request);
    }

    /* Update text boxes based on user settings. */
    private void setLanguage(String l) {
        if (l.equals("")) {
            l = "English";
        }
        switch (l) {
            case "English":
                loginButton.setText(R.string.join_info);
                backToLogin.setText(R.string.back_login_info);
                createRoom.setText(R.string.create_room_info);
                break;
            case "Norsk":
                loginButton.setText(R.string.join_info_1);
                backToLogin.setText(R.string.back_login_info_1);
                createRoom.setText(R.string.create_room_info_1);
                break;
            default:
                break;
        }
    }
}