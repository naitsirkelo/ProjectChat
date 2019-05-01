package com.example.projectchat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;


public class PreferencesCustom extends AppCompatActivity {

    Spinner langSpinner;
    String[] languages;
    TextView preferenceText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences_custom);

        /* Preventing keyboard from distorting layout. */
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        /* Define spinner with values from XML. */
        langSpinner = findViewById(R.id.languageSpinner);
        languages = getResources().getStringArray(R.array.pref_lang_languages_titles);
        ArrayAdapter<String> langAdapter = new ArrayAdapter<>(PreferencesCustom.this,
                android.R.layout.simple_spinner_item, languages);

        /* Update spinner position. */
        SharedPreferences pref = getSharedPreferences("Settings", MODE_PRIVATE);
        int pos = langAdapter.getPosition(pref.getString("storedLanguage", "English"));
        langSpinner.setSelection(pos);

        /* Return to main without updating value. */
        FloatingActionButton backButton = findViewById(R.id.backButtonPref);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        /* Store the updated value and return to Main. */
        FloatingActionButton saveButton = findViewById(R.id.saveButtonPref);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SharedPreferences pref = getSharedPreferences("Settings", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();

                String l = langSpinner.getSelectedItem().toString();
                UserDetails.language = l;
                editor.putString("storedLanguage", l);
                editor.apply();

                finish();
            }
        });

        /* Set language based on Userdetails. */
        findViewById(R.id.preferenceInfo);
        preferenceText.setText(Utility.languageSwitch(getString(R.string.content_pref_info), getString(R.string.content_pref_info_1)));
    }
}
