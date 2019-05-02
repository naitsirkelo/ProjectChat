package com.example.projectchat;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class AddItemShop extends AppCompatActivity {
    EditText itemName;
    RadioGroup itemForWho;
    RadioButton radioCommon, radioPrivate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item_shop);

        itemName = findViewById(R.id.itemName);
        itemForWho = findViewById(R.id.item_forwho);
        radioCommon = findViewById(R.id.radio_common);
        radioPrivate = findViewById(R.id.radio_private);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        FloatingActionButton exit = findViewById(R.id.exit_fab);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back = new Intent(AddItemShop.this, Shop.class);
                setResult(RESULT_CANCELED, back);
                finish();
            }
        });

        FloatingActionButton create = findViewById(R.id.create_fab);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String item = itemName.getText().toString();
                String forWho = "";
                /* Check which radio button was clicked. */
                switch(itemForWho.getCheckedRadioButtonId()) {
                    case R.id.radio_private:
                        forWho = UserDetails.username;
                        break;
                    case R.id.radio_common:
                        forWho = "common";
                        break;
                }

                if (!item.equals("")) {
                    Intent back = new Intent(AddItemShop.this, Shop.class);
                    back.putExtra("forWho", forWho);
                    back.putExtra("itemName", item);
                    setResult(RESULT_OK, back);
                    finish();
                } else {
                    Toast.makeText(AddItemShop.this, "Please enter an item name..", Toast.LENGTH_SHORT).show();
                }
            }
        });

        setLanguage(UserDetails.language);
    }

    /* Update text boxes based on user settings. */
    private void setLanguage(String l) {
        if (l.equals("")) {
            l = "English";
        }
        switch (l) {
            case "English":
                radioCommon.setText(R.string.radio_common_eng);
                radioPrivate.setText(R.string.radio_private_eng);
                break;
            case "Norsk":
                radioCommon.setText(R.string.radio_common_nor);
                radioPrivate.setText(R.string.radio_private_nor);
                break;
            default:
                break;
        }
    }
}
