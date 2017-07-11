package com.example.user.getfit;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by user on 7/11/2017.
 */

public class GymContract {

    public static final String URL_STRING="content://com.example.user.getfit";
    public static Uri BASE_URI=Uri.parse(URL_STRING);
    public static final String AUTHORITY="com.example.user.getfit";
    public static final String GYM_REQ="gyms";

    public static class SavedGyms implements BaseColumns{
        public static final String TABLE_NAME="gyms";
        public static final Uri CONTENT_URI=BASE_URI.buildUpon().appendPath(GYM_REQ).build();
        public static final String GYM_ID=BaseColumns._ID;
        public static final String NAME="name";
        public static final String RATING="rating";
        public static final String VICINITY="vicinity";
        public static final String STATUS="status";
    }
}
