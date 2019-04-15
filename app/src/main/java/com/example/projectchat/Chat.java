package com.example.projectchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Chat extends AppCompatActivity {
    LinearLayout layout;
    ImageView sendButton;
    EditText messageArea;
    ScrollView scrollView;
    Firebase reference1, reference2;
    SimpleDateFormat sdf;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        sdf = new SimpleDateFormat("EEE, MMM d 'AT' HH:mm a");

        layout =      findViewById(R.id.layout1);
        sendButton =  findViewById(R.id.sendButton);
        messageArea = findViewById(R.id.messageArea);
        scrollView =  findViewById(R.id.scrollView);
        scrollView.fullScroll(View.FOCUS_DOWN);


        Firebase.setAndroidContext(this);
        reference1 = new Firebase("https://projectchat-bf300.firebaseio.com/messages/" + UserDetails.username + "_" + UserDetails.chatWith);
        reference2 = new Firebase("https://projectchat-bf300.firebaseio.com/messages/" + UserDetails.chatWith + "_" + UserDetails.username);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                if(!messageText.equals("")){
                    Map<String, String> map = new HashMap<>();
                    String timestamp = sdf.format(new Date());

                    map.put("message", messageText);
                    map.put("user", UserDetails.username);
                    map.put("time", timestamp);
                    
                    reference1.push().setValue(map);
                    reference2.push().setValue(map);
                    messageArea.setText("");
                }
            }
        });

        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String message =  map.get("message").toString();
                String userName = map.get("user").toString();
                String time =     map.get("time").toString();

                if(userName.equals(UserDetails.username)){
                    addMessageBox("You ", message, time,1);
                }
                else{
                    addMessageBox(UserDetails.chatWith, message, time,2);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void addMessageBox(String name, String message, String time, int type) {

        TextView textMsg =  new TextView(Chat.this);
        TextView textName = new TextView(Chat.this);
        TextView textTime = new TextView(Chat.this);

        textName.setText(name);
        textName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        textMsg.setText(message);
        textTime.setText(time);
        textTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);

        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;

        if (type == 1) {
            lp1.gravity = Gravity.RIGHT;
            lp2.gravity = Gravity.RIGHT;
            lp3.gravity = Gravity.RIGHT;
            textMsg.setBackgroundResource(R.drawable.text_in);
        } else{
            lp1.gravity = Gravity.LEFT;
            lp2.gravity = Gravity.LEFT;
            lp3.gravity = Gravity.LEFT;
            textMsg.setBackgroundResource(R.drawable.text_out);
        }


        textName.setLayoutParams(lp1);
        textMsg.setLayoutParams(lp2);
        textTime.setLayoutParams(lp3);

        layout.addView(textName);
        layout.addView(textMsg);
        layout.addView(textTime);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }
}
