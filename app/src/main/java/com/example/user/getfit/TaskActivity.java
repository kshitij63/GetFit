package com.example.user.getfit;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.squareup.picasso.Picasso;

public class TaskActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener ,ProgressFragment.getTonext{
private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS=100;
 PlaceAutocompleteFragment autocompleteFragment;
    android.app.FragmentTransaction transaction;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
         transaction=getFragmentManager().beginTransaction();
         autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.autocomplete);
        transaction.hide(autocompleteFragment);
        transaction.commit();


        Bundle bundle=getIntent().getExtras();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

     navigationView.setNavigationItemSelectedListener(this);
        View view= navigationView.getHeaderView(0);
        ImageView imageView=(ImageView) view.findViewById(R.id.photo);

        //Layout layout=(Layout) findViewById(R.layout.nav_header_task);
        if(bundle!=null) {
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
 ExrecisePartFragment fragment=new ExrecisePartFragment();
           FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container,fragment);
            transaction.commit();

            // Handle the camera action
        } else if (id == R.id.get_macros) {

        } else if (id == R.id.nearby_gym) {
    GymFragment fragment =new GymFragment();
            FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container,fragment);
            transaction.commit();
        }
 else if (id == R.id.track_progress) {
            android.app.FragmentTransaction mtra=getFragmentManager().beginTransaction();
            mtra.hide(autocompleteFragment);
            mtra.commit();
            ProgressFragment fragment=new ProgressFragment();
            FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container,fragment);
            transaction.commit();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void YourCalorie(String calorie) {
        ProgressDetailsFragment fragment=new ProgressDetailsFragment();
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        Bundle bundle=new Bundle();
        bundle.putString("goal",calorie);
        fragment.setArguments(bundle);
        transaction.replace(R.id.container,fragment);
        transaction.commit();

    }
}
