package com.example.projectchat;

import android.content.Intent;
import android.os.Bundle;
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

public class HouseRules extends AppCompatActivity {

    private static final int REQUEST_NEW_RULE = 1;
    private static final String removeText = "Delete";

    TextView infoText;
    LinearLayout layoutRules;
    ScrollView scrollViewRules;
    int totalRules = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_rules);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("House Rules");
        setSupportActionBar(toolbar);

        /* Define button for adding new rule to list. */
        FloatingActionButton addRule = findViewById(R.id.addRule);
        addRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newTask = new Intent(HouseRules.this, CreateRule.class);
                startActivityForResult(newTask, REQUEST_NEW_RULE);
                overridePendingTransition(R.anim.enter_fromtop, R.anim.exit_fromtop);
            }
        });

        infoText = findViewById(R.id.infoText);
        layoutRules = findViewById(R.id.layoutRules);
        scrollViewRules = findViewById(R.id.scrollViewRules);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_NEW_RULE) {
            if (resultCode == RESULT_OK) {
                if (data != null && data.getExtras() != null) {
                    newHouseRule(data.getStringExtra("ruleVal"));
                }
            }
        }
    }

    private void newHouseRule(String rule) {

        final TextView textRule = new TextView(HouseRules.this);
        final Button removeButton = new Button(HouseRules.this);

        removeButton.setText(removeText);

        textRule.setText(rule);
        textRule.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        lp1.gravity = Gravity.START;
        lp2.gravity = Gravity.CENTER;
        lp3.gravity = Gravity.END;

        textRule.setLayoutParams(lp1);

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutRules.removeView(textRule);
                layoutRules.removeView(removeButton);

                totalRules--;
                showInfo(totalRules);
                //removeRule(textRule);
            }
        });

        layoutRules.addView(textRule);
        layoutRules.addView(removeButton);

        totalRules++;
        showInfo(totalRules);

        scrollViewRules.fullScroll(View.FOCUS_DOWN);
    }

    private void showInfo(int n) {
        if (n > 0) {
            infoText.setVisibility(View.GONE);
        } else {
            infoText.setVisibility(View.VISIBLE);
        }
    }
}
