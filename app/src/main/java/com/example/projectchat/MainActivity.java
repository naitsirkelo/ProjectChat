package com.example.projectchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_NEW_TASK = 1, REQUEST_CAPTURE_IMAGE = 2;
    private static final String
            urlEnglish = "http://www.nyinorge.no/en/Familiegjenforening/New-in-Norway/Housing/Renting-a-houseapartment/Your-rights-as-a-tenant/",
            urlNorsk = "http://www.nyinorge.no/no/Familiegjenforening/Ny-i-Norge/Bolig/A-leie-bolig/Rettigheter-som-leietaker/",
            completedText = "Done!",
            removeText = "Remove";
    TextView unameMain, customname;
    ImageView avatar;
    LinearLayout layout;
    ScrollView scrollView;
    Firebase reference;
    DrawerLayout drawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawer = findViewById(R.id.drawer_layout);

        /* Set Firebase reference for uploading tasks. */
        Firebase.setAndroidContext(this);

        /* Customize toolbar on homepage. */
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Room: " + UserDetails.roomId);
        setSupportActionBar(toolbar);

        /* Button to create new task. */
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newTask = new Intent(MainActivity.this, CreateTask.class);
                startActivityForResult(newTask, REQUEST_NEW_TASK);
                overridePendingTransition(R.anim.enter_fromtop, R.anim.exit_fromtop);
            }
        });

        /* Defining navigation drawer. */
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);


        downloadTasks();


        /* Importing user profile picture from Firebase. */
        avatar = headerView.findViewById(R.id.avatarImageView);
        if (UserDetails.avatar != null) {   /* If avatar already exists, load from storage. */
            avatar = UserDetails.avatar;
        }
        /* else {    If no avatar exists, try to load from Firebase.
            Glide.with(MainActivity.this)
                    .load(UserDetails.avatarUrl)
                    .into(avatar);
        } */

        /* Defining layout views. */
        unameMain = headerView.findViewById(R.id.usernameTextView);
        customname = headerView.findViewById(R.id.customnameTextView);

        unameMain.setText(UserDetails.username);
        customname.setText(UserDetails.showName);

        layout = findViewById(R.id.layout1);

        scrollView = findViewById(R.id.scrollView);
        scrollView.fullScroll(View.FOCUS_DOWN);

    }

    /* Download task items from the database. */
    private void downloadTasks() {

        String url = "https://projectchat-bf300.firebaseio.com/rooms/" + UserDetails.roomId + "/tasks.json";
        final ProgressDialog pd = new ProgressDialog(MainActivity.this);
        pd.setMessage("Downloading tasks...");
        pd.show();

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (s.equals("null")) {
                    Toast.makeText(MainActivity.this, "No Tasks To Download.", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        JSONObject json = new JSONObject(s);

                        /* Loop through objects in the tasks.json folder. */
                        Iterator keys = json.keys();

                        while (keys.hasNext()) {
                            String key = keys.next().toString();
                            JSONObject obj = json.getJSONObject(key);

                            String completed = obj.getString("done");

                            /* Getting data from current object, if not already completed. */
                            if (completed.equals("0")) {
                                String area = obj.getString("area");
                                String task = obj.getString("task");
                                String time = obj.getString("time");

                                addNewTask(area, task, time, true);
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

        RequestQueue rQueue = Volley.newRequestQueue(MainActivity.this);
        rQueue.add(request);
    }

    /* Add new task item to the Room view. */
    private void addNewTask(final String task, final String area, final String timestamp, boolean download) {

        final TextView textArea = new TextView(MainActivity.this);
        final TextView textTask = new TextView(MainActivity.this);
        final TextView textTime = new TextView(MainActivity.this);
        final Button completedButton = new Button(MainActivity.this);
        final Button removeButton = new Button(MainActivity.this);

        completedButton.setText(completedText);
        removeButton.setText(removeText);

        textTask.setText(task);
        textTask.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        textArea.setText(area);
        textArea.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        textTime.setText(timestamp);
        textTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        lp1.gravity = Gravity.START;
        lp2.gravity = Gravity.CENTER;
        lp3.gravity = Gravity.END;

        textTask.setLayoutParams(lp1);
        textArea.setLayoutParams(lp1);
        textTime.setLayoutParams(lp2);

        completedButton.setLayoutParams(lp3);
        removeButton.setLayoutParams(lp3);

        completedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completedButton.setBackgroundColor(Color.GREEN);
            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.removeView(textTask);
                layout.removeView(textArea);
                layout.removeView(textTime);
                layout.removeView(completedButton);
                layout.removeView(removeButton);

                /* Remove task from list and set as 'done' in database. */
                setTaskCompleted(task, area, timestamp);
            }
        });

        layout.addView(textTask);
        layout.addView(textArea);
        layout.addView(textTime);
        layout.addView(completedButton);
        layout.addView(removeButton);

        scrollView.fullScroll(View.FOCUS_DOWN);

        if (!download) {
            /* Placing data in map before pushing to Firebase. */
            Map<String, String> map = new HashMap<>();
            map.put("user", UserDetails.username);
            map.put("task", task);
            map.put("area", area);
            map.put("time", timestamp);
            map.put("done", "0");

            String key = formatKey(task, area, timestamp);
            reference = new Firebase("https://projectchat-bf300.firebaseio.com/rooms/" + UserDetails.roomId + "/tasks");
            reference.child(key).setValue(map);
        }
    }

    /* Set task as completed in database. */
    private void setTaskCompleted(String task, String area, String timestamp) {
        String key = formatKey(task, area, timestamp);
        reference = new Firebase("https://projectchat-bf300.firebaseio.com/rooms/" + UserDetails.roomId + "/tasks");
        reference.child(key).child("done").setValue("1");
    }

    /* Format strings for custom Firebase key ID. */
    private String formatKey(String task, String area, String timestamp) {
        String t = task.replaceAll("[^a-zA-Z0-9]", "");
        String a = area.replaceAll("[^a-zA-Z0-9]", "");
        String ts = timestamp.replaceAll("[^a-zA-Z0-9]", "");
        return (t + "_" + a + "_" + ts);
    }

    /* Take new profile picture. */
    private void openCameraIntent() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(pictureIntent, REQUEST_CAPTURE_IMAGE);
        }
    }

    /* Get data from other activities. */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAPTURE_IMAGE) {     /* Receive data from camera and convert to ImageView. */
            if (resultCode == RESULT_OK) {
                if (data != null && data.getExtras() != null) {

                    Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                    RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);

                    roundedBitmapDrawable.setCornerRadius(100.0f);
                    roundedBitmapDrawable.setAntiAlias(true);
                    avatar.setImageDrawable(roundedBitmapDrawable);
                    UserDetails.avatar = avatar;

                    drawer.openDrawer(Gravity.START);
                }
            }
        } else if (requestCode == REQUEST_NEW_TASK) {   /* Receive data about newly created task. */
            if (resultCode == RESULT_OK) {
                if (data != null && data.getExtras() != null) {

                    String task = data.getStringExtra("taskVal");
                    String area = data.getStringExtra("areaVal");
                    String timestamp = data.getStringExtra("timestampVal");

                    addNewTask(task, area, timestamp, false);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        /* Handling navigation view item clicks. */
        int id = item.getItemId();

        if (id == R.id.nav_shop) {              /* Shopping list activity. */
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(MainActivity.this, Shop.class));
                }
            }, 250);


        } else if (id == R.id.nav_users) {      /* Chat room activity. */
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(MainActivity.this, Users.class));
                }
            }, 250);

        } else if (id == R.id.nav_camera) {     /* Open camera activity. */
            openCameraIntent();

        } else if (id == R.id.nav_rules) {      /* Read or write house rules. */
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(MainActivity.this, HouseRules.class));
                }
            }, 250);

        } else if (id == R.id.nav_rights) {     /* Read rights in WebView. */
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent openPage = new Intent(MainActivity.this, Webpage.class);
                    switch (UserDetails.language) {
                        case 0:
                            openPage.putExtra("url", urlEnglish);
                            startActivity(openPage);
                            break;
                        case 1:
                            openPage.putExtra("url", urlNorsk);
                            startActivity(openPage);
                            break;
                        default:
                            break;
                    }
                }
            }, 250);

        } else if (id == R.id.nav_settings) {   /* Open Settings activity. */
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                }
            }, 250);

        } else if (id == R.id.nav_logout) {     /* Return to Login screen. */
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(MainActivity.this, Login.class));
                    finish();
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                }
            }, 250);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
