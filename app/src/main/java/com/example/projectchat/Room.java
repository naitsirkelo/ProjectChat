package com.example.projectchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import org.json.JSONException;
import org.json.JSONObject;

public class Room extends AppCompatActivity {
    TextView createRoom;
    EditText roomId;
    String id;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        createRoom = findViewById(R.id.createRoom);
        roomId = findViewById(R.id.input_id);
        loginButton = findViewById(R.id.joinButton);

        createRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Room.this, CreateRoom.class));
            }
        });

        SharedPreferences pref = this.getSharedPreferences("Login", MODE_PRIVATE);

        roomId.setText(pref.getString("storedRoom", ""));


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

                                        SharedPreferences pref = getSharedPreferences("Login", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = pref.edit();

                                        editor.putString("storedRoom", UserDetails.roomId);
                                        editor.apply();

                                        startActivity(new Intent(Room.this, MainActivity.class));
                                        finish();
                                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                    } else {
                                        Toast.makeText(Room.this, "Incorrect Room ID", Toast.LENGTH_LONG).show();
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
                }
            }
        });
    }
}