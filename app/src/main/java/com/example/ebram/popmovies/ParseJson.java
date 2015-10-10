package com.example.ebram.popmovies;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.ebram.popmovies.Data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Vector;

/**
 * Created by Ebram on 8/22/2015.
 */
public class ParseJson {
    private static Context mContext;

    public static int getMoviesFromJson(String moviesJsonStr, String sort, Context context) throws JSONException {
        mContext = context;

        // These are the names of the JSON objects that need to be extracted.
        final String OWM_LIST = "results";

        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray moviesArray = moviesJson.getJSONArray(OWM_LIST);
        Vector<ContentValues> cVVector = new Vector<ContentValues>(moviesJson.length());


        int id;
        String movieTitle;
        String posterPath;
        String overview;
        String voteAverage;
        String releaseDate;
        for (int i = 0; i < moviesArray.length(); i++) {


            id = moviesArray.getJSONObject(i).getInt("id");
            movieTitle = (moviesArray.getJSONObject(i).getString("title"));
            posterPath = ("http://image.tmdb.org/t/p/w185" + moviesArray.getJSONObject(i).getString("poster_path"));
            overview = (moviesArray.getJSONObject(i).getString("overview"));
            voteAverage = (moviesArray.getJSONObject(i).getString("vote_average"));
            releaseDate = (moviesArray.getJSONObject(i).getString("release_date"));

            ContentValues movieValues = new ContentValues();

            //Note No difference Between  Names In PopMovieEntry & RatedMovieEntry column
            movieValues.put(MovieContract.PopMovieEntry.COLUMN_MOVIE_ID, id);
            movieValues.put(MovieContract.PopMovieEntry.COLUMN_MOVIE_TITLE, movieTitle);
            movieValues.put(MovieContract.PopMovieEntry.COLUMN_MOVIE_OVERVIEW, overview);
            movieValues.put(MovieContract.PopMovieEntry.COLUMN_MOVIE_POSTER_PATH, posterPath);
            movieValues.put(MovieContract.PopMovieEntry.COLUMN_MOVIE_RELEASE_DATE, releaseDate);
            movieValues.put(MovieContract.PopMovieEntry.COLUMN_MOVIE_VOTE_AVERAGE, voteAverage);

            cVVector.add(movieValues);

        }
        int inserted = 0;
        // add to database
        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            if (sort.equals("vote_average.desc")) {
                context.getContentResolver().delete(MovieContract.RatedMovieEntry.CONTENT_URI, null, null);
                inserted = context.getContentResolver().bulkInsert(MovieContract.RatedMovieEntry.CONTENT_URI, cvArray);
            } else {
                context.getContentResolver().delete(MovieContract.PopMovieEntry.CONTENT_URI, null, null);
                inserted = context.getContentResolver().bulkInsert(MovieContract.PopMovieEntry.CONTENT_URI, cvArray);
            }
        }
        return inserted;

    }


}
