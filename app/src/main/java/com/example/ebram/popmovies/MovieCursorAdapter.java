package com.example.ebram.popmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Ebram Samy on 9/15/2015.
 */
public class MovieCursorAdapter extends CursorAdapter {

    public MovieCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);


        return view;
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String id = cursor.getString(MoviesFragment.COLUMN_MOVIE_ID);
        String title = cursor.getString(MoviesFragment.COLUMN_MOVIE_TITLE);
        TextView movieTit = viewHolder.movieTit;
        movieTit.setText(title);

        String postPath = cursor.getString(MoviesFragment.COLUMN_MOVIE_POSTER_PATH);
        ImageView moviePost = viewHolder.moviePost;
        URL url = null;
        try {
            url = new URL(postPath);
            Picasso.with(context).load(url.toString()).into(moviePost);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    public static class ViewHolder {

        public final TextView movieTit;

        public final ImageView moviePost;

        public ViewHolder(View view) {
            movieTit = (TextView) view.findViewById(R.id.movieTitle);
            moviePost = (ImageView) view.findViewById(R.id.moviePoster);
        }
    }
}
