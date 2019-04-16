package com.example.projectchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateTask extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        final EditText task = findViewById(R.id.input_task);
        final EditText area = findViewById(R.id.input_area);
        final SimpleDateFormat sdf = new SimpleDateFormat("EEE d MMM HH:mm");


        FloatingActionButton exit = findViewById(R.id.exit_fab);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toMain = new Intent(CreateTask.this, MainActivity.class);
                setResult(RESULT_CANCELED, toMain);
                finish();
                overridePendingTransition(R.anim.enter_fromtop, R.anim.exit_fromtop);
            }
        });

        FloatingActionButton create = findViewById(R.id.create_fab);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toMain = new Intent(CreateTask.this, MainActivity.class);
                toMain.putExtra("taskVal", task.getText().toString());
                toMain.putExtra("areaVal", area.getText().toString());
                toMain.putExtra("timestampVal", sdf.format(new Date()));

                setResult(RESULT_OK, toMain);
                finish();
                overridePendingTransition(R.anim.enter_fromtop, R.anim.exit_fromtop);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_fromtop, R.anim.exit_fromtop);
    }
}
