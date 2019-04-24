package com.example.projectchat;

import android.app.ProgressDialog;
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

import java.util.HashMap;
import java.util.Map;

public class OwnerInfo extends AppCompatActivity {

    TextView clearButton;
    EditText phoneInput, emailInput;
    Button saveButton;
    Firebase reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_info);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        clearButton = findViewById(R.id.clearButton);
        phoneInput = findViewById(R.id.input_phone);
        emailInput = findViewById(R.id.input_email);
        saveButton = findViewById(R.id.saveButton);

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneInput.setText("");
                emailInput.setText("");
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateInfo(phoneInput.getText().toString(), emailInput.getText().toString());
            }
        });

        downloadInfo();
        updateTextFields();
    }

    private void downloadInfo() {

        String url = "https://projectchat-bf300.firebaseio.com/rooms/" + UserDetails.roomId + "/owner.json";
        final ProgressDialog pd = new ProgressDialog(OwnerInfo.this);
        pd.setMessage("Downloading info...");
        pd.show();

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (s.equals("null")) {
                    Toast.makeText(OwnerInfo.this, "No info to download.", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        JSONObject json = new JSONObject(s);

                        String phone = json.getString("phone");
                        String email = json.getString("email");

                        SharedPreferences pref = getSharedPreferences("Owner", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();

                        editor.putString("storedPhoneOwner", phone);
                        editor.putString("storedEmailOwner", email);
                        editor.apply();

                        updateTextFields();

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

        RequestQueue rQueue = Volley.newRequestQueue(OwnerInfo.this);
        rQueue.add(request);
    }

    /* Update info in SharedPreference and database. */
    private void updateInfo(String phone, String email) {
        SharedPreferences pref = getSharedPreferences("Owner", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("storedPhoneOwner", phone);
        editor.putString("storedEmailOwner", email);
        editor.apply();

        /* Placing data in map before pushing to Firebase. */
        Map<String, String> map = new HashMap<>();
        map.put("phone", phone);
        map.put("email", email);

        String key = "owner";
        reference = new Firebase("https://projectchat-bf300.firebaseio.com/rooms/" + UserDetails.roomId);
        reference.child(key).setValue(map);

        Toast.makeText(OwnerInfo.this, "Owner Info Updated.", Toast.LENGTH_LONG).show();
    }

    /* Update info shown in text fields. */
    private void updateTextFields() {
        SharedPreferences pref = getSharedPreferences("Owner", MODE_PRIVATE);
        phoneInput.setText(pref.getString("storedPhoneOwner", ""));
        emailInput.setText(pref.getString("storedEmailOwner", ""));
    }
}
