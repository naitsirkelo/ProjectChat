package com.example.projectchat;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.firebase.client.Firebase;

import org.json.JSONException;
import org.json.JSONObject;

public class CreateRoom extends AppCompatActivity {
    EditText roomId;
    Button registerButton;
    String id;
    TextView login;
    Firebase reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        roomId = findViewById(R.id.input_roomId);
        registerButton = findViewById(R.id.registerButton);
        login = findViewById(R.id.loginButton);

        Firebase.setAndroidContext(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = roomId.getText().toString();

                if (id.equals("")) {
                    roomId.setError("Room ID is required.");
                } else if (!id.matches("[A-Za-z0-9]+")) {
                    roomId.setError("No special characters allowed.");
                } else if (id.length() < 5) {
                    roomId.setError("At least 6 letters/numbers required.");
                } else if (id.length() > 16) {
                    roomId.setError("Max. 16 letters/numbers allowed.");
                } else {
                    final ProgressDialog pd = new ProgressDialog(CreateRoom.this);
                    pd.setMessage("Storing room...");
                    pd.show();

                    String url = "https://projectchat-bf300.firebaseio.com/rooms.json";

                    StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            reference = new Firebase("https://projectchat-bf300.firebaseio.com/rooms");

                            if (s.equals("null")) {
                                reference.child(id).child("id").setValue(id);
                                Toast.makeText(CreateRoom.this, "Registration successful!", Toast.LENGTH_LONG).show();
                            } else {
                                try {
                                    JSONObject obj = new JSONObject(s);

                                    if (!obj.has(id)) {
                                        reference.child(id).child("id").setValue(id);
                                        Toast.makeText(CreateRoom.this, "Created successfully.", Toast.LENGTH_LONG).show();

                                        /* Set user as admin of the room created. */
                                        reference = new Firebase("https://projectchat-bf300.firebaseio.com/users");
                                        reference.child(UserDetails.username).child("admin").setValue(id);

                                        Intent i = new Intent(CreateRoom.this, Room.class);
                                        startActivity(i);
                                    } else {
                                        Toast.makeText(CreateRoom.this, "That ID is already in use..", Toast.LENGTH_LONG).show();
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
                    RequestQueue rQueue = Volley.newRequestQueue(CreateRoom.this);
                    rQueue.add(request);
                }
            }
        });
    }
}

