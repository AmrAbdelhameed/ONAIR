package com.example.amr.onair.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.amr.onair.R;
import com.example.amr.onair.fragments.AboutUsFragment;
import com.example.amr.onair.fragments.HomeClientOrStaffFragment;
import com.example.amr.onair.fragments.ProfileFragment;
import com.example.amr.onair.models.Client;
import com.example.amr.onair.models.Staff;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class MainClientOrStaffActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Toolbar toolbar;
    android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
    String sampleObject, Name;
    boolean staffCheck;
    Staff staff;
    Client client;
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_client_or_staff);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("About us");
        setSupportActionBar(toolbar);
        fragmentManager.beginTransaction().replace(R.id.content, new AboutUsFragment()).commit();

        gson = new Gson();
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferencesName", Context.MODE_PRIVATE);
        staffCheck = sharedPreferences.getBoolean("staffCheck", false);
        sampleObject = sharedPreferences.getString("sampleObject", "null");

        if (staffCheck) {
            Type type = new TypeToken<Staff>() {
            }.getType();
            staff = gson.fromJson(sampleObject, type);

            Name = staff.getName();
        } else {
            Type type = new TypeToken<Client>() {
            }.getType();
            client = gson.fromJson(sampleObject, type);

            Name = client.getName();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.edit_members, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            toolbar.setTitle("Home");
            Bundle sentBundle = new Bundle();
            sentBundle.putBoolean("staff", staffCheck);
            if (staffCheck)
                sentBundle.putSerializable("sampleObject", staff);
            else
                sentBundle.putSerializable("sampleObject", client);
            HomeClientOrStaffFragment homeClientOrStaffFragment = new HomeClientOrStaffFragment();
            homeClientOrStaffFragment.setArguments(sentBundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.content, homeClientOrStaffFragment, "").commit();
//            fragmentManager.beginTransaction().replace(R.id.content, new HomeClientOrStaffFragment()).commit();
        } else if (id == R.id.nav_profile) {
            toolbar.setTitle(Name + "'s profile");
//            fragmentManager.beginTransaction().replace(R.id.content, new ProfileFragment()).commit();
            Bundle sentBundle = new Bundle();
            sentBundle.putBoolean("staff", staffCheck);
            if (staffCheck)
                sentBundle.putSerializable("sampleObject", staff);
            else
                sentBundle.putSerializable("sampleObject", client);
            ProfileFragment mDetailsFragment = new ProfileFragment();
            mDetailsFragment.setArguments(sentBundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.content, mDetailsFragment, "").commit();
        } else if (id == R.id.nav_about) {
            toolbar.setTitle("About us");
            fragmentManager.beginTransaction().replace(R.id.content, new AboutUsFragment()).commit();
        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainClientOrStaffActivity.this, LoginActivity.class));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
