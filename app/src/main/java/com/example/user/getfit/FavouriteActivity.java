package com.example.user.getfit;

import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class FavouriteActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
ArrayList<Gym> gymArrayList;
    RecyclerView recyclerView1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        gymArrayList=new ArrayList<>();
        recyclerView1=(RecyclerView) findViewById(R.id.favourite_recycle);
getSupportLoaderManager().initLoader(1,null,this);
    }


    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = new CursorLoader(this,GymContract.SavedGyms.CONTENT_URI, null,
                null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
if(data.moveToFirst()){
    do{
        String name=data.getString(data.getColumnIndex(GymContract.SavedGyms.NAME));
        String rating=data.getString(data.getColumnIndex(GymContract.SavedGyms.RATING));
        String vicinity=data.getString(data.getColumnIndex(GymContract.SavedGyms.VICINITY));
        String status=data.getString(data.getColumnIndex(GymContract.SavedGyms.STATUS));
        Gym gym=new Gym("N/A",rating,name,vicinity,status);
        gymArrayList.add(gym);



    }
    while (data.moveToNext());
}
recyclerView1.setLayoutManager(new LinearLayoutManager(this));
        recyclerView1.setAdapter(new GymAdapter(this,gymArrayList,1009));

    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
