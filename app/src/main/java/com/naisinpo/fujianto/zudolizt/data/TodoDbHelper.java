package com.naisinpo.fujianto.zudolizt.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.naisinpo.fujianto.zudolizt.data.TodoContract.TodoEntry;

/**
 * Created by fujianto on 18/11/14.
 */
public class TodoDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "todotable.db";
    private static final int DATABASE_VERSION = 1;


    public TodoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Database creation SQL statement
         final String SQL_CREATE_TODO_TABLE = "CREATE TABLE "
                + TodoEntry.TABLE_NAME
                + "("
                + TodoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TodoEntry.COLUMN_CATEGORY + " TEXT NOT NULL, "
                + TodoEntry.COLUMN_SUMMARY + " TEXT NOT NULL,"
                + TodoEntry.COLUMN_DESCRIPTION
                + " TEXT NOT NULL"
                + ");";

         db.execSQL(SQL_CREATE_TODO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Replace old Table with new One.
        db.execSQL(" DROP TABLE IF EXISTS "+TodoEntry.TABLE_NAME);
        onCreate(db);
    }
}
