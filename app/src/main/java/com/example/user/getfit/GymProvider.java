package com.example.user.getfit;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by user on 7/11/2017.
 */

public class GymProvider extends ContentProvider {
    GymHelper helper;
    private static final int GYM = 100;
    private static final int GYM_WITH_ID = 101;

    public UriMatcher getUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(GymContract.AUTHORITY, GymContract.SavedGyms.TABLE_NAME, GYM);
        uriMatcher.addURI(GymContract.AUTHORITY, GymContract.SavedGyms.TABLE_NAME + "/#", GYM_WITH_ID);
        return uriMatcher;

    }


    @Override
    public boolean onCreate() {
        helper = new GymHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = helper.getReadableDatabase();
        int vari = getUriMatcher().match(uri);
        Cursor cr = null;
        if (vari == GYM) {
            cr = db.query(GymContract.SavedGyms.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder);
        } else {
            Log.e("Gym Provider", "Invalid Request");
        }
        cr.setNotificationUri(getContext().getContentResolver(), uri);


        return cr;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = helper.getWritableDatabase();
        Uri mUri = null;
        if (getUriMatcher().match(uri) == GYM) {
            long id = db.insert(GymContract.SavedGyms.TABLE_NAME, null, values);
            if (id > 0)
                mUri = ContentUris.withAppendedId(GymContract.SavedGyms.CONTENT_URI, id);
            else {
                Toast.makeText(getContext(), "Error Add Failed", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("Gym Provider", "Invalid Entry");
        }
        getContext().getContentResolver().notifyChange(uri, null);


        return mUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
