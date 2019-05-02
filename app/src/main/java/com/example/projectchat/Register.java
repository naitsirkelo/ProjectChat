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

public class Register extends AppCompatActivity {
    EditText username, showName, password;
    Button registerButton;
    String user, show, pass;
    TextView login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        username = findViewById(R.id.input_username);
        showName = findViewById(R.id.input_showName);
        password = findViewById(R.id.input_password);
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
                user = username.getText().toString();
                show = showName.getText().toString();
                pass = password.getText().toString();

                if (user.equals("")) {
                    username.setError("Account name is required.");
                } else if (show.equals("")) {
                    showName.setError("Custom username is required.");
                } else if (show.equals(user)) {
                    username.setError("Can't be equal.");
                    showName.setError("Can't be equal.");
                } else if (pass.equals("")) {
                    password.setError("Password is required.");
                } else if (!user.matches("[A-Za-z0-9]+")) {
                    username.setError("No special characters allowed.");
                } else if (user.length() < 3) {
                    username.setError("At least 3 letters/numbers required.");
                } else if (pass.length() < 6) {
                    password.setError("At least 6 letters/numbers required.");
                } else {
                    final ProgressDialog pd = new ProgressDialog(Register.this);
                    pd.setMessage("Storing user...");
                    pd.show();

                    String url = "https://projectchat-bf300.firebaseio.com/users.json";

                    StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            Firebase reference = new Firebase("https://projectchat-bf300.firebaseio.com/users");

                            if (s.equals("null")) {
                                reference.child(user).child("password").setValue(pass);
                                reference.child(user).child("showName").setValue(show);
                                reference.child(user).child("admin").setValue("0");
                                Toast.makeText(Register.this, "Registration successful!", Toast.LENGTH_LONG).show();
                            } else {
                                try {
                                    JSONObject obj = new JSONObject(s);

                                    /* Set User values in Firebase. */
                                    if (!obj.has(user)) {
                                        reference.child(user).child("password").setValue(pass);
                                        reference.child(user).child("showName").setValue(show);
                                        reference.child(user).child("admin").setValue("0");
                                        reference.child(user).child("blocked").setValue("0");

                                        Toast.makeText(Register.this, "Registration successful!", Toast.LENGTH_LONG).show();
                                        Intent i = new Intent(Register.this, Login.class);
                                        finish();
                                        startActivity(i);
                                    } else {
                                        Toast.makeText(Register.this, "That username is already taken..", Toast.LENGTH_LONG).show();
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
                    RequestQueue rQueue = Volley.newRequestQueue(Register.this);
                    rQueue.add(request);
                }
            }
        });
    }
}
