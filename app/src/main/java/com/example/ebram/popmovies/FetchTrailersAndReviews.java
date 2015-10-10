package com.example.ebram.popmovies;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
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
import java.util.Vector;

import com.example.ebram.popmovies.Data.MovieContract.ReviewsTrailersMovieEntry;

/**
 * Created by Ebram Samy on 10/7/2015.
 */
public class FetchTrailersAndReviews extends AsyncTask<String, Void, Void> {
    private static final int REVIEW_TYPE = 0;
    private static final int TRAILER_TYPE = 1;
    private final Context mContext;
    private int movieID;

    public FetchTrailersAndReviews(Context context) {
        mContext = context;
    }


    @Override
    protected Void doInBackground(String... params) {
        movieID = Integer.parseInt(params[0]);
        parseJasonTrailers(movieID);
        parseJasonReviews(movieID);

        return null;
    }

    public void parseJasonTrailers(int movieId) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String trailerJsonStr = null;
        try {
            final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/movie/";
            final String API_KEY = "api_key";
            Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon().appendPath(movieId + "").appendPath("videos")
                    .appendQueryParameter(API_KEY, [API KEY])
                    .build();
            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            trailerJsonStr = buffer.toString();
        } catch (IOException e) {
            return;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("Trailer", "Error closing stream", e);
                }
            }
        }
        try {
            int inserted = insertTrailers(trailerJsonStr, movieId);
        } catch (JSONException ex) {
            Log.e("Trailer", ex.getMessage(), ex);
            ex.printStackTrace();
        }
    }

    private int insertTrailers(String trailerJsonStr, int movieId) throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        final String OWM_LIST = "results";

        JSONObject trailerJson = new JSONObject(trailerJsonStr);
        JSONArray trailerArray = trailerJson.getJSONArray(OWM_LIST);
        Vector<ContentValues> cVVector = new Vector<ContentValues>(trailerJson.length());

        String trailerID;
        int trailerMovieID;
        String trailerKey;
        String trailerName;
        for (int i = 0; i < trailerArray.length(); i++) {
            trailerMovieID = movieId;

            trailerID = trailerArray.getJSONObject(i).getString("id");
            trailerKey = ("https://www.youtube.com/watch?v=" + trailerArray.getJSONObject(i).getString("key"));
            trailerName = (trailerArray.getJSONObject(i).getString("name"));

            ContentValues trailerValues = new ContentValues();

            trailerValues.put(ReviewsTrailersMovieEntry.COLUMN_REVIEW_TRAILER_ID, trailerID);
            trailerValues.put(ReviewsTrailersMovieEntry.COLUMN_REVIEW_TRAILER_MOVIE_ID, trailerMovieID);
            trailerValues.put(ReviewsTrailersMovieEntry.COLUMN_REVIEW_TRAILER_CONTENT, trailerKey);
            trailerValues.put(ReviewsTrailersMovieEntry.COLUMN_REVIEW_TRAILER_NAME, trailerName);
            trailerValues.put(ReviewsTrailersMovieEntry.COLUMN_REVIEW_TRAILER_TYPE, TRAILER_TYPE);
            cVVector.add(trailerValues);

        }
        int inserted = 0;
        // add to database
        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            inserted = mContext.getContentResolver().bulkInsert(ReviewsTrailersMovieEntry.CONTENT_URI, cvArray);
        }
        return inserted;
    }

    public void parseJasonReviews(int movieId) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String reviewsJsonStr = null;
        try {
            final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/movie/";
            final String API_KEY = "api_key";
            Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon().appendPath(movieId + "").appendPath("reviews")
                    .appendQueryParameter(API_KEY, [API KEY])
                    .build();
            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            reviewsJsonStr = buffer.toString();

        } catch (IOException e) {

            return;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("reviews", "Error closing stream", e);
                }
            }
        }
        try {
            int inserted = insertReviews(reviewsJsonStr, movieId);
        } catch (JSONException ex) {
            Log.e("reviews", ex.getMessage(), ex);
            ex.printStackTrace();
        }
    }

    private int insertReviews(String reviewsJsonStr, int movieId) throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        final String OWM_LIST = "results";

        JSONObject reviewsJson = new JSONObject(reviewsJsonStr);
        JSONArray reviewsArray = reviewsJson.getJSONArray(OWM_LIST);
        Vector<ContentValues> cVVector = new Vector<ContentValues>(reviewsJson.length());

        String reviewID;
        int reviewMovieID;
        String reviewAuthor;
        String reviewContent;
        for (int i = 0; i < reviewsArray.length(); i++) {
            reviewMovieID = movieId;


            reviewID = reviewsArray.getJSONObject(i).getString("id");
            reviewAuthor = (reviewsArray.getJSONObject(i).getString("author"));
            reviewContent = (reviewsArray.getJSONObject(i).getString("content"));

            ContentValues reviewValues = new ContentValues();

            reviewValues.put(ReviewsTrailersMovieEntry.COLUMN_REVIEW_TRAILER_ID, reviewID);
            reviewValues.put(ReviewsTrailersMovieEntry.COLUMN_REVIEW_TRAILER_MOVIE_ID, reviewMovieID);
            reviewValues.put(ReviewsTrailersMovieEntry.COLUMN_REVIEW_TRAILER_CONTENT, reviewContent);
            reviewValues.put(ReviewsTrailersMovieEntry.COLUMN_REVIEW_TRAILER_NAME, reviewAuthor);
            reviewValues.put(ReviewsTrailersMovieEntry.COLUMN_REVIEW_TRAILER_TYPE, REVIEW_TYPE);

            cVVector.add(reviewValues);

        }
        int inserted = 0;
        // add to database
        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            inserted = mContext.getContentResolver().bulkInsert(ReviewsTrailersMovieEntry.CONTENT_URI, cvArray);
        }
        return inserted;
    }

}
