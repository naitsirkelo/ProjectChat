package com.example.projectchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;


public class CreateRule extends AppCompatActivity {

    TextView createText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_rule);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        final EditText rule = findViewById(R.id.input_rule);

        FloatingActionButton exit = findViewById(R.id.exit_fab);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toMain = new Intent(CreateRule.this, HouseRules.class);
                setResult(RESULT_CANCELED, toMain);
                finish();
                overridePendingTransition(R.anim.enter_frombot, R.anim.exit_frombot);
            }
        });

        FloatingActionButton create = findViewById(R.id.create_fab);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toMain = new Intent(CreateRule.this, HouseRules.class);
                toMain.putExtra("ruleVal", rule.getText().toString());

                setResult(RESULT_OK, toMain);
                finish();
                overridePendingTransition(R.anim.enter_frombot, R.anim.exit_frombot);
            }
        });

        createText = findViewById(R.id.createInfo);
        setLanguage(UserDetails.language);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_frombot, R.anim.exit_frombot);
    }

    /* Update text boxes based on user settings. */
    private void setLanguage(String l) {
        if (l.equals("")) {
            l = "English";
        }
        switch (l) {
            case "English":
                createText.setText(R.string.define_create_rule_info);
                break;
            case "Norsk":
                createText.setText(R.string.define_create_rule_info_1);
                break;
            default:
                break;
        }
    }
}
