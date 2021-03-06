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

public class Login extends AppCompatActivity {
    TextView registerUser;
    EditText username, password;
    Button loginButton;
    String user, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        registerUser = findViewById(R.id.registerButton);
        username = findViewById(R.id.input_username);
        password = findViewById(R.id.input_password);
        loginButton = findViewById(R.id.loginButton);

        registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });

        updateValues();
        setLanguage(UserDetails.language);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = username.getText().toString();
                pass = password.getText().toString();

                if (user.equals("")) {
                    username.setError("Username is required.");
                } else if (pass.equals("")) {
                    password.setError("Password is required.");
                } else {
                    String url = "https://projectchat-bf300.firebaseio.com/users.json";
                    final ProgressDialog pd = new ProgressDialog(Login.this);
                    pd.setMessage("Logging in...");
                    pd.show();

                    StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            if (s.equals("null")) {
                                Toast.makeText(Login.this, "User not found.", Toast.LENGTH_LONG).show();
                            } else {
                                try {
                                    JSONObject obj = new JSONObject(s);

                                    if (!obj.has(user)) {
                                        Toast.makeText(Login.this, "User not found.", Toast.LENGTH_LONG).show();
                                    } else if (obj.getJSONObject(user).getString("password").equals(pass)) {

                                        UserDetails.username = user;
                                        UserDetails.password = pass;
                                        UserDetails.showName = obj.getJSONObject(user).getString("showName");

                                        Utility.savePreference_3(Login.this, "Login",
                                                "storedUser", UserDetails.username,
                                                "storedShow", UserDetails.showName,
                                                "storedPass", UserDetails.password);

                                        startActivity(new Intent(Login.this, Room.class));
                                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                    } else {
                                        Toast.makeText(Login.this, "Incorrect password.", Toast.LENGTH_LONG).show();
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

                    RequestQueue rQueue = Volley.newRequestQueue(Login.this);
                    rQueue.add(request);
                }
            }
        });
    }

    private void updateValues() {
        SharedPreferences pref = this.getSharedPreferences("Login", MODE_PRIVATE);
        username.setText(pref.getString("storedUser", ""));
        password.setText(pref.getString("storedPass", ""));

        pref = getSharedPreferences("Settings", MODE_PRIVATE);
        UserDetails.language = pref.getString("storedLanguage", "English");
    }

    /* Update text boxes based on user settings. */
    private void setLanguage(String l) {
        if (l.equals("")) {
            l = "English";
        }
        switch (l) {
            case "English":
                registerUser.setText(R.string.register_user_info);
                loginButton.setText(R.string.login_info);
                break;
            case "Norsk":
                registerUser.setText(R.string.register_user_info_1);
                loginButton.setText(R.string.login_info_1);
                break;
            default:
                break;
        }
    }
}