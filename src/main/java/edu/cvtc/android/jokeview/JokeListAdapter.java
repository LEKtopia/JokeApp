package edu.cvtc.android.jokeview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by gandrews7 on 11/15/16.
 */
public class JokeListAdapter extends BaseAdapter {

    private Context context;
    private List<Joke> jokeList;

    public JokeListAdapter(final Context context, final List<Joke> jokeList) {
        this.context = context;
        this.jokeList = jokeList;
    }

    @Override
    public int getCount() {
        return null != jokeList ? jokeList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return null != jokeList ? jokeList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Joke joke = jokeList.get(position);

        convertView = new JokeView(context, joke);

        return convertView;
    }
}
