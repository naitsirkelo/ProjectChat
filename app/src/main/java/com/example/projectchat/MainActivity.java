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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_NEW_TASK = 1, REQUEST_CAPTURE_IMAGE = 2;
    //private static final String path = "https://firebasestorage.googleapis.com/v0/b/projectchat-bf300.appspot.com/o";
    private static final String
            path = "gs://projectchat-bf300.appspot.com/",
            urlEnglish = "http://www.nyinorge.no/en/Familiegjenforening/New-in-Norway/Housing/Renting-a-houseapartment/Your-rights-as-a-tenant/",
            urlNorsk = "http://www.nyinorge.no/no/Familiegjenforening/Ny-i-Norge/Bolig/A-leie-bolig/Rettigheter-som-leietaker/";
    TextView unameMain, customname, areaTV, taskTV, tsTV;
    ImageView avatar;
    private ListView list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Room: " + UserDetails.roomId);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newTask = new Intent(MainActivity.this, CreateTask.class);
                startActivityForResult(newTask, REQUEST_NEW_TASK);
                overridePendingTransition(R.anim.enter_fromtop, R.anim.exit_fromtop);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);


        avatar = headerView.findViewById(R.id.avatarImageView);
        if (UserDetails.avatar != null) {   /* If avatar already exists, load from storage. */
            avatar = UserDetails.avatar;
        }
        else {    /* If no avatar exists, try to load from Firebase.
            Glide.with(MainActivity.this)
                    .load(UserDetails.avatarUrl)
                    .into(avatar);
                    */
        }


        unameMain = headerView.findViewById(R.id.usernameTextView);
        customname = headerView.findViewById(R.id.customnameTextView);

        unameMain.setText(UserDetails.username);
        customname.setText(UserDetails.showName);

        areaTV = findViewById(R.id.areaTV);
        taskTV = findViewById(R.id.taskTV);
        tsTV = findViewById(R.id.tsTV);

    }

    private void addNewTask(String a, String t, String ts) {
        areaTV.setText(a);
        taskTV.setText(t);
        tsTV.setText(ts);
    }

    private void openCameraIntent() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(pictureIntent, REQUEST_CAPTURE_IMAGE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAPTURE_IMAGE) {
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
        } else if (requestCode == REQUEST_NEW_TASK) {
            if (resultCode == RESULT_OK) {
                if (data != null && data.getExtras() != null) {
                    String area = data.getStringExtra("areaVal");
                    String task = data.getStringExtra("taskVal");
                    String timestamp = data.getStringExtra("timestampVal");

                    addNewTask(area, task, timestamp);
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
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_shop) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(MainActivity.this, Shop.class));
                }
            }, 250);

        } else if (id == R.id.nav_users) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(MainActivity.this, Users.class));
                }
            }, 250);

        } else if (id == R.id.nav_camera) {
            openCameraIntent();

        } else if (id == R.id.nav_rights) {
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
                        default: break;
                    }
                }
            }, 250);

        } else if (id == R.id.nav_settings) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                }
            }, 250);

        } else if (id == R.id.nav_logout) {
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
