package com.example.projectchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


public class CreateEvent extends AppCompatActivity {

    TextView createText;
    EditText typeEdit, whereEdit;
    DatePicker picker;
    TimePicker timePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        typeEdit = findViewById(R.id.input_type);
        whereEdit = findViewById(R.id.input_where);
        picker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);

        FloatingActionButton exit = findViewById(R.id.exit_fab);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back = new Intent(CreateEvent.this, Events.class);
                setResult(RESULT_CANCELED, back);
                finish();
            }
        });

        FloatingActionButton create = findViewById(R.id.create_fab);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = typeEdit.getText().toString();
                String where = whereEdit.getText().toString();

                if (!type.equals("") && !where.equals("")) {
                    Intent back = new Intent(CreateEvent.this, Events.class);
                    String date = picker.getDayOfMonth() + "/" + (picker.getMonth() + 1) + "/" + picker.getYear();
                    String time = timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute();
                    back.putExtra("type", type);
                    back.putExtra("where", where);
                    back.putExtra("date", date);
                    back.putExtra("time", time);

                    setResult(RESULT_OK, back);
                    finish();
                } else {
                    Toast.makeText(CreateEvent.this, "Please enter the Type and Location", Toast.LENGTH_SHORT).show();
                }
            }
        });

        createText = findViewById(R.id.createInfo);
        setLanguage(UserDetails.language);
    }

    /* Update text boxes based on user settings. */
    private void setLanguage(String l) {
        if (l.equals("")) {
            l = "English";
        }
        switch (l) {
            case "English":
                createText.setText(R.string.define_create_event_info);
                break;
            case "Norsk":
                createText.setText(R.string.define_create_event_info_1);
                break;
            default:
                break;
        }
    }
}
