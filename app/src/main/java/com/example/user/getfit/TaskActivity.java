package com.example.user.getfit;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.squareup.picasso.Picasso;

public class TaskActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ProgressFragment.getTonext, ExrecisePartFragment.GetNameListener {
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 100;
    PlaceAutocompleteFragment autocompleteFragment;
    android.app.FragmentTransaction transaction;
    SharedPreferences preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        preferences = getSharedPreferences("NotSET", MODE_PRIVATE);
        transaction = getFragmentManager().beginTransaction();
        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.autocomplete);
        transaction.hide(autocompleteFragment);
        transaction.commit();


        Bundle bundle = getIntent().getExtras();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);
        View view = navigationView.getHeaderView(0);
        ImageView imageView = (ImageView) view.findViewById(R.id.photo);

        //Layout layout=(Layout) findViewById(R.layout.nav_header_task);
        if (bundle != null) {
            Picasso.with(this).load(bundle.getString("photo")).into(imageView);
            Log.e("KU", bundle.getString("name"));
            TextView textView = (TextView) view.findViewById(R.id.email);
            textView.setText(bundle.getString("email"));
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.task, menu);
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

        if (id == R.id.Watch_Exercises) {
            android.app.FragmentTransaction mtra = getFragmentManager().beginTransaction();
            mtra.hide(autocompleteFragment);
            mtra.commit();
            ExrecisePartFragment fragment = new ExrecisePartFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, fragment);
            transaction.commit();

            // Handle the camera action
        } else if (id == R.id.get_macros) {
            android.app.FragmentTransaction mtra = getFragmentManager().beginTransaction();
            mtra.hide(autocompleteFragment);
            mtra.commit();
            Intent intent = new Intent(this, FavouriteActivity.class);
            startActivity(intent);
        } else if (id == R.id.nearby_gym) {

            check_permission();


        } else if (id == R.id.track_progress) {
            //(preferences.getInt("goal",0)==0) {
            android.app.FragmentTransaction mtra = getFragmentManager().beginTransaction();
            mtra.hide(autocompleteFragment);
            mtra.commit();
            ProgressFragment fragment = new ProgressFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, fragment);
            transaction.commit();
            //}
            //else {
            //  Intent intent=new Intent(TaskActivity.this,TestActivity.class);
            // startActivity(intent);
            //}


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void YourCalorie(String calorie) {
        if (calorie.equals("")) {
            Toast.makeText(this, "Calorie cannot be empty", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(TaskActivity.this, TestActivity.class);
            intent.putExtra("goal", calorie);
            startActivity(intent);
        }
    }

    @Override
    public void getName(String name) {

    }

    private void check_permission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        } else {
            GymFragment fragment = new GymFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, fragment);
            transaction.commit();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    GymFragment fragment = new GymFragment();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, fragment);
                    transaction.commit();

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }

            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
