package edu.cvtc.android.jokeview;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Class that hooks up to the JokeContentProvider for initialization and maintenance.
 * Uses JokeTable for assistance.
 *
 * Created by gandrews7 on 12/6/16.
 */
public class JokeDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "jokes.db";

    public static final int DATABASE_VERSION = 1;

    public JokeDatabaseHelper(final Context context, final String name, final CursorFactory factory, final int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        JokeTable.onCreate(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        JokeTable.onUpgrade(sqLiteDatabase, oldVersion, newVersion);
    }
}
