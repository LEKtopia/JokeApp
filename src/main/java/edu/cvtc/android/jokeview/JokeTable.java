package edu.cvtc.android.jokeview;

import android.database.sqlite.SQLiteDatabase;

/**
 * Class that provides helpful database table accessor variables,
 * and manages basic required database functionality.
 *
 * Created by gandrews7 on 12/6/16.
 */
public class JokeTable {

    public static final String TABLE_NAME = "joke_table";

    /**
     * Column names and IDs we can use for database access.
     */
    public static final String KEY_ID = "_id"; // The underscore is useful when using CursorAdapter
    public static final int INDEX_ID = 0;

    public static final String KEY_TEXT = "text";
    public static final int INDEX_TEXT = 1;

    public static final String KEY_RATING = "rating";
    public static final int INDEX_RATING = 2;

    /**
     * SQL statement to use when creating the database.
     */
    public static final String DATABASE_CREATE = "create table " + TABLE_NAME + " (" +
            KEY_ID + " integer primary key autoincrement, " +
            KEY_TEXT + " text not null, " +
            KEY_RATING + " integer not null);";

    /**
     * SQL statement used for upgrading the database.
     */
    public static final String DATABASE_DROP = "drop table if exists " + TABLE_NAME;

    /**
     * Initializes the database.
     *
     * @param database
     *          The database to initialize.
     */
    public static void onCreate(final SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    /**
     * Upgrades the database to a new version.
     *
     * @param database
     *          The database to upgrade.
     * @param oldVersion
     *          The old version of the database.
     * @param newVersion
     *          The new version of the database.
     */
    public static void onUpgrade(final SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL(DATABASE_DROP);
        onCreate(database);
    }

}
