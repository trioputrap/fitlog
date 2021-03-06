package com.example.trio.fitlog.model;


import android.provider.BaseColumns;

public class ActivityContract implements BaseColumns {
    public static final String TABLE_NAME = "activities";
    public static final String COLUMN_SERVER_ID = "server_id";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_TYPE_ID = "type_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DISTANCE = "distance";
    public static final String COLUMN_HOUR = "hour";
    public static final String COLUMN_MINUTE = "minute";
    public static final String COLUMN_PACE = "pace";
    public static final String COLUMN_DATETIME = "datetime";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_FLAG_INSERT = "flag_insert";
    public static final String COLUMN_FLAG_UPDATE = "flag_update";
    public static final String COLUMN_FLAG_DELETE = "flag_delete";

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ActivityContract.TABLE_NAME + " (" +
                    ActivityContract._ID + " INTEGER PRIMARY KEY," +
                    ActivityContract.COLUMN_SERVER_ID + " INTEGER," +
                    ActivityContract.COLUMN_USER_ID + " INTEGER," +
                    ActivityContract.COLUMN_TYPE_ID + " TEXT," +
                    ActivityContract.COLUMN_TITLE + " TEXT," +
                    ActivityContract.COLUMN_DISTANCE + " INTEGER," +
                    ActivityContract.COLUMN_HOUR + " INTEGER," +
                    ActivityContract.COLUMN_MINUTE + " INTEGER," +
                    ActivityContract.COLUMN_PACE + " TEXT," +
                    ActivityContract.COLUMN_DATETIME + " TEXT," +
                    ActivityContract.COLUMN_DESCRIPTION + " TEXT," +
                    ActivityContract.COLUMN_FLAG_INSERT + " INTEGER," +
                    ActivityContract.COLUMN_FLAG_UPDATE + " INTEGER," +
                    ActivityContract.COLUMN_FLAG_DELETE + " INTEGER)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ActivityContract.TABLE_NAME;
}
