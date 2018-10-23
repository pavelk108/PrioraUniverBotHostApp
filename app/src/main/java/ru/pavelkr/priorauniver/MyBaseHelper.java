package ru.pavelkr.priorauniver;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObservable;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class MyBaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String MYBASE_TABLE_NAME = "Logs";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_MESS = "Message";
    public static final String COLUMN_FLAG = "flag";
    private static final String MYBASE_TABLE_CREATE =
            "CREATE TABLE " + MYBASE_TABLE_NAME + " (" +
                            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            COLUMN_MESS + " TEXT, " +
                            COLUMN_FLAG + " INTEGER DEFAULT 0);";
    MyBaseHelper(Context context) {
        super(context, MYBASE_TABLE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MYBASE_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static void mark_row_read(Context context, int row_id) {
        MyBaseHelper helper = new MyBaseHelper(context);
        ContentValues values = new ContentValues();
        values.put(COLUMN_FLAG, 1);
        helper.getWritableDatabase().update(MYBASE_TABLE_NAME, values, COLUMN_ID + " = " + row_id, null);
        helper.close();
    }
}
