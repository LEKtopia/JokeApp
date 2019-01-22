package edu.cvtc.android.jokeview;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, JokeView.OnJokeChangeListener {

    private List<Joke> jokeList = new ArrayList<>();
    private List<Joke> filteredJokeList = new ArrayList<>();

    private JokeCursorAdapter jokeCursorAdapter;

    private Button addJokeButton;
    private EditText jokeEditText;
    private ListView jokeListView;

    /**
     * Filter menu reference variables.
     */
    private Menu filterMenu;
    private int filter;

    /**
     * Reference variable for the ListView item selected via the LongClick event.
     */
    private JokeView selectedView;

    /**
     * Keys for storing and retrieving values for state.
     */
    private static final String SAVED_TEXT_KEY = "jokeText";
    private static final String SAVED_FILTER_KEY = "filter";

    /**
     * ID for the CursorLoader to initialize and restart in LoaderManager to load the Cursor.
     */
    private static final int LOADER_ID = 1;

    /**
     * ActionMode and Callback for Action Bar Menu behavior that will run
     * when a user Long Clicks on a ListView item.
     */
    private ActionMode actionMode;
    private ActionMode.Callback callback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            actionMode = mode;
            final MenuInflater inflater = actionMode.getMenuInflater();
            inflater.inflate(R.menu.activity_main_action, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_remove:
                    removeJoke(MainActivity.this.selectedView.getJoke());
                    mode.finish();
                    break;
                default:
                    break;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
        }
    };

    private void removeJoke(final Joke joke) {
        final Uri uri = Uri.parse(JokeContentProvider.CONTENT_URI + "/joke/" + joke.getId());
        getContentResolver().delete(uri, null, null);
        fillData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeLayout();

        initializeEventListeners();

        /**
         * Initialize the CursorLoader for the first time.
         */
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);

        /**
         * Shared Preferences - Check for saved text to restore.
         * If there is not text, populate jokeEditText with
         * the empty String "".
         */
        final SharedPreferences preferences = getPreferences(MODE_PRIVATE);

        final String savedText = preferences.getString(SAVED_TEXT_KEY, "");

        jokeEditText.setText(savedText);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        /**
         * Shared Preferences - Save the text from jokeEditText
         */
        final SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        preferences.edit().putString(SAVED_TEXT_KEY, jokeEditText.getText().toString()).commit();
    }

    /**
     * Save instance state.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(SAVED_FILTER_KEY, filter);
    }

    /**
     * If there is no filter, default to SHOW_ALL
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        filter = savedInstanceState.getInt(SAVED_FILTER_KEY, Joke.SHOW_ALL);

        filter(filter);
    }


    private void initializeLayout() {

        setContentView(R.layout.activity_main);

        addJokeButton = (Button) findViewById(R.id.addJokeButton);
        jokeEditText = (EditText) findViewById(R.id.jokeEditText);

        jokeListView = (ListView) findViewById(R.id.jokeListViewGroup);
        jokeListView.setClickable(true);
        jokeListView.setLongClickable(true);

        jokeCursorAdapter = new JokeCursorAdapter(this, null, 0);
        jokeCursorAdapter.setOnJokeChangeListener(this);

        jokeListView.setAdapter(jokeCursorAdapter);
    }

    /**
     * Setting up the Options Menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        filterMenu = menu;

        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_filter, filterMenu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        filterMenu = menu;

        setMenuTitle();

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.submenu_like:
                filter(Joke.LIKE);
                return true;
            case R.id.submenu_dislike:
                filter(Joke.DISLIKE);
                return true;
            case R.id.submenu_unrated:
                filter(Joke.UNRATED);
                return true;
            case R.id.submenu_show_all:
                filter(Joke.SHOW_ALL);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void filter(int filterType) {

        filter = filterType;

        setMenuTitle();

        fillData();
    }

    private void setMenuTitle() {
        if (null != filterMenu) {
            filterMenu.getItem(0).setTitle(getMenuTitleText());
        }
    }

    private String getMenuTitleText() {
        switch (filter) {
            case Joke.LIKE:
                return getResources().getString(R.string.like_menuitem);
            case Joke.DISLIKE:
                return getResources().getString(R.string.dislike_menuitem);
            case Joke.UNRATED:
                return getResources().getString(R.string.unrated_menuitem);
            default:
                return getResources().getString(R.string.show_all_menuitem);
        }
    }

    private void initializeEventListeners() {
        addJokeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addJokeFromEditText();
            }
        });

        jokeEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {

                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_NUMPAD_ENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            addJokeFromEditText();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        jokeListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                view.setSelected(true);
                MainActivity.this.selectedView = (JokeView) view;

                actionMode = MainActivity.this.startSupportActionMode(callback);

                return true;
            }
        });
    }

    private void addJokeFromEditText() {
        final String jokeText = jokeEditText.getText().toString().trim();

        if (!jokeText.isEmpty()) {
            addJoke(new Joke(jokeText));
            jokeEditText.setText("");
        }

        /**
         * Hide the soft keyboard.
         */
        final View view = this.getCurrentFocus();
        if (null != view) {
            final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void addJoke(final Joke joke) {
        Uri uri = Uri.parse(JokeContentProvider.CONTENT_URI + "/joke/" + joke.getId());
        uri = getContentResolver().insert(uri, setUpContentValues(joke));

        final int id = Integer.parseInt(uri.getLastPathSegment());
        joke.setId(id);

        fillData();
    }


    /**
     * Implementation of LoaderCallbacks methods.
     */

    @Override
    public Loader onCreateLoader(int id, Bundle args) {

        final String[] projection = {JokeTable.KEY_ID, JokeTable.KEY_TEXT, JokeTable.KEY_RATING };

        final Uri uri = Uri.parse(JokeContentProvider.CONTENT_URI + "/filter/" + filter);

        final CursorLoader cursorLoader = new CursorLoader(this, uri, projection, null, null, null);

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        jokeCursorAdapter.swapCursor(data);
        jokeCursorAdapter.setOnJokeChangeListener(this);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        jokeCursorAdapter.swapCursor(null);
    }

    /**
     * Set up the ContentValues for inserting and updating Jokes.
     */
    private ContentValues setUpContentValues(final Joke joke) {
        final ContentValues contentValues = new ContentValues();

        contentValues.put(JokeTable.KEY_TEXT, joke.getText());
        contentValues.put(JokeTable.KEY_RATING, joke.getRating());

        return contentValues;
    }

    @Override
    public void onJokeChanged(JokeView view, Joke joke) {

        final Uri uri = Uri.parse(JokeContentProvider.CONTENT_URI + "/joke/" + joke.getId());

        getContentResolver().update(uri, setUpContentValues(joke), null, null);

        // Restart the loader and bind the data to our ListView
        fillData();
    }

    private void fillData() {
        getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
        jokeListView.setAdapter(jokeCursorAdapter);
    }
}







