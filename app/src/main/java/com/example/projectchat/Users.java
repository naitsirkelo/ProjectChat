package com.example.projectchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class Users extends AppCompatActivity {
    ListView usersList;
    TextView noUsersText;
    ArrayList<String> al = new ArrayList<>();
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        noUsersText = findViewById(R.id.noUsersText);

        usersList = findViewById(R.id.usersList);
        usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserDetails.chatWith = al.get(position);
                startActivity(new Intent(Users.this, Chat.class));
            }
        });

        pd = new ProgressDialog(Users.this);
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

        RequestQueue rQueue = Volley.newRequestQueue(Users.this);
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

                /* Add to chat list if users have same room ID. */
                if (!key.equals(UserDetails.username) && room.equals(UserDetails.roomId)) {
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

    /* Setting list and info visibility. */
    private void showUI() {
        if (al.size() < 1) {
            noUsersText.setText(Utility.languageSwitch(getString(R.string.no_chat_info), getString(R.string.no_chat_info_1)));
        } else {
            noUsersText.setText(Utility.languageSwitch(getString(R.string.chat_info), getString(R.string.chat_info_1)));
        }
    }
}
