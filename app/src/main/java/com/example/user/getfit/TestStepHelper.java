package com.example.user.getfit;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by user on 7/9/2017.
 */

public class TestStepHelper extends SQLiteOpenHelper {
    public TestStepHelper(Context context) {
        super(context, "steps.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table step(_id integer primary key autoincrement ,num_steps text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
