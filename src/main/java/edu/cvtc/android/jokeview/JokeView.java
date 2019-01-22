package edu.cvtc.android.jokeview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * Created by gandrews7 on 11/15/16.
 */
public class JokeView extends LinearLayout implements RadioGroup.OnCheckedChangeListener {

    /**
     * Displays the joke text.
     */
    private TextView jokeTextView;

    /**
     * Displays the rating radio group to LIKE/DISLIKE Jokes.
     */
    private RadioGroup ratingRadioGroup;
    private RadioButton likeButton;
    private RadioButton dislikeButton;

    /**
     * Model for this View, containing joke information.
     */
    private Joke joke;

    /**
     * Defines what happens when a Joke is changed.
     */
    private OnJokeChangeListener onJokeChangeListener;

    /**
     * Interface definition for the OnJokeChangeListener.
     */
    public static interface OnJokeChangeListener {
        public void onJokeChanged(JokeView view, Joke joke);
    }

    public JokeView(Context context, Joke joke) {
        super(context);

        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.joke_view, this, true);

        jokeTextView = (TextView) findViewById(R.id.jokeTextView);
        ratingRadioGroup = (RadioGroup) findViewById(R.id.ratingRadioGroup);
        likeButton = (RadioButton) findViewById(R.id.likeButton);
        dislikeButton = (RadioButton) findViewById(R.id.dislikeButton);

        ratingRadioGroup.setOnCheckedChangeListener(this);

        setJoke(joke);
    }

    public Joke getJoke() {
        return joke;
    }

    public void setJoke(final Joke joke) {

        this.joke = joke;

        jokeTextView.setText(joke.getText());

        switch (joke.getRating()) {
            case Joke.LIKE:
                likeButton.setChecked(true);
                break;
            case Joke.DISLIKE:
                dislikeButton.setChecked(true);
                break;
            default:
                break;
        }

        requestLayout();
    }

    public void setOnJokeChangeListener(OnJokeChangeListener onJokeChangeListener) {
        this.onJokeChangeListener = onJokeChangeListener;
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedID) {

        Log.d("joke_view", "Checked ID: " + checkedID);

        switch (checkedID) {
            case R.id.likeButton:
                likeButton.setChecked(true);
                joke.setRating(Joke.LIKE);
                notifyOnJokeChangeListener();
                break;
            case R.id.dislikeButton:
                dislikeButton.setChecked(true);
                joke.setRating(Joke.DISLIKE);
                notifyOnJokeChangeListener();
                break;
            default:
                ratingRadioGroup.clearCheck();
                joke.setRating(Joke.UNRATED);
                notifyOnJokeChangeListener();
                break;
        }
    }

    private void notifyOnJokeChangeListener() {
        if (null != onJokeChangeListener) {
            onJokeChangeListener.onJokeChanged(this, joke);
        }
    }
}
