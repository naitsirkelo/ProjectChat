package com.example.projectchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class CreateTask extends AppCompatActivity {

    TextView taskText, areaText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        final EditText task = findViewById(R.id.input_task);
        final EditText area = findViewById(R.id.input_area);
        final SimpleDateFormat sdf = new SimpleDateFormat("EEE d MMM HH:mm", Locale.getDefault());

        FloatingActionButton exit = findViewById(R.id.exit_fab);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toMain = new Intent(CreateTask.this, MainActivity.class);
                setResult(RESULT_CANCELED, toMain);
                finish();
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
            }
        });

        taskText = findViewById(R.id.taskInfo);
        areaText = findViewById(R.id.areaInfo);
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
                taskText.setText(R.string.define_task_info);
                areaText.setText(R.string.define_area_info);
                break;
            case "Norsk":
                taskText.setText(R.string.define_task_info_1);
                areaText.setText(R.string.define_area_info_1);
                break;
            default:
                break;
        }
    }
}
