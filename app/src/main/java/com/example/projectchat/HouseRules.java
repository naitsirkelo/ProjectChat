package com.example.projectchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
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

public class HouseRules extends AppCompatActivity {

    private static final int REQUEST_NEW_RULE = 1;
    private static final String removeText = "Delete";

    TextView infoText;
    LinearLayout layoutRules;
    ScrollView scrollViewRules;
    Firebase reference;
    Toolbar toolbar;
    CollapsingToolbarLayout tbLayout;
    int totalRules = 0, keyLength = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_rules);

        toolbar = findViewById(R.id.toolbar);
        tbLayout = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);

        /* Define button for adding new rule to list. */
        FloatingActionButton addRule = findViewById(R.id.addRule);
        addRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newTask = new Intent(HouseRules.this, CreateRule.class);
                startActivityForResult(newTask, REQUEST_NEW_RULE);
            }
        });

        infoText = findViewById(R.id.infoText);
        layoutRules = findViewById(R.id.layoutRules);
        scrollViewRules = findViewById(R.id.scrollViewRules);

        downloadRules();

        setLanguage(UserDetails.language);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_NEW_RULE) {
            if (resultCode == RESULT_OK) {
                if (data != null && data.getExtras() != null) {
                    newHouseRule(data.getStringExtra("ruleVal"), false);
                }
            }
        }
    }

    /* Download rule items from the database. */
    private void downloadRules() {

        String url = "https://projectchat-bf300.firebaseio.com/rooms/" + UserDetails.roomId + "/rules.json";
        final ProgressDialog pd = new ProgressDialog(HouseRules.this);
        pd.setMessage("Downloading rules...");
        pd.show();

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (s.equals("null")) {
                    Toast.makeText(HouseRules.this, "No Rules To Download.", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        JSONObject json = new JSONObject(s);

                        /* Loop through objects in the tasks.json folder. */
                        Iterator<?> keys = json.keys();
                        while (keys.hasNext()) {
                            String key = (String) keys.next();
                            JSONObject obj = json.getJSONObject(key);

                            /* Getting data from current object, if not hidden. */
                            String hidden = obj.getString("hidden");

                            /* Getting data from current object, if not already completed. */
                            if (hidden.equals("0")) {
                                newHouseRule(obj.getString("rule"), true);
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

        RequestQueue rQueue = Volley.newRequestQueue(HouseRules.this);
        rQueue.add(request);
    }

    private void newHouseRule(final String rule, boolean download) {

        final TextView textRule = new TextView(HouseRules.this);
        final Button removeButton = new Button(HouseRules.this);

        removeButton.setText(removeText);

        textRule.setText(rule);
        textRule.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        lp1.gravity = Gravity.START;
        lp2.gravity = Gravity.CENTER;

        textRule.setLayoutParams(lp1);
        removeButton.setLayoutParams(lp2);

        /* Defining behaviour of button to remove rules. */
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutRules.removeView(textRule);
                layoutRules.removeView(removeButton);

                totalRules--;
                showInfo(totalRules);
                removeRule(rule);
            }
        });

        layoutRules.addView(textRule);
        layoutRules.addView(removeButton);

        totalRules++;
        showInfo(totalRules);

        scrollViewRules.fullScroll(View.FOCUS_DOWN);

        if (!download) {
            /* Placing data in map before pushing to Firebase. */
            Map<String, String> map = new HashMap<>();
            map.put("user", UserDetails.username);
            map.put("hidden", "0");
            map.put("rule", rule);

            String key = formatKey(rule);
            reference = new Firebase("https://projectchat-bf300.firebaseio.com/rooms/" + UserDetails.roomId + "/rules");
            reference.child(key).setValue(map);
        }
    }

    private void showInfo(int n) {
        if (n > 0) {
            infoText.setVisibility(View.GONE);
        } else {
            infoText.setVisibility(View.VISIBLE);
        }
    }

    /* Format strings for custom Firebase key ID. */
    private String formatKey(String rule) {

        String r = rule.replaceAll(" ", "-");
        r = r.replaceAll("[^a-zA-Z0-9-]", "");
        r = r.substring(0, Math.min(r.length(), keyLength));

        return r;
    }

    /* Accessing database to remove stored rule. */
    private void removeRule(String rule) {
        String key = formatKey(rule);
        reference = new Firebase("https://projectchat-bf300.firebaseio.com/rooms/" + UserDetails.roomId + "/rules");
        reference.child(key).child("hidden").setValue("1");
    }

    /* Update text boxes based on user settings. */
    private void setLanguage(String l) {
        if (l.equals("")) {
            l = "English";
        }
        switch (l) {
            case "English":
                tbLayout.setTitle("House Rules");
                infoText.setText(R.string.house_rules_info);
                break;
            case "Norsk":
                tbLayout.setTitle("Husregler");
                infoText.setText(R.string.house_rules_info_1);
                break;
            default:
                break;
        }
    }
}
