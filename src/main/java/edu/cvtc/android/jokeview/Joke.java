package edu.cvtc.android.jokeview;

/**
 * Created by gandrews7 on 11/15/16.
 */
public class Joke {

    public static final int UNRATED = 0;
    public static final int LIKE = 1;
    public static final int DISLIKE = 2;
    public static final int SHOW_ALL = 3;

    private long id;
    private String text;
    private int rating;

    public Joke(final String text) {
        this.text = text;
        rating = UNRATED;
    }

    public Joke(final long id, final String text, final int rating) {
        this.id = id;
        this.text = text;
        this.rating = rating;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return text;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Joke
                && ((Joke) obj).getText().equals(text);
    }
}
