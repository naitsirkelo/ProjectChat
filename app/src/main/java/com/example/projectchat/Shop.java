package com.example.projectchat;

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

import com.firebase.client.Firebase;

import java.util.HashMap;
import java.util.Map;

public class Shop extends AppCompatActivity {

    private static final int REQUEST_NEW_ITEM = 1;
    Toolbar toolbar;
    CollapsingToolbarLayout tbLayout;
    TextView infoText;
    Firebase reference;
    FloatingActionButton newItem, removeItem;
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
        setLanguage(UserDetails.language);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_NEW_ITEM && resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                String forWho = data.getStringExtra("forWho");
                String itemName = data.getStringExtra("itemName");
                addItem(itemName, forWho);
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

    private void addItem(final String itemName, final String forWho) {

        final TextView textItem = new TextView(Shop.this);
        final Button removeButton = new Button(Shop.this);

        textItem.setText(itemName);
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
                layoutShop.removeView(infoText);
                layoutShop.removeView(removeButton);

                totalItems--;
                showInfo(totalItems);
                removeItem(itemName, forWho);
            }
        });

        layoutShop.addView(textItem);
        layoutShop.addView(removeButton);

        totalItems++;
        showInfo(totalItems);

        scrollViewShop.fullScroll(View.FOCUS_DOWN);

        /* Placing data in map before pushing to Firebase. */
        Map<String, String> map = new HashMap<>();
        map.put("itemName", itemName);
        map.put("forWho", forWho);
        map.put("hidden", "0");

        String key = formatKey(itemName, forWho);
        reference = new Firebase("https://projectchat-bf300.firebaseio.com/rooms/" + UserDetails.roomId + "/shoppingList");
        reference.child(key).setValue(map);

        UserDetails.item = 1;
        updateUI();
    }

    /* Format strings for custom Firebase key ID. */
    private String formatKey(String itemName, String forWho) {
        String t = itemName.replaceAll("[^a-zA-Z0-9]", "");
        String tt = forWho.replace("[^a-zA-Z0-9]", "");
        return (UserDetails.showName + "_" + t + "_" + tt);
    }

    private void updateUI() {
        switch (UserDetails.item) {
            case 1:
                infoText.setVisibility(View.GONE);
                newItem.hide();
                removeItem.show();
                break;
            case 0:
                setLanguage(UserDetails.language);
                infoText.setVisibility(View.VISIBLE);
                newItem.show();
                removeItem.hide();
                break;
            default:
                break;
        }
    }


    private void showInfo(int n) {
        if (n > 0) {
            infoText.setVisibility(View.GONE);
        } else {
            infoText.setVisibility(View.VISIBLE);
        }
    }

    /* Accessing database to remove stored item. */
    private void removeItem(String itemName, String forWho) {
        String key = formatKey(itemName, forWho);
        reference = new Firebase("https://projectchat-bf300.firebaseio.com/rooms/" + UserDetails.roomId + "/shoppingList");
        reference.child(key).child("hidden").setValue("1");
    }


}
