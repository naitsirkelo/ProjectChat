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

public class Shop extends AppCompatActivity {

    private static final int REQUEST_NEW_ITEM = 1;
    Toolbar toolbar;
    CollapsingToolbarLayout tbLayout;
    TextView infoText;
    Firebase reference;
    LinearLayout layoutShop;
    ScrollView scrollViewShop;
    int totalItems = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        toolbar = findViewById(R.id.toolbar);
        tbLayout = findViewById(R.id.toolbar_layout);
        layoutShop = findViewById(R.id.layoutShop);
        scrollViewShop = findViewById(R.id.scrollViewShop);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addItem = new Intent(Shop.this, AddItemShop.class);
                startActivityForResult(addItem, REQUEST_NEW_ITEM);
            }
        });

        infoText = findViewById(R.id.shopInfo);
        downloadItems();
        setLanguage(UserDetails.language);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_NEW_ITEM && resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                String forWho = data.getStringExtra("forWho");
                String itemName = data.getStringExtra("itemName");
                addItem(itemName, forWho, false);
            }
        }
    }

    /* Update text boxes based on user settings. */
    private void setLanguage(String l) {
        if (l.equals("")) {
            l = "English";
        }
        switch (l) {
            case "English":
                tbLayout.setTitle("Shopping List");
                infoText.setText(R.string.content_shop_info);
                break;
            case "Norsk":
                tbLayout.setTitle("Handleliste");
                infoText.setText(R.string.content_shop_info_1);
                break;
            default:
                break;
        }
    }

    private void downloadItems() {
        String url = "https://projectchat-bf300.firebaseio.com/rooms/" + UserDetails.roomId + "/shoppingList.json";
        final ProgressDialog pd = new ProgressDialog(Shop.this);
        pd.setMessage("Downloading items...");
        pd.show();

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (s.equals("null")) {
                    Toast.makeText(Shop.this, "No Items To Download.", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        JSONObject json = new JSONObject(s);

                        /* Loop through objects in the shoppingList.json folder. */
                        Iterator<?> keys = json.keys();
                        while (keys.hasNext()) {
                            String key = (String) keys.next();
                            JSONObject obj = json.getJSONObject(key);

                            /* Getting data from current object, if not hidden. */
                            String hidden = obj.getString("hidden");

                            /* Getting data from current object, if not already completed. */
                            if (hidden.equals("0")) {
                                String item = obj.getString("itemName");
                                String forWho = obj.getString("forWho");

                                addItem(item, forWho, true);
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

        RequestQueue rQueue = Volley.newRequestQueue(Shop.this);
        rQueue.add(request);
    }

    private void addItem(final String itemName, final String forWho, boolean download) {

        final TextView textItem = new TextView(Shop.this);
        final Button removeButton = new Button(Shop.this);

        String full = itemName;
        if (forWho.equals("common")) {
            full = full + "\n" + Utility.languageSwitch("For Everyone", "Felles");
        } else {
            full = full + "\n" + Utility.languageSwitch("Private", "Private");
        }
        textItem.setText(full);
        textItem.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        lp1.gravity = Gravity.START;
        lp2.gravity = Gravity.CENTER;

        textItem.setLayoutParams(lp1);
        removeButton.setLayoutParams(lp2);

        /* Defining behaviour of button to remove items. */
        removeButton.setText(Utility.languageSwitch(Utility.removeTextEng, Utility.removeTextNor));
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutShop.removeView(textItem);
                layoutShop.removeView(removeButton);

                removeItem(itemName, forWho);
            }
        });

        totalItems++;

        layoutShop.addView(textItem);
        layoutShop.addView(removeButton);

        scrollViewShop.fullScroll(View.FOCUS_DOWN);

        if (!download) {
            /* Placing data in map before pushing to Firebase. */
            Map<String, String> map = new HashMap<>();
            map.put("itemName", itemName);
            map.put("forWho", forWho);
            map.put("hidden", "0");

            String key = formatKey(itemName, forWho);
            reference = new Firebase("https://projectchat-bf300.firebaseio.com/rooms/" + UserDetails.roomId + "/shoppingList");
            reference.child(key).setValue(map);
        }
        showInfo();
    }

    private void showInfo() {
        if (totalItems == 0) {
            infoText.setVisibility(View.VISIBLE);
        } else {
            infoText.setVisibility(View.GONE);
        }
    }

    /* Format strings for custom Firebase key ID. */
    private String formatKey(String itemName, String forWho) {
        String t = itemName.replaceAll("[^a-zA-Z0-9]", "");
        String tt = forWho.replace("[^a-zA-Z0-9]", "");
        return (UserDetails.showName + "_" + t + "_" + tt);
    }

    /* Accessing database to remove stored item. */
    private void removeItem(String itemName, String forWho) {
        String key = formatKey(itemName, forWho);
        reference = new Firebase("https://projectchat-bf300.firebaseio.com/rooms/" + UserDetails.roomId + "/shoppingList");
        reference.child(key).child("hidden").setValue("1");

        totalItems--;
    }
}
