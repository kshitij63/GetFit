package com.example.user.getfit;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by user on 7/11/2017.
 */

public class GymHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION=1;
    private static final String DATABASE_NAME="gymtable.db";
    String createTable;


    public GymHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable="create table "+GymContract.SavedGyms.TABLE_NAME+"("
                +GymContract.SavedGyms.GYM_ID+" integer primary key autoincrement,"
                +GymContract.SavedGyms.NAME+" text,"
                +GymContract.SavedGyms.RATING+" text,"
                +GymContract.SavedGyms.STATUS+" text,"
                +GymContract.SavedGyms.VICINITY+ " text);";
        db.execSQL(createTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
