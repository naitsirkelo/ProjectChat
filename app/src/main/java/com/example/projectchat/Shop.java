package com.example.projectchat;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class Shop extends AppCompatActivity {

    Toolbar toolbar;
    CollapsingToolbarLayout tbLayout;
    TextView infoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        toolbar = findViewById(R.id.toolbar);
        tbLayout = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        infoText = findViewById(R.id.shopInfo);
        setLanguage(UserDetails.language);
    }

    /* Update text boxes based on user settings. */
    private void setLanguage(String l) {
        if (l.equals("")) {
            l = "English";
        }
        switch (l) {
            case "English":
                tbLayout.setTitle("Buy Some Stuff");
                infoText.setText(R.string.content_shop_info);
                break;
            case "Norsk":
                tbLayout.setTitle("Handle Varer");
                infoText.setText(R.string.content_shop_info_1);
                break;
            default:
                break;
        }
    }
}
