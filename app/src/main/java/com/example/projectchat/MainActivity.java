package com.example.projectchat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
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

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_NEW_TASK = 1, REQUEST_CAPTURE_IMAGE = 2;
    private static final String
            urlEnglish = "http://www.nyinorge.no/en/Familiegjenforening/New-in-Norway/Housing/Renting-a-houseapartment/Your-rights-as-a-tenant/",
            urlNorsk = "http://www.nyinorge.no/no/Familiegjenforening/Ny-i-Norge/Bolig/A-leie-bolig/Rettigheter-som-leietaker/";
    TextView unameMain, customname;
    ImageView avatar;
    LinearLayout layout;
    ScrollView scrollView;
    Firebase reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Set Firebase reference for uploading tasks. */
        Firebase.setAndroidContext(this);
        reference = new Firebase("https://projectchat-bf300.firebaseio.com/rooms/" + UserDetails.roomId + "/tasks");

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


        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Toast.makeText(MainActivity.this, "Task uploaded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    /* Add new task item to the Room view. */
    private void addNewTask(String task, String area, String timestamp) {

        TextView textArea = new TextView(MainActivity.this);
        TextView textTask = new TextView(MainActivity.this);
        TextView textTime = new TextView(MainActivity.this);

        Button completedButton = new Button(MainActivity.this);
        completedButton.setText("Done");

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
        lp3.gravity = Gravity.RIGHT;

        textTask.setLayoutParams(lp1);
        textArea.setLayoutParams(lp1);
        textTime.setLayoutParams(lp2);

        completedButton.setLayoutParams(lp3);

        layout.addView(textTask);
        layout.addView(textArea);
        layout.addView(textTime);
        layout.addView(completedButton);

        scrollView.fullScroll(View.FOCUS_DOWN);


        Map<String, String> map = new HashMap<>();

        map.put("user", UserDetails.username);
        map.put("task", task);
        map.put("area", area);
        map.put("time", timestamp);

        reference.push().setValue(map);

    }

    /* Take new profile picture. */
    private void openCameraIntent() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(pictureIntent, REQUEST_CAPTURE_IMAGE);
        }
    }

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


                }
            }
        } else if (requestCode == REQUEST_NEW_TASK) {   /* Receive data about newly created task. */
            if (resultCode == RESULT_OK) {
                if (data != null && data.getExtras() != null) {

                    String task = data.getStringExtra("taskVal");
                    String area = data.getStringExtra("areaVal");
                    String timestamp = data.getStringExtra("timestampVal");

                    addNewTask(task, area, timestamp);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
    public boolean onNavigationItemSelected(MenuItem item) {
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
