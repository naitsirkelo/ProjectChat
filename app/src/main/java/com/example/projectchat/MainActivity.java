package com.example.projectchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.ImageButton;
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
    private static final String notCompletedTextEng = "Done?", notCompletedTextNor = "Gjort?";
    TextView unameMain, customname, infoTextMain;
    ImageView avatar;
    LinearLayout layout;
    ScrollView scrollView;
    Firebase reference;
    DrawerLayout drawer;
    Toolbar toolbar;
    Menu menu;
    ImageButton eventBox;
    int totalTasks, boxPaddingTop = 25, boxPaddingBot = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawer = findViewById(R.id.drawer_layout);

        /* Set Firebase reference for uploading tasks. */
        Firebase.setAndroidContext(this);

        /* Customize toolbar on homepage. */
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(Utility.languageSwitch("Room: ", "Rom: ") + UserDetails.roomId);
        setSupportActionBar(toolbar);

        /* Button to create new task. */
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newTask = new Intent(MainActivity.this, CreateTask.class);
                startActivityForResult(newTask, REQUEST_NEW_TASK);
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


        /* Update task UI. */
        downloadTasks();


        /* Setting current avatar to UserDetails. */
        avatar = headerView.findViewById(R.id.avatarImageView);
        if (UserDetails.avatar != null) {   /* If avatar already exists, load from storage. */
            avatar = UserDetails.avatar;
        }

        eventBox = findViewById(R.id.eventBox);
        eventBoxUI();

        eventBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Events.class));
            }
        });

        /* Defining layout views. */
        unameMain = headerView.findViewById(R.id.usernameTextView);
        customname = headerView.findViewById(R.id.customnameTextView);
        unameMain.setText(UserDetails.username);
        customname.setText(UserDetails.showName);
        infoTextMain = findViewById(R.id.infoTextMain);

        scrollView = findViewById(R.id.scrollView);
        scrollView.fullScroll(View.FOCUS_DOWN);
        layout = findViewById(R.id.layout1);
        menu = navigationView.getMenu();

        setLanguage(UserDetails.language);
    }

    /* Update language and open navigation drawer when returning. */
    @Override
    protected void onRestart() {
        super.onRestart();
        setLanguage(UserDetails.language);
        eventBoxUI();
        drawer.openDrawer(GravityCompat.START);
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

                            String removed = obj.getString("hidden");

                            /* Getting data from current object, if not already completed. */
                            if (removed.equals("0")) {
                                String task = obj.getString("task");
                                String area = obj.getString("area");
                                String time = obj.getString("time");
                                String by = obj.getString("doneBy");

                                boolean completed = false;

                                /* If task is marked as completed, it will be marked green in UI. */
                                if (obj.getString("completed").equals("1")) {
                                    completed = true;
                                }
                                addNewTask(task, area, time, by, true, completed);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            finish();
            startActivity(getIntent());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /* Add new task item to the Room view. */
    private void addNewTask(final String task, final String area, final String timestamp,
                            final String doneBy, boolean download, boolean done) {

        final TextView textFull = new TextView(MainActivity.this);
        final Button completedButton = new Button(MainActivity.this);
        final Button removeButton = new Button(MainActivity.this);

        textFull.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(400, 150);

        lp1.gravity = Gravity.START;
        lp2.gravity = Gravity.CENTER;
        lp3.gravity = Gravity.CENTER;

        textFull.setLayoutParams(lp1);
        completedButton.setLayoutParams(lp2);
        removeButton.setLayoutParams(lp3);

        if (done) {
            completedButton.setBackgroundColor(getResources().getColor(R.color.check_green));
            String t = "OK!  -  " + doneBy;
            completedButton.setText(t);
            completedButton.setEnabled(false);
            completedButton.setTextColor(getResources().getColor(R.color.text_black));
        } else {
            completedButton.setText(Utility.languageSwitch(notCompletedTextEng, notCompletedTextNor));
        }

        completedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completedButton.setBackgroundColor(getResources().getColor(R.color.check_green));
                String t = "OK! - " + UserDetails.showName;
                completedButton.setText(t);
                setTaskCompleted(task, area, timestamp);
            }
        });

        removeButton.setText(Utility.languageSwitch(Utility.removeTextEng, Utility.removeTextNor));
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.removeView(textFull);
                layout.removeView(completedButton);
                layout.removeView(removeButton);

                /* Remove task from list and set as 'done' in database. */
                totalTasks--;
                showInfo(totalTasks);
                removeTask(task, area, timestamp);
            }
        });

        /* Adding custom string and buttons to list as a new layout. */
        String full = task + "   -   " + area + ".\nAdded: " + timestamp;
        textFull.setText(full);
        textFull.setPadding(0, boxPaddingTop, 0, boxPaddingBot);

        layout.addView(textFull);
        layout.addView(completedButton);
        layout.addView(removeButton);

        /* Counting tasks and updating TextView. */
        totalTasks++;
        showInfo(totalTasks);

        scrollView.fullScroll(View.FOCUS_DOWN);

        /* If added task was not downloaded from Firebase. */
        if (!download) {
            /* Placing data in map before pushing to Firebase. */
            Map<String, String> map = new HashMap<>();
            map.put("user", UserDetails.username);
            map.put("task", task);
            map.put("area", area);
            map.put("time", timestamp);
            map.put("hidden", "0");
            map.put("completed", "0");
            map.put("doneBy", "0");

            String key = formatKey(task, area, timestamp);
            reference = new Firebase("https://projectchat-bf300.firebaseio.com/rooms/" + UserDetails.roomId + "/tasks");
            reference.child(key).setValue(map);
        }
    }

    /* Set task as removed in database. */
    private void removeTask(String task, String area, String timestamp) {
        String key = formatKey(task, area, timestamp);
        reference = new Firebase("https://projectchat-bf300.firebaseio.com/rooms/" + UserDetails.roomId + "/tasks");
        reference.child(key).child("hidden").setValue("1");
    }

    /* Set task as completed in database. */
    private void setTaskCompleted(String task, String area, String timestamp) {
        String key = formatKey(task, area, timestamp);
        reference = new Firebase("https://projectchat-bf300.firebaseio.com/rooms/" + UserDetails.roomId + "/tasks");

        reference.child(key).child("completed").setValue(1);
        reference.child(key).child("doneBy").setValue(UserDetails.showName);
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
        if (requestCode == REQUEST_CAPTURE_IMAGE && resultCode == RESULT_OK) {     /* Receive data from camera and convert to ImageView. */
            if (data != null && data.getExtras() != null) {

                /* Converting bitmap from camera to an ImageView. */
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);

                roundedBitmapDrawable.setCornerRadius(100.0f);
                roundedBitmapDrawable.setAntiAlias(true);
                avatar.setImageDrawable(roundedBitmapDrawable);
                UserDetails.avatar = avatar;

                drawer.openDrawer(Gravity.START);
            }
        } else if (requestCode == REQUEST_NEW_TASK && resultCode == RESULT_OK) {   /* Receive data about newly created task. */
            if (data != null && data.getExtras() != null) {

                String task = data.getStringExtra("taskVal");
                String area = data.getStringExtra("areaVal");
                String timestamp = data.getStringExtra("timestampVal");

                addNewTask(task, area, timestamp, "", false, false);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            moveTaskToBack(true);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        /* Handling navigation view item clicks. */
        int id = item.getItemId();

        /* Delaying opening of activities to allow the navigation drawer to close first. */

        if (id == R.id.nav_users) {      /* Chat room activity. */
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(MainActivity.this, Users.class));
                }
            }, 250);

        } else if (id == R.id.nav_shop) {              /* Shopping list activity. */
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(MainActivity.this, Shop.class));
                }
            }, 250);

        } else if (id == R.id.nav_events) {      /* Events activity. */
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(MainActivity.this, Events.class));
                }
            }, 250);

        } else if (id == R.id.nav_rules) {      /* Read or write house rules. */
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(MainActivity.this, HouseRules.class));
                }
            }, 250);

        } else if (id == R.id.nav_camera) {     /* Open camera activity. */
            openCameraIntent();

        } else if (id == R.id.nav_owner) {      /* Store contact info about landlord. */
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(MainActivity.this, OwnerInfo.class));
                }
            }, 250);

        } else if (id == R.id.nav_rights) {     /* Read rights in WebView. */
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent openPage = new Intent(MainActivity.this, Webpage.class);
                    /* Open English or Norwegian version depending on setting. */
                    openPage.putExtra("url", Utility.languageSwitch(Utility.urlEnglish, Utility.urlNorsk));
                }
            }, 250);

        } else if (id == R.id.nav_preferences) {   /* Open Preferences activity. */
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(MainActivity.this, PreferencesCustom.class));
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

    private void showInfo(int n) {
        if (n > 0) {
            infoTextMain.setVisibility(View.GONE);
        } else {
            infoTextMain.setVisibility(View.VISIBLE);
        }
    }

    /* Show or hide popup in Main Activity. */
    private void eventBoxUI() {
        String url = "https://projectchat-bf300.firebaseio.com/rooms/" + UserDetails.roomId + "/events.json";
        eventBox.setVisibility(View.GONE);

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject json = new JSONObject(s);
                    Iterator<?> iterator = json.keys();

                    /* Looking for events other members have made. */
                    while (iterator.hasNext()) {
                        String key = (String) iterator.next();
                        JSONObject obj = json.getJSONObject(key);

                        String user = obj.getString("user");
                        String removed = obj.getString("hidden");

                        /* If there exists an event by another user in the room, show popup. */
                        if (!user.equals(UserDetails.showName) && removed.equals("0")) {
                            eventBox.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
            }
        });
        RequestQueue rQueue = Volley.newRequestQueue(MainActivity.this);
        rQueue.add(request);
    }

    /* Update text boxes based on user settings. */
    private void setLanguage(String l) {
        drawerLanguage(UserDetails.language, menu.findItem(R.id.nav_shop), "Shop", "Handle Varer");
        drawerLanguage(UserDetails.language, menu.findItem(R.id.nav_events), "Events", "Arrangementer");
        drawerLanguage(UserDetails.language, menu.findItem(R.id.nav_rules), "House Rules", "Husregler");
        drawerLanguage(UserDetails.language, menu.findItem(R.id.nav_camera), "New Avatar", "Nytt Profilbilde");
        drawerLanguage(UserDetails.language, menu.findItem(R.id.nav_owner), "Landlord Info", "Huseiers info");
        drawerLanguage(UserDetails.language, menu.findItem(R.id.nav_rights), "Rights As Tenant", "Leieboers rettigheter");
        drawerLanguage(UserDetails.language, menu.findItem(R.id.nav_preferences), "Settings", "Innstillinger");
        drawerLanguage(UserDetails.language, menu.findItem(R.id.nav_logout), "Logout", "Logg ut");

        if (l.equals("")) {
            l = "English";
        }
        switch (l) {
            case "English":
                infoTextMain.setText(R.string.content_main_info);
                break;
            case "Norsk":
                infoTextMain.setText(R.string.content_main_info_1);
                break;
            default:
                break;
        }
    }

    private void drawerLanguage(String lang, MenuItem item, String eng, String nor) {
        switch (lang) {
            case "English":
                item.setTitle(eng);
                break;
            case "Norsk":
                item.setTitle(nor);
                break;
            default:
                break;
        }
    }
}
