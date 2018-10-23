package ru.pavelkr.priorauniver;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by pavel on 20.04.2017.
 */

public class SubscribersTableHelper extends SQLiteOpenHelper {
    public static final String SUBSCRIBERS_TABLE_NAME = "Subscribers";
    public static final int DATABASE_VERSION = 1;
    public static final String COLUMN_CHATID = "chat_id";
    public static final String COLUMN_FNAME = "first_name";
    public static final String COLUMN_LNAME = "last_name";
    public static final String COLUMN_IS_PASSENGER = "is_passenger";


    private static final String SUBSCRIBERS_TABLE_CREATE =
            "CREATE TABLE " + SUBSCRIBERS_TABLE_NAME + " (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CHATID + " INTEGER, " +
                    COLUMN_FNAME + " TEXT, " +
                    COLUMN_LNAME + " TEXT, " +
                    COLUMN_IS_PASSENGER + " INTEGER DEFAULT 0);";
    SubscribersTableHelper(Context context) {
        super(context, SUBSCRIBERS_TABLE_NAME , null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SUBSCRIBERS_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
