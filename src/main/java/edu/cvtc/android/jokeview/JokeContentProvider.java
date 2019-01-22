package edu.cvtc.android.jokeview;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Class provides content from JokeDatabaseHelper to the application.
 * Provides joke information to a ListView through a CursorAdapter.
 * The databaseHelper stores jokes in a two-dimensional table, where
 * each row is a Joke and each column is a property of a Joke (id, text, rating).
 *
 * Note that CursorLoaders require a ContentProvider, which is why this application
 * wraps a SQLite databaseHelper into a content provider instead of managing
 * the databaseHelper transactions manually.
 *
 * Created by gandrews7 on 12/6/16.
 */
public class JokeContentProvider extends ContentProvider {

    private JokeDatabaseHelper databaseHelper;

    /**
     * ContentProvider URI constants.
     */
    private static final String AUTHORITY = "edu.cvtc.android.jokeview.provider";
    private static final String BASE_PATH = "joke_table";

    /**
     * This ContentProvider's content location. Used by our code
     * to interact with this provider.
     */
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    /**
     * Values for the UriMatcher.
     */
    private static final int UPDATE = 1;
    private static final int QUERY = 2;

    /**
     * UriMatcher we can use to match content URIs with possible
     * expected content URI formats to take specific actions in this provider.
     */
    private static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        matcher.addURI(AUTHORITY, BASE_PATH + "/joke/#", UPDATE);
        matcher.addURI(AUTHORITY, BASE_PATH + "/filter/#", QUERY);
    }


    /**
     * Initializes our databaseHelper.
     */
    @Override
    public boolean onCreate() {
        databaseHelper = new JokeDatabaseHelper(getContext(),
                JokeDatabaseHelper.DATABASE_NAME, null,
                JokeDatabaseHelper.DATABASE_VERSION);
        return false;
    }

    /**
     * Fetches rows from the joke table given a specific Uri that contains a filter,
     * returns a list of jokes from the joke table matching that filter
     * in the form of a Cursor object.
     *
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        final SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        checkColumns(projection);

        queryBuilder.setTables(JokeTable.TABLE_NAME);

        int uriCode = matcher.match(uri);
        switch (uriCode) {
            case QUERY:

                final String filter = uri.getLastPathSegment();

                if (!filter.equals("" + Joke.SHOW_ALL)) {
                    queryBuilder.appendWhere(JokeTable.KEY_RATING + "=" + filter);
                } else {
                    selection = null;
                }

                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        final SQLiteDatabase database = databaseHelper.getWritableDatabase();
        final Cursor cursor = queryBuilder.query(database, projection, selection, null, null, null, null);

        /**
         * Register to watch a content URI for changes. We need to provide
         * the content resolver from the this context. The listener attached to this
         * resolver will be notified.
         */
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    /**
     * We don't care of MIME types for this application.
     */
    @Override
    public String getType(Uri uri) {
        return null;
    }

    /**
     * Inserts a new joke into the joke table given a specific URI for a joke,
     * and the values of that joke, writes a new row in the table filled with
     * that joke's information and gives the joke a new ID, then returns
     * a URI containing the ID of the inserted joke.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase database = databaseHelper.getWritableDatabase();

        long id = 0; // ID of the inserted joke.

        final int uriCode = matcher.match(uri);
        switch (uriCode) {

            /**
             * Expects a joke ID, but we will do nothing with the passed-in ID since
             * the databaseHelper will automatically increment the ID.
             * IMPORTANT: The joke ID cannot be set to a negative number (e.g. -1);
             * -1 is not interpreted as a numerical value by the UriMatcher.
             */
            case UPDATE:

                id = database.insert(JokeTable.TABLE_NAME, null, values);

                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        /**
         * Alert any watchers of an underlying data change for content/view refreshing.
         */
        getContext().getContentResolver().notifyChange(uri, null);

        return Uri.parse(BASE_PATH + "/" + id);
    }

    /**
     * Removes a row from the joke table given a specific URI containing a joke ID,
     * removes rows in the table that match the ID and returns the number of rows removed.
     * Since IDs are automatically incremented on insertion, this will only ever remove
     * a single row from the joke table.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase database = databaseHelper.getWritableDatabase();

        int rowsDeleted = 0;

        int uriCode = matcher.match(uri);
        switch (uriCode) {
            case UPDATE:

                final String id = uri.getLastPathSegment();

                rowsDeleted = database.delete(JokeTable.TABLE_NAME, JokeTable.KEY_ID + "=" + id, null);

                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if (rowsDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    /**
     * Updates a row in the joke table given a specific URI containing a joke ID
     * and the new joke values, updates the values in the row with the matching ID
     * in the table.
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final SQLiteDatabase database = databaseHelper.getWritableDatabase();

        int rowsUpdated = 0;

        int uriCode = matcher.match(uri);
        switch (uriCode) {
            case UPDATE:

                final String id = uri.getLastPathSegment();

                rowsUpdated = database.update(JokeTable.TABLE_NAME, values, JokeTable.KEY_ID + "=" + id, null);

                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if (rowsUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    /**
     * Verifies the correct set of columns to return data from when performing a query.
     *
     * @param projection
     *          The set of columns to be queried.
     */
    private void checkColumns(final String[] projection) {
        final String[] available = {JokeTable.KEY_ID, JokeTable.KEY_TEXT, JokeTable.KEY_RATING};

        if (null != projection) {
            final HashSet<String> requestedColumns = new HashSet<>(Arrays.asList(projection));
            final HashSet<String> availableColumns = new HashSet<>(Arrays.asList(available));

            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection.");
            }
        }
    }

}
