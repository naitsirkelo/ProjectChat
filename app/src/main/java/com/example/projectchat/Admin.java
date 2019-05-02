package com.example.projectchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Iterator;

public class Admin extends AppCompatActivity {

    TextView adminText;
    ListView usersList;
    TextView noUsersText;
    ProgressDialog pd;
    Firebase reference;
    ArrayList<String> al = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        adminText = findViewById(R.id.adminInfo);
        noUsersText = findViewById(R.id.noUsersText);

        usersList = findViewById(R.id.usersList);
        usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                blockUser(al.get(position));
            }
        });

        FloatingActionButton backButton = findViewById(R.id.backButtonAdmin);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        pd = new ProgressDialog(Admin.this);
        pd.setMessage("Finding users...");
        pd.show();

        String url = "https://projectchat-bf300.firebaseio.com/users.json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                doOnSuccess(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(Admin.this);
        rQueue.add(request);
    }

    public void doOnSuccess(String s) {
        try {
            /* Getting 'users'. */
            JSONObject json = new JSONObject(s);
            Iterator<?> iterator = json.keys();

            /* Looping through users. */
            while (iterator.hasNext()) {

                String key = (String) iterator.next();
                JSONObject obj = json.getJSONObject(key);

                String room = obj.getString("room");
                String blocked = obj.getString("blocked");

                /* Add to chat list if users have same room ID. */
                if (!key.equals(UserDetails.username) && room.equals(UserDetails.roomId) && !blocked.equals(UserDetails.adminRoom)) {
                    al.add(key);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        showUI();
        usersList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, al));

        pd.dismiss();
    }

    private void blockUser(String user) {
        reference = new Firebase("https://projectchat-bf300.firebaseio.com/users");
        reference.child(user).child("blocked").setValue(UserDetails.adminRoom);
        reference.child(user).child("room").setValue("");
    }

    /* Setting list and info visibility. */
    private void showUI() {
        if (al.size() < 1) {
            noUsersText.setText(Utility.languageSwitch(getString(R.string.no_chat_info), getString(R.string.no_chat_info_1)));
        } else {
            noUsersText.setText(Utility.languageSwitch(getString(R.string.remove_user_info), getString(R.string.remove_user_info_1)));
        }
    }
}
