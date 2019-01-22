package edu.cvtc.android.jokeview;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by gandrews7 on 12/8/16.
 */
public class JokeCursorAdapter extends CursorAdapter {

    /**
     * The listener that will be notified when the internal state changes
     * for a Joke contained in a JokeView created/managed by this Adapter
     */
    private JokeView.OnJokeChangeListener onJokeChangeListener;

    /**
     * Parameterized constructor that takes in the context in which
     * the adapter is used and the Cursor to which it is bound.
     *
     * @param context
     * @param jokeCursor
     *              A database Cursor object that contains a result set
     *              of Joke objects to be bound to JokeView objects.
     * @param flags
     */
    public JokeCursorAdapter(final Context context, final Cursor jokeCursor, int flags) {
        super(context, jokeCursor, flags);
    }

    public void setOnJokeChangeListener(JokeView.OnJokeChangeListener onJokeChangeListener) {
        this.onJokeChangeListener = onJokeChangeListener;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        final Joke joke = new Joke( cursor.getLong(JokeTable.INDEX_ID),
                                    cursor.getString(JokeTable.INDEX_TEXT),
                                    cursor.getInt(JokeTable.INDEX_RATING));

        final JokeView jokeView = new JokeView(context, joke);
        jokeView.setOnJokeChangeListener(onJokeChangeListener);

        return jokeView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        final Joke joke = new Joke( cursor.getLong(JokeTable.INDEX_ID),
                cursor.getString(JokeTable.INDEX_TEXT),
                cursor.getInt(JokeTable.INDEX_RATING));

        ((JokeView) view).setOnJokeChangeListener(null); // stop recursive 'out of memory' issue from happening
        ((JokeView) view).setJoke(joke);
        ((JokeView) view).setOnJokeChangeListener(onJokeChangeListener);
    }
}
